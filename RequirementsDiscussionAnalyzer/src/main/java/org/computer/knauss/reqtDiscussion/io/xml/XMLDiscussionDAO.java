package org.computer.knauss.reqtDiscussion.io.xml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.computer.knauss.reqtDiscussion.io.DAOException;
import org.computer.knauss.reqtDiscussion.io.IDAOProgressMonitor;
import org.computer.knauss.reqtDiscussion.io.IDiscussionDAO;
import org.computer.knauss.reqtDiscussion.io.IDiscussionEventDAO;
import org.computer.knauss.reqtDiscussion.io.util.DateParser;
import org.computer.knauss.reqtDiscussion.io.util.XPathHelper;
import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.DiscussionFactory;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.JDOMException;

public class XMLDiscussionDAO implements IDiscussionDAO, IDiscussionEventDAO {

	private static final String ERROR_READ_ONLY = "XML data source is read only! Please choose a different data source.";
	public static final String PROP_FILENAME = "file";
	public static final String PROP_DISCUSSION_PATH = "xpath.discussion";
	public static final String PROP_DISCUSSION_ID_PATH = "xpath.discussionID";
	public static final String PROP_DISCUSSION_CREATED_PATH = "xpath.discussionCreated";
	private static final String PROP_DISCUSSION_CREATOR_PATH = "xpath.discussionCreator";
	private static final String PROP_DISCUSSION_DESCRIPTION_PATH = "xpath.discussionDescription";
	private static final String PROP_DISCUSSION_SUMMARY_PATH = "xpath.discussionSummary";
	private static final String PROP_DISCUSSION_TYPE_PATH = "xpath.discussionType";
	private static final String PROP_DISCUSSION_BY_ID_PATH = "xpath.discussionByID";
	private static final String PROP_DISCUSSION_EVENTS = "xpath.discussionEvents";
	private static final String PROP_DISCUSSION_EVENT_ID = "xpath.discussionEventID";
	private static final String PROP_DISCUSSION_EVENT_CREATOR = "xpath.discussionEventCreator";
	private static final String PROP_DISCUSSION_EVENT_CONTENT = "xpath.discussionEventContent";
	private static final String PROP_DISCUSSION_EVENT_CREATIONDATE = "xpath.discussionEventCreationDate";

	private Properties properties;
	private XPathHelper issueParser;

	@Override
	public Discussion getDiscussion(int discussionID) throws DAOException {
		loadFile();
		String query = String.format(
				getConfiguration().getProperty(PROP_DISCUSSION_BY_ID_PATH),
				discussionID);
		Object issue = issueParser.select(query);
		return extractDiscussion(issue);
	}

	@Override
	public Discussion getNextDiscussion() throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Discussion[] getMoreDiscussions(IDAOProgressMonitor progressMonitor)
			throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Discussion[] getDiscussions(IDAOProgressMonitor progressMonitor)
			throws DAOException {
		Discussion[] result = new Discussion[0];
		progressMonitor.setStep(0, "Reading file...");

		loadFile();

		List<Object> issueList = issueParser.select(getConfiguration()
				.getProperty(PROP_DISCUSSION_PATH));
		progressMonitor.setTotalSteps(issueList.size());
		result = new Discussion[issueList.size()];
		for (int i = 0; i < result.length; i++) {
			progressMonitor.setStep(i, "Loading discussion " + i + "...");
			result[i] = extractDiscussion(issueList.get(i));
		}
		progressMonitor.setStep(issueList.size(), "done.");
		return result;
	}

	private void loadFile() throws DAOException {
		issueParser = new XPathHelper();
		try {
			URL resource = getClass().getResource(
					getConfiguration().getProperty(PROP_FILENAME));
			if (resource == null) {
				throw new DAOException("Could not find resource '"
						+ getConfiguration().getProperty(PROP_FILENAME) + "'");
			}

			issueParser.setDocument(new FileInputStream(resource.getFile()));
		} catch (FileNotFoundException e) {
			throw new DAOException("Could not find file.", e);
		} catch (JDOMException e) {
			throw new DAOException("Could not parse file.", e);
		} catch (IOException e) {
			throw new DAOException("Could not read file.", e);
		}
	}

