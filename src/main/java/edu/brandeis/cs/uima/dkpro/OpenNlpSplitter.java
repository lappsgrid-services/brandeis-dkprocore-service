package edu.brandeis.cs.uima.dkpro;

import org.apache.uima.jcas.JCas;
import org.apache.uima.util.XmlCasSerializer;
import org.lappsgrid.serialization.lif.Container;

import java.io.ByteArrayOutputStream;

/**
 * Created by shi on 12/18/15.
 */
public class OpenNlpSplitter extends AbstractUimaService {

    @Override
    public String execute(Container json) throws UimaServiceException {
        String txt = json.getText();



        return null;
    }
}
