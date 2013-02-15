package org.computer.knauss.reqtDiscussion.io.jazz.rest;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.computer.knauss.reqtDiscussion.io.jazz.util.ui.DialogBasedJazzAccessConfiguration;

public class FormBasedAuthenticatedConnectorTest {

	//@Test
	public void test() throws Exception {
		// https://jazz.net/jazz/oslc/contexts/_1w8aQEmJEduIY7C8B09Hyw/workitems?oslc_cm.pageSize=10
		DialogBasedJazzAccessConfiguration config = new DialogBasedJazzAccessConfiguration();
		Properties properties = new Properties();
		properties.load(new FileInputStream("jazz-properties.txt"));
		config.configure(properties);
		IWebConnector fbac = new FormBasedAuthenticatedConnector(
				config);
		HttpResponse response = fbac
				.performHTTPSRequestXML("https://jazz.net/jazz/rootservices");
		System.out.println(response);
		// System.out.println(fbac.readResponse(response));

		JazzJDOMDAO domReader = new JazzJDOMDAO(fbac, new JazzAccessConfigurationDummy());
		domReader.readDocument(response.getEntity().getContent());
		// now that we have the rootservices, there are two paths to follow
		// 1. use SPARQL to query the first 10 stories according to
		// https://jazz.net/wiki/bin/view/Main/JFSIndexStoreQueryAPI

		// String queryResource = domReader.getChildAttribute("query",
		// "resource");
		// assertEquals("https://jazz.net/jazz/query", queryResource);
		// EntityUtils.consume(response.getEntity());
		//
		// URLCodec c = new URLCodec();
		//
		// String getQuery = queryResource
		// + "?query=PREFIX+dc"+c.encode(": <") +
		// "http://purl.org/dc/elements/1.1/"+c.encode(">")
		// + "+SELECT+?title+?description+?parent"
		// +
		// "+WHERE+%7B+$url+dc:title+?title+;+dc:description+?description+;+dc:isPartOf+?parent+.+%7D"
		// + "+LIMIT25";
		//
		// getQuery = queryResource +
		// "?query=SELECT+DISTINCT+$url+WHERE+"+c.encode("{")+"+$url+a+"+c.encode("<")+"http://jazz.net/xmlns/wi/defects#WorkItem"+c.encode("> }")+"+LIMIT25";
		//
		// System.out.println(getQuery);
		// response = fbac.performHTTPSRequestXML(getQuery);
		// System.out.println(fbac.readResponse(response));

		// 2. get the CM ServiceProvider according to
		// https://jazz.net/wiki/bin/view/Main/ResourceOrientedWorkItemAPIv2#Discovery
		String cmServiceProviderResource = domReader.getChildAttribute(
				"cmServiceProviders", "resource");
		assertEquals("https://jazz.net/jazz/oslc/workitems/catalog",
				cmServiceProviderResource);
		EntityUtils.consume(response.getEntity());

		response = fbac.performHTTPSRequestXML(cmServiceProviderResource);
		System.out.println("++++" + response);
		domReader.readDocument(response.getEntity().getContent());

		EntityUtils.consume(response.getEntity());
	}

}
