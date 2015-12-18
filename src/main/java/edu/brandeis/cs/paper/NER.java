package edu.brandeis.cs.paper;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.XMLOutputter;
import edu.stanford.nlp.util.CoreMap;

import java.util.List;
import java.util.Properties;

/**
 * Created by lapps on 3/24/2015.
 */
public class NER {


    public static String stanford(String txt) throws Exception{
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation annotation = new Annotation(txt);
        pipeline.annotate(annotation);
        List<CoreMap> list = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        XMLOutputter.xmlPrint(annotation, System.out);
//        for (CoreMap sent : list) {
//            String json = JsonWriter.objectToJson(sent);
//            System.out.println(json);
//            for (CoreLabel token : sent.get(CoreAnnotations.TokensAnnotation.class)) {
//                json = JsonWriter.objectToJson(token);
//                System.out.println(json);
//                System.out.println(token.value());
//                System.out.println(token.beginPosition());
//                System.out.println(token.endPosition());
//                System.out.println(token.lemma());
//                System.out.println(token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
//            }
//        }
        return null;
    }

    public static void main(String [] args) throws  Exception{
        //
        stanford("How are you today, James? I am fine.");
    }
}
