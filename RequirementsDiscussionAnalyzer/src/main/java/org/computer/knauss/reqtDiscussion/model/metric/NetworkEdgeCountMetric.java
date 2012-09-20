package org.computer.knauss.reqtDiscussion.model.metric;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.socialNetwork.Node;

public class NetworkEdgeCountMetric extends AbstractNetworkMetric {

	@Override
	public String getName() {
		return "#Edges";
	}

	@Override
	public Double considerDiscussions(Discussion[] wi) {
		Node[] nodes = getSocialNetwork().getActors();

		double result = 0.0;
		for (Node n1 : nodes)
			for (Node n2 : nodes)
				if (getSocialNetwork().getWeight(n1, n2) > 0)
					result++;

		return result;
	}

	@Override
	public int measurementType() {
		return RATIO_TYPE;
	}

}
