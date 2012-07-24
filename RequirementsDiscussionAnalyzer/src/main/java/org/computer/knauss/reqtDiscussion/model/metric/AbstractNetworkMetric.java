package org.computer.knauss.reqtDiscussion.model.metric;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.partition.FixedNumberPartition;
import org.computer.knauss.reqtDiscussion.model.partition.IDiscussionOverTimePartition;
import org.computer.knauss.reqtDiscussion.model.socialNetwork.PartitionedSocialNetwork;
import org.computer.knauss.reqtDiscussion.model.socialNetwork.SocialNetwork;

public abstract class AbstractNetworkMetric extends AbstractDiscussionMetric {

	private SocialNetwork socialNetwork;
	private IDiscussionOverTimePartition partition;
	private double minWeight = 0.0;

	public final static AbstractNetworkMetric[] STANDARD_METRICS = new AbstractNetworkMetric[] {
			new NetworkSubgraphCountMetric(),
			new NetworkDegreeCentralityMetric() };

	public AbstractNetworkMetric() {
		// Set a few reasonable defaults:
		this.partition = new FixedNumberPartition();
		this.partition.setPartitionCount(3);

		this.socialNetwork = new PartitionedSocialNetwork();
	}

	/**
	 * The minimal weight of edges that are included in the computation of this
	 * metric.
	 * 
	 * @return The minimal weight of edges that are included in the computation
	 *         of this metric.
	 */
	public double getMinWeight() {
		return minWeight;
	}

	/**
	 * Sets the minimal weight of edges that are included in the computation of
	 * this metric.
	 * 
	 * @param minWeight
	 */
	public void setMinWeight(double minWeight) {
		this.minWeight = minWeight;
	}

	public void setSocialNetwork(SocialNetwork sn) {
		this.socialNetwork = sn;
	}

	public SocialNetwork getSocialNetwork() {
		return this.socialNetwork;
	}

	public IDiscussionOverTimePartition getPartition() {
		return this.partition;
	}

	public void setPartition(IDiscussionOverTimePartition partition) {
		this.partition = partition;
	}

	public void initNetwork(Discussion[] discussions) {
		List<DiscussionEvent> tmp = new LinkedList<DiscussionEvent>();
		for (Discussion d : discussions) {
			Collections.addAll(tmp, d.getDiscussionEvents());
		}
		getPartition().setTimeInterval(discussions);
		getPartition().setDiscussionEvents(tmp.toArray(new DiscussionEvent[0]));
		getSocialNetwork().setDiscussionData(discussions, getPartition());
	}

	@Override
	public void initDiscussions(Discussion[] discussions) {
		initNetwork(discussions);
	}
}
