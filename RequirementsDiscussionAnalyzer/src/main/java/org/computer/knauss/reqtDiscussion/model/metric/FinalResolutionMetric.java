package org.computer.knauss.reqtDiscussion.model.metric;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.Incident;

public class FinalResolutionMetric extends AbstractDiscussionMetric {

	private static final String VALUE_SEPARATOR = " => ";
	public static final double UNKNOWN = -1.0;
	private List<String> resolutionTypes = new Vector<String>();

	@Override
	public String getName() {
		return "Final resolution";
	}

	@Override
	public Double considerDiscussions(Discussion[] wi) {
		double ret = UNKNOWN;

		for (Discussion d : wi) {
			Incident[] incidents = d.getIncidents();
			Arrays.sort(incidents, new Comparator<Incident>() {

				@Override
				public int compare(Incident o1, Incident o2) {
					return o1.getDate().compareTo(o2.getDate());
				}
			});
			for (Incident inc : incidents) {
				String newVal = parseNewResolution(inc.getSummary());
				if (newVal != null && !this.resolutionTypes.contains(newVal))
					this.resolutionTypes.add(newVal);
				ret = this.resolutionTypes.indexOf(newVal);
			}
		}

		return ret;
	}

	private String parseNewResolution(String summary) {
		int i = summary.indexOf(VALUE_SEPARATOR);
		int l = VALUE_SEPARATOR.length();
		if (i > 0)
			return summary.substring(i + l);
		return null;
	}

	public String decode(double d) {
		return this.resolutionTypes.get((int) d);
	}

}
