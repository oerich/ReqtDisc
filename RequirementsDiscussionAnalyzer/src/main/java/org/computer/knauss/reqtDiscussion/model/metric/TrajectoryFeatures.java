package org.computer.knauss.reqtDiscussion.model.metric;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.ModelElement;
import org.computer.knauss.reqtDiscussion.model.partition.FixedNumberPartition;

public class TrajectoryFeatures extends AbstractDiscussionMetric {

	@Override
	public String getName() {
		return "Trajectory Features";
	}

	@Override
	public int measurementType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Double considerDiscussions(Discussion[] discussions) {
		int resolution = 8;
		int[] clarifications = new int[resolution];
		int[] other = new int[resolution];
		int[] total = new int[resolution];
		int hasClarification = 0;
		int id = Integer.MAX_VALUE;

		for (Discussion d : discussions) {
			if (d.getID() < id)
				id = d.getID();
		}

		FixedNumberPartition partition = new FixedNumberPartition();
		partition.setPartitionCount(resolution);

		partition.setTimeInterval(discussions);
		partition.setModelElements(getEvents(discussions));

		for (int i = 0; i < resolution; i++) {
			other[i] = 0;
			clarifications[i] = 0;
			ModelElement[] modelElementsForPartition = partition
					.getModelElementsForPartition(i);
			total[i] = modelElementsForPartition.length;
			for (ModelElement me : modelElementsForPartition) {
				if (partition.isInClass(me)) {
					clarifications[i]++;
					hasClarification++;
				} else
					other[i]++;
			}
		}

		StringBuffer sb = new StringBuffer();
		sb.append(String.valueOf(id));
		sb.append("\t");
		sb.append("\t");
		sb.append(String.valueOf(hasClarification));
		sb.append("\t");
		for (int i = 0; i < resolution; i++) {
			if (other[i] == 0)
				sb.append(clarifications[i]);
			else if (clarifications[i] == 0)
				sb.append("0");
			else {
				sb.append("=");
				sb.append(clarifications[i]);
				sb.append("/");
				sb.append(other[i]);
			}
			sb.append("\t");
		}
		for (int i : total) {
			sb.append(String.valueOf(i));
			sb.append("\t");
		}
		for (int i = 0; i < 14; i++)
			sb.append("\t");
		sb.append(discussions.length);
		System.out.println(sb.toString());
		return 0.0;
	}

	private DiscussionEvent[] getEvents(Discussion[] discussions) {
		List<DiscussionEvent> tmp = new LinkedList<DiscussionEvent>();
		for (Discussion d : discussions)
			Collections.addAll(tmp, d.getDiscussionEvents());
		return tmp.toArray(new DiscussionEvent[0]);
	}
}
