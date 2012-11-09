package org.computer.knauss.reqtDiscussion.model.socialNetwork;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.partition.IDiscussionOverTimePartition;

public abstract class SocialNetwork {

	private IDiscussionOverTimePartition partition;
	private Discussion[] discussions;
	private Map<String, Node> nodes = new HashMap<String, Node>();
	private double edgeCutoffWeight;
	private AutomaticCutoffStrategy cutoff = INTEGER_CUTOFF;

	public abstract double getWeight(Node actor1, Node actor2);

	public Node[] getActors() {
		return nodes.values().toArray(new Node[0]);
	}

	public void setDiscussionData(Discussion[] discussions,
			IDiscussionOverTimePartition p) {
		// System.out.println(getClass().getSimpleName() + ": " + p);
		this.partition = p;
		this.discussions = discussions;

		this.nodes.clear();
		for (Discussion d : this.discussions)
			for (DiscussionEvent de : d.getDiscussionEvents()) {
				Node n = this.nodes.get(de.getCreator());
				if (n == null) {
					n = new Node();
					n.setLabel(de.getCreator());
					this.nodes.put(de.getCreator(), n);
				}
				if (partition.isInClass(de))
					n.setClarification(n.getClarification() + 1);
				else
					n.setCoordination(n.getCoordination() + 1);
			}
	}

	public IDiscussionOverTimePartition getDiscussionOverTimePartition() {
		return this.partition;
	}

	public Discussion[] getDiscussions() {
		return this.discussions;
	}

	public String toString() {
		return getClass().getSimpleName();
	}

	public double getEdgeCutoffWeight() {
		if (this.edgeCutoffWeight < 0) {
			// automatically determine a good cutoff
			List<Double> weights = new LinkedList<Double>();
			for (Node n1 : getActors())
				for (Node n2 : getActors()) {
					double w = getWeight(n1, n2);
					if (w > 0)
						weights.add(w);
				}
			return this.cutoff.getCutoff(weights);
		}
		return this.edgeCutoffWeight;
	}

	public void setEdgeCutoffWeight(double w) {
		this.edgeCutoffWeight = w;
	}

	public void setCutoffStrategy(AutomaticCutoffStrategy strategy) {
		this.cutoff = strategy;
	}

	private interface AutomaticCutoffStrategy {

		double getCutoff(List<Double> weights);

	}

	public static final AutomaticCutoffStrategy MEDIAN_CUTOFF = new AutomaticCutoffStrategy() {

		@Override
		public double getCutoff(List<Double> weights) {
			if (weights.size() <= 1)
				return 0;
			Collections.sort(weights);
			return weights.get(weights.size() / 2 - 1) + 0.01;
		}

	};

	public static final AutomaticCutoffStrategy INTEGER_CUTOFF = new AutomaticCutoffStrategy() {

		@Override
		public double getCutoff(List<Double> weights) {
			if (weights.size() <= 1)
				return 0;
			Collections.sort(weights);
			int i = 0;
			double median = weights.get(weights.size() / 2 - 1);
			while (median >= i)
				i++;
			return i;
		}

	};
	public static final AutomaticCutoffStrategy AVG_CUTOFF = new AutomaticCutoffStrategy() {

		@Override
		public double getCutoff(List<Double> weights) {
			if (weights.size() == 0)
				return 0;
			double sum = 0;
			for (double d : weights)
				sum += d;

			return sum / weights.size();
		}

	};

}
