package edu.brandeis.cs.uima.dkpro;

import edu.brandeis.cs.json.XmlToJson;
import edu.brandeis.cs.uima.UimaServiceException;
import org.apache.commons.io.FileUtils;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.lappsgrid.serialization.lif.Container;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by shi on 12/20/15.
 */
public class DkProOpenNlpService extends AbstractDkProOpenNlpService {
    static AnalysisEngine aae;

    static {
        try {
            aae = uimaDkProOpennlpInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String dsl = null;

    public DkProOpenNlpService(){
        dsl = getTemplate();
    }

    @Override
    public String execute(Container json) throws UimaServiceException {
        String txt = json.getText();
        try {
            String xml = uimaDkProOpennlp(aae, txt);
            return XmlToJson.transform(xml, dsl);
        } catch (Exception e) {
            e.printStackTrace();
            throw new UimaServiceException(e.getMessage());
        }
    }
}
