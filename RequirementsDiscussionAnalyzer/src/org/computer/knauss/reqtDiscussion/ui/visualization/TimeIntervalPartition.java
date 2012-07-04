package org.computer.knauss.reqtDiscussion.ui.visualization;

import java.sql.Date;

import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;

public class TimeIntervalPartition extends AbstractDiscussionOverTimePartition
		implements IDiscussionOverTimePartition {

	private static final long MILLIS_PER_YEAR = 31557600000l;
	private static final long MILLIS_PER_WEEK = 604800000l;
	public static final long MILLIS_PER_DAY = 86400000l;
	private Date to;
	private Date from;
	private Date minFrom;
	private Date maxTo;
	private FixedNumberPartition delegate = new FixedNumberPartition();
	private int partitionType;

	@Override
	public void setTimeInterval(Date from, Date to) {
		this.from = from;
		this.to = to;

		// We need to compute the maximum time on the fly.
		if (this.minFrom == null || this.from.before(this.minFrom))
			this.minFrom = this.from;
		if (this.maxTo == null || this.to.after(this.maxTo))
			this.maxTo = this.to;

		// Perhaps the user wants to show a fixed number of partitions.
		if (getPartitionType() == TYPE_PIXEL
				|| getPartitionType() == TYPE_FIXED_NUMBER) {
			this.delegate.setTimeInterval(from, to);
			return;
		}

		// otherwise we can start to use our delegate in an innovative manner
		this.delegate.setTimeInterval(this.minFrom, this.maxTo);

		// now - based on the type of this partition - compute the maximum
		// number of days, weeks, months, quarters, years.
		int partitions = 1;
		if (getPartitionType() == TYPE_DAYS)
			partitions = (int) ((this.maxTo.getTime() - this.minFrom.getTime()) / MILLIS_PER_DAY);
		else if (getPartitionType() == TYPE_WEEKS)
			partitions = (int) ((this.maxTo.getTime() - this.minFrom.getTime()) / MILLIS_PER_WEEK);
		else {
			// per year:
			double d = ((this.maxTo.getTime() - this.minFrom.getTime()) / MILLIS_PER_YEAR);
			if (getPartitionType() == TYPE_MONTH)
				partitions = (int) d * 12;
		}
		this.delegate.setPartitionCount(partitions);
	}

	@Override
	public void setPartitionType(int partitionCount) {
		this.partitionType = partitionCount;
	}

	@Override
	public int getPartitionType() {
		return this.partitionType;
	}

	@Override
	public void setPartitionCount(int count) {
		this.delegate.setPartitionCount(count);
	}

	@Override
	public int getPartitionCount() {
		return this.delegate.getPartitionCount();
	}

	@Override
	public int getPartitionForDiscussionEvent(DiscussionEvent wc) {
		return this.delegate.getPartitionForDiscussionEvent(wc);
	}

}
