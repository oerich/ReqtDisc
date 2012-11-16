package org.computer.knauss.reqtDiscussion.model.metric.eval;

import static org.junit.Assert.assertEquals;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.DiscussionFactory;
import org.computer.knauss.reqtDiscussion.model.metric.eval.AbstractBucketBalancingStrategy.Bucket;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GreedyDiscussionEventAllocationStrategyTest {

	private GreedyDiscussionEventAllocationStrategy alloc;

	@Before
	public void setUp() throws Exception {
		alloc = new GreedyDiscussionEventAllocationStrategy();
	}

	@After
	public void tearDown() throws Exception {
		DiscussionFactory.getInstance().clear();
	}

	@Test
	public void testWithoutAggregation() {
		Discussion[] discussions = new Discussion[6];
		createDiscussions(discussions, 6);

		GreedyDiscussionEventAllocationStrategy alloc = new GreedyDiscussionEventAllocationStrategy();
		Bucket[] buckets = alloc.distributedOverKBuckets(3, discussions, false);

		assertEquals(3, buckets.length);
		for (int j = 0; j < buckets.length; j++) {
			assertEquals("bucket " + j, 7, alloc.getBucketSize(buckets[j]));
		}
	}

	@Test
	public void testWithAggregation() {
		Discussion[] discussions = new Discussion[9];
		int n = 6;
		createDiscussions(discussions, n);

		int[] workitemIds = new int[] { 13764, 40774, 40775 };
		createDiscussions(discussions, n, workitemIds);

		Bucket[] buckets = alloc.distributedOverKBuckets(3, discussions, true);

		assertEquals(3, buckets.length);
		for (int j = 0; j < buckets.length; j++) {
			assertEquals("bucket " + j, 9, alloc.getBucketSize(buckets[j]));
		}
	}

	private void createDiscussions(Discussion[] discussions, int startIndex,
			int[] ids) {
		for (int i = 0; i < ids.length; i++) {
			discussions[startIndex + i] = DiscussionFactory.getInstance()
					.getDiscussion(ids[i]);
			DiscussionEvent[] des = new DiscussionEvent[i + 1];
			for (int j = 0; j < des.length; j++) {
				des[j] = new DiscussionEvent();
			}
			discussions[startIndex + i].addDiscussionEvents(des);

		}
	}

	private void createDiscussions(Discussion[] discussions, int n) {

		int[] ids = new int[n];
		for (int i = 0; i < n; i++) {
			ids[i] = i;
		}
		createDiscussions(discussions, 0, ids);
	}

	@Test
	public void testAggregateDiscussion() {
		int[] workitemIds = new int[] { 13764, 40774, 40775 };
		Discussion[] discussions = new Discussion[3];
		createDiscussions(discussions, 0, workitemIds);

		assertEquals(1, discussions[0].getDiscussionEvents().length);
		assertEquals(2, discussions[1].getDiscussionEvents().length);
		assertEquals(3, discussions[2].getDiscussionEvents().length);

		discussions = alloc.aggregateDiscussions(discussions);

		assertEquals(1, discussions.length);
		assertEquals(6, discussions[0].getDiscussionEvents().length);
	}
}
