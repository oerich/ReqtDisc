package org.computer.knauss.reqtDiscussion.model.clarificationPatterns;

import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.ui.visualization.IDiscussionOverTimePartition;

public class PerfectPattern implements IPatternClass {

	private IDiscussionOverTimePartition partition;

	@Override
	public void setCommentPartition(IDiscussionOverTimePartition p) {
		this.partition = p;
	}

	@Override
	public boolean matchesPattern(DiscussionEvent[] comments) {
		this.partition.setDiscussionEvents(comments);

		// should have clarification
		boolean hasEarlyClarification = false;
		// should not have negative level of understanding in the last half.
		boolean negativeInSecondHalf = false;

		int clarifInSecondHalf = 0;
		int otherInSecondHalf = 0;
		int clarificationTime = (int) (0.5 * this.partition.getPartitionCount());
		for (int i = 0; i < this.partition.getPartitionCount(); i++) {
			int inClass = 0;
			int notInClass = 0;
			for (DiscussionEvent wc : this.partition
					.getDiscussionEventForPartition(i)) {
				if (this.partition.isInClass(wc)) {
					inClass++;
					if (i < clarificationTime)
						hasEarlyClarification = true;
					else
						clarifInSecondHalf++;
				} else {
					notInClass++;
					if (i >= clarificationTime)
						otherInSecondHalf++;
				}
			}
			if (notInClass + inClass > 0 && notInClass <= inClass
					&& i > clarificationTime) {
				negativeInSecondHalf = true;
			}
		}
		return hasEarlyClarification && !negativeInSecondHalf
				&& clarifInSecondHalf < otherInSecondHalf;
	}

	@Override
	public String getName() {
		return "textbook-example";
	}

}
