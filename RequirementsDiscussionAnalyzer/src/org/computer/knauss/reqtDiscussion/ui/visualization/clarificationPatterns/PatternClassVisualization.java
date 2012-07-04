package org.computer.knauss.reqtDiscussion.ui.visualization.clarificationPatterns;

import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;

import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.clarificationPatterns.BackToDraftPattern;
import org.computer.knauss.reqtDiscussion.model.clarificationPatterns.ClosingGatePattern;
import org.computer.knauss.reqtDiscussion.model.clarificationPatterns.HappyEndingPattern;
import org.computer.knauss.reqtDiscussion.model.clarificationPatterns.IPatternClass;
import org.computer.knauss.reqtDiscussion.model.clarificationPatterns.IndifferentPattern;
import org.computer.knauss.reqtDiscussion.model.clarificationPatterns.NoSharedUnderstandingPattern;
import org.computer.knauss.reqtDiscussion.model.clarificationPatterns.PerfectPattern;
import org.computer.knauss.reqtDiscussion.ui.visualization.ICommentOverTimePartition;

public class PatternClassVisualization extends AbstractVisualizationStyle {

	private static final IPatternClass[] PATTERNS = new IPatternClass[] {
			new IndifferentPattern(), new NoSharedUnderstandingPattern(),
			new ClosingGatePattern(), new BackToDraftPattern(),
			new HappyEndingPattern(), new PerfectPattern() };

	@Override
	public Shape[] getShape(DiscussionEvent comment) {
		return new Shape[0];
	}

	@Override
	public void reset() {

	}

	@Override
	public Color getFillColor(DiscussionEvent comment, int i) {
		// return Color.GREEN;
		return Color.DARK_GRAY;
	}

	@Override
	public Color getDrawColor(DiscussionEvent comment, int i) {
		return null;
	}

	@Override
	public Shape[] layout(DiscussionEvent[] comments) {
		StringBuffer sb = new StringBuffer("Pattern: ");
		for (IPatternClass pattern : PATTERNS) {
			if (pattern.matchesPattern(comments)) {
				sb.append(pattern.getName());
				sb.append(',');
			}
		}

		FontRenderContext frc1 = new FontRenderContext(null, true, true);
		Font f = new Font("SansSerif", Font.ITALIC, 12);
		GlyphVector v = f.createGlyphVector(frc1, sb.toString());

		return new Shape[] { v.getOutline(5, TXT_LINE_HEIGHT + 5) };
	}

	@Override
	public void setDiscussionOverTimePartition(
			ICommentOverTimePartition partition, int xOffset, int yOffset) {
		super.setDiscussionOverTimePartition(partition, xOffset, yOffset);
		for (IPatternClass p : PATTERNS) {
			p.setCommentPartition(partition);
		}
	}

}
