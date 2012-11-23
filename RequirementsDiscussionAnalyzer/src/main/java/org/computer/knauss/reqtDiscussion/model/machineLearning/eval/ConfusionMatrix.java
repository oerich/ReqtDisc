package org.computer.knauss.reqtDiscussion.model.machineLearning.eval;

import java.text.DecimalFormat;

public class ConfusionMatrix {

	private String[] categories;
	private int[][] matrix;
	private DecimalFormat df;

	public void init(String[] categories) {
		this.categories = categories;
		this.matrix = new int[this.categories.length][this.categories.length];
	}

	private int index(String cat) {
		for (int i = 0; i < this.categories.length; i++) {
			if (categories[i].equals(cat))
				return i;
		}
		StringBuffer msg = new StringBuffer();
		msg.append("Category '");
		msg.append(cat);
		msg.append("' does not exist in [");
		for (String c : this.categories) {
			msg.append(c);
			msg.append(',');
		}
		msg.append("]");

		throw new RuntimeException(msg.toString());
	}

	public void report(String actual, String predicted) {
		this.matrix[index(actual)][index(predicted)]++;
	}

	public int getTruePositives(String category) {
		return this.matrix[index(category)][index(category)];
	}

	public int getFalsePositives(String category) {
		int col = index(category);
		int ret = 0;
		for (int row = 0; row < categories.length; row++) {
			if (row != col) {
				ret += this.matrix[row][col];
			}
		}
		return ret;
	}

	public int getFalseNegatives(String category) {
		int row = index(category);
		int ret = 0;
		for (int col = 0; col < categories.length; col++) {
			if (row != col) {
				ret += this.matrix[row][col];
			}
		}
		return ret;
	}

	public int getTrueNegatives(String category) {
		int row = index(category);
		int col = index(category);
		int ret = 0;

		for (int i = 0; i < this.matrix.length; i++) {
			for (int j = 0; j < this.matrix[i].length; j++) {
				if (i != row && j != col)
					ret += this.matrix[i][j];
			}
		}

		return ret;
	}

	public double getRecall(String category) {
		double truePositives = getTruePositives(category);
		double falseNegatives = getFalseNegatives(category);
		return truePositives / (truePositives + falseNegatives);
	}

	public double getPrecision(String category) {
		double truePositives = getTruePositives(category);
		double falsePositives = getFalsePositives(category);
		return truePositives / (truePositives + falsePositives);
	}

	public double getFMeasure(String category) {
		double recall = getRecall(category);
		double precision = getPrecision(category);
		return 2 * precision * recall / (precision + recall);
	}

	public double getSpecificity(String category) {
		double trueNegatives = getTrueNegatives(category);
		double falsePositives = getFalsePositives(category);
		return trueNegatives / (trueNegatives + falsePositives);
	}

	public ConfusionMatrix convertOrdering(String[] newCategoryOrder) {
		// check if this works out:
		for (int i = 0; i < newCategoryOrder.length; i++) {
			// throws an Exception if it does not exist.
			index(newCategoryOrder[i]);
		}

		ConfusionMatrix ret = new ConfusionMatrix();
		ret.init(newCategoryOrder);

		for (int row = 0; row < this.matrix.length; row++) {
			for (int col = 0; col < this.matrix[row].length; col++) {
				for (int val = this.matrix[row][col]; val > 0; val--) {
					ret.report(this.categories[row], this.categories[col]);
				}
			}
		}

		return ret;
	}

	public int[][] getConfusionMatrix() {
		return this.matrix;
	}

	public String[] getCategories() {
		return this.categories;
	}

	private DecimalFormat getDecimalFormat() {
		if (this.df == null) {
			this.df = new DecimalFormat("#.###");
		}
		return this.df;
	}

	public String layoutConfusionMatrix(String colSep, String rowSep) {
		DecimalFormat df = getDecimalFormat();
		// Print the heading of the confusion matrix
		StringBuffer line = new StringBuffer();
		line.append("actual");
		line.append("/");
		line.append("predicted");
		for (int i = 0; i < getCategories().length; i++) {
			line.append(colSep);
			line.append(getCategories()[i]);
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

		int[][] matrix = getConfusionMatrix();

		for (int row = 0; row < matrix.length; row++) {
			line.append(rowSep);
			String rowCategory = getCategories()[row];
			line.append(rowCategory);
			line.append(colSep);
			for (int col = 0; col < matrix[row].length; col++) {
				line.append(matrix[row][col]);
				line.append(colSep);
			}
			line.append(getTruePositives(rowCategory));
			line.append(colSep);
			line.append(getFalsePositives(rowCategory));
			line.append(colSep);
			line.append(getFalseNegatives(rowCategory));
			line.append(colSep);
			line.append(getTrueNegatives(rowCategory));
			line.append(colSep);
			line.append(df.format(getRecall(rowCategory)));
			line.append(colSep);
			line.append(df.format(getPrecision(rowCategory)));
			line.append(colSep);
			line.append(df.format(getFMeasure(rowCategory)));
			line.append(colSep);
			line.append(df.format(getSpecificity(rowCategory)));
		}
		return line.toString();
	}
}
