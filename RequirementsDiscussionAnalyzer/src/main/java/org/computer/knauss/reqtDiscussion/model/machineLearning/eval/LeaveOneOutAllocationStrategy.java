package org.computer.knauss.reqtDiscussion.model.machineLearning.eval;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;

public class LeaveOneOutAllocationStrategy extends
		AbstractBucketBalancingStrategy {

	private Discussion[] discussions;

	@Override
	public void distributedOverKBuckets(int k, Discussion[] discussions,
			boolean aggregateDiscussions) {
		if (aggregateDiscussions)
			discussions = aggregateDiscussions(discussions);

		System.out.print("Bucket-sizes: ");
		for (Discussion d : discussions)
			System.out.print(d.getDiscussionEvents().length + ",");
		System.out.println();
		this.discussions = discussions;
	}

	@Override
	public Discussion[] getDiscussionsForBucket(int k) {
		return new Discussion[] { this.discussions[k] };
	}

	@Override
	public DiscussionEvent[] getDiscussionEventsForBucket(int k) {
		return this.discussions[k].getDiscussionEvents();
	}

	@Override
	public int getNumberOfBuckets() {
		return this.discussions.length;
	}

}
