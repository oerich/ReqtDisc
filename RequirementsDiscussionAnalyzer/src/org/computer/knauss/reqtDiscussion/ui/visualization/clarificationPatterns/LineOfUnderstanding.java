package org.computer.knauss.reqtDiscussion.ui.visualization.clarificationPatterns;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Vector;

import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.partition.IDiscussionOverTimePartition;

public class LineOfUnderstanding extends AbstractVisualizationStyle {

	private static final BasicStroke PT10_STROKE = new BasicStroke(10,
			BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	private IDiscussionOverTimePartition partition;
	private int xOffset;
	private int yOffset;
	private boolean interpolarize;

	@Override
	public void setDiscussionOverTimePartition(
			IDiscussionOverTimePartition partition, int xOffset, int yOffset) {
		this.partition = partition;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	@Override
	public Shape[] getShape(DiscussionEvent comment) {
		return new Shape[0];
	}

	@Override
	public void reset() {

	}

	@Override
	public Color getFillColor(DiscussionEvent comment, int i) {
		// return Color.GRAY;
		return null;
	}

	@Override
	public Color getDrawColor(DiscussionEvent comment, int i) {
		return Color.GRAY;
	}

	@Override
	public Stroke getStroke() {
		return PT10_STROKE;
	}

	@Override
	public Shape[] layout(DiscussionEvent[] comments) {
		this.partition.setDiscussionEvents(comments);
		double partitionWidth = (double) X_SIZE
				/ (double) this.partition.getPartitionCount();
		List<Point2D> points = new Vector<Point2D>();
		points.add(new Point2D.Double(this.xOffset, this.yOffset));

		for (int i = 0; i < this.partition.getPartitionCount(); i++) {
			int clarificationPerPartition = 0;
			int otherPerPartition = 0;
			for (DiscussionEvent wc : this.partition
					.getDiscussionEventForPartition(i)) {
				if (this.partition.isInClass(wc)) {
					clarificationPerPartition++;
				} else {
					otherPerPartition++;
				}
			}

			if (clarificationPerPartition != 0 || otherPerPartition != 0) {
				double xVal = this.xOffset + i * partitionWidth
						+ partitionWidth / 2;
				int understanding = clarificationPerPartition
						- otherPerPartition;
				double yVal = this.yOffset
						+ understanding
						* (AlignedRectangularCommentStyle.COMMENT_HEIGHT + AlignedRectangularCommentStyle.VERTICAL_COMMENT_GAP);
				points.add(new Point2D.Double(xVal, yVal));
			}
		}

		Shape[] ret = new Shape[points.size() - 1];

		try {
			BezierSpline bs = new BezierSpline(
					points.toArray(new Point2D.Double[0]));
			for (int i = 0; i < points.size() - 1; i++) {
				if (!this.interpolarize) {
					ret[i] = new Line2D.Double(points.get(i), points.get(i + 1));
				} else {
					Point2D p1 = points.get(i);
					Point2D p2 = points.get(i + 1);

					ret[i] = new CubicCurve2D.Double(p1.getX(), p1.getY(),
							bs.firstControlPoints[i].getX(),
							bs.firstControlPoints[i].getY(),
							bs.secondControlPoints[i].getX(),
							bs.secondControlPoints[i].getY(), p2.getX(),
							p2.getY());
				}
			}
		} catch (IllegalArgumentException e) {
			// nothing todo - we cannot paint a line if there are no points.
			return new Shape[0];
		}

		return ret;
	}

	// / <summary>
	// / Bezier Spline methods
	// / </summary>
	public class BezierSpline {

		Point2D.Double[] firstControlPoints, secondControlPoints;

		// / <summary>
		// / Get open-ended Bezier Spline Control Points.
		// / </summary>
		// / <param name="knots">Input Knot Bezier spline points.</param>
		// / <param name="firstControlPoints">Output First Control points
		// / array of knots.Length - 1 length.</param>
		// / <param name="secondControlPoints">Output Second Control points
		// / array of knots.Length - 1 length.</param>
		// / <exception cref="ArgumentNullException"><paramref name="knots"/>
		// / parameter must be not null.</exception>
		// / <exception cref="ArgumentException"><paramref name="knots"/>
		// / array must contain at least two points.</exception>
		public BezierSpline(Point2D[] knots) {
			if (knots == null)
				throw new IllegalArgumentException("knots");
			int n = knots.length - 1;
			if (n < 1)
				throw new IllegalArgumentException(
						"At least two knot points required");
			if (n == 1) { // Special case: Bezier curve should be a straight
							// line.
				firstControlPoints = new Point2D.Double[1];
				// 3P1 = 2P0 + P3
				firstControlPoints[0] = new Point2D.Double(
						(2 * knots[0].getX() + knots[1].getX()) / 3,
						(2 * knots[0].getY() + knots[1].getY()) / 3);

				secondControlPoints = new Point2D.Double[1];
				// P2 = 2P1 � P0
				secondControlPoints[0] = new Point2D.Double(2
						* firstControlPoints[0].getX() - knots[0].getX(), 2
						* firstControlPoints[0].getY() - knots[0].getY());
				return;
			}

			// Calculate first Bezier control points
			// Right hand side vector
			double[] rhs = new double[n];

			// Set right hand side X values
			for (int i = 1; i < n - 1; ++i)
				rhs[i] = 4 * knots[i].getX() + 2 * knots[i + 1].getX();
			rhs[0] = knots[0].getX() + 2 * knots[1].getX();
			rhs[n - 1] = (8 * knots[n - 1].getX() + knots[n].getX()) / 2.0;
			// Get first control points X-values
			double[] x = GetFirstControlPoints(rhs);

			// Set right hand side Y values
			for (int i = 1; i < n - 1; ++i)
				rhs[i] = 4 * knots[i].getY() + 2 * knots[i + 1].getY();
			rhs[0] = knots[0].getY() + 2 * knots[1].getY();
			rhs[n - 1] = (8 * knots[n - 1].getY() + knots[n].getY()) / 2.0;
			// Get first control points Y-values
			double[] y = GetFirstControlPoints(rhs);

			// Fill output arrays.
			firstControlPoints = new Point2D.Double[n];
			secondControlPoints = new Point2D.Double[n];
			for (int i = 0; i < n; ++i) {
				// First control point
				firstControlPoints[i] = new Point2D.Double(x[i], y[i]);
				// Second control point
				if (i < n - 1)
					secondControlPoints[i] = new Point2D.Double(2
							* knots[i + 1].getX() - x[i + 1], 2
							* knots[i + 1].getY() - y[i + 1]);
				else
					secondControlPoints[i] = new Point2D.Double(
							(knots[n].getX() + x[n - 1]) / 2,
							(knots[n].getY() + y[n - 1]) / 2);
			}
		}

		// / <summary>
		// / Solves a tridiagonal system for one of coordinates (x or y)
		// / of first Bezier control points.
		// / </summary>
		// / <param name="rhs">Right hand side vector.</param>
		// / <returns>Solution vector.</returns>
		private double[] GetFirstControlPoints(double[] rhs) {
			int n = rhs.length;
			double[] x = new double[n]; // Solution vector.
			double[] tmp = new double[n]; // Temp workspace.

			double b = 2.0;
			x[0] = rhs[0] / b;
			for (int i = 1; i < n; i++) // Decomposition and forward
										// substitution.
			{
				tmp[i] = 1 / b;
				b = (i < n - 1 ? 4.0 : 3.5) - tmp[i];
				x[i] = (rhs[i] - x[i - 1]) / b;
			}
			for (int i = 1; i < n; i++)
				x[n - i - 1] -= tmp[n - i] * x[n - i]; // Backsubstitution.

			return x;
		}
	}

	public void setInterpolarize(boolean lsuInterpolarization) {
		this.interpolarize = lsuInterpolarization;
	}
}
