package org.computer.knauss.reqtDiscussion.model.socialNetwork;

public class Node {

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
	
//	public String toString() {
//		return this.label;
//	}
//	
//	public boolean equals(Object o){
//		return this.label.equals(o);
//	}
}
