package org.computer.knauss.reqtDiscussion.io.sql;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.computer.knauss.reqtDiscussion.io.DAOException;
import org.computer.knauss.reqtDiscussion.io.IDAOManager;
import org.computer.knauss.reqtDiscussion.io.IDiscussionDAO;
import org.computer.knauss.reqtDiscussion.io.IDiscussionEventDAO;
import org.computer.knauss.reqtDiscussion.ui.ctrl.IDiscussionEventClassificationDAO;

public class SQLDAOManager implements IDAOManager {

	private SQLDiscussionDAO discussionDAO;
	private Properties properties;
	private SQLDiscussionEventDAO discussionEventDAO;
	private String queryPropertyFilename;
	private String connectionPropertyFilename;

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
			this.discussionEventDAO.configure(getProperties());
		}
		return this.discussionEventDAO;
	}

	@Override
	public IDiscussionEventClassificationDAO getDiscussionEventClassificationDAO()
			throws DAOException {
		// TODO Auto-generated method stub
		return null;
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
