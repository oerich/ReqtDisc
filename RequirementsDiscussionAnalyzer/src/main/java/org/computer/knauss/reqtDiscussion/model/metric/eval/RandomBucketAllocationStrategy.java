package org.computer.knauss.reqtDiscussion.model.metric.eval;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionFactory;

final class RandomBucketAllocationStrategy extends
		AbstractBucketBalancingStrategy {

	protected RandomBucketAllocationStrategy() {
		super();
	}

	@Override
	public Bucket[] distributedOverKBuckets(int k, Discussion[] discussions,
			boolean aggregateDiscussions) {
		Bucket[] ret = new Bucket[k];
for (int i = 0; i < ret.length; i++) {
	ret[i]=new Bucket();
}
		Random r = new Random(System.currentTimeMillis());

		for (Discussion d : discussions) {
			if (!aggregateDiscussions)
				ret[r.nextInt(k)].add(new Discussion[] { d });
			else {
				if (!discussionAllocated(d, ret)) {
					int ids[] = getDiscussionAggregator()
							.getRelatedDiscussionIDs(d.getID());

					List<Discussion> tmp = new LinkedList<Discussion>();
					for (int i : ids)
						if (DiscussionFactory.getInstance().exists(i))
							tmp.add(DiscussionFactory.getInstance()
									.getDiscussion(i));
					ret[r.nextInt(k)].add(tmp.toArray(new Discussion[0]));
				}
			}
		}
		System.out.print("Bucket sizes: ");
		for (Bucket b : ret)
			System.out.print(getBucketSize(b) + ",");
		System.out.println();
		return ret;
	}

}
