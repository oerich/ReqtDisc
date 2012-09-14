package org.computer.knauss.reqtDiscussion.io.xml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.JFileChooser;

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
				this.properties.getProperty(PROP_DISCUSSION_BY_ID_PATH),
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

		List<Object> issueList = issueParser.select(this.properties
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
		checkProperties(this.properties);
		issueParser = new XPathHelper();
		try {
			issueParser.setDocument(new FileInputStream(getClass().getResource(
					this.properties.getProperty(PROP_FILENAME)).getFile()));
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
		int id = Integer.parseInt(getValue(issue,
				this.properties.getProperty(PROP_DISCUSSION_ID_PATH)));
		Discussion d = DiscussionFactory.getInstance().getDiscussion(id);
		d.setCreationDate(DateParser.getInstance().parseDate(
				getValue(issue, this.properties
						.getProperty(PROP_DISCUSSION_CREATED_PATH))));
		d.setCreator(getValue(issue,
				this.properties.getProperty(PROP_DISCUSSION_CREATOR_PATH)));
		d.setDescription(getValue(issue,
				this.properties.getProperty(PROP_DISCUSSION_DESCRIPTION_PATH)));
		d.setSummary(getValue(issue,
				this.properties.getProperty(PROP_DISCUSSION_SUMMARY_PATH)));
		d.setType(getValue(issue,
				this.properties.getProperty(PROP_DISCUSSION_TYPE_PATH)));

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
		checkProperties(properties);

		this.properties = properties;
	}

	private void checkProperties(Properties properties) throws DAOException {
		// throw exception if important property is missing
		if (properties == null)
			throw new DAOException("Configuration is null");
		if (!properties.containsKey(PROP_FILENAME)) {
			JFileChooser fc = new JFileChooser();
			int i = fc.showOpenDialog(null);
			if (i == JFileChooser.APPROVE_OPTION) {
				properties.setProperty(PROP_FILENAME, fc.getSelectedFile()
						.getName());
			} else {
				throw new DAOException("No filename specified.");
			}
		}
		if (!properties.containsKey(PROP_DISCUSSION_PATH))
			throw new DAOException("No path to discussion specified.");
		if (!properties.containsKey(PROP_DISCUSSION_ID_PATH))
			throw new DAOException("No path to id specified specified.");
	}

	@Override
	public DiscussionEvent[] getDiscussionEventsOfDiscussion(int discussionId)
			throws DAOException {
		loadFile();
		String query = String.format(
				this.properties.getProperty(PROP_DISCUSSION_BY_ID_PATH),
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
				this.properties.getProperty(PROP_DISCUSSION_EVENTS));
		DiscussionEvent[] ret = new DiscussionEvent[discussionEventElements
				.size()];

		for (int i = 0; i < ret.length; i++) {
			ret[i] = extractDiscussionEvent(discussionEventElements.get(i));
		}

		return ret;
	}

	private DiscussionEvent extractDiscussionEvent(Object object) {
		DiscussionEvent de = new DiscussionEvent();
		de.setID(Integer.parseInt(getValue(object,
				this.properties.getProperty(PROP_DISCUSSION_EVENT_ID))));
		de.setCreator(getValue(object,
				this.properties.getProperty(PROP_DISCUSSION_EVENT_CREATOR)));
		de.setContent(getValue(object,
				this.properties.getProperty(PROP_DISCUSSION_EVENT_CONTENT)));
		de.setCreationDate(DateParser.getInstance().parseDate(
				getValue(object, this.properties
						.getProperty(PROP_DISCUSSION_EVENT_CREATIONDATE))));
		return de;
	}

	@Override
	public Properties getConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> checkConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}
}
