package edu.brandeis.cs.uima.dkpro;

import edu.brandeis.cs.uima.dkpro.opennlp.OpenNlpPOSTagger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** 
* OpenNlpPOSTagger Tester. 
* 
* @author <Authors name> 
* @since <pre>ʮ���� 21, 2015</pre> 
* @version 1.0 
*/ 
public class OpenNlpPOSTaggerTest { 

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
    AbstractWebService ws = new OpenNlpPOSTagger();
    String res = ws.execute("How are you today? Fine thank you.");
    System.out.println(res);
} 


} 
