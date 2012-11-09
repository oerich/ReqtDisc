package org.computer.knauss.reqtDiscussion.model.socialNetwork;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.ModelElement;
import org.computer.knauss.reqtDiscussion.model.partition.IDiscussionOverTimePartition;

public class PartitionedSocialNetwork extends SocialNetwork {

	@Override
	public double getWeight(Node actor1, Node actor2) {
		if (actor1.equals(actor2))
			return 0.0;

		double ret = 0;

		int actor1Found;
		int actor2Found;

		IDiscussionOverTimePartition p = getDiscussionOverTimePartition();
		for (Discussion d : getDiscussions()) {
			p.setModelElements(d.getDiscussionEvents());
			// System.out.println("Partitions: " + p.getPartitionCount());
			for (int i = 0; i < p.getPartitionCount(); i++) {
				actor1Found = 0;
				actor2Found = 0;
				// System.out.println(i + " - " + p
				// .getWorkitemsForPartition(i).length);
				for (ModelElement wc : p.getModelElementsForPartition(i)) {
					if (actor1.getLabel().equals(wc.getCreator()))
						actor1Found += 1;
					if (actor2.getLabel().equals(wc.getCreator()))
						actor2Found += 1;
				}
				ret += Math.min(actor1Found, actor2Found);
			}
		}

		// System.out.println(actor1 + "-> " + actor2 + "=" + ret);
		return ret;
	}

}
