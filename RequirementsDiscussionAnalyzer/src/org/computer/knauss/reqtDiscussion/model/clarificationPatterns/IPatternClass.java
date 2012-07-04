package org.computer.knauss.reqtDiscussion.model.clarificationPatterns;

import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.ui.visualization.IDiscussionOverTimePartition;

public interface IPatternClass {

	public void setCommentPartition(IDiscussionOverTimePartition p);
	
	public boolean matchesPattern(DiscussionEvent[] discussionEvents);
	
	public String getName();
}
