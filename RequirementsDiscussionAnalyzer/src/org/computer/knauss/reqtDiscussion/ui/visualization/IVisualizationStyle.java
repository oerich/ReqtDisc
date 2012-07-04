package org.computer.knauss.reqtDiscussion.ui.visualization;

import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;

import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;

public interface IVisualizationStyle {

	public static final int X_SIZE = 600;
	public static final int TXT_LINE_HEIGHT = 16;
	public static final int TXT_HORIZONTAL_MARGIN = 2;

	/**
	 * Sets the partition algorithm that aggregates discEvents.
	 * 
	 * @param partition
	 *            - the partition algorithm that aggregates discEvents.
	 * @param xOffset
	 *            - the offset that should be added to all x-coordinates.
	 * @param yOffset
	 *            - the offset that should be added to all y-coordinates.
	 */
	public void setDiscussionOverTimePartition(
			IDiscussionOverTimePartition partition, int xOffset, int yOffset);

	/**
	 * Returns a number of shapes that represent this discEvent. Depending on
	 * the implementation, the shapes may be created in this method. More
	 * sophisticated algorithms might cache shapes or return pre-computed
	 * results from the layout method.
	 * 
	 * @param discEvent
	 *            the DiscussionEvent that should be visualized
	 * @return a number of shapes that represent this discEvent.
	 */
	public Shape[] getShape(DiscussionEvent discEvent);

	/**
	 * Gives the algorithm a chance to layout all DiscussionEvents at once.
	 * Depending on the implementation, this can be handeled by a number of
	 * calls of the getShape method. More sophisticated algorithms use this
	 * method to pre-compute a layout and give faster results in the getShape
	 * method.
	 * 
	 * @param discEvents
	 *            - the discussion events that should be layouted
	 * @return the shapes that represent the given discEvents.
	 */
	public Shape[] layout(DiscussionEvent[] discEvents);

	/**
	 * Gives the algorithm a chance to set everything on default again. This is
	 * used before a scene is painted (again). The results of a series of calls
	 * of getShape should give identical results after each reset.
	 */
	public void reset();

	/**
	 * Return the color that should fill the i. shape returned by the getShape
	 * method for this discEvent.
	 * 
	 * @param i
	 * @return null, if this shape should not be filled.
	 */
	public Color getFillColor(DiscussionEvent discEvent, int i);

	/**
	 * Return the color that should draw the i. shape returned by the getShape
	 * method for this discEvent.
	 * 
	 * @param discEvent
	 *            - the discEvent that should be painted
	 * @param i
	 *            - the i. shape of the representation of this discEvent as
	 *            returned by the getShape-Method.
	 * @return the i. shape of the representation of this discEvent as returned
	 *         by the getShape-Method, null if there should not be an outline of
	 *         this shape.
	 */
	public Color getDrawColor(DiscussionEvent discEvent, int i);

	public Stroke getStroke();

}
