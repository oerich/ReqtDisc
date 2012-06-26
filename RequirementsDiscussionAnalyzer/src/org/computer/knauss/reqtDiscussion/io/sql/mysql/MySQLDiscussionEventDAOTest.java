package org.computer.knauss.reqtDiscussion.io.sql.mysql;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.computer.knauss.reqtDiscussion.io.DAOException;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.junit.Before;
import org.junit.Test;

public class MySQLDiscussionEventDAOTest {

	private MySQLDiscussionEventDAO testDao;

	@Before
	public void setUp() throws Exception {
		this.testDao = new MySQLDiscussionEventDAO();
	}

	@Test
	public void testGetDiscussionEventsOfDiscussion() throws DAOException,
			FileNotFoundException, IOException {
		Properties p = new Properties();
		p.load(new FileInputStream("trento-mysql-properties.txt"));

		// System.out.println(p.getProperty(SQLDiscussionEventDAO.PROP_USER));

		this.testDao.configure(p);
		DiscussionEvent[] events = this.testDao
				.getDiscussionEventsOfDiscussion(12345);

		assertEquals(0, events.length);
		events = this.testDao.getDiscussionEventsOfDiscussion(32654);
		assertEquals(24, events.length);
	}

	@Test
	public void testStoreDiscussionEvents() throws DAOException {

		// DiscussionEvent de = new DiscussionEvent();
		// de.setID(1);
		// de.setDiscussionID(1);
		// de.setContent("TESTTESTTEST");
		// de.setCreationDate(new Date(System.currentTimeMillis()));
		// de.setCreator("JUnit");
		//
		// Properties p = new Properties();
		// p.setProperty(SQLDiscussionEventDAO.PROP_URL,
		// "jdbc:mysql://db.disi.unitn.it/developers");
		// p.setProperty(SQLDiscussionEventDAO.PROP_USER, "mist");
		// p.setProperty(SQLDiscussionEventDAO.PROP_PWD, "mist");
		// this.testDao.configure(p);
		//
		// this.testDao.storeDiscussionEvents(new DiscussionEvent[] { de });
	}

}
