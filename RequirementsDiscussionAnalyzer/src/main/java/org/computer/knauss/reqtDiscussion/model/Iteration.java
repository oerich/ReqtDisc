package org.computer.knauss.reqtDiscussion.model;

import java.sql.Timestamp;

public class Iteration {

	private String identifier;
	private String title;
	private String description;
	private Timestamp startDate;
	private Timestamp endDate;
	private boolean hasDeliverables;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Timestamp getStartDate() {
		return startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public boolean isHasDeliverables() {
		return hasDeliverables;
	}

	public void setHasDeliverables(boolean hasDeliverables) {
		this.hasDeliverables = hasDeliverables;
	}
}
