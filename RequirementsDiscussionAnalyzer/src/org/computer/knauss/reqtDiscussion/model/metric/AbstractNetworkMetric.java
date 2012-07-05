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

	public AbstractNetworkMetric() {
		// Set a few reasonable defaults:
		this.partition = new FixedNumberPartition();
		this.partition.setPartitionCount(3);

		this.socialNetwork = new PartitionedSocialNetwork();
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

	protected void initNetwork(Discussion[] wi) {
		// TODO Check how to deal with multiple discussions
		List<DiscussionEvent> tmp = new LinkedList<DiscussionEvent>();
		for (Discussion d : wi) {
			Collections.addAll(tmp, d.getAllComments());
		}
		getPartition().setTimeInterval(wi);
		getPartition().setDiscussionEvents(tmp.toArray(new DiscussionEvent[0]));
		getSocialNetwork().setDiscussionData(wi, getPartition());
	}
}
