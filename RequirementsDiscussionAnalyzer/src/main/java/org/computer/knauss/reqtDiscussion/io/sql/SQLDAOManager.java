package org.computer.knauss.reqtDiscussion.io.sql;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.computer.knauss.reqtDiscussion.io.DAOException;
import org.computer.knauss.reqtDiscussion.io.IDAOManager;
import org.computer.knauss.reqtDiscussion.io.IDiscussionDAO;
import org.computer.knauss.reqtDiscussion.io.IDiscussionEventClassificationDAO;
import org.computer.knauss.reqtDiscussion.io.IDiscussionEventDAO;

public class SQLDAOManager implements IDAOManager {

	private SQLDiscussionDAO discussionDAO;
	private Properties properties;
	private SQLDiscussionEventDAO discussionEventDAO;
	private String queryPropertyFilename;
	private String connectionPropertyFilename;
	private SQLDiscussionEventClassificationDAO discussionEventClassificationDAO;

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

	private Properties getProperties() {
		if (this.properties == null) {
			properties = new Properties();
			try {
				properties.load(new FileInputStream(
						this.connectionPropertyFilename));
				properties
						.load(new FileInputStream(this.queryPropertyFilename));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return this.properties;
	}

	@Override
	public void closeAllConnections() {
		ConnectionManager.getInstance().closeConnection();
	}

}
