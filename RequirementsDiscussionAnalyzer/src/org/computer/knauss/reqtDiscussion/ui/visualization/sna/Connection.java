package org.computer.knauss.reqtDiscussion.ui.visualization.sna;

public class Connection<WeightType, NodeType> {

	private NodeType target;
	private WeightType weight;

	public NodeType getTarget() {
		return target;
	}

	public void setTarget(NodeType target) {
		this.target = target;
	}

	public WeightType getWeight() {
		return weight;
	}

	public void setWeight(WeightType weight) {
		this.weight = weight;
	}

}
