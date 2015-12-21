package edu.brandeis.cs.uima.dkpro;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** 
* StanfordNlpTokenizer Tester. 
* 
* @author <Authors name> 
* @since <pre>ʮ���� 21, 2015</pre> 
* @version 1.0 
*/ 
public class StanfordNlpTokenizerTest { 

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
    AbstractDkProService ws = new StanfordNlpTokenizer();
    String res = ws.execute("How are you today? Fine thank you.");
    System.out.println(res);
} 


} 
