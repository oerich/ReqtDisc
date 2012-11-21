package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.metric.TrajectoryFeatures;

public class PrintTrajectoryFeatures extends AbstractDiscussionIterationCommand {

	private static final long serialVersionUID = 1L;
	private Map<Integer, Discussion> visitedDiscussions;
	private HighlightRelatedDiscussions hrd;
	private TrajectoryFeatures tf;

	public PrintTrajectoryFeatures() {
		super("Print trajectory features to sysout");
		this.visitedDiscussions = new HashMap<Integer, Discussion>();
		try {
			this.hrd = new HighlightRelatedDiscussions();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.tf = new TrajectoryFeatures();
	}

	@Override
	protected void processDiscussionHook(Discussion d) {
		if (this.visitedDiscussions.containsKey(d.getID()))
			return;
		List<Discussion> discussions = new LinkedList<Discussion>();
		if (this.hrd != null) {
			int[] relatedDiscussions = this.hrd.getRelatedDiscussionIDs(d
					.getID());

			for (Discussion disc : getDiscussionTableModel().getDiscussions()) {
				for (int i : relatedDiscussions) {
					if (disc.getID() == i) {
						visitedDiscussions.put(disc.getID(), disc);
						discussions.add(disc);
					}
				}
			}
			if (!this.visitedDiscussions.containsKey(d.getID()))
				throw new RuntimeException(
						"thought the original discussion was part of the related list");
		} else {
			discussions.add(d);
			this.visitedDiscussions.put(d.getID(), d);
		}

		tf.considerDiscussions(discussions.toArray(new Discussion[0]));
	}

	@Override
	protected void preProcessingHook() {
		this.visitedDiscussions.clear();
	}

}
