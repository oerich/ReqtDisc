package org.computer.knauss.reqtDiscussion.model;

public class DiscussionEventTagFilter implements IDiscussionEventFilter {

	private String tag;

	public DiscussionEventTagFilter(String tag) {
		this.tag = tag;
	}

	@Override
	public boolean accept(DiscussionEvent de) {
		if (de.getReferenceClassification().indexOf(this.tag) > -1)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Tag-Filter (" + this.tag + ")";
	}
}
