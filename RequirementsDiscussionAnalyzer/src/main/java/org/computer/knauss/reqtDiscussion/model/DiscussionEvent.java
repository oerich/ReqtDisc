package org.computer.knauss.reqtDiscussion.model;

import java.sql.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

public class DiscussionEvent extends ModelElement {

	private static final String NO_CLASS = "no class";
	private static final String IN_CLASS = "clarif";

	private int id;
	private int discussionID;
	private String content;
	private Date creationDate;
	private String creator;
	private Set<String> tags = new TreeSet<String>();
	private List<DiscussionEventClassification> eventClassification = new Vector<DiscussionEventClassification>();

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

	public void addTag(String tag) {
		this.tags.add(tag);
	}

	public void removeTag(String tag) {
		this.tags.remove(tag);
	}

	public String getTagString() {
		if (this.tags.size() == 0)
			return "";
		StringBuffer sb = new StringBuffer();

		for (String tag : this.tags) {
			sb.append(tag);
			sb.append(",");
		}

		String string = sb.toString();
		return string.substring(0, string.length() - 1);
	}

	public boolean hasTag(String tag) {
		return this.tags.contains(tag);
	}

	public void setDiscussionEventClassifications(
			DiscussionEventClassification[] deClassifications) {
		this.eventClassification.clear();
		for (DiscussionEventClassification wcc : deClassifications)
			this.eventClassification.add(wcc);
	}

	public DiscussionEventClassification[] getDiscussionEventClassifications() {
		return this.eventClassification
				.toArray(new DiscussionEventClassification[0]);
	}

	public String getReferenceClassification() {
		return getReferenceDiscussionEventClassification().getClassification();
	}

	public DiscussionEventClassification getReferenceDiscussionEventClassification() {
		return IClassificationFilter.NAME_FILTER
				.filterCommentClassifications(getDiscussionEventClassifications());
	}

	public void insertOrUpdateClassification(
			DiscussionEventClassification classification) {
		if (classification.getDiscussionEventID() != getID())
			throw new IllegalArgumentException(
					"Classification is not relevant for this DiscussionEvent");

		for (DiscussionEventClassification dec : this.eventClassification) {
			if (dec.getClassifiedby().equals(classification.getClassifiedby())) {
				this.eventClassification.remove(dec);
				this.eventClassification.add(classification);
				return;
			}
		}

		this.eventClassification.add(classification);
	}

	public boolean isInClass() {
		String referenceClassification = getReferenceClassification();
		return (referenceClassification != null && referenceClassification
				.toLowerCase().startsWith(IN_CLASS));
	}

	public boolean isClassified() {
		String referenceClassification = getReferenceClassification();
		return (referenceClassification != null && !referenceClassification
				.toLowerCase().startsWith(NO_CLASS));
	}
}
