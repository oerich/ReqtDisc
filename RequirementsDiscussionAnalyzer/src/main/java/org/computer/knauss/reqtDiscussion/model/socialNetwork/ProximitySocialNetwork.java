package org.computer.knauss.reqtDiscussion.model.socialNetwork;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.partition.TimeIntervalPartition;

public class ProximitySocialNetwork extends SocialNetwork {

	@Override
	public double getWeight(Node actor1, Node actor2) {
		if (actor1.equals(actor2))
			return 0.0;

		double ret = 0;

		// Based on Remko's Master thesis
		for (Discussion d : getDiscussions()) {

			DiscussionEvent[] events = d.getDiscussionEvents();
			// we are determining the distance between two posts:
			if (events.length > 0) {
				DiscussionEvent event1 = events[0];
				DiscussionEvent event2;

				// 1. if actor1 started the thread, there is a link to actor2.
				if (actor1.getLabel().equals(event1.getCreator())) {
					for (int i = 0; i < events.length; i++) {
						event2 = events[i];
						if (actor2.getLabel().equals(event2.getCreator())) {
							ret += getWeight(events, event1, event2);
						}
					}
				}

				event1 = null;
				// 2. if actor1 has a post after actor2, there is a link to
				// actor2.
				for (int i = 0; i < events.length; i++) {
					DiscussionEvent wc = events[i];
					if (actor2.getLabel().equals(wc.getCreator())) {
						event1 = wc;
					} else if (actor1.getLabel().equals(wc.getCreator())
							&& event1 != null) {
						// weight conversations higher
						int factor = 1;
						if (ret > 0)
							factor = 2;
						ret += getWeight(events, event1, wc) * factor;
						// only the first answer counts
						event1 = null;
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
				- post2.getCreationDate().getTime();
		int days = (int) (time / TimeIntervalPartition.MILLIS_PER_DAY);

		ret = Math.pow(0.6, posts) + Math.pow(0.6, days);
		return ret;
	}
}
