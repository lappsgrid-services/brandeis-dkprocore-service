package edu.brandeis.cs.opennlp;


import java.io.File;

public class CoRefExecute {

	public static void main(String[] args) {
		try {
            //
            String wordnetPath = new File(new File(CoRefExecute.class.getResource("/wordnet").toURI()),"3.1/dict").getAbsolutePath();
            System.setProperty("WNSEARCHDIR",wordnetPath );

			CoRefProcessor processor = new CoRefProcessor();
			
//			String storyText = "Pierre Vinken, 61 years old, will join the board as a nonexecutive director Nov. 29. Mr. Vinken is chairman of Elsevier N.V., " +
//					"the Dutch publishing group. Rudolph Agnew, 55 years old and former chairman of Consolidated Gold Fields PLC, was named a director of this British industrial conglomerate.";

//            String storyText = "Carol told Bob to attend the party. They arrived together.";
//            String storyText = "The project leader is refusing to help. The jerk thinks only of himself. ";

            String storyText = "Mike, Smith is a good person and he is from Boston.";
			System.out.println("Input sentences::\n" + storyText);
			
			processor.processContent(storyText);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
