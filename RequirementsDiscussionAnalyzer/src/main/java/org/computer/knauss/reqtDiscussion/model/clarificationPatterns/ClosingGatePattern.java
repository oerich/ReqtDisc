package org.computer.knauss.reqtDiscussion.model.clarificationPatterns;

import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.partition.IDiscussionOverTimePartition;

public class ClosingGatePattern implements IPatternClass {

	private final static double LIMIT = 0.75;
	private IDiscussionOverTimePartition partition;

	@Override
	public void setCommentPartition(IDiscussionOverTimePartition p) {
		this.partition = p;
	}

	@Override
	public boolean matchesPattern(DiscussionEvent[] comments) {
		if (comments.length == 0)
			return false;

		this.partition.setModelElements(comments);

		int limit = (int) (this.partition.getPartitionCount() * LIMIT);

		for (int i = 0; i < limit; i++) {
			if (this.partition.getModelElementsForPartition(i).length > 0)
				return false;
		}

		return true;
	}

	@Override
	public String getName() {
		return "last-ditch";
	}

}
