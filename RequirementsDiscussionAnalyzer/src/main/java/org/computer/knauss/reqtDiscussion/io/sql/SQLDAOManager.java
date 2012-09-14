package org.computer.knauss.reqtDiscussion.io.sql;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.computer.knauss.reqtDiscussion.io.DAOException;
import org.computer.knauss.reqtDiscussion.io.IDAOManager;
import org.computer.knauss.reqtDiscussion.io.IDiscussionDAO;
import org.computer.knauss.reqtDiscussion.io.IDiscussionEventClassificationDAO;
import org.computer.knauss.reqtDiscussion.io.IDiscussionEventDAO;
import org.computer.knauss.reqtDiscussion.io.IIncidentDAO;

public class SQLDAOManager implements IDAOManager {

	private SQLDiscussionDAO discussionDAO;
	private Properties properties;
	private SQLDiscussionEventDAO discussionEventDAO;
	private String queryPropertyFilename;
	private String connectionPropertyFilename;
	private SQLDiscussionEventClassificationDAO discussionEventClassificationDAO;
	private IIncidentDAO incidentDAO;

	public SQLDAOManager(String connectionPropertyFilename,
			String queryPropertyFilename) {
		this.connectionPropertyFilename = connectionPropertyFilename;
		this.queryPropertyFilename = queryPropertyFilename;
	}

	@Override
	public IDiscussionDAO getDiscussionDAO() throws DAOException {
		if (this.discussionDAO == null) {
			this.discussionDAO = new SQLDiscussionDAO();
			this.discussionDAO.setDiscussionEventDAO(getDiscussionEventDAO());
			this.discussionDAO.setIncidentDAO(getIncidentDAO());
			this.discussionDAO.configure(getProperties());
		}
		return this.discussionDAO;
	}

	@Override
	public IDiscussionEventDAO getDiscussionEventDAO() throws DAOException {
		if (this.discussionEventDAO == null) {
			this.discussionEventDAO = new SQLDiscussionEventDAO();
			this.discussionEventDAO
					.setDiscussionEventClassificationDAO(getDiscussionEventClassificationDAO());
			this.discussionEventDAO.configure(getProperties());
		}
		return this.discussionEventDAO;
	}

	@Override
	public IDiscussionEventClassificationDAO getDiscussionEventClassificationDAO()
			throws DAOException {
		if (this.discussionEventClassificationDAO == null) {
			this.discussionEventClassificationDAO = new SQLDiscussionEventClassificationDAO();
			this.discussionEventClassificationDAO.configure(getProperties());
		}
		return this.discussionEventClassificationDAO;
	}

	private Properties getProperties() throws DAOException {
		if (this.properties == null) {
			properties = new Properties();
			try {
				URL propertyURL = getClass().getResource(
						this.connectionPropertyFilename);
				if (propertyURL != null) {
					properties.load(new FileInputStream(propertyURL.getFile()));
				} else {
					System.err.println("Could not locate property file '"
							+ this.connectionPropertyFilename + "'!");
				}

				propertyURL = getClass()
						.getResource(this.queryPropertyFilename);
				if (propertyURL != null) {
					properties.load(new FileInputStream(propertyURL.getFile()));
				} else {
					System.err.println("Could not locate property file '"
							+ this.queryPropertyFilename + "'!");
				}
			} catch (FileNotFoundException e) {
				System.err.println("Could not find property file '"
						+ e.getMessage() + "'!");
				// e.printStackTrace();
			} catch (IOException e) {
				System.err.println("Could not read property file '"
						+ e.getMessage() + "'!");
				// e.printStackTrace();
			}
		}
		return this.properties;
	}

	@Override
	public void closeAllConnections() {
		ConnectionManager.getInstance().closeConnection();
	}

	@Override
	public IIncidentDAO getIncidentDAO() throws DAOException {
		if (this.incidentDAO == null) {
			this.incidentDAO = new SQLIncidentDAO();
			this.incidentDAO.configure(getProperties());
		}
		return this.incidentDAO;
	}

}
