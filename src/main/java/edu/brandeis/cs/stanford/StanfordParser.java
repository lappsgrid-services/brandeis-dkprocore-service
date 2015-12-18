package edu.brandeis.cs.stanford;

import com.cedarsoftware.util.io.JsonWriter;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by lapps on 10/3/2014.
 * $ export MAVEN_OPTS="-Xmx3072m -XX:MaxPermSize=128m"
 * $ mvn compile exec:java  -Dexec.mainClass="org.lapps.TestStanfordParser"
 */
public class StanfordParser {

    public static void main(String [] args) throws IOException {
        // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution
        Properties props = new Properties();
//        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse");
        props.put("annotators", "tokenize, ssplit, pos, parse");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // read some text in the text variable
//        String text = "My dog also likes eating sausage.";   // Add your text here!
        String text = "The sky is blue.";
        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);

        // run all Annotators on this text
        pipeline.annotate(document);

        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        PrintWriter out = new PrintWriter(System.out);

        HashMap<String, Object> fieldsMap = new HashMap<String, Object>();
        Map<Class, List<String>> fields = new HashMap<Class, List<String>>();
        fieldsMap.put(JsonWriter.FIELD_SPECIFIERS, fields);

        for(CoreMap sentence: sentences) {
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // this is the text of the token
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                // this is the POS tag of the token
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                // this is the NER label of the token
                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
            }

            // this is the parse tree of the current sentence
            Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);

            out.println();
            out.println("The first sentence parsed is:");
            tree.pennPrint(out);
            String jsontree = JsonWriter.objectToJson(tree.getChildrenAsList());

//            ObjectMapper mapper = new ObjectMapper();
//            mapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
//            String jsontree = mapper.writeValueAsString(tree);

            System.out.println();
            System.out.println("JSON (tree) = " + jsontree);

//            // this is the Stanford dependency graph of the current sentences
//            SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
        }
//
//        // This is the coreference link graph
//        // Each chain stores a set of mentions that link to each other,
//        // along with a method for getting the most representative mention
//        // Both sentence and token offsets start at 1!
//        Map<Integer, CorefChain> graph =
//                document.get(CorefChainAnnotation.class);
    }
}
