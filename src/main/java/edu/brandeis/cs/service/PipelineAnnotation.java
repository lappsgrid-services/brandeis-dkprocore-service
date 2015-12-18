package edu.brandeis.cs.service;

import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpNameFinder;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordNamedEntityRecognizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordPosTagger;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordSegmenter;
import edu.brandeis.cs.json2json.Json2Json;
import edu.brandeis.cs.uima.AnnotatorPipeline;
import edu.stanford.nlp.pipeline.XMLOutputter;
import gate.*;
import gate.util.Out;
import gate.util.persistence.PersistenceManager;
import opennlp.uima.namefind.NameFinder;
import opennlp.uima.namefind.TokenNameFinderModelResourceImpl;
import opennlp.uima.postag.POSModelResourceImpl;
import opennlp.uima.postag.POSTagger;
import opennlp.uima.sentdetect.SentenceDetector;
import opennlp.uima.sentdetect.SentenceModelResourceImpl;
import opennlp.uima.tokenize.Tokenizer;
import opennlp.uima.tokenize.TokenizerModelResourceImpl;
import opennlp.uima.util.UimaUtil;
import org.apache.commons.io.FileUtils;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.XmlCasSerializer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Iterator;
import java.util.Properties;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.ExternalResourceFactory.createDependencyAndBind;
import static org.apache.uima.fit.factory.JCasFactory.createJCasFromPath;

/**
 * Created by lapps on 4/1/2015.
 */
public abstract class PipelineAnnotation  implements IPipelineAnnotation{


    public static AnalysisEngine uimaDkproStanfordInit() throws Exception{
        AnalysisEngineDescription seg = createEngineDescription(StanfordSegmenter.class);
        AnalysisEngineDescription tagger = createEngineDescription(StanfordPosTagger.class);
        AnalysisEngineDescription ner = createEngineDescription(StanfordNamedEntityRecognizer.class);
        AnalysisEngineDescription writer = createEngineDescription(AnnotatorPipeline.NPNEWriter.class);
        AnalysisEngineDescription aaeDesc = createEngineDescription(
                new AnalysisEngineDescription[]{seg, tagger, ner, writer});
        AnalysisEngine aae = createEngine(aaeDesc);
        return aae;
    }

    public static String uimaDkpro(AnalysisEngine aae, String txt) throws Exception{
        JCas jcas = JCasFactory.createJCas();
        jcas.setDocumentText(txt);
        jcas.setDocumentLanguage("en");
        aae.process(jcas);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        XmlCasSerializer.serialize(jcas.getCas(), output);
        String xmlAnn = new String(output.toByteArray());
        jcas.release();
        return xmlAnn;
    }
    public static AnalysisEngine uimaDkproOpennlpInit() throws Exception {
        AnalysisEngineDescription seg = createEngineDescription(OpenNlpSegmenter.class);
        AnalysisEngineDescription tag = createEngineDescription(OpenNlpPosTagger.class);
        AnalysisEngineDescription ner = createEngineDescription(OpenNlpNameFinder.class);
        AnalysisEngineDescription writer = createEngineDescription(AnnotatorPipeline.NPNEWriter.class);
        AnalysisEngineDescription aaeDesc = createEngineDescription(
                new AnalysisEngineDescription[]{seg, tag, ner, writer});
        AnalysisEngine aae = createEngine(aaeDesc);
        return aae;
    }

    public static void gateInit() throws Exception {
        Out.prln("Initialising GATE...");
        File gatedir = FileUtils.toFile(PipelineAnnotation.class.getResource("/gate/"));
        System.setProperty("gate.site.config", new File(gatedir,"gate.xml").getPath());
        System.setProperty("gate.plugins.home", new File(gatedir, "plugins").getPath());
        Gate.init();
        Out.prln("...GATE initialised");
    }

    public static CorpusController gateLoadAnnie() throws Exception {
        // initialise ANNIE (this may take several minutes)
        Out.prln("Initialising ANNIE...");
        // load the ANNIE application from the saved state in plugins/ANNIE
        File pluginsHome = Gate.getPluginsHome();
        File anniePlugin = new File(pluginsHome, "ANNIE");
        File annieGapp = new File(anniePlugin, "ANNIE_with_defaults.gapp");
        CorpusController annieController =
                (CorpusController) PersistenceManager.loadObjectFromFile(annieGapp);
        Out.prln("...ANNIE loaded");
        return annieController;
    }

    public static CorpusController gateLoadOpenNLP() throws Exception {
        // initialise ANNIE (this may take several minutes)
        Out.prln("Initialising OpenNLP...");
        // load the ANNIE application from the saved state in plugins/ANNIE
        File pluginsHome = Gate.getPluginsHome();
        File opennlpPlugin = new File(pluginsHome, "OpenNLP");
        File opennlpResources = new File(opennlpPlugin, "resources");
        File opennlpGapp = new File(opennlpResources, "opennlp.gapp");
        CorpusController annieController =
                (CorpusController) PersistenceManager.loadObjectFromFile(opennlpGapp);
        Out.prln("...OpenNLP loaded");
        return annieController;
    }


    public static void main(String []args) throws Exception{
        gateInit();
        CorpusController annieController = gateLoadAnnie();
        String txt = gateExecute(annieController, "How are you, Mike?");
        System.out.println(txt);
    }

    public static String gateExecute(CorpusController annieController, String txt) throws Exception {
        // create a GATE corpus and add a document for each command-line
        // argument
        Corpus corpus = Factory.newCorpus("Prepare corpus ...");
        Document document = Factory.newDocument(txt);
        corpus.add(document);
        // tell the pipeline about the corpus and run it
        annieController.setCorpus(corpus);
        Out.prln("Running GATE Controller...");
        annieController.execute();
        Out.prln("...GATE Controller complete");
        // for each document, get an XML document with the
        // person and location names added
        Iterator iter = corpus.iterator();
        while(iter.hasNext()) {
            Document doc = (Document) iter.next();
            Out.prln("<------------------");
            Out.prln(doc.toXml());
            Out.prln("------------------>");
            return doc.toXml();
        }
        return null;
    }

