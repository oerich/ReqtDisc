package org.computer.knauss.reqtDiscussion.model.clarificationPatterns;

import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.ui.visualization.ICommentOverTimePartition;

public class HappyEndingPattern implements IPatternClass {

	private ICommentOverTimePartition partition;

	@Override
	public void setCommentPartition(ICommentOverTimePartition p) {
		this.partition = p;
	}

	@Override
	public boolean matchesPattern(DiscussionEvent[] comments) {
		this.partition.setWorkitemComments(comments);

		boolean backToDraft = false;
		boolean happyEnding = false;

		int secondHalf = this.partition.getPartitionCount() / 2;

		for (int i = 0; i < this.partition.getPartitionCount(); i++) {
			int inClass = 0;
			int notInClass = 0;
			for (DiscussionEvent wc : this.partition
					.getWorkitemsForPartition(i)) {
				if (this.partition.isInClass(wc))
					inClass++;
				else
					notInClass++;
			}
			if (notInClass > inClass) {
				if (backToDraft) {
					backToDraft = false;
					happyEnding = true;
				}
			} else {
				if (inClass >= notInClass && inClass + notInClass > 0) {
					if (i > secondHalf)
						backToDraft = true;
					happyEnding = false;
				}
			}
		}
		return happyEnding;
	}

	@Override
	public String getName() {
		return "happy-ending?";
	}

}
