package org.computer.knauss.reqtDiscussion.model.clarificationPatterns;

import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.ui.visualization.ICommentOverTimePartition;

public interface IPatternClass {

	public void setCommentPartition(ICommentOverTimePartition p);
	
	public boolean matchesPattern(DiscussionEvent[] discussionEvents);
	
	public String getName();
}
