package org.computer.knauss.reqtDiscussion.io;

import java.util.Properties;

import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;


public interface IDiscussionEventDAO {

	public void configure(Properties p) throws DAOException;

	public DiscussionEvent[] getDiscussionEventsOfDiscussion(int discussionId)
			throws DAOException;

	public DiscussionEvent getDiscussionEvent(int id) throws DAOException;

	public void storeDiscussionEvent(DiscussionEvent de) throws DAOException;

	public void storeDiscussionEvents(DiscussionEvent[] des)
			throws DAOException;
}
