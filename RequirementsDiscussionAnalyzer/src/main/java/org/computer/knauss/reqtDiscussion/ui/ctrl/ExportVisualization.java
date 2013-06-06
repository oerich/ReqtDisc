package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.awt.event.ActionEvent;

import org.computer.knauss.reqtDiscussion.Util;
import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.ui.DiscussionVisualizationPanel;

public class ExportVisualization extends AbstractCommand {

	private static final long serialVersionUID = 1L;
	private DiscussionVisualizationPanel discussionVisualization;

	public ExportVisualization(
			DiscussionVisualizationPanel discussionVisualization) {

		super("Export visualization");
		this.discussionVisualization = discussionVisualization;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Discussion[] d = getDiscussionTableModel().getSelectedDiscussions();
		Util.sortByID(d);

		this.discussionVisualization.setDiscussions(d);
		this.discussionVisualization.exportVisualization();
	}

}
