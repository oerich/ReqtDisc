package org.computer.knauss.reqtDiscussion.ui.visualization.clarificationPatterns;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.ui.visualization.IVisualizationStyle;

public class GreyBackgroundBox extends AbstractVisualizationStyle implements
		IVisualizationStyle {

	// private static final Color BACKGROUND_COLOR = new Color(229,229,229);
	private static final Color BACKGROUND_COLOR = new Color(242, 242, 242);

	@Override
	public Shape[] getShape(DiscussionEvent comment) {
		return new Shape[0];
	}

	@Override
	public Shape[] layout(DiscussionEvent[] comments) {
		this.partition.setWorkitemComments(comments);

		// figure out how large the box should be:
		int maxClarification = 0;
		int maxOther = 0;

		for (int i = 0; i < this.partition.getPartitionCount(); i++) {
			int clarificationPerPartition = 0;
			int otherPerPartition = 0;
			for (DiscussionEvent wc : this.partition
					.getWorkitemsForPartition(i)) {
				if (this.partition.isInClass(wc)) {
					clarificationPerPartition++;
				} else {
					otherPerPartition++;
				}
			}
			if (clarificationPerPartition > maxClarification)
				maxClarification = clarificationPerPartition;
			if (otherPerPartition > maxOther)
				maxOther = otherPerPartition;
		}

		int yMinus = maxOther
				* (AlignedRectangularCommentStyle.COMMENT_HEIGHT + AlignedRectangularCommentStyle.VERTICAL_COMMENT_GAP)
				+ AlignedRectangularCommentStyle.VERTICAL_COMMENT_GAP;
		int yPlus = maxClarification
				* (AlignedRectangularCommentStyle.COMMENT_HEIGHT + AlignedRectangularCommentStyle.VERTICAL_COMMENT_GAP)
				+ AlignedRectangularCommentStyle.VERTICAL_COMMENT_GAP;
		return new Shape[] { new RoundRectangle2D.Double(this.xOffset - 10,
				this.yOffset - yMinus - 10, X_SIZE + 20, yMinus + yPlus + 20,
				15, 15) };
	}

	@Override
	public void reset() {
	}

	@Override
	public Color getFillColor(DiscussionEvent comment, int i) {
		return BACKGROUND_COLOR;
	}

	@Override
	public Color getDrawColor(DiscussionEvent comment, int i) {
		return BACKGROUND_COLOR;
	}

}
