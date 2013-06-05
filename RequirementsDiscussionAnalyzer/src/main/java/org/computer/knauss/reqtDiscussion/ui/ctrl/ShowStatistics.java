package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;

import org.computer.knauss.reqtDiscussion.Util;
import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.metric.AbstractDiscussionMetric;
import org.computer.knauss.reqtDiscussion.model.metric.AbstractNetworkMetric;
import org.computer.knauss.reqtDiscussion.ui.metrics.MetricFrame;

public class ShowStatistics extends AbstractDiscussionIterationCommand {

	private static final long serialVersionUID = 1L;
	private List<AbstractDiscussionMetric> metrics;

	public ShowStatistics() {
		super("Show statistics");
	}

	@Override
	protected void preProcessingHook() {
		this.metrics = new LinkedList<AbstractDiscussionMetric>();

		// set the partitions to the currently used partition
		for (AbstractNetworkMetric m : AbstractNetworkMetric.STANDARD_METRICS) {
			m.setPartition(getVisualizationConfiguration()
					.getDiscussionPartition());
			m.setSocialNetwork(getVisualizationConfiguration()
					.getSocialNetwork());
		}

		Collections
				.addAll(this.metrics, AbstractDiscussionMetric.OTHER_METRICS);
		Collections.addAll(this.metrics,
				AbstractDiscussionMetric.STANDARD_METRICS);
		Collections
				.addAll(this.metrics, AbstractNetworkMetric.STANDARD_METRICS);

		System.out.println("=== Statistics ===");
		StringBuffer sb = new StringBuffer();
		for (AbstractDiscussionMetric m : this.metrics) {
			sb.append("\t");
			sb.append(m.getName());
		}
		System.out.println(sb.toString());
	}

	@Override
	protected void processDiscussionHook(Discussion[] d) {
		// compute
		int[] ids = new int[d.length];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = d[i].getID();
		}

		String key = Util.idsToKey(ids);
		System.out.print(key);

		for (AbstractDiscussionMetric m : this.metrics) {
			m.initDiscussions(d);
			Double result = m.considerDiscussions(d);
			m.getResults().put(key, result);
			System.out.print("\t" + result);
		}
		System.out.println();
	}

	@Override
	protected void postProcessingHook() {
		System.out.println("=== Overall Statistics ===");

		for (AbstractDiscussionMetric m : this.metrics)
			System.out.println(m);

		MetricFrame mf = new MetricFrame();
		mf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mf.update();
		mf.setVisible(true);
	}

}
