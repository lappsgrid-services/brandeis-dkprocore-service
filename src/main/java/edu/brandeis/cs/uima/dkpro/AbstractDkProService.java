package edu.brandeis.cs.uima.dkpro;

import edu.brandeis.cs.uima.AbstractWebService;
import org.apache.uima.analysis_component.AnalysisComponent;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.CAS;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.XmlCasSerializer;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

/**
 * Created by shi on 12/20/15.
 */
public abstract class AbstractDkProService  extends AbstractWebService {

    public static CAS uimaDkProInitDoc(AnalysisEngine aae) throws  Exception {
        final CAS document = CasCreationUtils.createCas(asList(aae.getMetaData()));
        document.setDocumentLanguage("en");
        return document;
    }

    public static AnalysisEngine uimaDkProInit(Class<? extends AnalysisComponent> ... componentClasses) throws Exception {
        List<AnalysisEngineDescription> aeds = new ArrayList<AnalysisEngineDescription>();
        for (Class<? extends AnalysisComponent> componentClass : componentClasses) {
            AnalysisEngineDescription aed = createEngineDescription(componentClass);
            aeds.add(aed);
        }
        String[] names = new String[aeds.size()];
        int i = 0;
        for (AnalysisEngineDescription aed : aeds) {
            names[i] = aed.getImplementationName() + "-" + i;
            i++;
        }
        AnalysisEngineDescription aaeDesc = createEngineDescription(aeds, asList(names), null, null,
                null);
        AnalysisEngine aae = createEngine(aaeDesc);
        return aae;
    }

    public static String uimaDkProXml(AnalysisEngine aae, String txt) throws Exception {
        CAS document = uimaDkProInitDoc(aae);
        document.setDocumentLanguage("en");
        document.setDocumentText(txt);
        aae.process(document);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        XmlCasSerializer.serialize(document, output);
        String xmlAnn = new String(output.toByteArray());
        document.release();
        System.out.println("<--------------------------------------------------");
        System.out.println(xmlAnn);
        System.out.println("-------------------------------------------------->");
        return xmlAnn;
    }
}