    public static edu.stanford.nlp.pipeline.StanfordCoreNLP stanfordnlpInit()throws Exception {
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner");
        edu.stanford.nlp.pipeline.StanfordCoreNLP pipeline = new edu.stanford.nlp.pipeline.StanfordCoreNLP(props);
        return pipeline;
    }

    public static String stanfordnlp(edu.stanford.nlp.pipeline.StanfordCoreNLP pipeline, String txt)throws Exception {
        edu.stanford.nlp.pipeline.Annotation annotation = new edu.stanford.nlp.pipeline.Annotation(txt);
        pipeline.annotate(annotation);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        XMLOutputter.xmlPrint(annotation, output, pipeline);
        String xmlAnn = new String(output.toByteArray());
        // System.out.println(xmlAnn);
        return xmlAnn;
    }


    public static JCas opennlpuimaInitDoc() throws  Exception {
        JCas document = createJCasFromPath(
                PipelineAnnotation.class.getResource("/uima/opennlp/descriptors/TypeSystem.xml").getPath());
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
                PipelineAnnotation.class.getResource("/en-sent.bin").toString());
        // Configure tokenizer
        AnalysisEngineDescription tokenizer = createEngineDescription(
                Tokenizer.class,
                UimaUtil.TOKEN_TYPE_PARAMETER, tokenType.getName(),
                UimaUtil.SENTENCE_TYPE_PARAMETER, sentenceType.getName());
        createDependencyAndBind(tokenizer,
                UimaUtil.MODEL_PARAMETER,
                TokenizerModelResourceImpl.class,
                PipelineAnnotation.class.getResource("/en-token.bin").toString());
        // Configure part-of-speech tagger
        AnalysisEngineDescription posTagger = createEngineDescription(
                POSTagger.class,
                UimaUtil.TOKEN_TYPE_PARAMETER, tokenType.getName(),
                UimaUtil.SENTENCE_TYPE_PARAMETER, sentenceType.getName(),
                UimaUtil.POS_FEATURE_PARAMETER , posFeature.getShortName());
        createDependencyAndBind(posTagger,
                UimaUtil.MODEL_PARAMETER,
                POSModelResourceImpl.class,
                PipelineAnnotation.class.getResource("/en-pos-perceptron.bin").toString());
        AnalysisEngineDescription personNer = createEngineDescription(
                NameFinder.class,
                UimaUtil.TOKEN_TYPE_PARAMETER, tokenType.getName(),
                UimaUtil.SENTENCE_TYPE_PARAMETER, sentenceType.getName(),
                UimaUtil.POS_FEATURE_PARAMETER, posFeature.getShortName(),
                "opennlp.uima.NameType",nameType.getName());
        createDependencyAndBind(personNer,
                UimaUtil.MODEL_PARAMETER,
                TokenNameFinderModelResourceImpl.class,
                PipelineAnnotation.class.getResource("/en-ner-person.bin").toString());
        AnalysisEngineDescription aaeDesc = createEngineDescription(
                new AnalysisEngineDescription[]{sentenceDetector, tokenizer, posTagger, personNer});
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


    static {
        try {
            gateInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public String getJSON(String doc) throws Exception{
//        return XML.toJSONObject(getXML(doc)).toString(2);
        return Json2Json.xml2json(getXML(doc));
    }

    public static class GateAnnie extends PipelineAnnotation{
        static CorpusController gateAnnie = null;

        public GateAnnie() throws Exception {
            if(gateAnnie == null)
                gateAnnie = gateLoadAnnie();
        }

        @Override
        public String getXML(String doc)  throws Exception{
            return gateExecute(gateAnnie, doc);
        }

    }

    public static class GateOpenNLP extends PipelineAnnotation{
        static CorpusController gateOpennlp = null;
        public GateOpenNLP() throws Exception {
            if(gateOpennlp == null)
                gateOpennlp = gateLoadOpenNLP();
        }

        @Override
        public String getXML(String doc) throws Exception{
            return gateExecute(gateOpennlp, doc);
        }
    }

    public static class UimaDkproOpenNLP extends PipelineAnnotation{
        static AnalysisEngine aee;
        public UimaDkproOpenNLP() throws Exception {
            if(aee == null)
                aee = uimaDkproOpennlpInit();
        }
        @Override
        public String getXML(String doc) throws Exception {
            return uimaDkpro(aee, doc);
        }

    }

    public static class UimaDkproStanford extends PipelineAnnotation{
        static AnalysisEngine aee;
        public UimaDkproStanford() throws Exception {
            if(aee == null)
                aee = uimaDkproStanfordInit();
        }

        @Override
        public String getXML(String doc) throws Exception {
            return uimaDkpro(aee, doc);
        }
    }

    public static class UimaOpenNLP extends PipelineAnnotation{
        static AnalysisEngine aae;
        public UimaOpenNLP() throws Exception {
            if(aae == null)
                aae = opennlpuimaInit();
        }
        @Override
        public String getXML(String doc) throws Exception {
            return opennlpuima(aae, doc);
        }

    }

    public static class StanfordCoreNLP extends PipelineAnnotation{
        static edu.stanford.nlp.pipeline.StanfordCoreNLP pipeline;
        public StanfordCoreNLP() throws Exception {
            if(pipeline == null)
                pipeline = stanfordnlpInit();
        }

        @Override
        public String getXML(String doc) throws Exception {
            return stanfordnlp(pipeline,doc);
        }
    }
}
