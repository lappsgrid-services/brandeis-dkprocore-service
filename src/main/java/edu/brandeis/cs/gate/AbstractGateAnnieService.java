package edu.brandeis.cs.gate;

import gate.CorpusController;

/**
 * Created by shi on 12/20/15.
 */
public abstract class AbstractGateAnnieService extends AbstractGateService {
    static CorpusController controller;

    static {
        try {
            gateInit();
            controller = gateLoadAnnie();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    static String getXML(String txt) throws Exception {
        return gateExecute(controller, txt);
    }
}
