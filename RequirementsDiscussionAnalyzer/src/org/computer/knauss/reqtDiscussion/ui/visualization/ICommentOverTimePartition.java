package org.computer.knauss.reqtDiscussion.ui.visualization;

import java.sql.Date;

import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;

public interface ICommentOverTimePartition {

	public static final int TYPE_PIXEL = -1;
	public static final int TYPE_DAYS = -2;
	public static final int TYPE_WEEKS = -3;
	public static final int TYPE_MONTH = -4;
	public static final int TYPE_FIXED_NUMBER = 0;

	public void setTimeInterval(Date from, Date to);

	public void setPartitionType(int partitionCount);

	public int getPartitionType();

	public void setPartitionCount(int count);

	public int getPartitionCount();

	public void setWorkitemComments(DiscussionEvent[] comments);

	public DiscussionEvent[] getWorkitemComments();

	public int getPartitionForWorkitemComment(DiscussionEvent wc);

	public DiscussionEvent[] getWorkitemsForPartition(int partition);

	public boolean isInClass(DiscussionEvent wc);

}
