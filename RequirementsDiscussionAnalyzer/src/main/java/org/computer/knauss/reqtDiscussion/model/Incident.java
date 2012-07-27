package org.computer.knauss.reqtDiscussion.model;

import java.sql.Date;

/**
 * An incident that might be of relevance to the trajectory of a discussion.
 * Examples of incidents are changes of the metadata of discussions.
 * 
 * @author eknauss
 * 
 */
public class Incident extends ModelElement {

	public static final int RESCHEDULE = 1;
	public static final int RESOLUTION = 2;
	public static final int RESOLUTION_SUCCESS = 3;
	public static final int RESOLUTION_FAILURE = 4;
	private String name;
	private Date date;
	private String summary;
	private int type;

	public int getType() {
		return this.type;
	}

	public void setType(int t) {
		this.type = t;
	}

	/**
	 * The name of the incident.
	 * 
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * The date of the incident
	 * 
	 * @return
	 */
	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * A short description of the summary.
	 * 
	 * @return
	 */
	public String getSummary() {
		return this.summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	@Override
	public int getID() {
		return 0;
	}

	@Override
	public Date getCreationDate() {
		return this.getDate();
	}

	@Override
	public String getCreator() {
		// TODO Auto-generated method stub
		return null;
	}
}
