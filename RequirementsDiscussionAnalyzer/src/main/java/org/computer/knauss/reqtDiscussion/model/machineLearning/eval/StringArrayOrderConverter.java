package org.computer.knauss.reqtDiscussion.model.machineLearning.eval;

public class StringArrayOrderConverter {

	private String[] left;
	private String[] right;

	public void setLeft(String[] left) {
		this.left = left;
	}

	public void setRight(String[] right) {
		this.right = right;
	}

	public int convertL2R(int i, String[] left, String[] right) {
		for (int j = 0; j < right.length; j++) {
			if (right[j].equals(left[i]))
				return j;
		}
		// no conversion needed
		return i;
	}

	public int convertL2R(int i) {
		return convertL2R(i, this.left, this.right);
	}

	public int convertR2L(int i) {
		return convertL2R(i, this.right, this.left);
	}

}
