package org.computer.knauss.reqtDiscussion.io.jazz;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.computer.knauss.reqtDiscussion.io.DAOException;
import org.computer.knauss.reqtDiscussion.io.IDAOManager;
import org.computer.knauss.reqtDiscussion.io.IDiscussionDAO;
import org.computer.knauss.reqtDiscussion.io.IDiscussionEventDAO;
import org.computer.knauss.reqtDiscussion.io.jazz.rest.JazzJDOMDAO;
import org.computer.knauss.reqtDiscussion.io.jazz.util.ui.DialogBasedJazzAccessConfiguration;
import org.computer.knauss.reqtDiscussion.ui.ctrl.IDiscussionEventClassificationDAO;

public class JazzDAOManager implements IDAOManager {

	private JazzJDOMDAO dao;

	@Override
	public IDiscussionDAO getDiscussionDAO() throws DAOException {
		return getDAO();
	}

	@Override
	public IDiscussionEventDAO getDiscussionEventDAO() throws DAOException {
		return getDAO();
	}

	private JazzJDOMDAO getDAO() throws DAOException {
		if (this.dao == null) {
			Properties p = new Properties();

			try {
				p.load(new FileInputStream("jazz-properties.txt"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			DialogBasedJazzAccessConfiguration config = new DialogBasedJazzAccessConfiguration();
			config.configure(p);
			this.dao = new JazzJDOMDAO(config);
			this.dao.setProjectArea("Rational Team Concert");
		}
		return this.dao;
	}

	@Override
	public IDiscussionEventClassificationDAO getDiscussionEventClassificationDAO()
			throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void closeAllConnections() {
		// TODO Auto-generated method stub

	}

}
