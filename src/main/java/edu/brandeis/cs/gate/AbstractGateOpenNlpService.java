package edu.brandeis.cs.gate;

import gate.CorpusController;

/**
 * Created by shi on 12/20/15.
 */
public abstract class AbstractGateOpenNlpService extends AbstractGateService {
    static CorpusController controller;

    static {
        try {
            gateInit();
            controller = gateLoadOpenNLP();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static String getXML(String txt) throws Exception {
        return gateExecute(controller, txt);
    }
}
