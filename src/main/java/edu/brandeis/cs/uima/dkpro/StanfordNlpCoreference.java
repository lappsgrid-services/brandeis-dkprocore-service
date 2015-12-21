package edu.brandeis.cs.uima.dkpro;

import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.*;
import edu.brandeis.cs.json.XmlToJson;
import edu.brandeis.cs.uima.ServiceException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.lappsgrid.serialization.lif.Container;


public class StanfordNlpCoreference extends AbstractDkProOpenNlpService {


    static AnalysisEngine aae;

    static {
        try {
            aae = uimaDkProInit(StanfordSegmenter.class, StanfordPosTagger.class,
                    StanfordParser.class, StanfordNamedEntityRecognizer.class,
                    StanfordCoreferenceResolver.class);
            System.out.println("StanfordCoreferenceResolver Init...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String dsl = null;

    public StanfordNlpCoreference() {
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
