package org.computer.knauss.reqtDiscussion.model.metric.eval;

import org.computer.knauss.reqtDiscussion.model.metric.PatternMetric;

public class ConfigurableLayouter implements IConfusionMatrixLayouter {

	private String rowSep;
	private String colSep;

	public ConfigurableLayouter(String colSep, String rowSep) {
		this.colSep = colSep;
		this.rowSep = rowSep;
	}

	@Override
	public String layoutConfusionMatrix(int[][] matrix, StringArrayOrderConverter converter,
			PatternMetric metric) {
		// Print the heading of the confusion matrix
		StringBuffer line = new StringBuffer();
		line.append("predicted");
		line.append("/");
		line.append("actual");
		for (int i = 0; i < 7; i++) {
			line.append(colSep);
			line.append(metric.decode(converter.convertR2L(i) - 1));
		}
		line.append(colSep);
		line.append("true positive");
		line.append(colSep);
		line.append("false positive");
		line.append(colSep);
		line.append("false negative");
		line.append(colSep);
		line.append("true negative");
		line.append(colSep);
		line.append("recall");
		line.append(colSep);
		line.append("precision");
		line.append(colSep);
		line.append("f-measure");
		line.append(colSep);
		line.append("specificity");

		int sum = 0;
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 7; j++) {
				sum += matrix[i][j];
			}
		}

		for (int i = 0; i < 7; i++) {
			line.append(rowSep);
			line.append(metric.decode(converter.convertR2L(i) - 1));
			line.append(colSep);
			int tp = 0, fp = 0, fn = 0, tn = 0;
			for (int j = 0; j < 7; j++) {
				line.append(matrix[i][j]);
				line.append(colSep);
				if (i == j) {
					tp = matrix[i][j];
					for (int n = 0; n < 7; n++) {
						if (n != j)
							fn += matrix[n][j];
					}
				} else {
					fp += matrix[i][j];
				}
			}
			tn = sum - (tp + fp + fn);
			line.append(tp);
			line.append(colSep);
			line.append(fp);
			line.append(colSep);
			line.append(fn);
			line.append(colSep);
			line.append(tn);
			// recall
			double recall = ((double) tp) / (((double) tp) + ((double) fn));
			line.append(colSep);
			line.append(recall);
			// precision
			double precision = ((double) tp)
					/ (((double) tp) + ((double) fp));
			line.append(colSep);
			line.append(precision);
			// f-measure
			line.append(colSep);
			line.append((2 * recall * precision) / (recall + precision));
			// specificity
			line.append(colSep);
			line.append(((double) tn) / (((double) tn) + ((double) fp)));
		}
		return line.toString();
	}

}