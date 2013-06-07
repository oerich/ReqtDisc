package org.computer.knauss.reqtDiscussion.model.machineLearning.eval;

import org.computer.knauss.reqtDiscussion.Util;
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

		for (Discussion d : discussions) {
			for (DiscussionEvent de : d.getDiscussionEvents()) {
				ret[Util.getRandom().nextInt(k)].add(de);
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
