/**
 * 
 */
package edu.brandeis.cs.uima;

/**
 * Define the Exception for Web Service Wrapping for Stanford NLP Tools
 * 
 * @author shicq@cs.brandeis.edu
 *
 */
public class UimaServiceException extends Exception {

	private static final long serialVersionUID = 7756634769548152198L;

	public UimaServiceException() {
		super("Stanford-Web-Service-Exception:");
	}
	
	public UimaServiceException(String message) {
		super("Stanford-Web-Service-Exception:" + message);
	}
	
	public UimaServiceException(String message, Throwable cause) {
        super("Stanford-Web-Service-Exception:" + message, cause);
    }
	
}
