package org.computer.knauss.reqtDiscussion.model.clarificationPatterns;

import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.ui.visualization.IDiscussionOverTimePartition;

public class NoSharedUnderstandingPattern implements IPatternClass {

	private IDiscussionOverTimePartition partition;

	@Override
	public void setCommentPartition(IDiscussionOverTimePartition p) {
		this.partition = p;
	}

	@Override
	public boolean matchesPattern(DiscussionEvent[] comments) {
		if (comments.length == 0)
			return false;
		this.partition.setDiscussionEvents(comments);

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
			if (notInClass > inClass)
				return false;
		}
		return true;
	}

	@Override
	public String getName() {
		return "discordant";
	}

}
