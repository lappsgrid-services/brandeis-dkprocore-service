package edu.brandeis.cs.uima.dkpro;

import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.*;
import edu.brandeis.cs.uima.AbstractUimaService;
import org.apache.uima.analysis_component.AnalysisComponent;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.CAS;
import org.apache.uima.resource.metadata.ResourceMetaData;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.XmlCasSerializer;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;


public abstract class AbstractDkProStanfordNlpService extends AbstractUimaService {

    public static CAS uimaDkProStanfordInitDoc(AnalysisEngine aae) throws  Exception {
        final CAS document = CasCreationUtils.createCas(asList(aae.getMetaData()));
        document.setDocumentLanguage("en");
        return document;
    }

    public static AnalysisEngine uimaDkProStanfordInit() throws Exception {
        AnalysisEngineDescription seg = createEngineDescription(StanfordSegmenter.class);
        AnalysisEngineDescription tagger = createEngineDescription(StanfordPosTagger.class);
        AnalysisEngineDescription parser = createEngineDescription(StanfordParser.class);
        AnalysisEngineDescription ner = createEngineDescription(StanfordNamedEntityRecognizer.class);
        AnalysisEngineDescription cor = createEngineDescription(StanfordCoreferenceResolver.class);
        AnalysisEngineDescription aaeDesc = createEngineDescription(
                seg, tagger, parser, ner, cor);
        AnalysisEngine aae = createEngine(aaeDesc);
        return aae;
    }


    public static AnalysisEngine uimaDkProStanfordInit(Class<? extends AnalysisComponent> ... componentClasses) throws Exception {
        List<AnalysisEngineDescription> aeds = new ArrayList<AnalysisEngineDescription>();
        for (Class<? extends AnalysisComponent> componentClass : componentClasses) {
            AnalysisEngineDescription aed = createEngineDescription(componentClass);
            aeds.add(aed);
        }

//        AnalysisEngineDescription seg = createEngineDescription(StanfordSegmenter.class);
//        AnalysisEngineDescription tagger = createEngineDescription(StanfordPosTagger.class);
//        AnalysisEngineDescription parser = createEngineDescription(StanfordParser.class);
//        AnalysisEngineDescription ner = createEngineDescription(StanfordNamedEntityRecognizer.class);
//        AnalysisEngineDescription cor = createEngineDescription(StanfordCoreferenceResolver.class);
        String[] names = new String[aeds.size()];
        int i = 0;
        for (AnalysisEngineDescription aed : aeds) {
            names[i] = aed.getImplementationName() + "-" + i;
            i++;
        }
        AnalysisEngineDescription aaeDesc = createEngineDescription(aeds, asList(names), null, null,
                null);
        AnalysisEngine aae = createEngine(aaeDesc);
        return aae;
    }


    public static String uimaDkProStanford(AnalysisEngine aae, String txt) throws Exception {
        CAS document = uimaDkProStanfordInitDoc(aae);
        document.setDocumentLanguage("en");
        document.setDocumentText(txt);
        aae.process(document);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        XmlCasSerializer.serialize(document, output);
        String xmlAnn = new String(output.toByteArray());
        document.release();
        return xmlAnn;
    }



    public static void main(String []args) throws Exception{
        AnalysisEngine aae = uimaDkProStanfordInit();
        String xml = uimaDkProStanford(aae, "How are you?");
        System.out.println(xml);

    }

}
