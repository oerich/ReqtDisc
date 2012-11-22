package org.computer.knauss.reqtDiscussion.model.machineLearning.eval;

public class ConfusionMatrix {

	private String[] categories;
	private int[][] matrix;

	public void init(String[] categories) {
		this.categories = categories;
		this.matrix = new int[this.categories.length][this.categories.length];
	}

	private int index(String cat) {
		for (int i = 0; i < this.categories.length; i++) {
			if (categories[i].equals(cat))
				return i;
		}
		return -1;
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

	public ConfusionMatrix convertOrdering(
			StringArrayOrderConverter orderConverter) {
		// check if this works out:
		for (int i = 0; i < this.categories.length; i++) {
			if (!this.categories[i].equals(orderConverter.getLeft()[i]))
				throw new RuntimeException(
						"Order of left array in OrderConverter does not match.");
		}

		ConfusionMatrix ret = new ConfusionMatrix();
		ret.init(orderConverter.getRight());

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

}
