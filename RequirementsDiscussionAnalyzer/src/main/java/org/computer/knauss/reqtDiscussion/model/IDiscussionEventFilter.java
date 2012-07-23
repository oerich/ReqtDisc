package org.computer.knauss.reqtDiscussion.model;


public interface IDiscussionEventFilter {

	boolean accept(DiscussionEvent de);
}
