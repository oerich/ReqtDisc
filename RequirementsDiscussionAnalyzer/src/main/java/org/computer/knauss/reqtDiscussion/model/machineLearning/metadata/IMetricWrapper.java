package org.computer.knauss.reqtDiscussion.model.machineLearning.metadata;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.metric.AbstractDiscussionMetric;

public interface IMetricWrapper {

	public void init(AbstractDiscussionMetric m, Object[] values);
	
	public Object measure(Discussion d);
	
	public void preprocess(Discussion d);
	
}
