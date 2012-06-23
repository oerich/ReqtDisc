package org.computer.knauss.reqtDiscussion.io.jazz.rest;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.util.Properties;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.message.BasicHttpResponse;
import org.computer.knauss.reqtDiscussion.io.jazz.IJazzDAO;
import org.computer.knauss.reqtDiscussion.io.sql.DAOException;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.jdom2.JDOMException;
import org.junit.Before;
import org.junit.Test;

public class JazzJDOMDAOTest {

	private IJazzDAO dao;

	@Before
	public void setup() throws IOException {
		this.dao = new JazzJDOMDAO(new ConnectorProbe());
	}

	@Test
	public void testLimit() {
		assertEquals(10, this.dao.getLimit());

		this.dao.setLimit(25);
		assertEquals(25, this.dao.getLimit());

		this.dao.setLimit(-5);
		assertEquals(0, this.dao.getLimit());
	}

	@Test
	public void testGetProjectAreas() throws Exception {
		String[] projectAreas = this.dao.getProjectAreas();
		assertEquals(5, projectAreas.length);

		assertEquals("Jazz Collaborative ALM", projectAreas[0]);
		assertEquals("Rational Team Concert", projectAreas[1]);
		assertEquals("Jazz Foundation", projectAreas[2]);
		assertEquals("Testing Purposes Only - CLM 2012  (Change Management)",
				projectAreas[3]);
		assertEquals("Rational AMC", projectAreas[4]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSelectWorkitemsNoProjectArea() throws JDOMException,
			IOException, Exception {
		this.dao.getWorkitemsForType("any", false);
	}

	@Test
	public void testSimpleQuery() throws JDOMException, IOException, Exception {
		this.dao.setProjectArea("Rational Team Concert");

		String[] results = this.dao.getWorkitemsForType("any", false);
		assertEquals(50, results.length);

		// System.out.println(results[0]);

		assertEquals("Not a correct xml fragment?", "<ChangeRequest resource=",
				results[0].substring(0, 24));
	}

	@Test
	public void testGetDiscussionEventsForDiscussion() throws DAOException {
		this.dao.setProjectArea("Rational Team Concert");

		DiscussionEvent[] des = this.dao
				.getDiscussionEventsOfDiscussion(117709);
		assertEquals(4, des.length);

		assertEquals(117709, des[0].getDiscussionID());
		assertEquals("https://jazz.net/jts/users/jimtykal", des[0].getCreator());
		assertEquals(new Date(1277806629252l), des[0].getCreationDate());
		assertEquals(
				"This is an important component of the sample application for Rational User Education. We will want to have lab exercises directed at .NET as well as Eclipse developers.",
				des[0].getContent());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testStoreDiscussionEvent() throws DAOException {
		this.dao.storeDiscussionEvent(new DiscussionEvent());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testStoreDiscussionEvents() throws DAOException {
		this.dao.storeDiscussionEvents(new DiscussionEvent[] { new DiscussionEvent() });
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetSpecificDiscussionEvents() throws DAOException {
		this.dao.getDiscussionEvent(123);
	}

	private class ConnectorProbe implements IWebConnector {

		HttpResponseDummy response = new HttpResponseDummy();

		@Override
		public HttpResponse performHTTPSRequestXML(String requestURL)
				throws Exception {

			if (requestURL.endsWith("rootservices"))
				this.response.entity.stream = new FileInputStream(
						"testfiles/jazz.xml/rootservices.xml");
			else if (requestURL.endsWith("catalog"))
				this.response.entity.stream = new FileInputStream(
						"testfiles/jazz.xml/catalog.xml");
			else if (requestURL.endsWith("services.xml"))
				this.response.entity.stream = new FileInputStream(
						"testfiles/jazz.xml/services.xml");
			else if (requestURL.endsWith("workitems")
					|| requestURL.indexOf("query") > 0) {
				// if this file is missing, store the results of the query under
				// its name.
				this.response.entity.stream = new FileInputStream(
						"testfiles/jazz.xml/50-stories.xml");
			} else if (requestURL.endsWith("comments")) {
				// if this file is missing, store the comments for the first
				// story in '50-stories.xml' into that file.
				this.response.entity.stream = new FileInputStream(
						"testfiles/jazz.xml/117709-comments.xml");
			}
			return response;
		}

		@Override
		public void configure(Properties properties) {
			
		}

	}

	private class HttpResponseDummy extends BasicHttpResponse {

		HttpEntityDummy entity = new HttpEntityDummy();

		public HttpResponseDummy() {
			super(new StatusLine() {

				@Override
				public int getStatusCode() {
					return 0;
				}

				@Override
				public String getReasonPhrase() {
					return null;
				}

				@Override
				public ProtocolVersion getProtocolVersion() {
					return null;
				}
			});
		}

		@Override
		public HttpEntity getEntity() {
			return entity;
		}

		@Override
		public void setEntity(HttpEntity entity) {
			this.entity = (HttpEntityDummy) entity;
		}

	}

	private class HttpEntityDummy implements HttpEntity {

		InputStream stream;

		@Override
		public void consumeContent() throws IOException {

		}

		@Override
		public InputStream getContent() throws IOException,
				IllegalStateException {
			return stream;
		}

		@Override
		public Header getContentEncoding() {
			return null;
		}

		@Override
		public long getContentLength() {
			return 0;
		}

		@Override
		public Header getContentType() {
			return null;
		}

		@Override
		public boolean isChunked() {
			return false;
		}

		@Override
		public boolean isRepeatable() {
			return false;
		}

		@Override
		public boolean isStreaming() {
			return false;
		}

		@Override
		public void writeTo(OutputStream arg0) throws IOException {

		}

	}

}
