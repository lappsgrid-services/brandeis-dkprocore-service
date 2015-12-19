package edu.brandeis.cs.uima.dkpro;

import edu.brandeis.cs.json.JsonJsonUtil;
import groovy.json.JsonBuilder;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.util.XmlSlurper;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.regex.Pattern;

/**
 * OpenNlpSplitter Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Dec 19, 2015</pre>
 */
public class OpenNlpSplitterTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: execute(Container json)
     */
    @Test
    public void testExecute() throws Exception {

        String templateDsl = FileUtils.readFileToString(new File(OpenNlpSplitterTest.class.getResource("/template.dsl").toURI()), "UTF-8");
        Binding binding = new Binding();
        GroovyShell shell = new GroovyShell(binding);
        XmlSlurper parser = new XmlSlurper();
        Object xml = parser.parse(new File(OpenNlpSplitterTest.class.getResource("/dkpro_uima.xml").toURI()));
        JsonBuilder jb = new JsonBuilder();
        JsonJsonUtil util = new JsonJsonUtil();
        System.out.println("------------------------------------");
        System.out.println(xml);
        System.out.println("------------------------------------");
        binding.setVariable(REF_XML_SOURCE, xml);
        binding.setVariable(REF_JSON_BUILDER, jb);
        binding.setVariable(REF_JSONJSON_UTIL, util);
        String script = String.format("%s.call(\n %s \n)", REF_JSON_BUILDER, filterJson(templateDsl));
        shell.evaluate(script);
        String js = jb.toPrettyString();
        System.out.println(js);

    }

    protected static String replacingWithSpace(String s, String keyword, String match) {
        String pattern = String.format("\\.%s\\s*\\{", keyword);
        String replacement = String.format("\\.%s\\{", match);
        return s.replaceAll(pattern, replacement);
    }

    protected static String replacing(String s, String keyword, String match) {
        return s.replaceAll(Pattern.quote(keyword), match);
    }

    private static String filterJson(String dsl) {
        dsl = dsl.trim();
        if (!dsl.startsWith("{")) {
            dsl = String.format("{ %s }", dsl);
        }
        // enable keywords foreach and select
        // replace .foreach { by .collect{
        dsl = replacingWithSpace(dsl, KEYWORD_FOREACH, KEYWORD_FOREACH_MATCH);
        // replace .select { by .findAll {
        dsl = replacingWithSpace(dsl, KEYWORD_SELECT, KEYWORD_SELECT_MATCH);

        // replace global json
        dsl = replacing(dsl, KEYWORD_GLOBAL, KEYWORD_GLOBAL_MATCH);
        // replace local json
        dsl = replacing(dsl, KEYWORD_LOCAL, KEYWORD_LOCAL_MATCH);
        // replace util
        dsl = replacing(dsl, KEYWORD_UTIL, KEYWORD_UTIL_MATCH);
        return dsl;
    }

    public static final String REF_XML_SOURCE = "__source_xml__";
    public static final String REF_JSON_BUILDER = "__json_builder__";
    public static final String REF_JSONJSON_UTIL = "__jsonjson_util__";

    public static final String KEYWORD_GLOBAL = "&:";
    public static final String KEYWORD_GLOBAL_MATCH = REF_XML_SOURCE + ".";
    public static final String KEYWORD_LOCAL = "&.";
    public static final String KEYWORD_LOCAL_MATCH = "it.";
    public static final String KEYWORD_FOREACH  = "foreach";
    public static final String KEYWORD_FOREACH_MATCH  = "collect";
    public static final String KEYWORD_SELECT = "select";
    public static final String KEYWORD_SELECT_MATCH = "findAll";

    public static final String KEYWORD_UTIL = "%.";
    public static final String KEYWORD_UTIL_MATCH = REF_JSONJSON_UTIL + ".";

} 
