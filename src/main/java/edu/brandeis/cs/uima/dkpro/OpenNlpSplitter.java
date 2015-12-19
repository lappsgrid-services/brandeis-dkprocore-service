package edu.brandeis.cs.uima.dkpro;

import edu.brandeis.cs.uima.AbstractUimaService;
import edu.brandeis.cs.uima.UimaServiceException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.XmlCasSerializer;
import org.lappsgrid.serialization.lif.Container;

import java.io.ByteArrayOutputStream;


public class OpenNlpSplitter extends AbstractUimaService {

    @Override
    public String execute(Container json) throws UimaServiceException {
        String txt = json.getText();



        return null;
    }
}
