package edu.brandeis.cs.uima.dkpro;

import edu.brandeis.cs.uima.dkpro.stanford.StanfordNlpPOSTagger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** 
* StanfordNlpPOSTagger Tester. 
* 
* @author <Authors name> 
* @since <pre>ʮ���� 21, 2015</pre> 
* @version 1.0 
*/ 
public class StanfordNlpPOSTaggerTest { 

@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: execute(Container json) 
* 
*/ 
@Test
public void testExecute() throws Exception {
    AbstractDkProService ws = new StanfordNlpPOSTagger();
    String res = ws.execute("How are you today? Fine thank you.");
    System.out.println(res);
} 


} 
