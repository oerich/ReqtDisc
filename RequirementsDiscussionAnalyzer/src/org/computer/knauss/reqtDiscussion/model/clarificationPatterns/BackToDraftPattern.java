package org.computer.knauss.reqtDiscussion.model.clarificationPatterns;

import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.ui.visualization.IDiscussionOverTimePartition;

public class BackToDraftPattern implements IPatternClass {

	private IDiscussionOverTimePartition partition;

	@Override
	public void setCommentPartition(IDiscussionOverTimePartition p) {
		this.partition = p;
	}

	@Override
	public boolean matchesPattern(DiscussionEvent[] comments) {
		this.partition.setDiscussionEvents(comments);

		boolean wasGoodLevelOfUnderstanding = false;
		boolean backToDraft = false;

		for (int i = 0; i < this.partition.getPartitionCount(); i++) {
			int inClass = 0;
			int notInClass = 0;
			for (DiscussionEvent wc : this.partition
					.getDiscussionEventForPartition(i)) {
				if (this.partition.isInClass(wc))
					inClass++;
				else
					notInClass++;
			}
			if (notInClass > inClass && inClass + notInClass > 0) {
				wasGoodLevelOfUnderstanding = true;
				if (backToDraft) {
					backToDraft = false;
				}
			} else {
				if (wasGoodLevelOfUnderstanding && inClass >= notInClass && inClass + notInClass > 0) {
					backToDraft = true;
				}
			}
		}
		return backToDraft;
	}

	@Override
	public String getName() {
		return "back-to-draft";
	}

}
