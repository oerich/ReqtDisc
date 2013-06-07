package org.computer.knauss.reqtDiscussion.model.machineLearning.eval;

import java.util.LinkedList;
import java.util.List;

import org.computer.knauss.reqtDiscussion.Util;
import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;

public final class RandomBucketAllocationStrategy extends
		AbstractBucketBalancingStrategy {
	
	private DiscussionBucket[] buckets;

	public RandomBucketAllocationStrategy() {
		super();
	}

	@Override
	public void distributedOverKBuckets(int k, Discussion[] discussions,
			boolean aggregateDiscussions) {
		DiscussionBucket[] ret = new DiscussionBucket[k];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = new DiscussionBucket();
		}
		if (aggregateDiscussions)
			discussions = aggregateDiscussions(discussions);

		for (Discussion d : discussions) {
			ret[Util.getRandom().nextInt(k)].add(d);
		}
		System.out.print("Bucket sizes: ");
		for (DiscussionBucket b : ret)
			System.out.print(getBucketSize(b) + ",");
		System.out.println();
		this.buckets = ret;
	}

	@Override
	public Discussion[] getDiscussionsForBucket(int k) {
		return this.buckets[k].toArray(new Discussion[0]);
	}

	@Override
	public DiscussionEvent[] getDiscussionEventsForBucket(int k) {
		List<DiscussionEvent> tmp = new LinkedList<DiscussionEvent>();

		for (Discussion d : this.buckets[k])
			for (DiscussionEvent de : d.getDiscussionEvents())
				tmp.add(de);

		return tmp.toArray(new DiscussionEvent[0]);
	}

	@Override
	public int getNumberOfBuckets() {
		if (this.buckets == null)
			return 0;
		return this.buckets.length;
	}
}
