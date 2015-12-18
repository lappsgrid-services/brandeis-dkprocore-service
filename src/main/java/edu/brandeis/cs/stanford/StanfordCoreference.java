package edu.brandeis.cs.stanford;

import com.cedarsoftware.util.io.JsonWriter;
import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by lapps on 10/2/2014.
 * $ export MAVEN_OPTS="-Xmx3072m -XX:MaxPermSize=128m"
 * $ mvn compile exec:java  -Dexec.mainClass="org.lapps.TestStanfordCoreference"
 *
 */
public class StanfordCoreference {
        public static void main(String[] args) throws IOException {
            Properties props = new Properties();
            props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
            props.put("dcoref.score", true);
            StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

//            String inputText = "Pierre Vinken, 61 years old, will join the board as a nonexecutive director Nov. 29. Mr. Vinken is chairman of Elsevier N.V., " +
//                    "the Dutch publishing group. Rudolph Agnew, 55 years old and former chairman of Consolidated Gold Fields PLC, was named a director of this British industrial conglomerate.";


//          String inputText = "The atom is a basic unit of matter, it consists of a dense central nucleus surrounded by a cloud of negatively charged electrons.";
            String inputText = "John and Mary went to the store. They bought some milk.";
            Annotation document = new Annotation(inputText);
            pipeline.annotate(document);
            Map<Integer, CorefChain> graph = document.get(CorefCoreAnnotations.CorefChainAnnotation.class);
            System.out.println();
            System.out.println(inputText);
            System.out.println();
            for(Integer id : graph.keySet()) {

                CorefChain c =   graph.get(id);
                System.out.println( "ClusterId: " + id);
                CorefChain.CorefMention cm = c.getRepresentativeMention();
                System.out.println( "Representative Mention: " + inputText.subSequence(cm.startIndex, cm.endIndex));
                System.out.println();
                List<CorefChain.CorefMention> cms = c.getMentionsInTextualOrder();

                String jsoncms = JsonWriter.objectToJson(cms);
                System.out.println();
                System.out.println("JSON (cms) = " + jsoncms);
                System.out.println();
                System.out.print("Mentions:  ")  ;
                for (CorefChain.CorefMention mention : cms) {
                    String jsonmention = JsonWriter.objectToJson(mention);
                    System.out.println();
                    System.out.println("JSON (mention) = " + jsonmention);
                    System.out.println();
                    System.out.println(inputText.subSequence(mention.startIndex, mention.endIndex) + "|") ;
                }
                System.out.println("");
            }
        }
}
