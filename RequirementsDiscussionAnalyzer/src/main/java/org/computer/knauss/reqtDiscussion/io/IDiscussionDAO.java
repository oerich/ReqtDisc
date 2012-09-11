package org.computer.knauss.reqtDiscussion.io;

import org.computer.knauss.reqtDiscussion.model.Discussion;

public interface IDiscussionDAO {

	/**
	 * As opposed to getDiscussions(), this method should return the discussion
	 * regardless of the level of abstraction. This makes it possible, to query
	 * subitems to a Discussion.
	 * 
	 * @param discussionID
	 * @return
	 * @throws DAOException
	 */
	public Discussion getDiscussion(int discussionID) throws DAOException;

	public Discussion getNextDiscussion() throws DAOException;

	public Discussion[] getMoreDiscussions(IDAOProgressMonitor progressMonitor)
			throws DAOException;

	/**
	 * Should only return discussions on appropriate level of abstraction (i.e.
	 * stories in jazz).
	 * 
	 * @param progressMonitor
	 *            add to see what is going on
	 * @return
	 * @throws DAOException
	 */
	public Discussion[] getDiscussions(IDAOProgressMonitor progressMonitor)
			throws DAOException;

	public Discussion[] getDiscussions() throws DAOException;

	public void storeDiscussion(Discussion d) throws DAOException;

	public void storeDiscussions(Discussion[] ds) throws DAOException;

	/**
	 * Especially when loading data from the web, it is delivered in chunks
	 * (typically 50-100 items, e.g. from jazz.net). This method should return
	 * true, if there is more data available.
	 * 
	 * @return
	 */
	public boolean hasMoreDiscussions();
}
