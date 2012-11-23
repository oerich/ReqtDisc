package org.computer.knauss.reqtDiscussion.model.machineLearning.eval;

import java.util.Random;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;

public class EventBasedRandomAllocationStrategy extends
		AbstractBucketBalancingStrategy {

	private DiscussionEventBucket[] buckets;

	@Override
	public void distributedOverKBuckets(int k, Discussion[] discussions,
			boolean aggregateDiscussions) {
		DiscussionEventBucket[] ret = new DiscussionEventBucket[k];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = new DiscussionEventBucket();
		}
		Random r = new Random(System.currentTimeMillis());

		for (Discussion d : discussions) {
			for (DiscussionEvent de : d.getDiscussionEvents()) {
				ret[r.nextInt(k)].add(de);
			}
		}
		System.out.print("Bucket sizes: ");
		for (DiscussionEventBucket b : ret)
			System.out.print(getBucketSize(b) + ",");
		System.out.println();
		this.buckets = ret;
	}

	@Override
	public Discussion[] getDiscussionsForBucket(int k) {
		throw new RuntimeException("Not applicable");
	}

	@Override
	public DiscussionEvent[] getDiscussionEventsForBucket(int k) {
		return this.buckets[k].toArray(new DiscussionEvent[0]);
	}

	@Override
	public int getNumberOfBuckets() {
		return this.buckets.length;
	}

}
