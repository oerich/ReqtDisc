package org.computer.knauss.reqtDiscussion.model;

public class WorkitemCommentTagFilter implements IDiscussionEventFilter {

	private String tag;

	public WorkitemCommentTagFilter(String tag) {
		this.tag = tag;
	}

	@Override
	public boolean accept(DiscussionEvent wic) {
		if (wic.getReferenceClassification().indexOf(this.tag) > -1)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Tag-Filter (" + this.tag + ")";
	}
}
