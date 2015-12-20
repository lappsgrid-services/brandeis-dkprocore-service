package edu.brandeis.cs.uima;

import de.tudarmstadt.ukp.dkpro.core.io.text.TextReader;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpNameFinder;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordNamedEntityRecognizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordParser;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordPosTagger;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordSegmenter;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.XMLOutputter;
import opennlp.uima.namefind.NameFinder;
import opennlp.uima.namefind.TokenNameFinderModelResourceImpl;
import opennlp.uima.postag.POSModelResourceImpl;
import opennlp.uima.postag.POSTagger;
import opennlp.uima.sentdetect.SentenceDetector;
import opennlp.uima.sentdetect.SentenceModelResourceImpl;
import opennlp.uima.tokenize.Tokenizer;
import opennlp.uima.tokenize.TokenizerModelResourceImpl;
import opennlp.uima.util.UimaUtil;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.XmlCasSerializer;

import java.io.ByteArrayOutputStream;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReader;
import static org.apache.uima.fit.factory.ExternalResourceFactory.createDependencyAndBind;
import static org.apache.uima.fit.factory.JCasFactory.createJCasFromPath;


public abstract class AbstractUimaStanfordNlpService extends AbstractWebService {

    public static String stanfordnlp(StanfordCoreNLP pipeline, String txt)throws Exception {
        edu.stanford.nlp.pipeline.Annotation annotation = new edu.stanford.nlp.pipeline.Annotation(txt);
        pipeline.annotate(annotation);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        XMLOutputter.xmlPrint(annotation, output, pipeline);
        String xmlAnn = new String(output.toByteArray());
        return xmlAnn;
    }


    public static JCas opennlpuimaInitDoc() throws  Exception {
        JCas document = createJCasFromPath(
                AbstractUimaStanfordNlpService.class.getResource("/uima/opennlp/descriptors/TypeSystem.xml").getPath());
        document.setDocumentLanguage("en");
        return document;
    }

    public static AnalysisEngine opennlpuimaInit() throws Exception{
        JCas document = opennlpuimaInitDoc();
        Type tokenType = document.getTypeSystem().getType("opennlp.uima.Token");
        Type sentenceType = document.getTypeSystem().getType("opennlp.uima.Sentence");
        Type nameType = document.getTypeSystem().getType("opennlp.uima.Person");
        Feature posFeature = tokenType.getFeatureByBaseName("pos");
        // Configure sentence detector
        AnalysisEngineDescription sentenceDetector = createEngineDescription(
                SentenceDetector.class,
                UimaUtil.SENTENCE_TYPE_PARAMETER, sentenceType.getName());
        createDependencyAndBind(sentenceDetector,
                UimaUtil.MODEL_PARAMETER,
                SentenceModelResourceImpl.class,
                AbstractUimaStanfordNlpService.class.getResource("/en-sent.bin").toString());
        // Configure tokenizer
        AnalysisEngineDescription tokenizer = createEngineDescription(
                Tokenizer.class,
                UimaUtil.TOKEN_TYPE_PARAMETER, tokenType.getName(),
                UimaUtil.SENTENCE_TYPE_PARAMETER, sentenceType.getName());
        createDependencyAndBind(tokenizer,
                UimaUtil.MODEL_PARAMETER,
                TokenizerModelResourceImpl.class,
                AbstractUimaStanfordNlpService.class.getResource("/en-token.bin").toString());

        // Configure part-of-speech tagger
        AnalysisEngineDescription posTagger = createEngineDescription(
                POSTagger.class,
                UimaUtil.TOKEN_TYPE_PARAMETER, tokenType.getName(),
                UimaUtil.SENTENCE_TYPE_PARAMETER, sentenceType.getName(),
                UimaUtil.POS_FEATURE_PARAMETER , posFeature.getShortName());
        createDependencyAndBind(posTagger,
                UimaUtil.MODEL_PARAMETER,
                POSModelResourceImpl.class,
                AbstractUimaStanfordNlpService.class.getResource("/en-pos-perceptron.bin").toString());

        AnalysisEngineDescription personNer = createEngineDescription(
                NameFinder.class,
                UimaUtil.TOKEN_TYPE_PARAMETER, tokenType.getName(),
                UimaUtil.SENTENCE_TYPE_PARAMETER, sentenceType.getName(),
                UimaUtil.POS_FEATURE_PARAMETER, posFeature.getShortName(),
                "opennlp.uima.NameType",nameType.getName());
        createDependencyAndBind(personNer,
                UimaUtil.MODEL_PARAMETER,
                TokenNameFinderModelResourceImpl.class,
                AbstractUimaStanfordNlpService.class.getResource("/en-ner-person.bin").toString());

        AnalysisEngineDescription aaeDesc = createEngineDescription(
                sentenceDetector, tokenizer, posTagger, personNer);
        AnalysisEngine aae = createEngine(aaeDesc);
        return aae;
    }

    public static String opennlpuima(AnalysisEngine aae, String txt) throws Exception {
        JCas document = opennlpuimaInitDoc();
        document.setDocumentLanguage("en");
        document.setDocumentText(txt);
        aae.process(document);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        XmlCasSerializer.serialize(document.getCas(), output);
        String xmlAnn = new String(output.toByteArray());
        document.release();
        return xmlAnn;
    }


    public static void uimaDkProStanford() throws Exception{
        CollectionReader reader = createReader(
                TextReader.class,
                TextReader.PARAM_SOURCE_LOCATION, "src/main/resources",
                TextReader.PARAM_LANGUAGE, "en",
                TextReader.PARAM_PATTERNS, new String[] { "[+]*.txt" });
        AnalysisEngineDescription seg = createEngineDescription(StanfordSegmenter.class);
        AnalysisEngineDescription tagger = createEngineDescription(StanfordPosTagger.class);
        AnalysisEngineDescription ner = createEngineDescription(StanfordNamedEntityRecognizer.class);
        AnalysisEngineDescription parser = createEngineDescription(StanfordParser.class);
        AnalysisEngineDescription writer = createEngineDescription(AnnotatorPipeline.NPNEWriter.class);
        SimplePipeline.runPipeline(reader, seg, tagger, ner, writer);
    }



    public static void uimaDkProOpennlp() throws Exception {
        CollectionReader reader = createReader(
                TextReader.class,
                TextReader.PARAM_SOURCE_LOCATION, "src/main/resources",
                TextReader.PARAM_LANGUAGE, "en",
                TextReader.PARAM_PATTERNS, new String[] { "[+]*.txt" });
        AnalysisEngineDescription seg = createEngineDescription(OpenNlpSegmenter.class);
        AnalysisEngineDescription tag = createEngineDescription(OpenNlpPosTagger.class);
        AnalysisEngineDescription ner = createEngineDescription(OpenNlpNameFinder.class);
        AnalysisEngineDescription writer = createEngineDescription(AnnotatorPipeline.NPNEWriter.class);
        SimplePipeline.runPipeline(reader, seg, tag, ner, writer);
    }


}
