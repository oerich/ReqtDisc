package org.computer.knauss.reqtDiscussion.model.clarificationPatterns;

import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.ModelElement;
import org.computer.knauss.reqtDiscussion.model.partition.IDiscussionOverTimePartition;

public class SystematicPattern implements IPatternClass {

	private String patternNo;
	private IDiscussionOverTimePartition partition;

	@Override
	public void setCommentPartition(IDiscussionOverTimePartition p) {
		this.partition = p;
	}

	@Override
	public boolean matchesPattern(DiscussionEvent[] discussionEvents) {
		this.patternNo = "";
		this.partition.setModelElements(discussionEvents);

		int val = this.partition.getPartitionCount();
		this.partition.setPartitionCount(4);

		for (int i = 0; i < this.partition.getPartitionCount(); i++) {
			if (i >0)
				patternNo += "\t";
			int inClass = 0, notInClass = 0;

			for (ModelElement me : this.partition
					.getModelElementsForPartition(i)) {
				if (this.partition.isInClass(me))
					inClass++;
				else
					notInClass++;
			}

			if (inClass + notInClass == 0)
				patternNo += "?";
			else if (inClass >= notInClass)
				patternNo += "0";
			else
				patternNo += "1";
		}

		this.partition.setPartitionCount(val);
		return true;
	}

	@Override
	public String getName() {
		return patternNo;
	}

}
