package org.computer.knauss.reqtDiscussion.model;

public interface IDiscussionFilter {

	/**
	 * Returns true, if the discussion is in, false if it is out.
	 * @param d
	 * @return
	 */
	boolean accept(Discussion d); 
	
}
