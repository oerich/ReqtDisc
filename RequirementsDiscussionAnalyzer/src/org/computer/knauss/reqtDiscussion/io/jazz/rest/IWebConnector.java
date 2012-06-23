package org.computer.knauss.reqtDiscussion.io.jazz.rest;

import java.util.Properties;

import org.apache.http.HttpResponse;
import org.computer.knauss.reqtDiscussion.io.jazz.IJazzDAO;

public interface IWebConnector {

	public abstract HttpResponse performHTTPSRequestXML(String requestURL)
			throws Exception;
	
	/**
	 * @see IJazzDAO for required properties.
	 * @param properties
	 */
	public void configure(Properties properties);

}