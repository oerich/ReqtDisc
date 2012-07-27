package org.computer.knauss.reqtDiscussion.model.partition;

import java.sql.Date;

import org.computer.knauss.reqtDiscussion.model.ModelElement;

public class PixelPartition extends AbstractDiscussionOverTimePartition
		implements IDiscussionOverTimePartition {

	private Date to;
	private Date from;
	private int pixels = 100;

	@Override
	public void setTimeInterval(Date from, Date to) {
		this.from = from;
		this.to = to;
	}

	@Override
	public void setPartitionType(int partitionCount) {
		// not supported.
	}

	@Override
	public int getPartitionType() {
		return TYPE_PIXEL;
	}

	@Override
	public int getPartitionForModelElement(ModelElement me) {
		// 1. compute the total length of the timeline
		long totalLength = this.to.getTime() - this.from.getTime();

		// if there is only one comment...
		if (totalLength <= 0)
			return pixels / 2;

		long thisLength = me.getCreationDate().getTime() - this.from.getTime();
		// System.out.println("x = " + thisLength + "/" + totalLength);

		return (int) (((double) thisLength / (double) totalLength) * this.pixels);
	}

	@Override
	public void setPartitionCount(int count) {
		this.pixels = count;
	}

	@Override
	public int getPartitionCount() {
		return this.pixels;
	}

}
