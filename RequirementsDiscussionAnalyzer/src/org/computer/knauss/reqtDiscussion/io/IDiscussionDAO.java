package org.computer.knauss.reqtDiscussion.io;

import org.computer.knauss.reqtDiscussion.model.Discussion;

public interface IDiscussionDAO {

	public Discussion getDiscussion(int discussionID) throws DAOException;

	public Discussion getNextDiscussion() throws DAOException;

	public Discussion[] getDiscussions() throws DAOException;

	public void storeDiscussion(Discussion d) throws DAOException;

	public void storeDiscussions(Discussion[] ds) throws DAOException;

}
