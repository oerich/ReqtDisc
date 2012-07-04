package org.computer.knauss.reqtDiscussion.model.clarificationPatterns;

import java.sql.Date;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;

public abstract class AbstractDiscussionMetric {

	public final static AbstractDiscussionMetric TIME_LENGTH = new AbstractDiscussionMetric() {

		private DecimalFormat df;

		@Override
		public String getName() {
			return "time length [days]";
		}

		@Override
		public Double considerDiscussions(Discussion[] wi) {
			if (wi == null || wi.length <= 0)
				return 0d;
			Date min = wi[0].getDateCreated();
			Date max = wi[0].getDateCreated();
			for (Discussion w : wi)
				for (DiscussionEvent wc : w.getAllComments()) {
					if (min.after(wc.getCreationDate()))
						min = wc.getCreationDate();
					if (max.before(wc.getCreationDate()))
						max = wc.getCreationDate();
				}

			// return the duration in days.
			return (double) (max.getTime() - min.getTime()) / 86400000;
		}

		public DecimalFormat getDecimalFormat() {
			if (this.df == null) {
				this.df = new DecimalFormat("#.##");
			}
			return this.df;
		}
	};
	public final static AbstractDiscussionMetric NUMBER_COMMENTS = new AbstractDiscussionMetric() {

		@Override
		public String getName() {
			return "#comments";
		}

		@Override
		public Double considerDiscussions(Discussion[] wis) {
			if (wis == null)
				return 0.0;
			int length = 0;
			for (Discussion wi : wis) {
				length += wi.getAllComments().length;
			}
			return (double) length;
		}
	};
	public final static AbstractDiscussionMetric NUMBER_CONTRIBUTORS = new AbstractDiscussionMetric() {

		@Override
		public String getName() {
			return "#contributors";
		}

		@Override
		public Double considerDiscussions(Discussion[] wis) {
			if (wis == null)
				return 0.0;
			Set<String> contributors = new HashSet<String>();
			for (Discussion wi : wis) {
				contributors.add(wi.getCreator());
				for (DiscussionEvent wc : wi.getAllComments()) {
					contributors.add(wc.getCreator());
				}
			}
			return (double) contributors.size();
		}
	};

	public static final AbstractDiscussionMetric[] STANDARD_METRICS = {
			TIME_LENGTH, NUMBER_COMMENTS, NUMBER_CONTRIBUTORS };

	public abstract String getName();

	private Map<Discussion, Double> values = new HashMap<Discussion, Double>();
	private DecimalFormat df;

	public final Double getAverage() {
		Double sum = 0d;
		int instances = this.values.size();
		for (Double n : this.values.values()) {
			sum += n;
		}
		return sum / instances;
	}

	public final Double getMin() {
		Double min = Double.MAX_VALUE;
		for (Double n : this.values.values()) {
			if (n < min)
				min = n;
		}
		return min;
	}

	public final Double getMax() {
		Double max = Double.MIN_VALUE;
		for (Double n : this.values.values()) {
			if (n > max)
				max = n;
		}
		return max;
	}

	public final void computeMetric(Discussion[] discussions) {
		for (Discussion wi : discussions) {
			this.values.put(wi, considerDiscussions(new Discussion[] { wi }));
		}
	}

	public DecimalFormat getDecimalFormat() {
		if (this.df == null) {
			this.df = new DecimalFormat("#.#");
		}
		return this.df;
	}

	public abstract Double considerDiscussions(Discussion[] wi);

	public final void reset() {
		this.values.clear();
	}

	public String toString() {
		DecimalFormat df = getDecimalFormat();
		return getName() + " (avg,min,max): " + df.format(getAverage()) + ", "
				+ df.format(getMin()) + ", " + df.format(getMax());
	}
}
