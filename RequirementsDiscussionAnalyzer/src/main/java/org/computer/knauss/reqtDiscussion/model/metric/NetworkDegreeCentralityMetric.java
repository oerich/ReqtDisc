package org.computer.knauss.reqtDiscussion.model.metric;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.socialNetwork.Node;

public class NetworkDegreeCentralityMetric extends AbstractNetworkMetric {

	@Override
	public String getName() {
		return "Network Centrality";
	}

	@Override
	public Double considerDiscussions(Discussion[] d) {
		if (d.length == 0)
			return 0.0;

		// initNetwork(wi);

		// compute centrality of each node
		Node[] nodes = getSocialNetwork().getActors();
		if (nodes.length <= 2)
			return 0.0;

		// compute network centrality and remember the max
		double[] centrality = new double[nodes.length];
		double maxCentrality = 0;
		for (int i = 0; i < nodes.length; i++) {
			centrality[i] = degreeCentrality(nodes[i]);
			if (centrality[i] > maxCentrality)
				maxCentrality = centrality[i];
		}

		double ret = 0;
		for (int i = 0; i < centrality.length; i++) {
			ret += maxCentrality - centrality[i];
		}
		return ret / ((nodes.length - 1) * (nodes.length - 2));
	}

	double degreeCentrality(Node node) {
		Node[] nodes = getSocialNetwork().getActors();

		if (nodes.length < 2)
			return 0.0;
		double ret =0;
		for (int j = 0; j < nodes.length; j++) {
			double weight = getSocialNetwork().getWeight(node, nodes[j]);
					
			if (weight > getMinWeight())
				ret++;
		}
		return ret;
	}

	@Override
	public int measurementType() {
		// TODO I have no idea...
		return ORDINAL_TYPE;
	}

}
