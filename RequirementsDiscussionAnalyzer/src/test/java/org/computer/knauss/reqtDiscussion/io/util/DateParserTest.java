package org.computer.knauss.reqtDiscussion.io.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Date;

import org.junit.Test;

// TODO change to timestamp in whole application!
public class DateParserTest {

	@SuppressWarnings("deprecation")
	@Test
	public void testJazz() {
		Date date = DateParser.JAZZ_PARSER
				.parseDate("2010-06-11T03:44:02.373Z");
		assertNotNull(date);
		assertEquals(110, date.getYear());
		assertEquals(5, date.getMonth());
		// assertEquals(11, date.getDay());

		Date d2 = new Date(110, 5, 11);
		assertTrue("We also have the timemillis", d2.before(date));

	}

	@SuppressWarnings("deprecation")
	@Test
	public void testPSQL() {
		Date date = DateParser.PSQL_PARSER.parseDate("2007-10-03 05:37:18.837");
		assertNotNull(date);
		assertEquals(107, date.getYear());
		assertEquals(9, date.getMonth());
		assertEquals(3, date.getDay());

		Date d2 = new Date(107, 9, 3);
		assertTrue("We also have the timemillis", d2.before(date));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testJira() {
		Date date = DateParser.JIRA_PARSER
				.parseDate("Tue, 29 Nov 2011 14:50:24 +0100");
		assertNotNull(date);
		assertEquals(111, date.getYear());
		assertEquals(10, date.getMonth());
		// assertEquals(29, date.getDay());

		Date d2 = new Date(111, 10, 29);
		assertTrue("We also have the timemillis", d2.before(date));
	}

	@Test
	public void testMixtureOfFormats() {
		Date d1 = new Date(111, 10, 29);
		Date d2 = new Date(111, 10, 30);

		Date date = DateParser.getInstance().parseDate(
				"Tue, 29 Nov 2011 14:50:24 +0100");
		assertTrue("We also have the timemillis", d1.before(date));
		assertTrue("We also have the timemillis", d2.after(date));
		
		date = DateParser.getInstance().parseDate(
				"2011-11-29 14:50:24");
		assertTrue("We also have the timemillis", d1.before(date));
		assertTrue("We also have the timemillis", d2.after(date));
		
		date = DateParser.getInstance().parseDate(
				"2011-11-29T14:50:24");
		assertTrue("We also have the timemillis", d1.before(date));
		assertTrue("We also have the timemillis", d2.after(date));

	}

}
