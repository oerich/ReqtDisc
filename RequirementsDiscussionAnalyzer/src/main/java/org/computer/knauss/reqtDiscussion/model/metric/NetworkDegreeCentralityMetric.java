package org.computer.knauss.reqtDiscussion.model.metric;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.socialNetwork.Node;

public class NetworkDegreeCentralityMetric extends AbstractNetworkMetric {

	@Override
	public String getName() {
		return "Network Centrality";
	}

	@Override
	public Double considerDiscussions(Discussion[] wi) {
		if (wi.length == 0)
			return 0.0;

		// initNetwork(wi);

		// compute centrality of each node
		Node[] nodes = getSocialNetwork().getActors();
		if (nodes.length <= 2)
			return 0.0;

		double[] centrality = new double[nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			Node node = nodes[i];
			double nodeCentrality = degreeCentrality(node);
			centrality[i] = nodeCentrality;
		}

		// compute network centrality
		double maxCentrality = 0;
		for (double c : centrality)
			if (c > maxCentrality)
				maxCentrality = c;

		double ret = 0;
		for (int i = 0; i < centrality.length; i++) {
			ret += maxCentrality - centrality[i];
		}
		return ret / (nodes.length - 2);
	}

	double degreeCentrality(Node node) {
		Node[] nodes = getSocialNetwork().getActors();

		if (nodes.length < 2)
			return 0.0;
		double nodeWeight = 0;
		for (int j = 0; j < nodes.length; j++) {
			double weight = getSocialNetwork().getWeight(node, nodes[j]);
			if (weight > getMinWeight())
				nodeWeight += weight;
		}
		double nodeCentrality = nodeWeight / (nodes.length - 1);
		return nodeCentrality;
	}

	@Override
	public int measurementType() {
		// TODO I have no idea...
		return ORDINAL_TYPE;
	}

}
