package org.computer.knauss.reqtDiscussion.ui.visualization.clarificationPatterns;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.ui.visualization.IDiscussionOverTimePartition;

public class Grid extends AbstractVisualizationStyle {

	private static final BasicStroke DASHED_STROKE = new BasicStroke(1f,
			BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[] {
					5f, 10f }, 2f);

	@Override
	public void setDiscussionOverTimePartition(
			IDiscussionOverTimePartition partition, int xOffset, int yOffset) {
	}

	@Override
	public Shape[] getShape(DiscussionEvent comment) {
		return new Shape[0];
	}

	public Shape[] layout(DiscussionEvent[] comments) {
		return new Shape[] { new Line2D.Double(100, 0, 100, 600),
				new Line2D.Double(250, 0, 250, 600),
				new Line2D.Double(400, 0, 400, 600),
				new Line2D.Double(550, 0, 550, 600),
				new Line2D.Double(700, 0, 700, 600), };
	}

	@Override
	public void reset() {
	}

	@Override
	public Color getFillColor(DiscussionEvent comment, int i) {
		return null;
	}

	@Override
	public Color getDrawColor(DiscussionEvent comment, int i) {
		return Color.GRAY;
	}

	@Override
	public Stroke getStroke() {
		return DASHED_STROKE;
	}

	public void setUsePartition(boolean usePartitionForGrid) {
		// TODO Auto-generated method stub

	}
}
