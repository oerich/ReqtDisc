package org.computer.knauss.reqtDiscussion.model.machineLearning.metadata;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.metric.AbstractDiscussionMetric;

public class DefaultMetricWrapper implements IMetricWrapper {

	private Object[] values;
	private AbstractDiscussionMetric metric;
	private double min = Double.MAX_VALUE;
	private double max = Double.MIN_VALUE;

	@Override
	public void init(AbstractDiscussionMetric m, Object[] values) {
		this.metric = m;
		this.values = values;
	}

	@Override
	public Object measure(Discussion d) {
		double v = this.metric.considerDiscussions(new Discussion[] { d });
		if (min > max)
			return ((Double)v).intValue();

		double x = (v / this.max) * values.length;
		
		for (int i = 0; i < this.values.length + 1; i++) {
			if (i > x)
				return this.values[i-1];
		}
		// special case: maximum
		return this.values[this.values.length - 1];
	}

	@Override
	public void preprocess(Discussion d) {
		double v = this.metric.considerDiscussions(new Discussion[] { d });
		if (v < min)
			min = v;

		if (v > max)
			max = v;
	}

}
