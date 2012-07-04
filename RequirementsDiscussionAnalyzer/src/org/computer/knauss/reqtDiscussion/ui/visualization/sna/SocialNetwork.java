package org.computer.knauss.reqtDiscussion.ui.visualization.sna;

import java.util.HashMap;
import java.util.Map;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.ui.visualization.ICommentOverTimePartition;

public abstract class SocialNetwork {

	private ICommentOverTimePartition partition;
	private Discussion[] workitems;
	private Map<String, Node> nodes = new HashMap<String, Node>();

	public abstract double getWeight(Node actor1, Node actor2);

	public Node[] getActors() {
		return nodes.values().toArray(new Node[0]);
	}

	public void setDiscussionData(Discussion[] discussions,
			ICommentOverTimePartition p) {
		// System.out.println(getClass().getSimpleName() + ": " + p);
		this.partition = p;
		this.workitems = discussions;

		this.nodes.clear();
		for (Discussion wi : this.workitems)
			for (DiscussionEvent wc : wi.getAllComments()) {
				Node n = this.nodes.get(wc.getCreator());
				if (n == null) {
					n = new Node();
					n.setLabel(wc.getCreator());
					this.nodes.put(wc.getCreator(), n);
				}
				if (partition.isInClass(wc))
					n.setClarification(n.getClarification() + 1);
				else
					n.setCoordination(n.getCoordination() + 1);
			}
	}

	public ICommentOverTimePartition getCommentOverTimePartition() {
		return this.partition;
	}

	public Discussion[] getDiscussions() {
		return this.workitems;
	}

	public String toString() {
		return getClass().getSimpleName();
	}
}
