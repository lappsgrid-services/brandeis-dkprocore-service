package edu.brandeis.cs.opennlp;

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

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CoRefProcessor {

	public void processContent(String input) throws Exception{

		// get modelling resources
		final SentenceDetectorME sentDetector = this.createSentenceModel();
		final TokenizerME tokenizer = this.createTokenFinder();
		final NameFinderME personFinder = this.createPersonFinder();
		final Parser corefParser = this.createParserModel();
        final Linker linker = this.createCoRefLinker();


		// get sentences
		Span[] sentSpans = sentDetector.sentPosDetect(input);

		List<Mention> document = new ArrayList<Mention>();
		List<Parse> parses = new ArrayList<Parse>();


		for(int sentNum = 0 ; sentNum < sentSpans.length ; sentNum++) {

			String sentenceText = input.substring(sentSpans[sentNum].getStart() , sentSpans[sentNum].getEnd());
			int sentStart = sentSpans[sentNum].getStart();

			Span[] sentTokens = tokenizer.tokenizePos(sentenceText);

//            ParserTool.parseLine(sentence, parser, 1);
			// create a parse tree for the sentence, based on the tokens
			Parse sentParse = this.createNewSentenceParse(sentenceText, sentStart, sentTokens);
			
			
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
		}


		if (document.size() > 0) {
			// this was for treebank linker, but I'm using DefaultLinker....

			DiscourseEntity[] entities = linker.getEntities(document.toArray(new Mention[document.size()]));

			System.out.println("\nNow displaying all discourse entities::");
			for(DiscourseEntity ent : entities) {

				Iterator<MentionContext> entMentions = ent.getMentions();
				
				
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




	private Parse createNewSentenceParse(final String sentenceText, int sentenceStart, final Span[] sentenceTokens) {

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


	private SentenceDetectorME createSentenceModel() {
		try {
			URL sentenceModelUrl = getClass().getResource("/en-sent.bin");
			final SentenceModel sentenceModel = new SentenceModel(sentenceModelUrl.openStream());
			final SentenceDetectorME sentDetector = new SentenceDetectorME(sentenceModel);
			return sentDetector;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private TokenizerME createTokenFinder() {
		try {
			URL tokenModelUrl = this.getClass().getResource("/en-token.bin");
			final TokenizerModel tokenModel = new TokenizerModel(tokenModelUrl.openStream());
			final TokenizerME tokenDetector = new TokenizerME(tokenModel);
			return tokenDetector;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private TokenNameFinderModel createPersonModel() {
		try {

			URL personModelUrl = this.getClass().getResource("/en-ner-person.bin");

			final TokenNameFinderModel personModel = new TokenNameFinderModel(personModelUrl.openStream());

			return personModel;

		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private NameFinderME createPersonFinder() {
		try {

			final TokenNameFinderModel personModel = this.createPersonModel();

			return new NameFinderME(personModel);

		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	private Parser createParserModel() {
		try {

			URL parseModelUrl = this.getClass().getResource("/en-parser-chunking.bin");
			final ParserModel parseModel = new ParserModel(parseModelUrl.openStream());
			
			final Parser parser = ParserFactory.create(parseModel);

			return parser;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private Linker createCoRefLinker() {
		try {

			URL coRefModelUrl = this.getClass().getResource("/coref");
			Linker linker = new DefaultLinker(coRefModelUrl.getPath(), LinkerMode.TEST);
			return linker;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}



}
