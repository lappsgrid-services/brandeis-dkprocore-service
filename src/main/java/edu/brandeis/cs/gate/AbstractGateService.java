package edu.brandeis.cs.gate;

import edu.brandeis.cs.uima.AbstractWebService;
import edu.brandeis.cs.uima.UimaServiceException;
import gate.*;
import gate.util.Out;
import gate.util.persistence.PersistenceManager;
import org.apache.commons.io.FileUtils;
import org.apache.xerces.impl.io.UTF8Reader;
import org.lappsgrid.api.WebService;
import org.lappsgrid.metadata.ServiceMetadata;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.Serializer;
import org.lappsgrid.serialization.lif.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import static org.lappsgrid.discriminator.Discriminators.Uri;


public abstract class AbstractGateService extends AbstractWebService {

    public static void gateInit() throws Exception {
        Out.prln("Initialising GATE...");
        File gatedir = FileUtils.toFile(AbstractGateService.class.getResource("/gate/"));
        System.setProperty("gate.site.config", new File(gatedir,"gate.xml").getPath());
        System.setProperty("gate.plugins.home", new File(gatedir, "plugins").getPath());
        Gate.init();
        Out.prln("...GATE initialised");
    }

    public static CorpusController gateLoadAnnie() throws Exception {
        // initialise ANNIE (this may take several minutes)
        Out.prln("Initialising ANNIE...");
        // load the ANNIE application from the saved state in plugins/ANNIE
        File pluginsHome = Gate.getPluginsHome();
        File anniePlugin = new File(pluginsHome, "ANNIE");
        File annieGapp = new File(anniePlugin, "ANNIE_with_defaults.gapp");
        CorpusController annieController =
                (CorpusController) PersistenceManager.loadObjectFromFile(annieGapp);
        Out.prln("...ANNIE loaded");
        return annieController;
    }

    public static CorpusController gateLoadOpenNLP() throws Exception {
        // initialise ANNIE (this may take several minutes)
        Out.prln("Initialising OpenNLP...");
        // load the ANNIE application from the saved state in plugins/ANNIE
        File pluginsHome = Gate.getPluginsHome();
        File opennlpPlugin = new File(pluginsHome, "OpenNLP");
        File opennlpResources = new File(opennlpPlugin, "resources");
        File opennlpGapp = new File(opennlpResources, "opennlp.gapp");
        CorpusController annieController =
                (CorpusController) PersistenceManager.loadObjectFromFile(opennlpGapp);
        Out.prln("...OpenNLP loaded");
        return annieController;
    }

    public static String gateExecute(CorpusController annieController, String txt) throws Exception {
        // create a GATE corpus and add a document for each command-line
        // argument
        Corpus corpus = Factory.newCorpus("Prepare corpus ...");
        Document document = Factory.newDocument(txt);
        corpus.add(document);
        // tell the pipeline about the corpus and run it
        annieController.setCorpus(corpus);
        Out.prln("Running GATE Controller...");
        annieController.execute();
        Out.prln("...GATE Controller complete");
        // for each document, get an XML document with the
        // person and location names added
        Iterator iter = corpus.iterator();
        while(iter.hasNext()) {
            Document doc = (Document) iter.next();
            Out.prln("<------------------");
            Out.prln(doc.toXml());
            Out.prln("------------------>");
            return doc.toXml();
        }
        return null;
    }
}
