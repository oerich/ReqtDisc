package org.computer.knauss.reqtDiscussion.io.xml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.computer.knauss.reqtDiscussion.io.DAOException;
import org.computer.knauss.reqtDiscussion.io.IDAOManager;
import org.computer.knauss.reqtDiscussion.io.IDiscussionDAO;
import org.computer.knauss.reqtDiscussion.io.IDiscussionEventClassificationDAO;
import org.computer.knauss.reqtDiscussion.io.IDiscussionEventDAO;
import org.computer.knauss.reqtDiscussion.io.IIncidentDAO;

public class XmlDAOManager implements IDAOManager {

	private XMLDiscussionDAO xmlDiscussionDAO;
	private Properties properties;
	private String configurationPath;

	public XmlDAOManager(String configurationPath) {
		this.properties = new Properties();
		this.configurationPath = configurationPath;
	}

	@Override
	public IDiscussionDAO getDiscussionDAO() throws DAOException {
		return getXMLDiscussionDAO();
	}

	@Override
	public IDiscussionEventDAO getDiscussionEventDAO() throws DAOException {
		return getXMLDiscussionDAO();
	}

	@Override
	public IDiscussionEventClassificationDAO getDiscussionEventClassificationDAO()
			throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IIncidentDAO getIncidentDAO() throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void closeAllConnections() {
		// TODO Auto-generated method stub

	}

	private XMLDiscussionDAO getXMLDiscussionDAO() throws DAOException {
		if (this.xmlDiscussionDAO == null) {
			try {
				this.properties.load(new FileInputStream(getClass()
						.getResource(configurationPath).getFile()));
			} catch (FileNotFoundException e) {
				throw new DAOException("Could not find property file '"
						+ e.getMessage() + "'!", e);
			} catch (IOException e) {
				throw new DAOException("Could not read property file '"
						+ e.getMessage() + "'!", e);
			}
			this.xmlDiscussionDAO = new XMLDiscussionDAO();
			this.xmlDiscussionDAO.configure(this.properties);
		}
		return this.xmlDiscussionDAO;
	}

}
