package org.computer.knauss.reqtDiscussion.model.metric;

import static org.junit.Assert.assertEquals;

import java.sql.Date;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.DiscussionFactory;
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
		DiscussionFactory.getInstance().clear();
		Discussion d = DiscussionFactory.getInstance().getDiscussion(1);
		d.setCreator("1");
		d.setCreationDate(new Date(System.currentTimeMillis() - 1000));

		DiscussionEvent d1 = new DiscussionEvent();
		d1.setCreator("1");
		d1.setCreationDate(new Date(System.currentTimeMillis() - 1000));

		DiscussionEvent d2 = new DiscussionEvent();
		d2.setCreator("1");
		d2.setCreationDate(new Date(System.currentTimeMillis()));

		DiscussionEvent d3 = new DiscussionEvent();
		d3.setCreator("2");
		d3.setCreationDate(new Date(System.currentTimeMillis()));

		d.addDiscussionEvents(new DiscussionEvent[] { d1 });
		m.initNetwork(new Discussion[] { d });
		// lets see if the partitions are okay:
		if (d.getCreationDate().getTime() == d1.getCreationDate().getTime()) {
			assertEquals(0,
					m.getPartition().getDiscussionEventForPartition(0).length);
			assertEquals(1,
					m.getPartition().getDiscussionEventForPartition(1).length);
			assertEquals(0,
					m.getPartition().getDiscussionEventForPartition(2).length);
		} else {
			assertEquals(1,
					m.getPartition().getDiscussionEventForPartition(0).length);
			assertEquals(0,
					m.getPartition().getDiscussionEventForPartition(1).length);
			assertEquals(0,
					m.getPartition().getDiscussionEventForPartition(2).length);
		}

		assertEquals(0.0, m.considerDiscussions(new Discussion[] { d }), 0.001);
		d.addDiscussionEvents(new DiscussionEvent[] { d2 });
		m.initNetwork(new Discussion[] { d });
		assertEquals(0.0, m.considerDiscussions(new Discussion[] { d }), 0.001);
		d.addDiscussionEvents(new DiscussionEvent[] { d3 });
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

		d.addDiscussionEvents(new DiscussionEvent[] { d4, d5, d6 });
		m.initNetwork(new Discussion[] { d });
		assertEquals(0.0, m.considerDiscussions(new Discussion[] { d }), 0.01);

		DiscussionEvent d7 = new DiscussionEvent();
		d7.setCreator("1");
		d7.setCreationDate(new Date(System.currentTimeMillis()));

		DiscussionEvent d8 = new DiscussionEvent();
		d8.setCreator("8");
		d8.setCreationDate(new Date(System.currentTimeMillis() - 1000));

		d.addDiscussionEvents(new DiscussionEvent[] { d7, d8 });
		m.initNetwork(new Discussion[] { d });
		assertEquals(0.399, m.considerDiscussions(new Discussion[] { d }), 0.01);
	}

}
