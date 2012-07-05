package org.computer.knauss.reqtDiscussion.ui.visualization.clarificationPatterns;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;

import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.partition.IDiscussionOverTimePartition;
import org.computer.knauss.reqtDiscussion.ui.visualization.IVisualizationStyle;

public abstract class AbstractVisualizationStyle implements IVisualizationStyle {

	private static final BasicStroke BASIC_STROKE = new BasicStroke(1f);
	protected int yOffset;
	protected int xOffset;
	protected IDiscussionOverTimePartition partition;

	@Override
	public abstract Shape[] getShape(DiscussionEvent comment);

	@Override
	public Shape[] layout(DiscussionEvent[] comments) {
		// List<Shape> tmp = new Vector<Shape>(comments.length);
		// for (WorkitemComment wc : comments) {
		// for (Shape s : getShape(wc)) {
		// tmp.add(s);
		// }
		// }
		// return tmp.toArray(new Shape[0]);

		// This method is optional and should be used for two purposes:
		// 1. allow for more sophisticated layout up front
		// 2. return some background for all shapes.
		return new Shape[0];
	}

	@Override
	public abstract void reset();

	@Override
	public abstract Color getFillColor(DiscussionEvent comment, int i);

	@Override
	public abstract Color getDrawColor(DiscussionEvent comment, int i);

	@Override
	public Stroke getStroke() {
		return BASIC_STROKE;
	}

	@Override
	public void setDiscussionOverTimePartition(
			IDiscussionOverTimePartition partition, int xOffset, int yOffset) {
		this.partition = partition;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
}
