package edu.brandeis.cs.uima;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.XmlCasSerializer;

import java.io.ByteArrayOutputStream;

/**
 * Created by lapps on 3/27/2015.
 */
public class CasWriter extends JCasConsumer_ImplBase {
    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
        try {
//            String xml = new CasToInlineXml().generateXML(aJCas.getCas());
//            System.out.println(xml);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            XmlCasSerializer.serialize(aJCas.getCas(), output);
            String xmlAnn = new String(output.toByteArray());
            System.out.println(xmlAnn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    } /* process() */
} /* class */
