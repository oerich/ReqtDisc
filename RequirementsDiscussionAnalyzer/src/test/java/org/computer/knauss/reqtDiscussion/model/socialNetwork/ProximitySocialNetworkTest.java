package org.computer.knauss.reqtDiscussion.model.socialNetwork;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Date;

import org.computer.knauss.reqtDiscussion.Util;
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
		// DiscussionFactory.getInstance().clear();
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

	// /////////////////////////////////
	// The example
	//
	// Post number User name Post date
	// 1. User A 12-06-2010 5:10 PM
	// 2. User B 13-06-2010 6:29 PM
	// 3. User C 16-06-2010 8:02 PM
	// 4. User A 17-06-2010 8:34 PM
	// 5. User A 17-06-2010 9:12 AM
	// 6. User D 30-06-2010 3:54 PM
	// 7. User C 02-07-2010 4:23 PM
	// 8. User D 04-07-2010 6:12 PM
	// 9. User A 13-07-2010 7:43 PM
	// 10. User E 21-09-2010 4:12 PM
	// 11. User A 22-09-2010 2:12 PM
	// ////////////////////////////////
	private DiscussionEvent[] createExample() {
		DiscussionEvent des[] = new DiscussionEvent[11];
		des[0] = new DiscussionEvent();
		des[0].setCreator("User A");
		des[0].setCreationDate(Util.parseDate("2010-06-12T17:10:00.000"));
		des[1] = new DiscussionEvent();
		des[1].setCreator("User B");
		des[1].setCreationDate(Util.parseDate("2010-06-13T18:29:00.000"));
		des[2] = new DiscussionEvent();
		des[2].setCreator("User C");
		des[2].setCreationDate(Util.parseDate("2010-06-16T20:02:00.000"));
		des[3] = new DiscussionEvent();
		des[3].setCreator("User A");
		des[3].setCreationDate(Util.parseDate("2010-06-17T20:34:00.000"));
		des[4] = new DiscussionEvent();
		des[4].setCreator("User A");
		// XXX Is this 'am' on purpose?
		des[4].setCreationDate(Util.parseDate("2010-06-17T9:12:00.000"));
		des[5] = new DiscussionEvent();
		des[5].setCreator("User D");
		des[5].setCreationDate(Util.parseDate("2010-06-30T15:54:00.000"));
		des[6] = new DiscussionEvent();
		des[6].setCreator("User C");
		des[6].setCreationDate(Util.parseDate("2010-07-02T16:23:00.000"));
		des[7] = new DiscussionEvent();
		des[7].setCreator("User D");
		des[7].setCreationDate(Util.parseDate("2010-07-04T18:12:00.000"));
		des[8] = new DiscussionEvent();
		des[8].setCreator("User A");
		des[8].setCreationDate(Util.parseDate("2010-07-13T19:43:00.000"));
		des[9] = new DiscussionEvent();
		des[9].setCreator("User E");
		des[9].setCreationDate(Util.parseDate("2010-09-21T16:12:00.000"));
		des[10] = new DiscussionEvent();
		des[10].setCreator("User A");
		des[10].setCreationDate(Util.parseDate("2010-09-22T14:12:00.000"));
		return des;
	}

	@Test
	public void testExampleNetwork() {
		// Example from Gabor's Masters Thesis
		Discussion d = DiscussionFactory.getInstance().getDiscussion(-5);
		DiscussionEvent[] des = createExample();

		d.addDiscussionEvents(des);
		this.sn.setDiscussionData(new Discussion[] { d }, null);

		Node[] nodes = this.sn.getActors();
		assertEquals(5, nodes.length);
		Node userA = getNodeForName(nodes, "User A");
		Node userB = getNodeForName(nodes, "User B");
		Node userC = getNodeForName(nodes, "User C");
		Node userD = getNodeForName(nodes, "User D");
		Node userE = getNodeForName(nodes, "User E");
		assertNotNull(userA);
		assertNotNull(userB);
		assertNotNull(userC);
		assertNotNull(userD);
		assertNotNull(userE);

		// First, there are unrelated users in the network!
		assertTrue(this.sn.getWeight(userB, userC) == 0);
		assertTrue(this.sn.getWeight(userB, userD) == 0);
		assertTrue(this.sn.getWeight(userB, userE) == 0);
		assertTrue(this.sn.getWeight(userC, userE) == 0);
		assertTrue(this.sn.getWeight(userD, userE) == 0);
	}

	@Test
	public void testPrinciple1() {
		// Example from Gabor's Masters Thesis
		Discussion d = DiscussionFactory.getInstance().getDiscussion(-5);
		DiscussionEvent[] des = createExample();

		d.addDiscussionEvents(des);
		this.sn.setDiscussionData(new Discussion[] { d }, null);

		Node[] nodes = this.sn.getActors();
		Node userA = getNodeForName(nodes, "User A");
		Node userB = getNodeForName(nodes, "User B");
		Node userC = getNodeForName(nodes, "User C");
		Node userD = getNodeForName(nodes, "User D");
		Node userE = getNodeForName(nodes, "User E");

		// Principle 1: Sequence User A, User B results in edge User B-> User A
		assertTrue(this.sn.getWeight(userB, userA) > 0);
		assertTrue(this.sn.getWeight(userC, userA) > 0);
		assertTrue(this.sn.getWeight(userD, userA) > 0);
		assertTrue(this.sn.getWeight(userE, userA) > 0);

		assertTrue(this.sn.getWeight(userA, userB) > 0);
		assertTrue(this.sn.getWeight(userC, userB) > 0);
		assertTrue(this.sn.getWeight(userD, userB) > 0);
		assertTrue(this.sn.getWeight(userE, userB) > 0);

		assertTrue(this.sn.getWeight(userA, userC) > 0);
		assertTrue(this.sn.getWeight(userD, userC) > 0);
		assertTrue(this.sn.getWeight(userE, userC) > 0);

		assertTrue(this.sn.getWeight(userA, userD) > 0);
		assertTrue(this.sn.getWeight(userC, userD) > 0);
		assertTrue(this.sn.getWeight(userE, userD) > 0);

		assertTrue(this.sn.getWeight(userA, userE) > 0);
	}

	@Test
	public void testPrinciple2() {
		Discussion d = DiscussionFactory.getInstance().getDiscussion(-4);

		DiscussionEvent[] example = createExample();
		DiscussionEvent des[] = new DiscussionEvent[6];
		des[0] = example[0];
		des[1] = example[1];
		des[2] = example[2];
		des[3] = example[3];
		des[4] = example[4];
		des[5] = example[5];

		d.addDiscussionEvents(des);

		this.sn.setDiscussionData(new Discussion[] { d }, null);
		Node[] nodes = this.sn.getActors();

		Node userA = getNodeForName(nodes, "User A");
		Node userB = getNodeForName(nodes, "User B");
		Node userC = getNodeForName(nodes, "User C");
		Node userD = getNodeForName(nodes, "User D");

		// Principle 2: Only create weights to users that posted since the last
		// post of this user

		double b2c = this.sn.getWeight(userB, userC);
		double c2b = this.sn.getWeight(userC, userB);
		double c2a = this.sn.getWeight(userC, userA);
		double c2d = this.sn.getWeight(userC, userD);

		// Add a new comment from User C.
		d.addDiscussionEvents(new DiscussionEvent[] { example[6] });
		this.sn.setDiscussionData(new Discussion[] { d }, null);
		nodes = this.sn.getActors();

		// This should result in an increased weight between C and A / D.
		assertTrue(c2a < this.sn.getWeight(userC, userA));
		assertTrue(c2d < this.sn.getWeight(userC, userD));

		// This should not change the weight between C and B.
		assertEquals(c2b, this.sn.getWeight(userC, userB), 0.001);
		// I have no idea how this should influence the weight between B and C
		assertEquals(b2c, this.sn.getWeight(userB, userC), 0.001);
	}

	@Test
	public void testPrinciple3a() {
		// Principle 3: Count multiple posts as one, if there are no posts in
		// between.

		// Example from Gabor's Masters Thesis
		Discussion d = DiscussionFactory.getInstance().getDiscussion(-5);

		DiscussionEvent[] example = createExample();
		// Test strategy: user A has two consecutive posts. Leave the second
		// post out, compute the weights, add it, compare if the weights changed
		// according to principle 3
		// To get rid of effects of other principles, we let the thread begin
		// with userB instead of userA and ignore userB for the test.

		// Attention! Comment 4 is earlier then comment 3.
		DiscussionEvent des[] = new DiscussionEvent[9];
		des[0] = example[1];
		des[1] = example[2];
		des[2] = example[4];
		des[3] = example[5];
		des[4] = example[6];
		des[5] = example[7];
		des[6] = example[8];
		des[7] = example[9];
		des[8] = example[10];

		d.addDiscussionEvents(des);
		this.sn.setDiscussionData(new Discussion[] { d }, null);

		Node[] nodes = this.sn.getActors();
		Node userA = getNodeForName(nodes, "User A");
		Node userC = getNodeForName(nodes, "User C");
		Node userD = getNodeForName(nodes, "User D");
		Node userE = getNodeForName(nodes, "User E");

		// store the current weights.
		double a2c = this.sn.getWeight(userA, userC);
		double a2d = this.sn.getWeight(userA, userD);
		double a2e = this.sn.getWeight(userA, userE);
		double c2a = this.sn.getWeight(userC, userA);
		double d2a = this.sn.getWeight(userD, userA);
		double e2a = this.sn.getWeight(userE, userA);

		// Add a new comment from User A.
		d.addDiscussionEvents(new DiscussionEvent[] { example[3] });
		this.sn.setDiscussionData(new Discussion[] { d }, null);
		nodes = this.sn.getActors();

		// There is nothing that should have changed between A and the other
		// nodes.
		assertEquals(a2c, this.sn.getWeight(userA, userC), 0.001);
		assertEquals(a2d, this.sn.getWeight(userA, userD), 0.001);
		assertEquals(a2e, this.sn.getWeight(userA, userE), 0.001);

		// There should be increases of weights from all later comments.
		assertTrue(c2a < this.sn.getWeight(userC, userA));
		assertTrue(d2a < this.sn.getWeight(userD, userA));
		assertEquals("This post is just too long after", e2a,
				this.sn.getWeight(userE, userA), 0.001);
	}

	@Test
	public void testPrinciple3b() {
		// Principle 3: Count multiple posts as one, if there are no posts in
		// between.

		// Example from Gabor's Masters Thesis
		Discussion d = DiscussionFactory.getInstance().getDiscussion(-5);
		DiscussionEvent[] example = createExample();

		// Test strategy: user A has two consecutive posts. Leave the first
		// post out, compute the weights, add it, compare if the weights changed
		// according to principle 3
		// To get rid of effects of other principles, we let the thread begin
		// with userB instead of userA and ignore userB for the test.

		// Attention! Comment 4 is earlier then comment 3.
		DiscussionEvent des[] = new DiscussionEvent[9];
		des[0] = example[1];
		des[1] = example[2];
		des[2] = example[3];
		des[3] = example[5];
		des[4] = example[6];
		des[5] = example[7];
		des[6] = example[8];
		des[7] = example[9];
		des[8] = example[10];

		d.addDiscussionEvents(des);
		this.sn.setDiscussionData(new Discussion[] { d }, null);

		Node[] nodes = this.sn.getActors();
		Node userA = getNodeForName(nodes, "User A");
		Node userC = getNodeForName(nodes, "User C");
		Node userD = getNodeForName(nodes, "User D");
		Node userE = getNodeForName(nodes, "User E");

		// store the current weights.
		double a2c = this.sn.getWeight(userA, userC);
		double a2d = this.sn.getWeight(userA, userD);
		double a2e = this.sn.getWeight(userA, userE);
		double c2a = this.sn.getWeight(userC, userA);
		double d2a = this.sn.getWeight(userD, userA);
		double e2a = this.sn.getWeight(userE, userA);

		// Add a new comment from User A.
		d.addDiscussionEvents(new DiscussionEvent[] { example[4] });
		this.sn.setDiscussionData(new Discussion[] { d }, null);
		nodes = this.sn.getActors();

		// The weight from A to earlier posts should increase
		assertTrue(a2c < this.sn.getWeight(userA, userC));
		assertEquals(a2d, this.sn.getWeight(userA, userD), 0.001);
		assertEquals(a2e, this.sn.getWeight(userA, userE), 0.001);

		// There is nothing that should have changed between later posts and
		// userA
		assertEquals(c2a, this.sn.getWeight(userC, userA), 0.001);
		assertEquals(d2a, this.sn.getWeight(userD, userA), 0.001);
		assertEquals(e2a, this.sn.getWeight(userE, userA), 0.001);
	}

	@Test
	public void testPrinciple4() {
		// Principle 4: Thread-starter has a link to everybody who participates
		// in the thread.

		// Example from Gabor's Masters Thesis
		Discussion d = DiscussionFactory.getInstance().getDiscussion(-5);
		DiscussionEvent[] example = createExample();

		// Test strategy: User A started the thread. We filter all other posts
		// of user A to avoid weights from the other principles.

		DiscussionEvent des[] = new DiscussionEvent[7];
		des[0] = example[0];
		des[1] = example[1];
		des[2] = example[2];
		des[3] = example[5];
		des[4] = example[6];
		des[5] = example[7];
		des[6] = example[9];

		d.addDiscussionEvents(des);
		this.sn.setDiscussionData(new Discussion[] { d }, null);

		Node[] nodes = this.sn.getActors();
		Node userA = getNodeForName(nodes, "User A");
		Node userB = getNodeForName(nodes, "User B");
		Node userC = getNodeForName(nodes, "User C");
		Node userD = getNodeForName(nodes, "User D");
		Node userE = getNodeForName(nodes, "User E");

		assertTrue(0 < this.sn.getWeight(userA, userB));
		assertTrue(0 < this.sn.getWeight(userA, userC));
		assertTrue(0 < this.sn.getWeight(userA, userD));
		assertTrue(0 < this.sn.getWeight(userA, userE));
	}

	@Test
	public void testPrinciple5() {
		// Principle 5: weights of multiple edges between users are summed up.
		// If an edge already exists, we add the double of the amount of the new
		// edge to account for the dialogue that seems to evolve.
		// Example from Gabor's Masters Thesis
		Discussion d1 = DiscussionFactory.getInstance().getDiscussion(-5);
		Discussion d2 = DiscussionFactory.getInstance().getDiscussion(-4);
		DiscussionEvent[] example = createExample();

		// Test strategy: user C and D have a dialog going on. We extract each
		// pair, compute the weight, then we add both pairs and see if the
		// factor 2 is included.

		// Attention! Comment 4 is earlier then comment 3.
		DiscussionEvent des1[] = new DiscussionEvent[2];
		des1[0] = example[2];
		des1[1] = example[5];
		DiscussionEvent des2[] = new DiscussionEvent[2];
		des2[0] = example[6];
		des2[1] = example[7];

		// We add the discussion starter to make this test independent from
		// principle 4
		d1.addDiscussionEvents(new DiscussionEvent[] { example[0], des1[0],
				des1[1] });
		this.sn.setDiscussionData(new Discussion[] { d1 }, null);

		Node[] nodes = this.sn.getActors();
		Node userC = getNodeForName(nodes, "User C");
		Node userD = getNodeForName(nodes, "User D");

		// store the current weights.
		double d2c1 = this.sn.getWeight(userD, userC);

		// some basic assumptions. If the direction of the edges changes, this
		// might be the other way round.
		assertEquals(0, this.sn.getWeight(userC, userD), 0.001);
		assertTrue(0 < d2c1);

		// now the second pair of posts
		d2.addDiscussionEvents(new DiscussionEvent[] { example[0], des2[0],
				des2[1] });
		this.sn.setDiscussionData(new Discussion[] { d2 }, null);
		double d2c2 = this.sn.getWeight(userD, userC);
		assertEquals(0, this.sn.getWeight(userC, userD), 0.001);
		assertTrue(0 < d2c2);

		// now construct the complete thread:
		d1.addDiscussionEvents(des2);
		this.sn.setDiscussionData(new Discussion[] { d1 }, null);
		assertEquals(d2c1 + 2 * d2c2, this.sn.getWeight(userD, userC), 0.001);
	}

	@Test
	public void testExampleWithTwoActors() {
		Discussion d = DiscussionFactory.getInstance().getDiscussion(-4);

		DiscussionEvent[] example = createExample();
		DiscussionEvent des[] = new DiscussionEvent[2];
		des[0] = example[0];
		des[1] = example[1];

		d.addDiscussionEvents(des);

		this.sn.setDiscussionData(new Discussion[] { d }, null);
		Node[] nodes = this.sn.getActors();
		assertEquals(0, this.sn.getWeight(getNodeForName(nodes, "User A"),
				getNodeForName(nodes, "User A")), 0.001);
		double a2b = this.sn.getWeight(getNodeForName(nodes, "User A"),
				getNodeForName(nodes, "User B"));
		assertTrue("A->B, because A provoked an answer by B", a2b > 0);
		double b2a = this.sn.getWeight(getNodeForName(nodes, "User A"),
				getNodeForName(nodes, "User B"));
		assertTrue(
				"B->A, because A started the thread, kind of a reward for thread starters",
				b2a > 0);

		assertEquals("Computation of both weights is equal", a2b, b2a, 0.001);

		DiscussionEvent de = new DiscussionEvent();
		de.setCreator("User A");
		de.setCreationDate(Util.parseDate("2010-06-17T20:34:00.000"));

		d.addDiscussionEvents(new DiscussionEvent[] { de });
		this.sn.setDiscussionData(new Discussion[] { d }, null);
		nodes = this.sn.getActors();
		assertTrue("Reply from A adds weight", a2b < this.sn.getWeight(
				getNodeForName(nodes, "User A"),
				getNodeForName(nodes, "User B")));
	}

	@Test
	public void testDialogue() {
		Discussion d = DiscussionFactory.getInstance().getDiscussion(-4);

		DiscussionEvent[] example = createExample();
		DiscussionEvent des[] = new DiscussionEvent[2];
		des[0] = example[0];
		des[1] = example[1];

		d.addDiscussionEvents(des);

		this.sn.setDiscussionData(new Discussion[] { d }, null);
		Node[] nodes = this.sn.getActors();
		double a2b = this.sn.getWeight(getNodeForName(nodes, "User A"),
				getNodeForName(nodes, "User B"));
		double b2a = this.sn.getWeight(getNodeForName(nodes, "User B"),
				getNodeForName(nodes, "User A"));

		// add a new comment from the actor that started the thread (post 4 in
		// the example).
		d.addDiscussionEvents(new DiscussionEvent[] { example[3] });
		this.sn.setDiscussionData(new Discussion[] { d }, null);
		nodes = this.sn.getActors();

		assertEquals(0, this.sn.getWeight(getNodeForName(nodes, "User A"),
				getNodeForName(nodes, "User A")), 0.001);

		assertTrue("Reply from A adds weight", a2b < this.sn.getWeight(
				getNodeForName(nodes, "User A"),
				getNodeForName(nodes, "User B")));
		// TODO is this correct?
		assertEquals(
				"Reply from A should add weight, because B provoked A's answer",
				b2a, this.sn.getWeight(getNodeForName(nodes, "User B"),
						getNodeForName(nodes, "User A")), 0.001);
	}

	@Test
	public void testOtherCommentMatters() {
		Discussion d = DiscussionFactory.getInstance().getDiscussion(-4);

		DiscussionEvent[] example = createExample();
		DiscussionEvent des[] = new DiscussionEvent[2];
		des[0] = example[0];
		des[1] = example[2];

		d.addDiscussionEvents(des);

		this.sn.setDiscussionData(new Discussion[] { d }, null);
		Node[] nodes = this.sn.getActors();
		double a2c = this.sn.getWeight(getNodeForName(nodes, "User A"),
				getNodeForName(nodes, "User C"));

		d.addDiscussionEvents(new DiscussionEvent[] { example[1] });
		this.sn.setDiscussionData(new Discussion[] { d }, null);
		nodes = this.sn.getActors();

		assertEquals(0, this.sn.getWeight(getNodeForName(nodes, "User A"),
				getNodeForName(nodes, "User A")), 0.001);

		assertTrue(
				"Comment from B is between A and C and should reduce the weight",
				a2c > this.sn.getWeight(getNodeForName(nodes, "User A"),
						getNodeForName(nodes, "User C")));
	}

	@Test
	public void testEmptyDiscussions() {
		Discussion d = DiscussionFactory.getInstance().getDiscussion(-4);

		this.sn.setDiscussionData(new Discussion[] { d }, null);
		Node[] nodes = this.sn.getActors();
		assertEquals(0, nodes.length);

		assertEquals(0, this.sn.getWeight(new Node(), new Node()), 0.001);
	}

	private Node getNodeForName(Node[] nodes, String name) {
		for (Node n : nodes)
			if (name.equals(n.getLabel()))
				return n;

		return null;
	}
}
