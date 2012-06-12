package org.computer.knauss.reqtDiscussion.model;

import java.sql.Date;

public class DiscussionEvent extends ModelElement {

	private int id;
	private int discussionID;
	private String content;
	private Date creationDate;
	private String creator;

	@Override
	public int getID() {
		return this.id;
	}

	public int getDiscussionID() {
		return discussionID;
	}

	public void setDiscussionID(int discussionID) {
		this.discussionID = discussionID;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public void setID(int id) {
		this.id = id;
	}

}