	@Override
	public Discussion[] getDiscussions() throws DAOException {
		return getDiscussions(IDAOProgressMonitor.NULL_PROGRESS_MONITOR);
	}

	private Discussion extractDiscussion(Object issue) {
		int id = Integer.parseInt(getValue(issue, getConfiguration()
				.getProperty(PROP_DISCUSSION_ID_PATH)));
		Discussion d = DiscussionFactory.getInstance().getDiscussion(id);
		d.setCreationDate(DateParser.getInstance().parseDate(
				getValue(
						issue,
						getConfiguration().getProperty(
								PROP_DISCUSSION_CREATED_PATH))));
		d.setCreator(getValue(issue,
				getConfiguration().getProperty(PROP_DISCUSSION_CREATOR_PATH)));
		d.setDescription(getValue(issue,
				getConfiguration()
						.getProperty(PROP_DISCUSSION_DESCRIPTION_PATH)));
		d.setSummary(getValue(issue,
				getConfiguration().getProperty(PROP_DISCUSSION_SUMMARY_PATH)));
		d.setType(getValue(issue,
				getConfiguration().getProperty(PROP_DISCUSSION_TYPE_PATH)));

		DiscussionEvent[] events = getDiscussionEventsForDiscussion(issue);
		for (DiscussionEvent e : events)
			e.setDiscussionID(d.getID());
		d.addDiscussionEvents(events);
		return d;
	}

	private String getValue(Object relativeTo, String xpath) {
		Object element = issueParser.select(relativeTo, xpath).get(0);
		if (element instanceof Element) {
			// System.out.println(((Element) element).getValue());
			return ((Element) element).getValue();
		}
		if (element instanceof Attribute)
			return ((Attribute) element).getValue();
		return element.toString();
	}

	@Override
	public void storeDiscussion(Discussion d) throws DAOException {
		throw new DAOException(ERROR_READ_ONLY);
	}

	@Override
	public void storeDiscussions(Discussion[] ds) throws DAOException {
		throw new DAOException(ERROR_READ_ONLY);
	}

	@Override
	public boolean hasMoreDiscussions() {
		return false;
	}

	@Override
	public void configure(Properties properties) throws DAOException {
		if (properties == null)
			throw new DAOException("Properties must not be null.");
		for (String key : properties.stringPropertyNames())
			getConfiguration().setProperty(key, properties.getProperty(key));
	}

	@Override
	public DiscussionEvent[] getDiscussionEventsOfDiscussion(int discussionId)
			throws DAOException {
		loadFile();
		String query = String.format(
				getConfiguration().getProperty(PROP_DISCUSSION_BY_ID_PATH),
				discussionId);
		Object issue = issueParser.select(query);
		Discussion d = extractDiscussion(issue);
		DiscussionEvent[] events = getDiscussionEventsForDiscussion(issue);
		for (DiscussionEvent e : events)
			e.setDiscussionID(d.getID());
		return events;
	}

	@Override
	public DiscussionEvent getDiscussionEvent(int id) throws DAOException {
		throw new DAOException(
				"XML data source: Extraction of single discussion events not implemented.");
	}

	@Override
	public void storeDiscussionEvent(DiscussionEvent de) throws DAOException {
		throw new DAOException(ERROR_READ_ONLY);
	}

	@Override
	public void storeDiscussionEvents(DiscussionEvent[] des)
			throws DAOException {
		throw new DAOException(ERROR_READ_ONLY);
	}

	private DiscussionEvent[] getDiscussionEventsForDiscussion(Object issue) {
		List<Object> discussionEventElements = this.issueParser.select(issue,
				getConfiguration().getProperty(PROP_DISCUSSION_EVENTS));
		DiscussionEvent[] ret = new DiscussionEvent[discussionEventElements
				.size()];

		for (int i = 0; i < ret.length; i++) {
			ret[i] = extractDiscussionEvent(discussionEventElements.get(i));
		}

		return ret;
	}

