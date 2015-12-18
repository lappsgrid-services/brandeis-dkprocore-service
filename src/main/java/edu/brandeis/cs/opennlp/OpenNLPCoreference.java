package edu.brandeis.cs.opennlp;


import com.cedarsoftware.util.io.JsonWriter;
import opennlp.tools.coref.DefaultLinker;
import opennlp.tools.coref.DiscourseEntity;
import opennlp.tools.coref.Linker;
import opennlp.tools.coref.LinkerMode;
import opennlp.tools.coref.mention.DefaultParse;
import opennlp.tools.coref.mention.Mention;
import opennlp.tools.coref.mention.MentionContext;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.parser.*;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 *
 * $ export MAVEN_OPTS="-Xmx3072m -XX:MaxPermSize=128m"
 * $ mvn compile exec:java  -Dexec.mainClass="org.lapps.TestOpenNLPCoreference"
 */
public class OpenNLPCoreference {

//    public static Parser _parser = null;
//
//    public static Parse parseSentence(final String text) {
//        final Parse p = new Parse(text,
//                // a new span covering the entire text
//                new Span(0, text.length()),
//                // the label for the top if an incomplete node
//                AbstractBottomUpParser.INC_NODE,
//                // the probability of this parse...uhhh...?
//                1,
//                // the token index of the head of this parse
//                0);
//
//        // make sure to initialize the _tokenizer correctly
//        final Span[] spans = _tokenizer.tokenizePos(text);
//
//        for (int idx=0; idx < spans.length; idx++) {
//            final Span span = spans[idx];
//            // flesh out the parse with individual token sub-parses
//            p.insert(new Parse(text,
//                    span,
//                    AbstractBottomUpParser.TOK_NODE,
//                    0,
//                    idx));
//        }
//
//        Parse actualParse = parse(p);
//        return actualParse;
//    }
//
//    public static Parse parse(final Parse p) {
//        // lazy initializer
//        if (_parser == null) {
//            InputStream modelIn = null;
//            try {
//                // Loading the parser model
//                modelIn = getClass().getResourceAsStream("/en-parser-chunker.bin");
//                final ParserModel parseModel = new ParserModel(modelIn);
//                modelIn.close();
//
//                _parser = ParserFactory.create(parseModel);
//            } catch (final IOException ioe) {
//                ioe.printStackTrace();
//            } finally {
//                if (modelIn != null) {
//                    try {
//                        modelIn.close();
//                    } catch (final IOException e) {} // oh well!
//                }
//            }
//        }
//        return _parser.parse(p);
//    }
//
//    public DiscourseEntity[] findEntityMentions(Linker _linker, final String[] sentences,
//                                                final String[][] tokens) {
//        // tokens should correspond to sentences
//        assert(sentences.length == tokens.length);
//
//        // list of document mentions
//        final List<Mention> document = new ArrayList<Mention>();
//
//        for (int i=0; i < sentences.length; i++) {
//            // generate the sentence parse tree
//            final Parse parse = parseSentence(sentences[i], tokens[i]);
//
//            final DefaultParse parseWrapper = new DefaultParse(parse, i);
//            final Mention[] extents = _linker.getMentionFinder().getMentions(parseWrapper);
//
//            //Note: taken from TreebankParser source...
//            for (int ei=0, en=extents.length; ei<en; ei++) {
//                // construct parses for mentions which don't have constituents
//                if (extents[ei].getParse() == null) {
//                    // not sure how to get head index, but it doesn't seem to be used at this point
//                    final Parse snp = new Parse(parse.getText(),
//                            extents[ei].getSpan(), "NML", 1.0, 0);
//                    parse.insert(snp);
//                    // setting a new Parse for the current extent
//                    extents[ei].setParse(new DefaultParse(snp, i));
//                }
//            }
//            document.addAll(Arrays.asList(extents));
//        }
//
//        if (!document.isEmpty()) {
//            return _linker.getEntities(document.toArray(new Mention[0]));
//        }
//        return new DiscourseEntity[0];
//    }

