package org.computer.knauss.reqtDiscussion.model.machineLearning.eval;

import java.text.DecimalFormat;

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

	private DecimalFormat getDecimalFormat() {
		if (this.df == null) {
			this.df = new DecimalFormat("#.###");
		}
		return this.df;
	}

	@Override
	public String layoutConfusionMatrix(ConfusionMatrix confusionMatrix) {
		DecimalFormat df = getDecimalFormat();
		// Print the heading of the confusion matrix
		StringBuffer line = new StringBuffer();
		line.append("actual");
		line.append("/");
		line.append("predicted");
		for (int i = 0; i < confusionMatrix.getCategories().length; i++) {
			line.append(colSep);
			line.append(confusionMatrix.getCategories()[i]);
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

		int[][] matrix = confusionMatrix.getConfusionMatrix();

		for (int row = 0; row < matrix.length; row++) {
			line.append(rowSep);
			String rowCategory = confusionMatrix.getCategories()[row];
			line.append(rowCategory);
			line.append(colSep);
			for (int col = 0; col < matrix[row].length; col++) {
				line.append(matrix[row][col]);
				line.append(colSep);
			}
			line.append(confusionMatrix.getTruePositives(rowCategory));
			line.append(colSep);
			line.append(confusionMatrix.getFalsePositives(rowCategory));
			line.append(colSep);
			line.append(confusionMatrix.getFalseNegatives(rowCategory));
			line.append(colSep);
			line.append(confusionMatrix.getTrueNegatives(rowCategory));
			line.append(colSep);
			line.append(df.format(confusionMatrix.getRecall(rowCategory)));
			line.append(colSep);
			line.append(df.format(confusionMatrix.getPrecision(rowCategory)));
			line.append(colSep);
			line.append(df.format(confusionMatrix.getFMeasure(rowCategory)));
			line.append(colSep);
			line.append(df.format(confusionMatrix.getSpecificity(rowCategory)));
		}
		return line.toString();
	}
}