package edu.brandeis.cs.service;

/**
 * Created by lapps on 4/1/2015.
 */
public interface IPipelineAnnotation {
    public String getXML(String doc) throws Exception;
    public String getJSON(String doc) throws Exception;
}
