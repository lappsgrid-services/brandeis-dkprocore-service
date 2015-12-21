package edu.brandeis.cs.uima.dkpro;

import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordSegmenter;
import edu.brandeis.cs.json.XmlToJson;
import edu.brandeis.cs.uima.ServiceException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.lappsgrid.serialization.lif.Container;


public class StanfordNlpSplitter extends AbstractDkProOpenNlpService {


    static AnalysisEngine aae;

    static {
        try {
            aae = uimaDkProInit(StanfordSegmenter.class);
            System.out.println("StanfordNlpSplitter Init...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String dsl = null;

    public StanfordNlpSplitter(){
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
