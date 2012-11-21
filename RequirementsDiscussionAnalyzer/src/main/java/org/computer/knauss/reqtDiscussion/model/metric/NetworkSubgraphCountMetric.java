package org.computer.knauss.reqtDiscussion.model.metric;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.socialNetwork.Node;

public class NetworkSubgraphCountMetric extends AbstractNetworkMetric {

	@Override
	public String getName() {
		return "Subgraph count";
	}

	@Override
	public Double considerDiscussions(Discussion[] wi) {
		// initNetwork(wi);

		Node[] nodes = getSocialNetwork().getActors();
		Set<Node> notVisited = new TreeSet<Node>(Arrays.asList(nodes));
		List<Set<Node>> subGraphs = new LinkedList<Set<Node>>();

		while (!notVisited.isEmpty()) {
			Set<Node> subgraph = new TreeSet<Node>();
			subGraphs.add(subgraph);
			// find a node that is not visited
			for (Node n : nodes) {
				if (notVisited.contains(n)) {
					subgraph.add(n);
					notVisited.remove(n);
					break;
				}

			}

			// remove all reachable nodes from set and add them to result list
			boolean foundNew = true;
			while (foundNew) {
				foundNew = false;
				List<Node> tmp = new LinkedList<Node>();
				for (Node n1 : subgraph) {
					for (Node n2 : notVisited) {
						if (getSocialNetwork().getWeight(n1, n2)
								+ getSocialNetwork().getWeight(n2, n1) > getMinWeight()) {
							tmp.add(n2);
						}
					}
				}
				if (tmp.size() > 0) {
					foundNew = true;
					subgraph.addAll(tmp);
					notVisited.removeAll(tmp);
				}
			}
		}

		return (double) subGraphs.size();
	}

	@Override
	public int measurementType() {
		return ORDINAL_TYPE;
	}

}
