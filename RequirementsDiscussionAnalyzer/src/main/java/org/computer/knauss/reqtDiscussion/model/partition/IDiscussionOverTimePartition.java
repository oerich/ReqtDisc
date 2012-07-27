package org.computer.knauss.reqtDiscussion.model.partition;

import java.sql.Date;

import org.computer.knauss.reqtDiscussion.model.ModelElement;

public interface IDiscussionOverTimePartition {

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

	public void setModelElements(ModelElement[] comments);

	public ModelElement[] getModelElements();

	public int getPartitionForModelElement(ModelElement wc);

	public ModelElement[] getModelElementsForPartition(int partition);

	public boolean isInClass(ModelElement wc);

	public void setTimeInterval(ModelElement[] selectedElements);

}
