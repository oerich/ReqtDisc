package org.computer.knauss.reqtDiscussion.model;

import java.sql.Date;
import java.util.Collections;
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
	private List<DiscussionEvent> dEvents = new Vector<DiscussionEvent>();
	private List<Incident> incidents = new Vector<Incident>();

	Discussion() {
		// Limit visibility of constructor.
	}

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

	public Date getCreationDate() {
		return dateCreated;
	}

	public void setCreationDate(Date dateCreated) {
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

	public DiscussionEvent[] getDiscussionEvents() {
		return this.dEvents.toArray(new DiscussionEvent[0]);
	}

	public void addDiscussionEvents(DiscussionEvent[] des) {
		Collections.addAll(this.dEvents, des);
	}

	public Incident[] getIncidents() {
		return this.incidents.toArray(new Incident[0]);
	}

	public void addIncidents(Incident[] incidents) {
		Collections.addAll(this.incidents, incidents);
	}
}
