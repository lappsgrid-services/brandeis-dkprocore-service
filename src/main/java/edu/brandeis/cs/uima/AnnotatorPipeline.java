package edu.brandeis.cs.uima;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.constituent.NP;
import de.tudarmstadt.ukp.dkpro.core.io.text.TextReader;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpNameFinder;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordNamedEntityRecognizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordPosTagger;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordSegmenter;
import opennlp.uima.sentdetect.SentenceDetector;
import opennlp.uima.sentdetect.SentenceModelResourceImpl;
import opennlp.uima.tokenize.Tokenizer;
import opennlp.uima.tokenize.TokenizerModelResourceImpl;
import opennlp.uima.util.UimaUtil;
import org.apache.uima.analysis_component.AnalysisComponent;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;
import org.apache.uima.util.CasToInlineXml;
import org.apache.uima.util.InvalidXMLException;

import java.io.IOException;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReader;

//import fr.univnantes.atal.uima.playground.annotators.WordCountAnnotator;
//import fr.univnantes.atal.uima.playground.resources.WordCounterImpl;


/**
 * Created by lapps on 3/26/2015.
 */
public class AnnotatorPipeline {


    public static AnalysisEngineDescription sentenceDetect() throws ResourceInitializationException,
            InvalidXMLException {
        // Create descriptors
        AnalysisEngineDescription sentenceDetector = createDescriptionAndBindModel(
                SentenceDetector.class,
                "http://opennlp.sourceforge.net/models-1.5/en-sent.bin",
                SentenceModelResourceImpl.class);
        AnalysisEngineDescription tokenizer = createDescriptionAndBindModel(
                Tokenizer.class,
                "http://opennlp.sourceforge.net/models-1.5/en-token.bin",
                TokenizerModelResourceImpl.class);
        return AnalysisEngineFactory.createEngineDescription(sentenceDetector, tokenizer);

    }

    protected static void execute() throws IOException,
            AnalysisEngineProcessException, ResourceInitializationException, InvalidXMLException {
//        URL resourceURL = AnnotatorPipeline.class.getResource("/test.txt");
//        File file = FileUtils.toFile(resourceURL);
//        String txt = FileUtils.readFileToString(file);

        String txt = "How are you?";
        AnalysisEngineDescription aggregate = sentenceDetect();
        AnalysisEngine engine = AnalysisEngineFactory.createEngine(aggregate);
        JCas jCas = engine.newJCas();
        jCas.setDocumentText(txt);
        engine.process(jCas);
    }

    private static AnalysisEngineDescription createDescriptionAndBindModel(
            Class<? extends AnalysisComponent> aeClass, String modelURL,
            Class<? extends SharedResourceObject> srClass)
            throws ResourceInitializationException, InvalidXMLException {
        AnalysisEngineDescription description = AnalysisEngineFactory.createEngineDescription(
                aeClass, UimaUtil.TOKEN_TYPE_PARAMETER,
                Tokenizer.class.getName(), UimaUtil.SENTENCE_TYPE_PARAMETER,
                SentenceDetector.class.getName());
        ExternalResourceFactory.createDependencyAndBind(description, UimaUtil.MODEL_PARAMETER, srClass,
                modelURL);
        return description;
    }



    public static class NPNEWriter extends JCasConsumer_ImplBase {

        @Override
        public void process(JCas aJCas) throws AnalysisEngineProcessException {
            try {
                String xml = new CasToInlineXml().generateXML(aJCas.getCas());
                System.out.println(xml);
            } catch (CASException e) {
                e.printStackTrace();
            }
    /* all sentences */
            for (Sentence sentence : JCasUtil.select(aJCas, Sentence.class)) {
//                StringBuffer sb = new StringBuffer();
//                sentence.prettyPrint(4, 2, sb, false);
//                System.out.println(sb);


      /* all Noun Phrases within that sentence */
                for (NP nounphrase : JCasUtil.selectCovered(aJCas, NP.class, sentence)) {

        /* all Named Entities within that noun phrase */
                    for (NamedEntity ne : JCasUtil.selectCovered(aJCas, NamedEntity.class, nounphrase)) {
//                        StringBuffer sb = new StringBuffer();
//                        ne.prettyPrint(4, 2, sb, false);
//                        System.out.println(sb);

                        System.out.println("NP " + nounphrase.getCoveredText() + "\tNE " + ne.getCoveredText());
                    } /* for each NamedEntity within the noun phrase */
                } /* for each noun phrase within the sentence */
            } /* for each sentence */
        } /* process() */
    } /* class */



