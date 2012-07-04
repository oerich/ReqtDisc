package org.computer.knauss.reqtDiscussion.model;

import java.util.List;

public interface IFilteredWorkitemCommentList {
	
	public void setWorkitemCommentList(List<DiscussionEvent> data);
	
	/**
	 * A filtered list of classification items
	 * @return A filtered list of classification items
	 */
	public List<DiscussionEvent> getClassificationItemList();
	
	public DiscussionEvent getWorkitemComment(int i);
	
	public void addFilter(IDiscussionEventFilter filter);
	
	public void removeFilter(IDiscussionEventFilter filter);

	public void clear();

	public Object[] getFilters();
	
	public void add(DiscussionEvent event);
}
