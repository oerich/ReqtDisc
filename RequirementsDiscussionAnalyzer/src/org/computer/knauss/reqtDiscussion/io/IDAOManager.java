package org.computer.knauss.reqtDiscussion.io;

import org.computer.knauss.reqtDiscussion.ui.ctrl.IDiscussionEventClassificationDAO;

public interface IDAOManager {

	public IDiscussionDAO getDiscussionDAO() throws DAOException;

	public IDiscussionEventDAO getDiscussionEventDAO() throws DAOException;

	public IDiscussionEventClassificationDAO getDiscussionEventClassificationDAO() throws DAOException;

	public void closeAllConnections();

}
