package org.computer.knauss.reqtDiscussion.model;

import static org.junit.Assert.*;

import org.computer.knauss.reqtDiscussion.model.partition.FixedNumberPartition;
import org.computer.knauss.reqtDiscussion.model.partition.IDiscussionOverTimePartition;
import org.computer.knauss.reqtDiscussion.model.socialNetwork.PartitionedSocialNetwork;
import org.junit.Test;

public class VisualizationConfigurationTest {

	@Test
	public void test() {
		VisualizationConfiguration vc = new VisualizationConfiguration();
		
		assertNotNull(vc.getDiscussionPartition());
		assertEquals(8, vc.getDiscussionPartition().getPartitionCount());
		
		IDiscussionOverTimePartition partition = new FixedNumberPartition();
		vc.setPartition(partition);
		assertEquals(partition, vc.getDiscussionPartition());
		
		PartitionedSocialNetwork partSocialNetwork = new PartitionedSocialNetwork();
		vc.setSocialNetwork(partSocialNetwork);
		assertEquals(partSocialNetwork, vc.getSocialNetwork());
	}

}
