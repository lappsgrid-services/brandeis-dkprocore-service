package edu.brandeis.cs.uima.dkpro;

import de.tudarmstadt.ukp.dkpro.core.io.text.TextReader;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpNameFinder;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordNamedEntityRecognizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordParser;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordPosTagger;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordSegmenter;
import edu.brandeis.cs.uima.AnnotatorPipeline;
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


public class AbstractDkProStanfordNlpService {


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

}
