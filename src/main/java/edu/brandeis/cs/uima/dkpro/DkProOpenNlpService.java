package edu.brandeis.cs.uima.dkpro;

import edu.brandeis.cs.json.XmlToJson;
import edu.brandeis.cs.uima.ServiceException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.lappsgrid.serialization.lif.Container;

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
    public String execute(Container json) throws ServiceException {
        String txt = json.getText();
        try {
            String xml = uimaDkProXml(aae, txt);
            return XmlToJson.transform(xml, dsl);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }
    }
}