	private DiscussionEvent extractDiscussionEvent(Object object) {
		DiscussionEvent de = new DiscussionEvent();
		de.setID(Integer.parseInt(getValue(object, getConfiguration()
				.getProperty(PROP_DISCUSSION_EVENT_ID))));
		de.setCreator(getValue(object,
				getConfiguration().getProperty(PROP_DISCUSSION_EVENT_CREATOR)));
		de.setContent(getValue(object,
				getConfiguration().getProperty(PROP_DISCUSSION_EVENT_CONTENT)));
		de.setCreationDate(DateParser.getInstance().parseDate(
				getValue(
						object,
						getConfiguration().getProperty(
								PROP_DISCUSSION_EVENT_CREATIONDATE))));
		return de;
	}

	@Override
	public Properties getConfiguration() {
		if (this.properties == null) {
			this.properties = new Properties();
			this.properties.setProperty(PROP_FILENAME, "");
			this.properties.setProperty(PROP_DISCUSSION_BY_ID_PATH, "");
			this.properties.setProperty(PROP_DISCUSSION_CREATED_PATH, "");
			this.properties.setProperty(PROP_DISCUSSION_CREATOR_PATH, "");
			this.properties.setProperty(PROP_DISCUSSION_DESCRIPTION_PATH, "");
			this.properties.setProperty(PROP_DISCUSSION_EVENT_CONTENT, "");
			this.properties.setProperty(PROP_DISCUSSION_EVENT_CREATIONDATE, "");
			this.properties.setProperty(PROP_DISCUSSION_EVENT_CREATOR, "");
			this.properties.setProperty(PROP_DISCUSSION_EVENT_ID, "");
			this.properties.setProperty(PROP_DISCUSSION_EVENTS, "");
			this.properties.setProperty(PROP_DISCUSSION_ID_PATH, "");
			this.properties.setProperty(PROP_DISCUSSION_PATH, "");
			this.properties.setProperty(PROP_DISCUSSION_SUMMARY_PATH, "");
			this.properties.setProperty(PROP_DISCUSSION_TYPE_PATH, "");
		}
		return this.properties;
	}

	@Override
	public Map<String, String> checkConfiguration() {
		Map<String, String> ret = new HashMap<String, String>();

		check(getConfiguration(), PROP_FILENAME,
				"Path to an XML file with discussions", ret);
		check(getConfiguration(),
				PROP_DISCUSSION_PATH,
				"XPath expression that selects all discussions in the XML document (e.g. //discussion).",
				ret);
		check(getConfiguration(),
				PROP_DISCUSSION_ID_PATH,
				"XPath expression that selects the ID of a discussion relative to the discussion element.",
				ret);
		check(getConfiguration(), PROP_DISCUSSION_BY_ID_PATH,
				"XPath to discussion with id %d", ret);
		check(getConfiguration(), PROP_DISCUSSION_CREATED_PATH,
				"Relative XPath to creationDate of this discussion", ret);
		check(getConfiguration(), PROP_DISCUSSION_CREATOR_PATH,
				"Relative XPath to creator of this discussion", ret);
		check(getConfiguration(), PROP_DISCUSSION_DESCRIPTION_PATH,
				"Relative XPath to description of this discussion", ret);
		check(getConfiguration(), PROP_DISCUSSION_EVENT_CONTENT,
				"Relative XPath to content of this discussion event", ret);
		check(getConfiguration(), PROP_DISCUSSION_EVENT_CREATIONDATE,
				"Relative XPath to creationDate of this discussion event", ret);
		check(getConfiguration(), PROP_DISCUSSION_EVENT_CREATOR,
				"Relative XPath to creator of this discussion event", ret);
		check(getConfiguration(), PROP_DISCUSSION_EVENT_ID,
				"Relative XPath to ID of this discussion event", ret);
		check(getConfiguration(), PROP_DISCUSSION_EVENTS,
				"Relative XPath to events of this discussion", ret);
		check(getConfiguration(), PROP_DISCUSSION_SUMMARY_PATH,
				"Relative XPath to summary of this discussion", ret);
		check(getConfiguration(), PROP_DISCUSSION_TYPE_PATH,
				"Relative XPath to type of this discussion", ret);
		return ret;
	}

	private void check(Properties p, String key, String remark,
			Map<String, String> report) {
		if (!p.containsKey(key) || "".equals(p.getProperty(key)))
			report.put(key, remark);
	}

}
