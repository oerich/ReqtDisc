package org.computer.knauss.reqtDiscussion.model.metric;

import static org.junit.Assert.assertEquals;

import java.sql.Date;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.junit.Test;

public class NetworkDegreeCentralityMetricTest {

	@Test
	public void testGetName() {
		AbstractNetworkMetric m = new NetworkDegreeCentralityMetric();
		assertEquals("Network Centrality", m.getName());

		assertEquals(3, m.getPartition().getPartitionCount());
	}

	@Test
	public void testConsiderDiscussions() {
		AbstractNetworkMetric m = new NetworkDegreeCentralityMetric();
		m.initNetwork(new Discussion[0]);
		assertEquals(0.0, m.considerDiscussions(new Discussion[0]), 0.001);

		Discussion d = new Discussion();
		d.setCreator("1");
		d.setDateCreated(new Date(System.currentTimeMillis() - 1000));

		DiscussionEvent d1 = new DiscussionEvent();
		d1.setCreator("1");
		d1.setCreationDate(new Date(System.currentTimeMillis() - 1000));

		DiscussionEvent d2 = new DiscussionEvent();
		d2.setCreator("1");
		d2.setCreationDate(new Date(System.currentTimeMillis()));

		DiscussionEvent d3 = new DiscussionEvent();
		d3.setCreator("2");
		d3.setCreationDate(new Date(System.currentTimeMillis()));

		d.addComments(new DiscussionEvent[] { d1 });
		m.initNetwork(new Discussion[] { d });
		// lets see if the partitions are okay:
		assertEquals(1,
				m.getPartition().getDiscussionEventForPartition(1).length);
		assertEquals(0,
				m.getPartition().getDiscussionEventForPartition(2).length);
		assertEquals(0,
				m.getPartition().getDiscussionEventForPartition(3).length);

		assertEquals(0.0, m.considerDiscussions(new Discussion[] { d }), 0.001);
		d.addComments(new DiscussionEvent[] { d2 });
		m.initNetwork(new Discussion[] { d });
		assertEquals(0.0, m.considerDiscussions(new Discussion[] { d }), 0.001);
		d.addComments(new DiscussionEvent[] { d3 });
		m.initNetwork(new Discussion[] { d });
		assertEquals(0.0, m.considerDiscussions(new Discussion[] { d }), 0.001);

		DiscussionEvent d4 = new DiscussionEvent();
		d4.setCreator("3");
		d4.setCreationDate(new Date(System.currentTimeMillis()));

		DiscussionEvent d5 = new DiscussionEvent();
		d5.setCreator("4");
		d5.setCreationDate(new Date(System.currentTimeMillis()));

		DiscussionEvent d6 = new DiscussionEvent();
		d6.setCreator("5");
		d6.setCreationDate(new Date(System.currentTimeMillis()));

		d.addComments(new DiscussionEvent[] { d4, d5, d6 });
		m.initNetwork(new Discussion[] { d });
		assertEquals(0.33, m.considerDiscussions(new Discussion[] { d }), 0.01);

		DiscussionEvent d7 = new DiscussionEvent();
		d7.setCreator("1");
		d7.setCreationDate(new Date(System.currentTimeMillis()));

		DiscussionEvent d8 = new DiscussionEvent();
		d8.setCreator("8");
		d8.setCreationDate(new Date(System.currentTimeMillis() - 1000));

		d.addComments(new DiscussionEvent[] { d7, d8 });
		m.initNetwork(new Discussion[] { d });
		assertEquals(0.649, m.considerDiscussions(new Discussion[] { d }), 0.01);
	}

}
