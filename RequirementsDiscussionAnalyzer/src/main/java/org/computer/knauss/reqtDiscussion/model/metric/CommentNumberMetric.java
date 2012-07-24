package org.computer.knauss.reqtDiscussion.model.metric;

import org.computer.knauss.reqtDiscussion.model.Discussion;

public class CommentNumberMetric extends AbstractDiscussionMetric {

	@Override
	public String getName() {
		return "#comments";
	}

	@Override
	public Double considerDiscussions(Discussion[] wis) {
		if (wis == null)
			return 0.0;
		int length = 0;
		for (Discussion wi : wis) {
			length += wi.getDiscussionEvents().length;
		}
		return (double) length;
	}

}
