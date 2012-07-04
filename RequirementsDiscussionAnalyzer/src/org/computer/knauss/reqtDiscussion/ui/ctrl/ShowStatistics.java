package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.awt.event.ActionEvent;

import org.computer.knauss.reqtDiscussion.model.clarificationPatterns.AbstractWorkitemMetric;
import org.computer.knauss.reqtDiscussion.ui.uiModel.DiscussionTableModel;

public class ShowStatistics extends AbstractCommand {

	private static final long serialVersionUID = 1L;

	public ShowStatistics() {
		super("show statistics");
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		System.out.println("=== Statistics ===");
		for (AbstractWorkitemMetric m : AbstractWorkitemMetric.STANDARD_METRICS) {
			m.computeMetric(((DiscussionTableModel) getWorkitemTableModel())
					.getDiscussions());
			System.out.println(m);
		}
	}

}
