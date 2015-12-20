package edu.brandeis.cs.uima;

import edu.brandeis.cs.json.XmlToJson;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.lappsgrid.serialization.lif.Container;

/**
 * Created by shi on 12/20/15.
 */
public class UimaOpenNlpNamedEntityRecognizer extends UimaOpenNlpService {

    static AnalysisEngine aae;

    static {
        try {
            aae = opennlpuimaInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String dsl = null;

    public UimaOpenNlpNamedEntityRecognizer(){
        dsl = getTemplate();
    }

    @Override
    public String execute(Container json) throws UimaServiceException {
        String txt = json.getText();
        try {
            String xml = opennlpuima(aae, txt);
            return XmlToJson.transform(xml, dsl);
        } catch (Exception e) {
            e.printStackTrace();
            throw new UimaServiceException(e.getMessage());
        }
    }
}
