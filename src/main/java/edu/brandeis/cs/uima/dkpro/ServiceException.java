/**
 * 
 */
package edu.brandeis.cs.uima.dkpro;

/**
 * Define the Exception for Web Service Wrapping for Stanford NLP Tools
 * 
 * @author shicq@cs.brandeis.edu
 *
 */
public class ServiceException extends Exception {

	private static final long serialVersionUID = 7756634769548152198L;

	public ServiceException() {
		super("Stanford-Web-Service-Exception:");
	}
	
	public ServiceException(String message) {
		super("Stanford-Web-Service-Exception:" + message);
	}
	
	public ServiceException(String message, Throwable cause) {
        super("Stanford-Web-Service-Exception:" + message, cause);
    }
	
}
