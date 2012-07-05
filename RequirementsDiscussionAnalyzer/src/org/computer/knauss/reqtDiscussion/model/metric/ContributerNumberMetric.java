package org.computer.knauss.reqtDiscussion.model.metric;

import java.util.HashSet;
import java.util.Set;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;

public class ContributerNumberMetric extends AbstractDiscussionMetric {

	@Override
	public String getName() {
		return "#contributors";
	}

	@Override
	public Double considerDiscussions(Discussion[] wis) {
		if (wis == null)
			return 0.0;
		Set<String> contributors = new HashSet<String>();
		for (Discussion wi : wis) {
			contributors.add(wi.getCreator());
			for (DiscussionEvent wc : wi.getAllComments()) {
				contributors.add(wc.getCreator());
			}
		}
		return (double) contributors.size();
	}
}
