package edu.brandeis.cs.uima.dkpro;

import edu.brandeis.cs.uima.AbstractWebService;
import org.apache.uima.analysis_component.AnalysisComponent;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

/**
 * Created by shi on 12/20/15.
 */
public abstract class AbstractDkProService  extends AbstractWebService {


    public static AnalysisEngine uimaDkProInit(Class<? extends AnalysisComponent> ... componentClasses) throws Exception {
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

}
