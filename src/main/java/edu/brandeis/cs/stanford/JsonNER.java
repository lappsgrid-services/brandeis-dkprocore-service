package edu.brandeis.cs.stanford;

import com.cedarsoftware.util.io.JsonWriter;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Created by lapps on 11/20/2014.
 */
public class JsonNER {

    // prepare the directory
    public static void main(String [] args) throws IOException  {
//        File dir = null;
//        java.util.Iterator<File> iter = FileUtils.iterateFiles(dir, new String[]{}, true);
//        while(iter.hasNext()) {
//            File fil = iter.next();
//            String text = readFile(fil);
//            opennlpNER(text);
//            stanfordNER(text);
//        }
        String text = "Stanford NER is a Java implementation of a Named Entity Recognizer. Named Entity Recognition (NER) labels sequences of words in a text which are the names of things, such as person and company names, or gene and protein names. It comes with well-engineered feature extractors for Named Entity Recognition, and many options for defining feature extractors. Included with the download are good named entity recognizers for English, particularly for the 3 classes (PERSON, ORGANIZATION, LOCATION), and we also make available on this page various other models for different languages and circumstances, including models trained on just the CoNLL 2003 English training data. The distributional similarity features in some models improve performance but the models require considerably more memory.";
        stanfordNER(text);
    }

    public static String readFile(File file) {
        try {
            // read file from string
            return FileUtils.readFileToString(file, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveFile(String text, File file) {
        try {
            // write string into file.
            FileUtils.writeStringToFile(file, text, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //
    public static String opennlpNER(String text){

        return null;
    }

    //
    public static String stanfordNER(String text) throws IOException {
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse");
        props.put("dcoref.score", true);
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);
        // run all Annotators on this text
        pipeline.annotate(document);
        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
//        PrintWriter out = new PrintWriter(System.out);
//        HashMap<String, Object> fieldsMap = new HashMap<String, Object>();
//        Map<Class, List<String>> fields = new HashMap<Class, List<String>>();
//        fieldsMap.put(JsonWriter.FIELD_SPECIFIERS, fields);
        for(CoreMap sentence: sentences) {
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
//                // this is the text of the token
//                String word = token.get(CoreAnnotations.TextAnnotation.class);
//                // this is the POS tag of the token
//                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
//                // this is the NER label of the token
//                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                String json = JsonWriter.objectToJson(token);
                System.out.println(json);
                // saveFile();
            }
        }
        return null;
    }
}
