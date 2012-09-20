package org.computer.knauss.reqtDiscussion.io.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Properties;

import org.computer.knauss.reqtDiscussion.io.DAOException;
import org.computer.knauss.reqtDiscussion.io.IDAOProgressMonitor;
import org.computer.knauss.reqtDiscussion.io.util.DateParser;
import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.DiscussionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class XMLDiscussionDAOTest {

	private static final String TESTFILE = "/jira-export-dummy.xml";
	private XMLDiscussionDAO testDAO;

	@Before
	public void setup() {
		this.testDAO = new XMLDiscussionDAO();
	}

	@After
	public void tearDown() {
		DiscussionFactory.getInstance().clear();
	}

	@Test(expected = DAOException.class)
	public void testUnitialized() throws DAOException {
		// Should not work without specifying a file name
		this.testDAO.getDiscussions();
	}

	@Test(expected = DAOException.class)
	public void testEmptyConfig() throws DAOException {
		this.testDAO.configure(null);
		// Should not work without specifying a file name
		this.testDAO.getDiscussions();
	}

	@Test
	public void testGetDiscussions() throws DAOException,
			FileNotFoundException, IOException, ParseException {
		setupDAO();
		Discussion[] discussions = this.testDAO.getDiscussions();

		assertEquals(2, discussions.length);
		assertEquals("We strip the prefix of the key (EJD-)...", 1000,
				discussions[0].getID());
		assertEquals("We strip the prefix of the key...", 1050,
				discussions[1].getID());

		checkFirstDiscussion(discussions[0]);
	}

	@Test
	public void testGetDiscussionsWithProgress() throws FileNotFoundException,
			IOException, DAOException {
		setupDAO();

		DAOProgressMonitorDummy monitor = new DAOProgressMonitorDummy();
		Discussion[] discussions = this.testDAO.getDiscussions(monitor);

		assertEquals(2, monitor.totalSteps);
		assertEquals(2, monitor.step);
		assertEquals("done.", monitor.message);

		// of course we also want to get the results...
		assertEquals(2, discussions.length);
		assertEquals("We strip the prefix of the key (TNG-)...", 1000,
				discussions[0].getID());
		assertEquals("We strip the prefix of the key...", 1050,
				discussions[1].getID());

		checkFirstDiscussion(discussions[0]);

	}

	private void checkFirstDiscussion(Discussion d) {
		assertEquals(
				DateParser.getInstance().parseDate(
						"Wed, 16 Aug 2006 13:55:42 +0200"), d.getCreationDate());

		assertEquals("User Two", d.getCreator());
		assertEquals("[EJD-1000] A first item in JIRA for testing purposes.",
				d.getSummary());
		assertEquals("We need to have some test data not covered by an NDA.",
				d.getDescription());
		assertEquals("Feature", d.getType());
		assertEquals(7, d.getDiscussionEvents().length);
	}

	private void setupDAO() throws IOException, FileNotFoundException,
			DAOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(getClass().getResource(
				"/jira-xml-properties.txt").getFile()));
		properties.setProperty(XMLDiscussionDAO.PROP_FILENAME, TESTFILE);

		this.testDAO.configure(properties);
	}

	@Test(expected = DAOException.class)
	public void testStoreDiscussion() throws FileNotFoundException,
			IOException, DAOException {
		setupDAO();
		this.testDAO.storeDiscussion(DiscussionFactory.getInstance()
				.getDiscussion(12345));
	}

	@Test(expected = DAOException.class)
	public void testStoreDiscussions() throws FileNotFoundException,
			IOException, DAOException {
		setupDAO();
		this.testDAO.storeDiscussions(new Discussion[] { DiscussionFactory
				.getInstance().getDiscussion(12345) });
	}

	@Test
	public void testMoreDiscussions() throws FileNotFoundException,
			IOException, DAOException {
		assertFalse(this.testDAO.hasMoreDiscussions());
		setupDAO();
		assertFalse(this.testDAO.hasMoreDiscussions());
		this.testDAO.getDiscussions();
		assertFalse(this.testDAO.hasMoreDiscussions());
	}

	@Test
	public void testGetDiscussionByID() throws FileNotFoundException,
			IOException, DAOException {
		setupDAO();
		checkFirstDiscussion(this.testDAO.getDiscussion(1000));
	}

	// Now on to a few tests about loading the comments
	@Test
	public void testDiscussionEventsOfDiscussion()
			throws FileNotFoundException, IOException, DAOException {
		setupDAO();
		DiscussionEvent[] events = this.testDAO
				.getDiscussionEventsOfDiscussion(1000);
		assertEquals(7, events.length);
		assertEquals(1000, events[0].getDiscussionID());
		assertEquals(11991, events[0].getID());
		assertEquals("user2", events[0].getCreator());
		assertEquals(
				DateParser.getInstance().parseDate(
						"Thu, 15 Mar 2007 17:29:52 +0100"),
				events[0].getCreationDate());
	}

	@Test(expected = DAOException.class)
	public void testStoreDiscussionEvent() throws DAOException {
		this.testDAO.storeDiscussionEvent(new DiscussionEvent());
	}

	@Test(expected = DAOException.class)
	public void testStoreDiscussionEvents() throws DAOException {
		this.testDAO
				.storeDiscussionEvents(new DiscussionEvent[] { new DiscussionEvent() });
	}

	@Test(expected = DAOException.class)
	public void testGetDiscussionEventByID() throws DAOException {
		this.testDAO.getDiscussionEvent(123456);
	}

	private class DAOProgressMonitorDummy implements IDAOProgressMonitor {

		String message;
		int step;
		int totalSteps;

		@Override
		public void setTotalSteps(int steps) {
			this.totalSteps = steps;
		}

		@Override
		public void setStep(int step) {
			this.step = step;
		}

		@Override
		public void setStep(int step, String message) {
			this.step = step;
			this.message = message;
		}

		@Override
		public boolean isCancelled() {
			return false;
		}

	}
}
