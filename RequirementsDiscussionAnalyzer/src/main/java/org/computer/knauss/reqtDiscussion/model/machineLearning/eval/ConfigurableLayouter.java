package org.computer.knauss.reqtDiscussion.model.machineLearning.eval;

import java.text.DecimalFormat;

import org.computer.knauss.reqtDiscussion.model.metric.PatternMetric;

public class ConfigurableLayouter implements IConfusionMatrixLayouter {

	private String rowSep;
	private String colSep;
	private DecimalFormat df;

	public ConfigurableLayouter(String colSep, String rowSep) {
		this.colSep = colSep;
		this.rowSep = rowSep;
	}

	public String getRowSep() {
		return rowSep;
	}

	public void setRowSep(String rowSep) {
		this.rowSep = rowSep;
	}

	public String getColSep() {
		return colSep;
	}

	public void setColSep(String colSep) {
		this.colSep = colSep;
	}

	@Override
	public String layoutConfusionMatrix(int[][] matrix, StringArrayOrderConverter converter,
			PatternMetric metric) {
		DecimalFormat df = getDecimalFormat();
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
			line.append(df.format(recall));
			// precision
			double precision = ((double) tp)
					/ (((double) tp) + ((double) fp));
			line.append(colSep);
			line.append(df.format(precision));
			// f-measure
			line.append(colSep);
			line.append(df.format((2 * recall * precision) / (recall + precision)));
			// specificity
			line.append(colSep);
			line.append(df.format(((double) tn) / (((double) tn) + ((double) fp))));
		}
		return line.toString();
	}

	
	private DecimalFormat getDecimalFormat() {
		if (this.df == null) {
			this.df = new DecimalFormat("#.###");
		}
		return this.df;
	}
}