    public static void stanford() throws Exception{
        CollectionReader reader = createReader(
                TextReader.class,
                TextReader.PARAM_SOURCE_LOCATION, "src/main/resources",
                TextReader.PARAM_LANGUAGE, "en",
                TextReader.PARAM_PATTERNS, new String[] { "[+]*.txt" });
        AnalysisEngineDescription seg = createEngineDescription(StanfordSegmenter.class);
        AnalysisEngineDescription tagger = createEngineDescription(StanfordPosTagger.class);
        AnalysisEngineDescription ner = createEngineDescription(StanfordNamedEntityRecognizer.class);
//        AnalysisEngineDescription parser = createEngineDescription(StanfordParser.class);
        AnalysisEngineDescription writer = createEngineDescription(NPNEWriter.class);
        SimplePipeline.runPipeline(reader, seg, tagger, ner, writer);
    }


    public static void opennlp() throws Exception {
        CollectionReader reader = createReader(
                TextReader.class,
                TextReader.PARAM_SOURCE_LOCATION, "src/main/resources",
                TextReader.PARAM_LANGUAGE, "en",
                TextReader.PARAM_PATTERNS, new String[] { "[+]*.txt" });
        AnalysisEngineDescription seg = createEngineDescription(OpenNlpSegmenter.class);
        AnalysisEngineDescription tag = createEngineDescription(OpenNlpPosTagger.class);
        AnalysisEngineDescription ner = createEngineDescription(OpenNlpNameFinder.class);
        AnalysisEngineDescription writer = createEngineDescription(NPNEWriter.class);
        SimplePipeline.runPipeline(reader, seg, tag, ner, writer);
    }

    public static void main(String[] args) throws Exception {
//        execute();
        stanford();
//        opennlp();
//        AnalysisEngineDescription sentenceDetector =
//                    createEngineDescription(SentenceDetector.class,
//                            UimaUtil.MODEL_PARAMETER, createExternalResourceDescription(
//                                    SentenceModelResourceImpl.class,"file:en-sent.bin"),
//                            UimaUtil.SENTENCE_TYPE_PARAMETER, "fr.univnantes.atal.uima.playground.types.Sentence");

//        try {
//            SimplePipeline.runPipeline(
//                    createReaderDescription(TextReader.class,
//                            TextReader.PARAM_SOURCE_LOCATION, "input/*",
//                            TextReader.PARAM_LANGUAGE, "en"),
//                    createEngineDescription(SentenceDetector.class,
//                            "opennlp.uima.ModelName", createExternalResourceDescription(
//                                    SentenceModelResourceImpl.class,
//                                    "file:models/en-sent.bin"),
//                            "opennlp.uima.SentenceType", "fr.univnantes.atal.uima.playground.types.Sentence"),
//                    createEngineDescription(Tokenizer.class,
//                            "opennlp.uima.ModelName", createExternalResourceDescription(
//                                    TokenizerModelResourceImpl.class,
//                                    "file:models/en-token.bin"),
//                            "opennlp.uima.SentenceType", "fr.univnantes.atal.uima.playground.types.Sentence",
//                            "opennlp.uima.TokenType", "fr.univnantes.atal.uima.playground.types.Token"),
//                    createEngineDescription(NameFinder.class,
//                            "opennlp.uima.ModelName", createExternalResourceDescription(
//                                    TokenNameFinderModelResourceImpl.class,
//                                    "file:models/en-ner-person.bin"),
//                            "opennlp.uima.NameType", "fr.univnantes.atal.uima.playground.types.Person",
//                            "opennlp.uima.SentenceType", "fr.univnantes.atal.uima.playground.types.Sentence",
//                            "opennlp.uima.TokenType", "fr.univnantes.atal.uima.playground.types.Token"),
//                    createEngineDescription(WordCountAnnotator.class,
//                            WordCountAnnotator.WORD_COUNTER_KEY,
//                            createExternalResourceDescription(
//                                    WordCountAnnotator.WORD_COUNTER_KEY,
//                                    WordCounterImpl.class, "")
//
//                    ),
//                    createEngineDescription(
//                            XmiWriter.class,
//                            XmiWriter.PARAM_TARGET_LOCATION, "output"));
//        } catch(IOException | UIMAException e) {
//            System.err.println("Encountered an exception: " + e.getMessage());
//            e.printStackTrace(System.err);
//        }



//        // uimaFIT automatically uses all type systems listed in META-INF/org.uimafit/types.txt
//
//        // uimaFIT doesn't provide any collection readers - so we will just instantiate a JCas and
//        // run it through our AE
//        JCas jCas = JCasFactory.createJCas();
//
//        // Instantiate the analysis engine using the value "uimaFIT" for the parameter
//        // PARAM_STRING ("stringParam").
//        AnalysisEngine analysisEngine = AnalysisEngineFactory.createPrimitive(
//                GetStartedQuickAE.class,
//                GetStartedQuickAE.PARAM_STRING, "uimaFIT");
//
//        // run the analysis engine and look for a special greeting in your console.
//        analysisEngine.process(jCas);

    }
}
