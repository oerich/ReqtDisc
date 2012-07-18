package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.awt.event.ActionEvent;
import java.util.Arrays;

import org.computer.knauss.reqtDiscussion.model.metric.AbstractDiscussionMetric;
import org.computer.knauss.reqtDiscussion.model.metric.AbstractNetworkMetric;
import org.computer.knauss.reqtDiscussion.ui.uiModel.DiscussionTableModel;

public class ShowStatistics extends AbstractCommand {

	private static final long serialVersionUID = 1L;

	public ShowStatistics() {
		super("show statistics");
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		StringBuffer sb = new StringBuffer();
		System.out.println("=== Statistics ===");
		for (AbstractDiscussionMetric m : AbstractDiscussionMetric.STANDARD_METRICS) {
			m.computeMetric(((DiscussionTableModel) getWorkitemTableModel())
					.getDiscussions());
			System.out.println(m);
			// for the details:
			sb.append("\t");
			sb.append(m.getName());
		}
		for (AbstractNetworkMetric nm : AbstractNetworkMetric.STANDARD_METRICS) {
			nm.computeMetric(((DiscussionTableModel) getWorkitemTableModel())
					.getDiscussions());
			System.out.println(nm);
			sb.append("\t");
			sb.append(nm.getName());
		}
		System.out.println("=== Details ===");
		System.out.println(sb.toString());

		String[] keys = AbstractDiscussionMetric.STANDARD_METRICS[0]
				.getResults().keySet().toArray(new String[0]);
		Arrays.sort(keys);
		for (String key : keys) {
			System.out.print(key);
			for (AbstractDiscussionMetric m : AbstractDiscussionMetric.STANDARD_METRICS) {
				System.out.print("\t" + m.getResults().get(key));
			}
			for (AbstractNetworkMetric nm : AbstractNetworkMetric.STANDARD_METRICS) {
				System.out.print("\t" + nm.getResults().get(key));
			}
			System.out.println();
		}
	}

}
