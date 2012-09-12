package org.computer.knauss.reqtDiscussion.io.xml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.computer.knauss.reqtDiscussion.io.DAOException;
import org.computer.knauss.reqtDiscussion.io.IDAOProgressMonitor;
import org.computer.knauss.reqtDiscussion.io.IDiscussionDAO;
import org.computer.knauss.reqtDiscussion.io.util.DateParser;
import org.computer.knauss.reqtDiscussion.io.util.XPathHelper;
import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionFactory;
import org.jdom2.Element;
import org.jdom2.JDOMException;

public class XMLDiscussionDAO implements IDiscussionDAO {

	public static final String PROP_FILENAME = "file";
	public static final String PROP_DISCUSSION_PATH = "xpath.discussion";
	public static final String PROP_DISCUSSION_ID_PATH = "xpath.discussionID";
	public static final String PROP_DISCUSSION_CREATED_PATH = "xpath.discussionCreated";
	private static final String PROP_DISCUSSION_CREATOR_PATH = "xpath.discussionCreator";
	private static final String PROP_DISCUSSION_DESCRIPTION_PATH = "xpath.discussionDescription";
	private static final String PROP_DISCUSSION_SUMMARY_PATH = "xpath.discussionSummary";
	private static final String PROP_DISCUSSION_TYPE_PATH = "xpath.discussionType";
	private Properties properties;
	private XPathHelper issueParser;

	@Override
	public Discussion getDiscussion(int discussionID) throws DAOException {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Discussion[] getDiscussions() throws DAOException {
		checkProperties(this.properties);

		Discussion[] result = new Discussion[0];

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

		List<Object> issueList = issueParser.select(this.properties
				.getProperty(PROP_DISCUSSION_PATH));

		result = new Discussion[issueList.size()];
		for (int i = 0; i < result.length; i++) {
			Object issue = issueList.get(i);
			int id = Integer.parseInt(getValue(issue,
					this.properties.getProperty(PROP_DISCUSSION_ID_PATH)));
			Discussion d = DiscussionFactory.getInstance().getDiscussion(id);
			d.setCreationDate(DateParser.getInstance().parseDate(
					getValue(issue, this.properties
							.getProperty(PROP_DISCUSSION_CREATED_PATH))));
			d.setCreator(getValue(issue,
					this.properties.getProperty(PROP_DISCUSSION_CREATOR_PATH)));
			d.setDescription(getValue(issue, this.properties
					.getProperty(PROP_DISCUSSION_DESCRIPTION_PATH)));
			d.setSummary(getValue(issue,
					this.properties.getProperty(PROP_DISCUSSION_SUMMARY_PATH)));
			d.setType(getValue(issue,
					this.properties.getProperty(PROP_DISCUSSION_TYPE_PATH)));
			result[i] = d;
		}
		return result;
	}

	private String getValue(Object relativeTo, String xpath) {
		Object element = issueParser.select(relativeTo, xpath).get(0);
		if (element instanceof Element) {
			// System.out.println(((Element) element).getValue());
			return ((Element) element).getValue();
		}
		return element.toString();
	}

	@Override
	public void storeDiscussion(Discussion d) throws DAOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void storeDiscussions(Discussion[] ds) throws DAOException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasMoreDiscussions() {
		// TODO Auto-generated method stub
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
		if (!properties.containsKey(PROP_FILENAME))
			throw new DAOException("No filename specified.");
		if (!properties.containsKey(PROP_DISCUSSION_PATH))
			throw new DAOException("No path to discussion specified.");
		if (!properties.containsKey(PROP_DISCUSSION_ID_PATH))
			throw new DAOException("No path to id specified specified.");
	}

}
