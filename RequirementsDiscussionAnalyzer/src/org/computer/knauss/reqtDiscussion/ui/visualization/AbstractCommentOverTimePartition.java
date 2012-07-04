package org.computer.knauss.reqtDiscussion.ui.visualization;

import java.util.List;
import java.util.Vector;

import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;

public abstract class AbstractCommentOverTimePartition implements
		ICommentOverTimePartition {

	private DiscussionEvent[] comments;

	@Override
	public void setWorkitemComments(DiscussionEvent[] comments) {
		this.comments = comments;
	}

	@Override
	public DiscussionEvent[] getWorkitemComments() {
		return this.comments;
	}

	@Override
	public abstract int getPartitionForWorkitemComment(DiscussionEvent wc);

	@Override
	public DiscussionEvent[] getWorkitemsForPartition(int partition) {
		List<DiscussionEvent> tmp = new Vector<DiscussionEvent>();

		for (DiscussionEvent wc : this.comments) {
			if (partition == getPartitionForWorkitemComment(wc))
				tmp.add(wc);
		}

		return tmp.toArray(new DiscussionEvent[0]);
	}

	@Override
	public boolean isInClass(DiscussionEvent wic) {
		return (wic.getReferenceClassification() != null && wic
				.getReferenceClassification().toLowerCase()
				.startsWith("clarif"));
	}

}
