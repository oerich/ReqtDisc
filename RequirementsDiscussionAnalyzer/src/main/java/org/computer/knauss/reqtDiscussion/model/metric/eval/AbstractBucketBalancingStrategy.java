package org.computer.knauss.reqtDiscussion.model.metric.eval;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.computer.knauss.reqtDiscussion.model.ComplexDiscussion;
import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionFactory;
import org.computer.knauss.reqtDiscussion.ui.ctrl.HighlightRelatedDiscussions;

public abstract class AbstractBucketBalancingStrategy {

	public static final AbstractBucketBalancingStrategy RANDOM_BUCKET = new RandomBucketAllocationStrategy();
	public static final AbstractBucketBalancingStrategy GREEDY_DISCUSSION_EVENT = new GreedyDiscussionEventAllocationStrategy();
	private HighlightRelatedDiscussions discussionAggregator;

	protected AbstractBucketBalancingStrategy() {
		// do not allow too many instances around here.
	}

	/**
	 * work around an annoying feature of Java generics //
	 * (http://stackoverflow.
	 * com/questions/217065/cannot-create-an-array-of-linkedlists-in-java)
	 */
	public class Bucket extends LinkedList<Discussion[]> {

		private static final long serialVersionUID = 1L;

	}

	public abstract Bucket[] distributedOverKBuckets(int k,
			Discussion[] discussions, boolean aggregateDiscussions);

	protected boolean discussionAllocated(Discussion nd, Bucket[] buckets) {
		for (Bucket b : buckets)
			for (Discussion ds[] : b) {
				for (Discussion d : ds) {
					if (d.equals(nd))
						return true;
				}
			}
		return false;
	}

	protected HighlightRelatedDiscussions getDiscussionAggregator() {
		if (this.discussionAggregator == null)
			try {
				this.discussionAggregator = new HighlightRelatedDiscussions();
			} catch (IOException e) {
				e.printStackTrace();
			}
		return this.discussionAggregator;
	}

	protected Discussion aggregateDiscussion(Discussion d) {
		int ids[] = getDiscussionAggregator()
				.getRelatedDiscussionIDs(d.getID());

		List<Discussion> tmp = new LinkedList<Discussion>();
		for (int i : ids)
			if (DiscussionFactory.getInstance().exists(i))
				tmp.add(DiscussionFactory.getInstance().getDiscussion(i));

		if (tmp.size() == 1)
			return d;
		return new ComplexDiscussion(tmp.toArray(new Discussion[0]));
	}

	protected Discussion[] aggregateDiscussions(Discussion[] discussions) {
		Set<Integer> visited = new TreeSet<Integer>();
		List<Discussion> ret = new LinkedList<Discussion>();

		for (Discussion d : discussions) {
			if (!visited.contains(d.getID())) {
				int ids[] = getDiscussionAggregator().getRelatedDiscussionIDs(
						d.getID());
				for (Integer i : ids) {
					visited.add(i);
				}
				ret.add(aggregateDiscussion(d));
			}
		}

		return ret.toArray(new Discussion[0]);
	}

	protected int getBucketSize(Bucket b) {
		int ret = 0;

		for (Discussion[] ds : b) {
			for (Discussion d : ds)
				ret += d.getDiscussionEvents().length;
		}

		return ret;
	}

}
