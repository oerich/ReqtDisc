package org.computer.knauss.reqtDiscussion.ui.visualization.clarificationPatterns;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import org.computer.knauss.reqtDiscussion.model.ModelElement;

public class CommunicationFocusBoxStyle extends AbstractVisualizationStyle {

	@Override
	public Shape[] getShape(ModelElement modelElement) {
		Shape ret = null;
		double partitionWidth = (double) X_SIZE
				/ (double) this.partition.getPartitionCount();

		int i = this.partition.getPartitionForModelElement(modelElement);
		if (!this.partition.isInClass(modelElement))
			// first: coordination
			ret = new Rectangle2D.Double(this.xOffset + i * partitionWidth, this.yOffset
					- partitionWidth, partitionWidth, partitionWidth);
		else
			// second: clarification
			ret = new Rectangle2D.Double(this.xOffset + i * partitionWidth, this.yOffset,
					partitionWidth, partitionWidth);

		return new Shape[] { ret };
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public Color getFillColor(ModelElement modelElement, int i) {
		boolean isClarification = this.partition.isInClass(modelElement);
		int partition = this.partition
				.getPartitionForModelElement(modelElement);

		int clarifCount = 0, coordCount = 0;
		for (ModelElement me : this.partition
				.getModelElementsForPartition(partition)) {
			if (this.partition.isInClass(me))
				clarifCount++;
			else if (this.partition.isClassified(me))
				coordCount++;
		}

		if ((isClarification && clarifCount > coordCount) || !isClarification
				&& coordCount > clarifCount)
			return Color.LIGHT_GRAY;
		return Color.white;
	}

	@Override
	public Color getDrawColor(ModelElement modelElement, int i) {
		return Color.GRAY;
	}

}
