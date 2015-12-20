package edu.brandeis.cs.uima.dkpro;

import edu.brandeis.cs.json.XmlToJson;
import edu.brandeis.cs.uima.UimaServiceException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.lappsgrid.serialization.lif.Container;


public class DkProStanfordNlpService extends AbstractDkProStanfordNlpService {


    static AnalysisEngine aae;

    static {
        try {
            aae = uimaDkProStanfordInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String dsl = null;

    public DkProStanfordNlpService(){
        dsl = getTemplate();
    }

    @Override
    public String execute(Container json) throws UimaServiceException {
        String txt = json.getText();
        try {
            String xml = uimaDkProStanford(aae, txt);
            return XmlToJson.transform(xml, dsl);
        } catch (Exception e) {
            e.printStackTrace();
            throw new UimaServiceException(e.getMessage());
        }
    }
}
