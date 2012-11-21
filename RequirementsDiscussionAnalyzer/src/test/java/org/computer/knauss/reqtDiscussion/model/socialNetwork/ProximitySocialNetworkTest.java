package org.computer.knauss.reqtDiscussion.model.socialNetwork;

import static org.junit.Assert.*;

import java.sql.Date;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.DiscussionFactory;
import org.computer.knauss.reqtDiscussion.model.partition.FixedNumberPartition;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProximitySocialNetworkTest {

	private Discussion discussion;
	private ProximitySocialNetwork sn;

	@Before
	public void setup() {
//		DiscussionFactory.getInstance().clear();
		discussion = DiscussionFactory.getInstance().getDiscussion(1);
		discussion.setCreator("1");
		discussion.setCreationDate(new Date(System.currentTimeMillis() - 1000));

		DiscussionEvent d1 = new DiscussionEvent();
		d1.setCreator("1");
		d1.setCreationDate(new Date(System.currentTimeMillis() - 1000));

		DiscussionEvent d2 = new DiscussionEvent();
		d2.setCreator("1");
		d2.setCreationDate(new Date(System.currentTimeMillis()));

		DiscussionEvent d3 = new DiscussionEvent();
		d3.setCreator("2");
		d3.setCreationDate(new Date(System.currentTimeMillis()));

		discussion.addDiscussionEvents(new DiscussionEvent[] { d1, d2, d3 });

		this.sn = new ProximitySocialNetwork();
		this.sn.setDiscussionData(new Discussion[] { this.discussion },
				new FixedNumberPartition());
	}
	
	@After
	public void tearDown() throws Exception {
		DiscussionFactory.getInstance().clear();
	}

	@Test
	public void testGetWeight() {
		Node[] actors = this.sn.getActors();
		assertEquals(2, actors.length);

		assertEquals(1.6, this.sn.getWeight(actors[0], actors[1]), 0.01);
		assertEquals(1.359, this.sn.getWeight(actors[1], actors[0]), 0.01);

	}

	@Test
	public void testEdgeCutoffWeight() {
		assertEquals(0.0, this.sn.getEdgeCutoffWeight(), 0.01);
		this.sn.setEdgeCutoffWeight(1.4);
		assertEquals(1.4, this.sn.getEdgeCutoffWeight(), 0.01);

		this.sn.setCutoffStrategy(SocialNetwork.MEDIAN_CUTOFF);
		this.sn.setEdgeCutoffWeight(-1);
		assertEquals(1.369, this.sn.getEdgeCutoffWeight(), 0.01);
		
		this.sn.setCutoffStrategy(SocialNetwork.INTEGER_CUTOFF);
		this.sn.setEdgeCutoffWeight(-1);
		assertEquals(2, this.sn.getEdgeCutoffWeight(), 0.01);
		
		this.sn.setCutoffStrategy(SocialNetwork.AVG_CUTOFF);
		this.sn.setEdgeCutoffWeight(-1);
		assertEquals(1.48, this.sn.getEdgeCutoffWeight(), 0.01);
	}

}
