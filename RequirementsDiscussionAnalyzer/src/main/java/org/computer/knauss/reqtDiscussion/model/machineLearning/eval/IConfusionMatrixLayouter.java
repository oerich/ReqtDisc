package org.computer.knauss.reqtDiscussion.model.machineLearning.eval;

import org.computer.knauss.reqtDiscussion.model.metric.PatternMetric;

public interface IConfusionMatrixLayouter {
	/**
	 * 
	 * @param matrix
	 * @param c
	 * @param metric
	 * @return
	 */
	String layoutConfusionMatrix(int[][] matrix, StringArrayOrderConverter c,
			PatternMetric metric);
}