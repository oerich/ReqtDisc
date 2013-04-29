package org.computer.knauss.reqtDiscussion.model.socialNetwork;

import java.util.Arrays;
import java.util.Comparator;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.partition.TimeIntervalPartition;

public class ProximitySocialNetwork extends SocialNetwork {

	@Override
	public double getWeight(Node actor1, Node actor2) {
		if (actor1.equals(actor2))
			return 0.0;

		double ret = 0;

		// Based on Gabor's Masters thesis
		for (Discussion d : getDiscussions()) {

			DiscussionEvent[] des = d.getDiscussionEvents();
			Arrays.sort(des, new Comparator<DiscussionEvent>() {

				@Override
				public int compare(DiscussionEvent o1, DiscussionEvent o2) {
					return o1.getCreationDate().compareTo(o2.getCreationDate());
				}
			});
			// we are determining the distance between two posts:
			if (des.length > 0) {
				DiscussionEvent de1 = des[0];
				DiscussionEvent de2;

				// 1. if actor1 started the thread, there is a link to actor2.
				if (actor1.getLabel().equals(de1.getCreator())) {
					for (int i = 0; i < des.length; i++) {
						de2 = des[i];
						if (actor2.getLabel().equals(de2.getCreator())) {
							ret += getWeight(des, de1, de2);
						}
					}
				}

				de1 = null;
				// 2. if actor1 has a post after actor2, there is a link to
				// actor2.

				// The fact that Actor1 is the thread starter implies ret != 0.
				int lastActor1Comment = -1;
				if (ret != 0)
					lastActor1Comment = 0;
				for (int i = 1; i < des.length; i++) {
					// 2.1 find the next occurrence of actor1
					if (actor1.getLabel().equals(des[i].getCreator())) {
						// 2.2 find the last comment from actor2 before that
						for (int j = i; j > lastActor1Comment; j--) {
							if (actor2.getLabel().equals(des[j].getCreator())) {
								// weight conversations higher
								int factor = 1;
								if (ret > 0)
									factor = 2;
								ret += getWeight(des, des[j], des[i]) * factor;
								// do not look further into history
								j = 0;
							}

						}

						lastActor1Comment = i;
					}
				}

			}
		}
		return ret;
	}

	private double getWeight(DiscussionEvent[] comments, DiscussionEvent post1,
			DiscussionEvent post2) {
		double ret = 0;

		int pos1 = 0;
		int pos2 = 0;

		for (int i = 0; i < comments.length; i++) {
			DiscussionEvent wc = comments[i];
			if (wc.equals(post1))
				pos1 = i;
			else if (wc.equals(post2))
				pos2 = i;
		}

		int posts = pos2 - pos1;
		if (posts <= 0)
			return 0;

		long time = post2.getCreationDate().getTime()
				- post1.getCreationDate().getTime();
		double days = ((double) time / (double) TimeIntervalPartition.MILLIS_PER_DAY);
		ret = Math.pow(0.6, posts) + Math.pow(0.6, days);
		return ret;
	}
}
