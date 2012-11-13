package org.computer.knauss.reqtDiscussion.model;

public class DiscussionEventClassification {

	private String classification = "no classification found";
	private double confidence = 0;
	private String classifiedby = "";
	private int discussionEventID = -1;
	private String comment = "";

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public double getConfidence() {
		return confidence;
	}

	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}

	public String getClassifiedby() {
		return classifiedby;
	}

	public void setClassifiedby(String classifiedby) {
		this.classifiedby = classifiedby;
	}

	public int getDiscussionEventID() {
		return discussionEventID;
	}

	public void setWorkitemcommentid(int discussionEventID) {
		this.discussionEventID = discussionEventID;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
}
