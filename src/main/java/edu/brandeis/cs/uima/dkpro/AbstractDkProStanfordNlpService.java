package edu.brandeis.cs.uima.dkpro;

import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.*;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.CAS;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.XmlCasSerializer;

import java.io.ByteArrayOutputStream;

import static java.util.Arrays.asList;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;


public abstract class AbstractDkProStanfordNlpService extends AbstractDkProService {

    public static CAS uimaDkProStanfordInitDoc(AnalysisEngine aae) throws  Exception {
        final CAS document = CasCreationUtils.createCas(asList(aae.getMetaData()));
        document.setDocumentLanguage("en");
        return document;
    }

    public static AnalysisEngine uimaDkProStanfordInit() throws Exception {
        AnalysisEngineDescription seg = createEngineDescription(StanfordSegmenter.class);
        AnalysisEngineDescription tagger = createEngineDescription(StanfordPosTagger.class);
        AnalysisEngineDescription parser = createEngineDescription(StanfordParser.class);
        AnalysisEngineDescription ner = createEngineDescription(StanfordNamedEntityRecognizer.class);
        AnalysisEngineDescription cor = createEngineDescription(StanfordCoreferenceResolver.class);
        AnalysisEngineDescription aaeDesc = createEngineDescription(
                seg, tagger, parser, ner, cor);
        AnalysisEngine aae = createEngine(aaeDesc);
        return aae;
    }



    public static String uimaDkProStanford(AnalysisEngine aae, String txt) throws Exception {
        CAS document = uimaDkProStanfordInitDoc(aae);
        document.setDocumentLanguage("en");
        document.setDocumentText(txt);
        aae.process(document);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        XmlCasSerializer.serialize(document, output);
        String xmlAnn = new String(output.toByteArray());
        document.release();
        return xmlAnn;
    }



    public static void main(String []args) throws Exception{
        AnalysisEngine aae = uimaDkProStanfordInit();
        String xml = uimaDkProStanford(aae, "How are you?");
        System.out.println(xml);

    }

}
