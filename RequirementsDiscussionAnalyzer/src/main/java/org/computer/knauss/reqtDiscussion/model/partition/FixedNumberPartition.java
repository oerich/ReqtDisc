package org.computer.knauss.reqtDiscussion.model.partition;

import java.sql.Date;

import org.computer.knauss.reqtDiscussion.model.ModelElement;

public class FixedNumberPartition extends AbstractDiscussionOverTimePartition
		implements IDiscussionOverTimePartition {

	private int partitionCount = 8;
	private Date to;
	private Date from;

	@Override
	public void setTimeInterval(Date from, Date to) {
		this.from = from;
		this.to = to;
	}

	@Override
	public void setPartitionType(int partitionCount) {
	}

	@Override
	public int getPartitionType() {
		return TYPE_FIXED_NUMBER;
	}

	@Override
	public void setPartitionCount(int count) {
		this.partitionCount = count;
		if (this.partitionCount <= 0)
			this.partitionCount = 1;
	}

	@Override
	public int getPartitionCount() {
		return this.partitionCount;
	}

	@Override
	public int getPartitionForModelElement(ModelElement me) {
		// 1. compute the total length of the timeline
		long totalLength = this.to.getTime() - this.from.getTime();

		// Special condition: We have only one date
		if (totalLength <= 0)
			return this.partitionCount / 2;

		// Special condition: the comment is on the last date:
		if (to.equals(me.getCreationDate()))
			return this.partitionCount - 1;

		// Special condition: the comment is outside of the date range.
		if (from.after(me.getCreationDate()))
			return 0;
		if (this.to.before(me.getCreationDate()))
			return getPartitionCount() - 1;

		long thisLength = me.getCreationDate().getTime() - this.from.getTime();
		// System.out.println("x = " + thisLength + "/" + totalLength);

		return (int) (((double) thisLength / (double) totalLength) * this.partitionCount);
	}

}
