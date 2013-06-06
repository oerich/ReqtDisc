package org.computer.knauss.reqtDiscussion.ui.ctrl;

import org.computer.knauss.reqtDiscussion.Util;
import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.ui.DiscussionVisualizationPanel;

public class ExportAllVisualizations extends AbstractDiscussionIterationCommand {

	private static final long serialVersionUID = 1L;
	private DiscussionVisualizationPanel discussionVisualization;

	public ExportAllVisualizations(
			DiscussionVisualizationPanel discussionVisualization) {
		super("Export all visualizations");
		this.discussionVisualization = discussionVisualization;
	}

	@Override
	protected void processDiscussionHook(Discussion[] d) {
		Util.sortByID(d);

		this.discussionVisualization.setDiscussions(d);
		this.discussionVisualization.exportVisualization();
	}
}
