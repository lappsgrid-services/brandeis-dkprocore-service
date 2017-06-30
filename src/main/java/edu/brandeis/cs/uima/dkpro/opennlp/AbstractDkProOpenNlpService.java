package edu.brandeis.cs.uima.dkpro.opennlp;

import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpNameFinder;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpParser;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
import edu.brandeis.cs.uima.dkpro.AbstractDkProService;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.CAS;
import org.apache.uima.util.XmlCasSerializer;

import java.io.ByteArrayOutputStream;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;


public abstract class AbstractDkProOpenNlpService  extends AbstractDkProService {


    public static AnalysisEngine uimaDkProOpennlpInit() throws Exception {
        AnalysisEngineDescription seg = createEngineDescription(OpenNlpSegmenter.class);
        AnalysisEngineDescription tag = createEngineDescription(OpenNlpPosTagger.class);
        AnalysisEngineDescription parser = createEngineDescription(OpenNlpParser.class);
        AnalysisEngineDescription ner = createEngineDescription(OpenNlpNameFinder.class);

        AnalysisEngineDescription aaeDesc = createEngineDescription(
                seg, tag, parser, ner);
        AnalysisEngine aae = createEngine(aaeDesc);
        return aae;
    }




    public static void main(String []args) throws Exception{
        AnalysisEngine aae = uimaDkProOpennlpInit();
        String xml = uimaDkProXml(aae, "How are you?");
        System.out.println(xml);

    }
}
