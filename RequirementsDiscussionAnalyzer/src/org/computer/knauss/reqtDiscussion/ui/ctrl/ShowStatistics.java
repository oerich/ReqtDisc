package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.computer.knauss.reqtDiscussion.io.Util;
import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.metric.AbstractDiscussionMetric;
import org.computer.knauss.reqtDiscussion.model.metric.AbstractNetworkMetric;
import org.computer.knauss.reqtDiscussion.ui.uiModel.DiscussionTableModel;

public class ShowStatistics extends AbstractDiscussionIterationCommand {

	private static final long serialVersionUID = 1L;

	public ShowStatistics() {
		super("show statistics");
	}

	@Override
	protected void preProcessingHook() {
		metrics = new LinkedList<AbstractDiscussionMetric>();
		Collections.addAll(metrics, AbstractDiscussionMetric.OTHER_METRICS);
		Collections.addAll(metrics, AbstractDiscussionMetric.STANDARD_METRICS);
		Collections.addAll(metrics, AbstractNetworkMetric.STANDARD_METRICS);

		try {
			hrd = new HighlightRelatedDiscussions();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// init
		for (Discussion d : ((DiscussionTableModel) getDiscussionTableModel())
				.getDiscussions()) {
			discussionMap.put(d.getID(), d);
		}

		System.out.println("=== Statistics ===");
		StringBuffer sb = new StringBuffer();
		for (AbstractDiscussionMetric m : metrics) {
			sb.append("\t");
			sb.append(m.getName());
		}
		System.out.println(sb.toString());
	}

	Set<Integer> visited = new HashSet<Integer>();
	Map<Integer, Discussion> discussionMap = new HashMap<Integer, Discussion>();
	HighlightRelatedDiscussions hrd = null;
	List<AbstractDiscussionMetric> metrics;

	@Override
	protected void processDiscussionHook(Discussion d) {
		// compute
		if (!visited.contains(d.getID())) {
			int[] related = new int[] { d.getID() };
			if (hrd != null)
				related = hrd.getRelatedDiscussionIDs(d.getID());
			List<Discussion> tmp = new LinkedList<Discussion>();
			for (int id : related) {
				if (!visited.contains(id)) {
					visited.add(id);
					tmp.add(discussionMap.get(id));
				}
			}
			String key = Util.idsToKey(related);
			System.out.print(key);

			Discussion[] tmpDiscussions = tmp.toArray(new Discussion[0]);
			for (AbstractDiscussionMetric m : metrics) {
				m.initDiscussions(tmpDiscussions);
				Double result = m.considerDiscussions(tmpDiscussions);
				m.getResults().put(key, result);
				System.out.print("\t" + result);
			}
			System.out.println();
		}
	}

	@Override
	protected void postProcessingHook() {
		System.out.println("=== Overall Statistics ===");

		for (AbstractDiscussionMetric m : metrics)
			System.out.println(m);
	}

}
