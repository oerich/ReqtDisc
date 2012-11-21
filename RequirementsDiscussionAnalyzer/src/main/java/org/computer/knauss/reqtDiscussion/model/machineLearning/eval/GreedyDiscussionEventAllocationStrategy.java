package org.computer.knauss.reqtDiscussion.model.machineLearning.eval;

import java.util.Arrays;
import java.util.Comparator;

import org.computer.knauss.reqtDiscussion.model.Discussion;

class GreedyDiscussionEventAllocationStrategy extends
		AbstractBucketBalancingStrategy {

	@Override
	public Bucket[] distributedOverKBuckets(int k, Discussion[] discussions,
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

		Bucket[] ret = new Bucket[k];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = new Bucket();
		}
		for (Discussion d : discussions) {
			Arrays.sort(ret, new Comparator<Bucket>() {

				@Override
				public int compare(Bucket o1, Bucket o2) {
					return ((Integer) getBucketSize(o1))
							.compareTo(getBucketSize(o2));
				}

			});
			ret[0].add(new Discussion[] { d });
		}
		System.out.print("Bucket sizes: ");
		for (Bucket b : ret)
			System.out.print(getBucketSize(b) + ",");
		System.out.println();
		return ret;
	}

}
