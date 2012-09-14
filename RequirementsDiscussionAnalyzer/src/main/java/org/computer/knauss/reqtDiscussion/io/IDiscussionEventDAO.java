package org.computer.knauss.reqtDiscussion.io;

import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;

/**
 * Assume the following schema: discussionEventClassification(discussioneventid,
 * classifiedby, classification, confidence, comment)
 * 
 * @author eknauss
 * 
 */
public interface IDiscussionEventDAO extends IConfigurable {

	public DiscussionEvent[] getDiscussionEventsOfDiscussion(int discussionId)
			throws DAOException;

	public DiscussionEvent getDiscussionEvent(int id) throws DAOException;

	public void storeDiscussionEvent(DiscussionEvent de) throws DAOException;

	public void storeDiscussionEvents(DiscussionEvent[] des)
			throws DAOException;
}
