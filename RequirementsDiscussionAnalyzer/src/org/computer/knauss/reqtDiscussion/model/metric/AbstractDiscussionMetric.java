package org.computer.knauss.reqtDiscussion.model.metric;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.computer.knauss.reqtDiscussion.model.Discussion;

public abstract class AbstractDiscussionMetric {

	public final static AbstractDiscussionMetric TIME_LENGTH = new TimeLengthMetric();
	public final static AbstractDiscussionMetric NUMBER_COMMENTS = new CommentNumberMetric();
	public final static AbstractDiscussionMetric NUMBER_CONTRIBUTORS = new ContributerNumberMetric();

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
