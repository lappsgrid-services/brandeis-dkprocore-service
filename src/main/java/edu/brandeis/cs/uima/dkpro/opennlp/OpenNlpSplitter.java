package edu.brandeis.cs.uima.dkpro.opennlp;

import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
import edu.brandeis.cs.json.XmlToJson;
import edu.brandeis.cs.uima.dkpro.AbstractDkProService;
import edu.brandeis.cs.uima.dkpro.ServiceException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.lappsgrid.serialization.lif.Container;


public class OpenNlpSplitter extends AbstractDkProOpenNlpService {
    static AnalysisEngine aae;

    static {
        try {
            aae = AbstractDkProService.uimaDkProInit(OpenNlpSegmenter.class);
            System.out.println("OpenNlpSplitter Init...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String dsl = null;

    public OpenNlpSplitter(){
        dsl = getTemplate();
    }

    @Override
    public String execute(Container json) throws ServiceException {
        String txt = json.getText();
        try {
            String xml = AbstractDkProService.uimaDkProXml(aae, txt);
            return XmlToJson.transform(xml, dsl);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }
    }
}
