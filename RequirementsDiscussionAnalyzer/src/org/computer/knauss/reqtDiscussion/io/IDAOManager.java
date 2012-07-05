package org.computer.knauss.reqtDiscussion.io;


public interface IDAOManager {

	public IDiscussionDAO getDiscussionDAO() throws DAOException;

	public IDiscussionEventDAO getDiscussionEventDAO() throws DAOException;

	public IDiscussionEventClassificationDAO getDiscussionEventClassificationDAO() throws DAOException;

	public void closeAllConnections();

}