    public static void main(String [] args) throws URISyntaxException, IOException {



        //
        String wordnetPath = new File(new File(Object.class.getResource("/wordnet").toURI()),"3.1/dict").getAbsolutePath();
        System.out.println(wordnetPath);
        System.setProperty("WNSEARCHDIR",wordnetPath);

		String inputText = "Pierre Vinken, 61 years old, will join the board as a nonexecutive director Nov. 29. Mr. Vinken is chairman of Elsevier N.V., " +
					"the Dutch publishing group. Rudolph Agnew, 55 years old and former chairman of Consolidated Gold Fields PLC, was named a director of this British industrial conglomerate.";

//        String inputText = "Some of our colleagues are going to be supportive. These kinds of people will earn our gratitude. ";

        // create model from resources
        final SentenceDetectorME sentDetector = createSentenceModel();
        final TokenizerME tokenizer = createTokenFinder();
        final NameFinderME personFinder = createPersonFinder();
        final Parser corefParser = createParserModel();
        final Linker linker = createCoRefLinker();


        Span[] sentSpans = sentDetector.sentPosDetect(inputText);

        System.out.println("Length = " + sentSpans.length);

        List<Parse> parses = new ArrayList<Parse>();
        List<Mention> document = new ArrayList<Mention>();

        for(int sentNum = 0 ; sentNum < sentSpans.length ; sentNum++) {
            String sentenceText = inputText.substring(sentSpans[sentNum].getStart() , sentSpans[sentNum].getEnd());
            int sentStart = sentSpans[sentNum].getStart();

            Span[] sentTokens = tokenizer.tokenizePos(sentenceText);


            // create a parse tree for the sentence, based on the tokens
            Parse sentParse = createNewSentenceParse(sentenceText, sentStart, sentTokens);


            // chunker parse the blank sentenceParse
            sentParse = corefParser.parse(sentParse);


            // try TreebankNameFinder method hack... needs to be after the POS tagger :)
            sentParse = TreebankNameFinder.addNERsToParse(personFinder, "person", sentParse);

            System.out.println("\nSentence#" + (sentNum+1) + " parse after POS & NER tag:");
            sentParse.show();


            // add to list of parses
            parses.add(sentParse);


            // now wrap the parsed sentence result in a DefaultParse object, so it can be used in coref
            DefaultParse sentParseInd = new DefaultParse(sentParse, sentNum);


            // get all mentions in the parsed sentence
            Mention[] extents = linker.getMentionFinder().getMentions(sentParseInd);


            // Copy & paste from TreebankLinker source code.. edited for var name changes
            //construct new parses for mentions which don't have constituents.
            for (int ei=0,en=extents.length;ei<en;ei++) {
                if (extents[ei].getParse() == null) {
                    //not sure how to get head index, but its not used at this point.
                    Parse snp = new Parse(sentParse.getText(),extents[ei].getSpan(),"NML",1.0,0);
                    sentParse.insert(snp);
                    extents[ei].setParse(new DefaultParse(snp, sentNum));
                }
            }

            document.addAll(Arrays.asList(extents));


            if (document.size() > 0) {
                // this was for treebank linker, but I'm using DefaultLinker....

                DiscourseEntity[] entities = linker.getEntities(document.toArray(new Mention[document.size()]));

                System.out.println("\nNow displaying all discourse entities::");
                for(DiscourseEntity ent : entities) {

                    Iterator<MentionContext> entMentions = ent.getMentions();

                    String json = JsonWriter.objectToJson(ent);
                    System.out.println("JSON = " + json);

                    String mentionString = "";

                    while(entMentions.hasNext()) {

                        Mention men = entMentions.next();


                        if(mentionString.equals("")) {
                            mentionString = men.toString();
                        } else {
                            mentionString = mentionString + " :: " + men.toString();
                        }


                    }

                    System.out.println("\tMention set:: [ " + mentionString + " ]");
                }

                System.out.println("\n\nNow printing out the named entities from mention sets::");

                for(DiscourseEntity ent : entities) {

                    Iterator<MentionContext> entMentions = ent.getMentions();

                    while(entMentions.hasNext()) {

                        Mention men = entMentions.next();
                        if(men.getNameType() != null) {
                            System.out.println("\t[" + men.toString() + "]");
                        }

                    }
                }
            }
        }




//	    File dir = new File(TestCoreference.class.getResource("/coref").toURI());
//
//        System.out.println(dir);
//
//        Linker _linker = null;
//        try {
//            // coreference resolution linker
//            _linker = new DefaultLinker(
//                    // LinkerMode should be TEST
//                    //Note: I tried LinkerMode.EVAL for a long time
//                    // before realizing that this was the problem
//                    dir.getAbsolutePath(), LinkerMode.TEST);
//        } catch (final IOException ioe) {
//            ioe.printStackTrace();
//        }
//
//
//
//        System.out.println("Hello World");
    }


    public static Parse createNewSentenceParse(final String sentenceText, int sentenceStart, final Span[] sentenceTokens) {

        Parse sentParse = new Parse(sentenceText, new Span(0, sentenceText.length()), AbstractBottomUpParser.INC_NODE, 1, 0);

        for (int tokenIndex = 0; tokenIndex < sentenceTokens.length; tokenIndex++) {

            int tokenStart = sentenceTokens[tokenIndex].getStart();
            int tokenEnd = sentenceTokens[tokenIndex].getEnd();

            // flesh out the parse with token sub-parses
            sentParse.insert(new Parse(sentenceText, new Span(tokenStart, tokenEnd),
                    AbstractBottomUpParser.TOK_NODE, 1, tokenIndex));
        }

        return sentParse;
    }


    public static SentenceDetectorME createSentenceModel() {
        try {
            URL sentenceModelUrl = Object.class.getResource("/en-sent.bin");
            final SentenceModel sentenceModel = new SentenceModel(sentenceModelUrl.openStream());
            final SentenceDetectorME sentDetector = new SentenceDetectorME(sentenceModel);
            return sentDetector;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static TokenizerME createTokenFinder() {
        try {
            URL tokenModelUrl = Object.class.getResource("/en-token.bin");
            final TokenizerModel tokenModel = new TokenizerModel(tokenModelUrl.openStream());
            final TokenizerME tokenDetector = new TokenizerME(tokenModel);
            return tokenDetector;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static TokenNameFinderModel createPersonModel() {
        try {

            URL personModelUrl = Object.class.getResource("/en-ner-person.bin");

            final TokenNameFinderModel personModel = new TokenNameFinderModel(personModelUrl.openStream());

            return personModel;

        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static NameFinderME createPersonFinder() {
        try {

            final TokenNameFinderModel personModel = createPersonModel();

            return new NameFinderME(personModel);

        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static Parser createParserModel() {
        try {

            URL parseModelUrl = Object.class.getResource("/en-parser-chunking.bin");
            final ParserModel parseModel = new ParserModel(parseModelUrl.openStream());

            final Parser parser = ParserFactory.create(parseModel);

            return parser;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Linker createCoRefLinker() {
        try {

            URL coRefModelUrl = Object.class.getResource("/coref");
            Linker linker = new DefaultLinker(coRefModelUrl.getPath(), LinkerMode.TEST);
            return linker;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
 
