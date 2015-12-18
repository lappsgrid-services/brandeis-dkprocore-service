package edu.brandeis.cs.paper;

import edu.brandeis.cs.json2json.Json2Json;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.XMLOutputter;
import edu.stanford.nlp.util.CoreMap;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * Created by lapps on 12/12/2014.
 */
public class Tagger {

    protected static POSTaggerME loadPOSTagger() throws Exception {
        POSTaggerME postagger;
        InputStream stream = Tagger.class.getResourceAsStream("/en-pos-maxent.bin");
        POSModel model = new POSModel(stream);
        postagger = new POSTaggerME(model);
        stream.close();
        return postagger;
    }

    public static String opennlp(String txt)throws Exception{
        POSTaggerME postagger = loadPOSTagger();
        String tags[] = postagger.tag(new String[]{txt});
        return null;
    }

    public static String stanford(String txt) throws Exception{
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation annotation = new Annotation(txt);
        pipeline.annotate(annotation);
        List<CoreMap> list = annotation.get(CoreAnnotations.SentencesAnnotation.class);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        XMLOutputter.xmlPrint(annotation, output);
        String xmlAnn = new String(output.toByteArray());
        System.out.println(xmlAnn);
        String xml2json = Json2Json.xml2json(xmlAnn);
        System.out.println(xml2json);

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
        stanford("How are you today? I am fine.");
    }
}
