package edu.brandeis.cs.json;


import groovy.json.JsonBuilder;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.util.XmlSlurper;
import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class XmlToJson {


//    public static String transform(String xmlStr, File templateDslFile) throws ParserConfigurationException, SAXException, IOException {
//
//        String templateDsl = FileUtils.readFileToString(new File(OpenNlpSplitterTest.class.getResource("/template.dsl").toURI()), "UTF-8");
//
//
//    }

    public static String transform(String xmlStr, String templateDsl) throws ParserConfigurationException, SAXException, IOException {
        Binding binding = new Binding();
        GroovyShell shell = new GroovyShell(binding);
        XmlSlurper parser = new XmlSlurper();
        JsonBuilder jb = new JsonBuilder();
        JsonJsonUtil util = new JsonJsonUtil();
        Object xml = parser.parseText(xmlStr);
        System.out.println(xml);
        binding.setVariable(REF_XML_SOURCE, xml);
        binding.setVariable(REF_JSON_BUILDER, jb);
        binding.setVariable(REF_JSONJSON_UTIL, util);
        String script = String.format("%s.call(\n %s \n)", REF_JSON_BUILDER, filterJson(templateDsl));
        shell.evaluate(script);
        String js = jb.toPrettyString();
        System.out.println(js);
        return js;
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
        dsl = replacingWithSpace(dsl, KEYWORD_FOREACH, KEYWORD_FOREACH_MATCH);
        dsl = replacingWithSpace(dsl, KEYWORD_SELECT, KEYWORD_SELECT_MATCH);
        dsl = replacing(dsl, KEYWORD_GLOBAL, KEYWORD_GLOBAL_MATCH);
        dsl = replacing(dsl, KEYWORD_LOCAL, KEYWORD_LOCAL_MATCH);
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
