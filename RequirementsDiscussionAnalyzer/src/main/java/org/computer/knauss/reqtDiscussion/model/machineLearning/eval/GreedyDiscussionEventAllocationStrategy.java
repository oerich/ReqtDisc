package org.computer.knauss.reqtDiscussion.model.machineLearning.eval;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;

public class GreedyDiscussionEventAllocationStrategy extends
		AbstractBucketBalancingStrategy {

	private DiscussionBucket[] buckets;

	@Override
	public void distributedOverKBuckets(int k, Discussion[] discussions,
			boolean aggregateDiscussions) {

		// Create complex discussions if needed
		if (aggregateDiscussions) {
			discussions = aggregateDiscussions(discussions);
		}

		// largest Discussions first
		Arrays.sort(discussions, new Comparator<Discussion>() {

			@Override
			public int compare(Discussion o1, Discussion o2) {
				return -1
						* ((Integer) o1.getDiscussionEvents().length)
								.compareTo((Integer) o2.getDiscussionEvents().length);
			}
		});

		DiscussionBucket[] ret = new DiscussionBucket[k];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = new DiscussionBucket();
		}
		for (Discussion d : discussions) {
			Arrays.sort(ret, new Comparator<DiscussionBucket>() {

				@Override
				public int compare(DiscussionBucket o1, DiscussionBucket o2) {
					return ((Integer) getBucketSize(o1))
							.compareTo(getBucketSize(o2));
				}

			});
			ret[0].add(d);
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
