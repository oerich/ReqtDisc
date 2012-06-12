package org.computer.knauss.reqtDiscussion.io.jazz.rest;

import org.apache.http.HttpResponse;

public interface IWebConnector {

	public abstract HttpResponse performHTTPSRequestXML(String requestURL)
			throws Exception;

}