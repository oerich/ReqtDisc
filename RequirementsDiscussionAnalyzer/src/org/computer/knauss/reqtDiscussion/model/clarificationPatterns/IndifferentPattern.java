package org.computer.knauss.reqtDiscussion.model.clarificationPatterns;

import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.ui.visualization.ICommentOverTimePartition;

public class IndifferentPattern implements IPatternClass {

	private ICommentOverTimePartition partition;

	@Override
	public void setCommentPartition(ICommentOverTimePartition p) {
		this.partition = p;
	}

	@Override
	public boolean matchesPattern(DiscussionEvent[] comments) {
		this.partition.setWorkitemComments(comments);

		for (DiscussionEvent wc : comments) {
			if (this.partition.isInClass(wc))
				return false;
		}
		return true;
	}

	@Override
	public String getName() {
		return "indifferent";
	}

}
