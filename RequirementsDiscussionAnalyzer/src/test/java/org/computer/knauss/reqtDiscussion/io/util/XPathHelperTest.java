package org.computer.knauss.reqtDiscussion.io.util;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class XPathHelperTest {

	private static final String DOORS_GENERAL_EXAMPLE_TESTFILE_PATH = XPathHelperTest.class
			.getResource("/jazz.xml/general_test.xml").getFile();
	private static final String BOOK_EXAMPLE_TESTFILE_PATH = XPathHelperTest.class
			.getResource("/jazz.xml/buchbsp.xml").getFile();

	private XPathHelper helper;

	@Before
	public void setup() {
		helper = new XPathHelper();
	}

	@Test
	public void testDocument() throws SAXException, IOException,
			ParserConfigurationException, JDOMException {
		helper.setDocument(new FileInputStream(BOOK_EXAMPLE_TESTFILE_PATH));
		org.jdom2.Document d = helper.getDocument();
		assertEquals("inventory", d.getRootElement().getName());
	}

	@Test
	public void testBuchBsp() throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException,
			JDOMException {
		helper.setDocument(new FileInputStream(BOOK_EXAMPLE_TESTFILE_PATH));
		String xmlExp = "//book[author='Neal Stephenson']/title/text()";
		// helper.resolve(xmlExp);
		assertEquals(2, helper.count(xmlExp));
		assertEquals(3, helper.count("//book"));
	}

	@Test
	public void testDaimlerBsp() throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException,
			JDOMException {
		helper.setDocument(new FileInputStream(
				DOORS_GENERAL_EXAMPLE_TESTFILE_PATH));
		assertEquals(1, helper.count("/doorsexport"));
		assertEquals(1,
				helper.count("//object[column/heading/span='Eingänge']/table"));
		assertEquals(
				6,
				helper.count("//object[column/heading/span='Eingänge']/table/tr"));
		assertEquals(
				5,
				helper.count("//object[column/heading/span='Ausgänge']/table/tr"));
		assertEquals(
				3,
				helper.count("//object[column/heading/span='Parameter']/table/tr"));
		// helper.resolve("//object[column/heading/span='Eingänge']/table");
		// printRec(
		// helper.select("//object[column/heading/span='Eingänge']/table") );
		List<Object> list = helper
				.select("//object[column/heading/span='Eingänge']/table/tr");
		list = helper.select(list.get(0), ".//td");
		assertEquals(3, list.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSelectWithNullNode() throws XPathExpressionException,
			SAXException, IOException, ParserConfigurationException,
			JDOMException {
		helper.setDocument(new FileInputStream(
				DOORS_GENERAL_EXAMPLE_TESTFILE_PATH));
		helper.select(null, "//");
	}

	@Test
	public void testGetResourcesFromRootservices() throws IOException,
			JDOMException {
		FileInputStream documentStream = new FileInputStream(getClass()
				.getResource("/jazz.xml/rootservices.xml").getFile());
		helper.setDocument(documentStream);
		Attribute queryResource = (Attribute) helper
				.select("//query/@resource").get(0);
		assertEquals("https://jazz.net/jazz/query", queryResource.getValue());
		documentStream.close();
	}

	@Test
	public void testRelativeSelect() throws FileNotFoundException,
			JDOMException, IOException {
		FileInputStream documentStream = new FileInputStream(
				BOOK_EXAMPLE_TESTFILE_PATH);
		helper.setDocument(documentStream);
		List<Object> books = helper.select("//book");

		assertEquals(3, books.size());
		assertEquals("14.95", ((Element) helper
				.select(books.get(0), ".//price").get(0)).getValue());
		assertEquals("5.99", ((Element) helper.select(books.get(1), ".//price")
				.get(0)).getValue());
		assertEquals("7.50", ((Element) helper.select(books.get(2), ".//price")
				.get(0)).getValue());
		documentStream.close();
	}

	@Test
	public void testSelectAttributes() throws FileNotFoundException,
			JDOMException, IOException {
		FileInputStream documentStream = new FileInputStream(
				BOOK_EXAMPLE_TESTFILE_PATH);
		helper.setDocument(documentStream);

		assertEquals(1, helper.select("//inventory").size());
		assertEquals("some url", ((Attribute) helper
				.select("//inventory/@next").get(0)).getValue());

		documentStream.close();

	}

	@Test
	public void testGetJazzNextChangeRequestsQuery() throws JDOMException,
			IOException {
		URL resource50stories = getClass().getResource(
				"/jazz.xml/50-stories.xml");
		if (resource50stories != null) {
			FileInputStream documentStream = new FileInputStream(
					resource50stories.getFile());
			helper.setDocument(documentStream);
			assertEquals(50, helper.select("//ChangeRequest").size());

			List<Object> list = helper.select(".");
			assertEquals(1, list.size());

			Attribute nextAttrib = (Attribute) helper.select(
					"//Collection/@next").get(0);
			assertEquals(
					"https://jazz.net/jazz/oslc/contexts/_1w8aQEmJEduIY7C8B09Hyw/workitems?oslc_cm.pageSize=50&_resultToken=_qMxpUbTjEeGVKOo_oXemGQ&_startIndex=50",
					nextAttrib.getValue());
			documentStream.close();
		} else {
			System.err
					.println("Testfile: '/jazz.xml/50-stories.xml' is missing.");
		}
	}
}
