package org.computer.knauss.reqtDiscussion.model.metric;

import static org.junit.Assert.assertEquals;

import java.sql.Date;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.DiscussionFactory;
import org.computer.knauss.reqtDiscussion.model.socialNetwork.Node;
import org.computer.knauss.reqtDiscussion.model.socialNetwork.SocialNetwork;
import org.junit.BeforeClass;
import org.junit.Test;

public class NetworkSubgraphCountMetricTest {

	@BeforeClass
	public static void setup() {
		DiscussionFactory.getInstance().clear();
	}

	@Test
	public void testDisconnectedNetwork() {
		// Create a network with six nodes
		double[][] network = new double[6][6];

		// start with no connections
		SocialNetworkDummy sn = new SocialNetworkDummy();
		sn.setWeight(network);

		NetworkSubgraphCountMetric m = new NetworkSubgraphCountMetric();
		m.setSocialNetwork(sn);
		// Bad trick: the social network dummy ignores the discussions anyway. I
		// need to test later, if it correctly initializes social network and
		// time partition.
		assertEquals(6, m.considerDiscussions(new Discussion[0]), 0.01);
	}

	@Test
	public void testConnectedNetwork() {
		// Create a network with six nodes
		double[][] network = new double[6][6];

		// create a path:
		network[0][1] = 1;
		network[1][2] = 1;
		network[2][3] = 1;
		network[3][4] = 1;
		network[4][5] = 1;
		// create a path back:
		network[1][0] = 1;
		network[2][1] = 1;
		network[3][2] = 1;
		network[4][3] = 1;
		network[5][4] = 1;

		SocialNetworkDummy sn = new SocialNetworkDummy();
		sn.setWeight(network);

		NetworkSubgraphCountMetric m = new NetworkSubgraphCountMetric();
		m.setSocialNetwork(sn);
		// Bad trick: the social network dummy ignores the discussions anyway. I
		// need to test later, if it correctly initializes social network and
		// time partition.
		assertEquals(1, m.considerDiscussions(new Discussion[0]), 0.01);
	}

	@Test
	public void testWithRealisticData() {
		AbstractNetworkMetric m = new NetworkSubgraphCountMetric();
		m.initNetwork(new Discussion[0]);
		assertEquals(0.0, m.considerDiscussions(new Discussion[0]), 0.001);

		Discussion d = DiscussionFactory.getInstance().getDiscussion(1);
		d.setCreator("1");
		d.setDateCreated(new Date(System.currentTimeMillis() - 1000));

		DiscussionEvent d1 = new DiscussionEvent();
		d1.setCreator("1");
		d1.setCreationDate(new Date(System.currentTimeMillis() - 999));

		DiscussionEvent d2 = new DiscussionEvent();
		d2.setCreator("1");
		d2.setCreationDate(new Date(System.currentTimeMillis()));

		DiscussionEvent d3 = new DiscussionEvent();
		d3.setCreator("2");
		d3.setCreationDate(new Date(System.currentTimeMillis()));

		d.addComments(new DiscussionEvent[] { d1 });
		m.initNetwork(new Discussion[] { d });
		assertEquals(1.0, m.considerDiscussions(new Discussion[] { d }), 0.001);
		// lets see if the partitions are okay:
		if (d.getDateCreated().getTime() == d1.getCreationDate().getTime()) {
			assertEquals(1,
					m.getPartition().getDiscussionEventForPartition(1).length);
			assertEquals(0,
					m.getPartition().getDiscussionEventForPartition(2).length);
			assertEquals(0,
					m.getPartition().getDiscussionEventForPartition(3).length);
		} else {
			assertEquals(0,
					m.getPartition().getDiscussionEventForPartition(1).length);
			assertEquals(1,
					m.getPartition().getDiscussionEventForPartition(2).length);
			assertEquals(0,
					m.getPartition().getDiscussionEventForPartition(3).length);
		}

		d.addComments(new DiscussionEvent[] { d3 });
		m.initNetwork(new Discussion[] { d });
		assertEquals(2.0, m.considerDiscussions(new Discussion[] { d }), 0.001);
		d.addComments(new DiscussionEvent[] { d2 });
		m.initNetwork(new Discussion[] { d });
		assertEquals(1.0, m.considerDiscussions(new Discussion[] { d }), 0.001);

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
		assertEquals(1.0, m.considerDiscussions(new Discussion[] { d }), 0.01);

		DiscussionEvent d7 = new DiscussionEvent();
		d7.setCreator("1");
		d7.setCreationDate(new Date(System.currentTimeMillis()));

		DiscussionEvent d8 = new DiscussionEvent();
		d8.setCreator("8");
		d8.setCreationDate(new Date(System.currentTimeMillis() - 1000));

		d.addComments(new DiscussionEvent[] { d7, d8 });
		m.initNetwork(new Discussion[] { d });
		assertEquals(1.0, m.considerDiscussions(new Discussion[] { d }), 0.01);
	}

	class SocialNetworkDummy extends SocialNetwork {

		private double[][] weightMatrix;

		@Override
		public double getWeight(Node actor1, Node actor2) {
			return this.weightMatrix[Integer.valueOf(actor1.getLabel())][Integer
					.valueOf(actor2.getLabel())];
		}

		public void setWeight(double[][] weightMatrix) {
			this.weightMatrix = weightMatrix;
		}

		@Override
		public Node[] getActors() {
			Node[] ret = new Node[this.weightMatrix.length];
			for (int i = 0; i < ret.length; i++) {
				ret[i] = new Node();
				ret[i].setLabel(String.valueOf(i));
			}

			return ret;
		}

	}

}
