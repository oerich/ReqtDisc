package org.computer.knauss.reqtDiscussion.model.metric;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.Incident;

public class ResolutionChangesMetric extends AbstractDiscussionMetric {

	@Override
	public String getName() {
		return "Resolution Changes";
	}

	@Override
	public Double considerDiscussions(Discussion[] wi) {
		double ret = 0.0;
		for (Discussion d : wi) {
			for (Incident inc : d.getIncidents()) {
				if ("resolution".equals(inc.getName()))
					ret++;
			}
		}
		return ret;
	}

	@Override
	public int measurementType() {
		return RATIO_TYPE;
	}


}
