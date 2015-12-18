package edu.brandeis.cs.paper;

//import org.apache.commons.io.FileUtils;

import edu.brandeis.cs.json2json.Json2Json;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import gate.CorpusController;
import org.apache.commons.io.FileUtils;
import org.apache.uima.analysis_engine.AnalysisEngine;

import java.io.File;
import java.util.Collection;

/**
 * Created by lapps on 3/27/2015.
 */
public class RunSpaceBank extends Compare {

    public static void main(String [] args) throws Exception{
        File root = new File("C:\\Users\\lapps\\Skydrive\\concept mapping\\ISO-Space\\SpaceBank");
        Collection<File> txtFiles = FileUtils.listFiles(root, new String[]{"txt"}, true);
        gateInit();
        CorpusController gateAnnie = gateLoadAnnie();
        CorpusController gateOpennlp = gateLoadOpenNLP();
        StanfordCoreNLP pipeline = stanfordnlpInit();
        AnalysisEngine aae = opennlpuimaInit();

        for(File fil: txtFiles) {

            try {
                // Gate Annie
                File xmlFile = new File(fil.getPath().replace(".txt", ".gateannie.xml"));
                String xml = gateExecute(gateAnnie, fil.getAbsolutePath());
                FileUtils.writeStringToFile(xmlFile, xml, "UTF-8");
                String xml2json = Json2Json.xml2json(xml);
                File jsonFile = new File(fil.getPath().replace(".txt", ".gateannie.xml.json"));
                FileUtils.writeStringToFile(jsonFile, xml2json.toString());

                // Gate Opennlp
                xmlFile = new File(fil.getPath().replace(".txt", ".gateopennlp.xml"));
                xml = gateExecute(gateOpennlp, fil.getAbsolutePath());
                FileUtils.writeStringToFile(xmlFile, xml,"UTF-8");
                xml2json = Json2Json.xml2json(xml);
                jsonFile = new File(fil.getPath().replace(".txt", ".gateopennlp.xml.json"));
                FileUtils.writeStringToFile(jsonFile, xml2json.toString());

                // stanfordnlp
                xmlFile = new File(fil.getPath().replace(".txt", ".stanfordnlp.xml"));
                xml = stanfordnlp(pipeline, fil.getAbsolutePath());
                FileUtils.writeStringToFile(xmlFile, xml, "UTF-8");
                xml2json = Json2Json.xml2json(xml);
                jsonFile = new File(fil.getPath().replace(".txt", ".stanfordnlp.xml.json"));
                FileUtils.writeStringToFile(jsonFile, xml2json.toString());

                // uimaopennlp
                xmlFile = new File(fil.getPath().replace(".txt", ".uimaopennlp.xml"));
                xml = opennlpuima(aae, fil.getAbsolutePath());
                FileUtils.writeStringToFile(xmlFile, xml, "UTF-8");
                xml2json = Json2Json.xml2json(xml);
                jsonFile = new File(fil.getPath().replace(".txt", ".uimaopennlp.xml.json"));
                FileUtils.writeStringToFile(jsonFile, xml2json.toString());


            }catch(Throwable th) {
                th.printStackTrace();
            }
        }

    }

}
