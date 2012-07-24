package org.computer.knauss.reqtDiscussion.model;

import org.computer.knauss.reqtDiscussion.model.partition.IDiscussionOverTimePartition;
import org.computer.knauss.reqtDiscussion.model.partition.TimeIntervalPartition;
import org.computer.knauss.reqtDiscussion.model.socialNetwork.SocialNetwork;

/**
 * This class supports to set a common configuration throughout the application.
 * 
 * @author eknauss
 * 
 */
public class VisualizationConfiguration {

	private IDiscussionOverTimePartition partition = new TimeIntervalPartition();
	private SocialNetwork socialNetwork;

	public IDiscussionOverTimePartition getDiscussionPartition() {
		return partition;
	}

	public void setPartition(IDiscussionOverTimePartition partition) {
		this.partition = partition;
	}

	public SocialNetwork getSocialNetwork() {
		return socialNetwork;
	}

	public void setSocialNetwork(SocialNetwork socialNetwork) {
		this.socialNetwork = socialNetwork;
	}

}
