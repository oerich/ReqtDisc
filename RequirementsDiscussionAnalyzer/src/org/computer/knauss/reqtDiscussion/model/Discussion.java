package org.computer.knauss.reqtDiscussion.model;

import java.sql.Date;
import java.util.List;
import java.util.Vector;

public class Discussion extends ModelElement {

	private int id;
	private String summary;
	private String description;
	private String type;
	private Date dateCreated;
	private String creator;
	private String status;
	private List<DiscussionEvent> comments = new Vector<DiscussionEvent>();

	@Override
	public int getID() {
		return this.id;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setId(int id) {
		this.id = id;
	}

	public DiscussionEvent[] getAllComments() {
		return this.comments.toArray(new DiscussionEvent[0]);
	}

	public void addComments(DiscussionEvent[] wcs) {
		for (DiscussionEvent de : wcs) {
			this.comments.add(de);
		}
	}

}
