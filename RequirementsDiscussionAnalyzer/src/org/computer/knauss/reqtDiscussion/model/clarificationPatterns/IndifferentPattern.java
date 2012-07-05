package org.computer.knauss.reqtDiscussion.model.clarificationPatterns;

import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.partition.IDiscussionOverTimePartition;

public class IndifferentPattern implements IPatternClass {

	private IDiscussionOverTimePartition partition;

	@Override
	public void setCommentPartition(IDiscussionOverTimePartition p) {
		this.partition = p;
	}

	@Override
	public boolean matchesPattern(DiscussionEvent[] comments) {
		this.partition.setDiscussionEvents(comments);

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
