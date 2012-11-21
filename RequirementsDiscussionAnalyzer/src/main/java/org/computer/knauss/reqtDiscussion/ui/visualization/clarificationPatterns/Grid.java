package org.computer.knauss.reqtDiscussion.ui.visualization.clarificationPatterns;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import org.computer.knauss.reqtDiscussion.model.ModelElement;

public class Grid extends AbstractVisualizationStyle {

	private static final BasicStroke DASHED_STROKE = new BasicStroke(1f,
			BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[] {
					5f, 10f }, 2f);
	private boolean usePartitionForGrid = false;

	@Override
	public Shape[] getShape(ModelElement comment) {
		return new Shape[0];
	}

	public Shape[] layout(ModelElement[] comments) {
		if (!this.usePartitionForGrid)
			return new Shape[] { new Line2D.Double(100, 0, 100, 600),
					new Line2D.Double(250, 0, 250, 600),
					new Line2D.Double(400, 0, 400, 600),
					new Line2D.Double(550, 0, 550, 600),
					new Line2D.Double(700, 0, 700, 600), };
		if (this.partition == null)
			return new Shape[0];

		Shape[] ret = new Shape[this.partition.getPartitionCount() + 1];

		int n = (700 - 100) / this.partition.getPartitionCount();

		for (int i = 0; i < ret.length; i++) {
			ret[i] = new Line2D.Double(100 + i * n, 0, 100 + i * n, 700);
		}
		return ret;
	}

	@Override
	public void reset() {
	}

	@Override
	public Color getFillColor(ModelElement comment, int i) {
		return null;
	}

	@Override
	public Color getDrawColor(ModelElement comment, int i) {
		return Color.GRAY;
	}

	@Override
	public Stroke getStroke() {
		return DASHED_STROKE;
	}

	public void setUsePartition(boolean usePartitionForGrid) {
		this.usePartitionForGrid = usePartitionForGrid;
	}
}
