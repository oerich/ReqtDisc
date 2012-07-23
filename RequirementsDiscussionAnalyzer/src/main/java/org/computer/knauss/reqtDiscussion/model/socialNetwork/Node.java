package org.computer.knauss.reqtDiscussion.model.socialNetwork;

public class Node implements Comparable<Node> {

	private String label;
	private int clarification;
	private int coordination;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getClarification() {
		return clarification;
	}

	public void setClarification(int clarification) {
		this.clarification = clarification;
	}

	public int getCoordination() {
		return coordination;
	}

	public void setCoordination(int coordination) {
		this.coordination = coordination;
	}

	@Override
	public int compareTo(Node o) {
		if (getLabel() == null || o.getLabel() == null)
			return 0;
		return getLabel().compareTo(o.getLabel());
	}

	// public String toString() {
	// return this.label;
	// }
	//
	// public boolean equals(Object o){
	// return this.label.equals(o);
	// }
}
