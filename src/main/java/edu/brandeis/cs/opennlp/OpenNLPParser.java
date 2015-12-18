package edu.brandeis.cs.opennlp;

import com.cedarsoftware.util.io.JsonWriter;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;

import java.io.IOException;
import java.net.URL;

/**
 * Created by lapps on 10/3/2014.
 * $ export MAVEN_OPTS="-Xmx3072m -XX:MaxPermSize=128m"
 * $ mvn compile exec:java  -Dexec.mainClass="org.lapps.TestOpenNLPParser"
 */
public class OpenNLPParser {
    public static Parser createParserModel() {
        try {
            URL parseModelUrl = ParserModel.class.getResource("/en-parser-chunking.bin");
            System.out.println("parseModelUrl = " + parseModelUrl);
            final ParserModel parseModel = new ParserModel(parseModelUrl.openStream());
            final Parser parser = ParserFactory.create(parseModel);
            return parser;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String [] args) throws IOException {
        Parser parser = createParserModel();
        String sentence = "The sky is blue.";
        Parse topParses[] = ParserTool.parseLine(sentence, parser, 1);
        for (Parse p : topParses) {
            String jsonparse = JsonWriter.objectToJson(p);

//            ObjectMapper mapper = new ObjectMapper();
//            mapper.setVisibility(com.fasterxml.jackson.annotation.PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
//            String jsonparse = mapper.writeValueAsString(p);
//
            System.out.println();
            System.out.println("JSON (parse) = " + jsonparse);
            System.out.println();
            p.show();
            p.showCodeTree();
        }
    }

}
