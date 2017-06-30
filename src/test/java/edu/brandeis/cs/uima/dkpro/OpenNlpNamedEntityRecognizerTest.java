package edu.brandeis.cs.uima.dkpro;

import edu.brandeis.cs.uima.dkpro.opennlp.OpenNlpNamedEntityRecognizer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** 
* OpenNlpNamedEntityRecognizer Tester. 
* 
* @author <Authors name> 
* @since <pre>ʮ���� 21, 2015</pre> 
* @version 1.0 
*/ 
public class OpenNlpNamedEntityRecognizerTest { 

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
    AbstractDkProService ws = new OpenNlpNamedEntityRecognizer();
    String res = ws.execute("How are you today, Mike? Fine, thank you.");
    System.out.println(res);
} 


} 
