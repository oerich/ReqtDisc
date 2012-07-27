package org.computer.knauss.reqtDiscussion.ui.visualization.clarificationPatterns;

import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Area;
import java.awt.geom.Line2D;

import org.computer.knauss.reqtDiscussion.model.Incident;
import org.computer.knauss.reqtDiscussion.model.ModelElement;
import org.computer.knauss.reqtDiscussion.model.partition.IDiscussionOverTimePartition;
import org.computer.knauss.reqtDiscussion.ui.visualization.IVisualizationStyle;

public class IncidentTextStyle extends AbstractVisualizationStyle
		implements IVisualizationStyle {

	private IDiscussionOverTimePartition partition;
	private int upperOffset;
	private int lowerOffset;
	private int xOffset;
	private int yOffset;

	@Override
	public void setDiscussionOverTimePartition(
			IDiscussionOverTimePartition partition, int xOffset, int yOffset) {
		this.partition = partition;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	@Override
	public Shape[] getShape(ModelElement comment) {
		if (!(comment instanceof Incident))
			return new Shape[0];
		
		Incident incident = (Incident)comment;
		int x = this.partition.getPartitionForModelElement(incident)
				+ this.xOffset;
		int y1 = this.yOffset;
		int y2 = getWorkitemCommentY(incident);

		FontRenderContext frc1 = new FontRenderContext(null, true, true);
		FontRenderContext frc2 = new FontRenderContext(null, true, true);

		
		Font f = new Font("SansSerif", Font.PLAIN, 12);
		GlyphVector v1 = f.createGlyphVector(frc1, incident.getName());
		GlyphVector v2 = f.createGlyphVector(frc2, incident.getSummary());

		Shape line = new Line2D.Double(x, y1, x, y2);

		Area a = new Area(line);
		a.add(new Area(v1.getOutline()));
		a.add(new Area(v2.getOutline()));
		return new Shape[] { line,
				v1.getOutline(x + TXT_HORIZONTAL_MARGIN, y2),
				v2.getOutline(x + TXT_HORIZONTAL_MARGIN, y2 + TXT_LINE_HEIGHT) };
	}

	private int getWorkitemCommentY(ModelElement wic) {
		if (this.partition.isInClass(wic))
			return this.yOffset + getUpperOffset();
		return this.yOffset + getLowerOffset();
	}

	private int getUpperOffset() {
		if (this.upperOffset == 7)
			this.upperOffset = 1;
		else
			this.upperOffset++;
		return 40 * this.upperOffset;
	}

	private int getLowerOffset() {
		if (this.lowerOffset == 7)
			this.lowerOffset = 1;
		else
			this.lowerOffset++;
		return -40 * this.lowerOffset;
	}

	@Override
	public void reset() {
		this.upperOffset = 0;
		this.lowerOffset = 0;
	}

	@Override
	public Color getFillColor(ModelElement comment, int i) {
		if (i == 0)
			return null;
		return Color.BLACK;
	}

	@Override
	public Color getDrawColor(ModelElement comment, int i) {
		if (i == 0)
			return Color.BLACK;
		return null;
	}

}
