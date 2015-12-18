package edu.brandeis.cs.opennlp;

public class CorefParse {
//
//	private Map<Parse, Integer> parseMap;
//	private List<Parse> parses;
//
//	public CorefParse(List<Parse> parses, DiscourseEntity[] entities) {
//
//		this.parses = parses;
//
//		parseMap = new HashMap<Parse, Integer>();
//
//		for (int ei=0,en=entities.length;ei<en;ei++) {
//
//			if (entities[ei].getNumMentions() > 1) {
//
//				for (Iterator<MentionContext> mi = entities[ei].getMentions(); mi.hasNext();) {
//
//					MentionContext mc = mi.next();
//
//					Parse mentionParse = ((DefaultParse) mc.getParse()).getParse();
//
//					parseMap.put(mentionParse,ei+1);
//
//				}
//			}
//		}
//	}
//
//
//	public void show() {
//
//		for (int pi=0,pn=parses.size();pi<pn;pi++) {
//
//			Parse p = parses.get(pi);
//
//			this.show(p);
//
//			System.out.println();
//
//		}
//
//	}
//
//	private void show(Parse p) {
//
//		int start = p.getSpan().getStart();
//
//		if (!p.getType().equals(Parser.TOK_NODE)) {
//
//			System.out.print("(");
//
//			System.out.print(p.getType());
//
//			if (parseMap.containsKey(p)) {
//
//				System.out.print("#"+parseMap.get(p));
//			}
//
//			System.out.print(" ");
//		}
//
//		Parse[] children = p.getChildren();
//
//		for (int pi=0,pn=children.length;pi<pn;pi++) {
//
//			Parse c = children[pi];
//
//			Span s = c.getSpan();
//
//			if (start < s.getStart()) {
//
//				System.out.print(p.getText().substring(start, s.getStart()));
//			}
//
//			this.show(c);
//
//			start = s.getEnd();
//		}
//
//		System.out.print(p.getText().substring(start, p.getSpan().getEnd()));
//
//		if (!p.getType().equals(Parser.TOK_NODE)) {
//
//			System.out.print(")");
//
//		}
//	}
}