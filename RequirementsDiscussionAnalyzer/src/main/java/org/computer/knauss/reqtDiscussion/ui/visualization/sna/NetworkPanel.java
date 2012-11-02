package org.computer.knauss.reqtDiscussion.ui.visualization.sna;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Point2D;

import javax.swing.JPanel;
import javax.swing.JViewport;

import org.computer.knauss.reqtDiscussion.model.socialNetwork.Connection;
import org.computer.knauss.reqtDiscussion.model.socialNetwork.Node;
import org.computer.knauss.reqtDiscussion.model.socialNetwork.SNAGraphProvider;
import org.computer.knauss.reqtDiscussion.model.socialNetwork.SocialNetwork;
import org.computer.knauss.reqtDiscussion.ui.visualization.IZoomable;

import de.te.layouting.geometry.Bounds;
import de.te.layouting.geometry.IBounds;
import de.te.layouting.layouting.forcebased.ForceBasedLayouter;
import de.te.layouting.layouting.forcebased.forces.BorderRepulsionForce;
import de.te.layouting.layouting.forcebased.forces.CoulumbNodeRepulsionForce;
import de.te.layouting.layouting.forcebased.forces.HookeConnectionForce;

public class NetworkPanel extends JPanel implements IZoomable {

	private static final Bounds BOUNDS = new Bounds(0, 0, 2000, 2000);
	private static final long serialVersionUID = 1L;
	private IScaler scaler = IScaler.DEFAULT_SCALER;
	private SocialNetwork network;
	private ForceBasedLayouter<SocialNetwork, Connection<Double, Node>, Node> layouter;
	private SNAGraphProvider graphProvider;
	private double zoomFactor = 1.0d;
	private BorderRepulsionForce borderRepulsionForce = new BorderRepulsionForce(
			new Bounds());
	private Rectangle captureRect;

	public NetworkPanel() {
		this.graphProvider = new SNAGraphProvider();
		setPreferredSize(new Dimension((int) BOUNDS.getWidth(),
				(int) BOUNDS.getHeight()));

	}

	public SocialNetwork getNetwork() {
		return network;
	}

	public void zoomToFitRect(Rectangle rect) {
		double xZoom = rect.getWidth() / BOUNDS.getWidth();
		double yZoom = rect.getHeight() / BOUNDS.getHeight();

		setZoomFactor(Math.min(xZoom, yZoom));
	}

	public void zoomToFillRect(Rectangle rect) {
		double xZoom = (BOUNDS.getWidth()) / rect.getWidth();
		double yZoom = (BOUNDS.getHeight()) / rect.getHeight();

		setZoomFactor(Math.min(xZoom, yZoom));
	}

	public void setNetwork(SocialNetwork network) {
		this.network = network;
		// this.graphProvider.printMatrix(network);

		this.graphProvider.setMinWeight(this.network.getEdgeCutoffWeight());

		int nodeCount = network.getActors().length;
		BOUNDS.setHeight(nodeCount * 100);
		BOUNDS.setWidth(nodeCount * 100);

		IBounds b = this.borderRepulsionForce.getBounds();
		b.setX(BOUNDS.getX() - 10);
		b.setY(BOUNDS.getY() - 10);
		b.setWidth(BOUNDS.getWidth() + 10);
		b.setHeight(BOUNDS.getHeight() + 10);

		getLayouter().initialize(this.network, this.graphProvider, BOUNDS, 200);
		getLayouter().layout(1);

		setPreferredSize(new Dimension(
				(int) (BOUNDS.getWidth() * this.zoomFactor),
				(int) (BOUNDS.getHeight() * this.zoomFactor)));
		repaint();
	}

	public ForceBasedLayouter<SocialNetwork, Connection<Double, Node>, Node> getLayouter() {
		if (this.layouter == null) {
			this.layouter = new ForceBasedLayouter<SocialNetwork, Connection<Double, Node>, Node>();
			this.layouter.setDamping(0.6);

			this.layouter.addForce(new HookeConnectionForce());
			this.layouter.addForce(new CoulumbNodeRepulsionForce());

			// Will be initialized when the network is set...
			this.layouter.addForce(borderRepulsionForce);

		}
		return this.layouter;
	}

	public void setZoomFactor(double d) {
		// unscaled center:
		Point2D oCenter = getViewportCenter();
		oCenter.setLocation(oCenter.getX()/ this.zoomFactor, oCenter.getY() / this.zoomFactor);

		this.zoomFactor = d;
		setPreferredSize(new Dimension(
				(int) (BOUNDS.getWidth() * this.zoomFactor),
				(int) (BOUNDS.getHeight() * this.zoomFactor)));
		
		// scale center to new zoomfactor and set it:
		oCenter.setLocation(oCenter.getX()*this.zoomFactor, oCenter.getY() * this.zoomFactor);
		setViewportCenter(oCenter);
//		revalidate();
		invalidate();
		repaint();
	}

	private Point2D getViewportCenter() {
		JViewport vp = (JViewport) this.getParent();
		Point2D p = vp.getViewPosition();
		return new Point2D.Double(p.getX() + vp.getWidth() / 2, p.getY() + vp.getHeight() / 2);
	}

	private void setViewportCenter(Point2D p) {
		JViewport vp = (JViewport) this.getParent();
		Rectangle viewRect = vp.getViewRect();

//		viewRect.x = (int) (p.getX() - viewRect.width / 2);
//		viewRect.y = (int) (p.getY() - viewRect.height / 2);
		viewRect.x = (int) (p.getX());
		viewRect.y = (int) (p.getY());

		scrollRectToVisible(viewRect);
	}

	@Override
	public void paint(Graphics g) {
		g.clearRect(getBounds().x, getBounds().y, getBounds().width,
				getBounds().height);
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		((Graphics2D) g).scale(zoomFactor, zoomFactor);
		g.setColor(Color.white);
		g.fillRect(0, 0, (int) BOUNDS.getWidth(), (int) BOUNDS.getHeight());
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, (int) BOUNDS.getWidth(), (int) BOUNDS.getHeight());
		for (Node a : this.network.getActors()) {
			g.setColor(Color.GRAY);

			Point2D.Double pos = this.graphProvider.getPosition(a);
			for (Connection<Double, Node> c : this.graphProvider.getSuccessors(
					this.network, a)) {
				Point2D.Double pos2 = this.graphProvider.getPosition(c
						.getTarget());

				Stroke otherStroke = ((Graphics2D) g).getStroke();
				((Graphics2D) g).setStroke(new BasicStroke((float) this.scaler
						.scaleDown(c.getWeight(), 32)));
				g.drawLine((int) pos.x, (int) pos.y, (int) pos2.x, (int) pos2.y);
				((Graphics2D) g).setStroke(otherStroke);
			}
		}
		for (Node a : this.network.getActors())
			paintNode(a, this.graphProvider.getPosition(a), g);

		((Graphics2D) g).scale(1 / zoomFactor, 1 / zoomFactor);
		if (this.captureRect != null) {
			g.setColor(Color.RED);
			((Graphics2D) g).draw(captureRect);
			g.setColor(new Color(255, 255, 255, 150));
			((Graphics2D) g).fill(captureRect);
		}
	}

	private void paintNode(Node a, Point2D.Double pos, Graphics g) {
		Slice[] slices = new Slice[] {
				new Slice(a.getClarification(), Color.RED),
				new Slice(a.getCoordination(), Color.BLUE) };

		int d = 8 + a.getClarification() + a.getCoordination();
		d = (int) this.scaler.scaleDown(d, 64);

		paintPie((Graphics2D) g, new Rectangle((int) pos.x - d / 2, (int) pos.y
				- d / 2, d, d), slices);

		g.setColor(Color.BLACK);
		g.drawString(a.getLabel(), (int) pos.x + d / 2, (int) pos.y);
	}

	/**
	 * In parts from http://www.tutorialspoint.com/javaexamples/gui_piechart.htm
	 * 
	 * @param g
	 * @param area
	 * @param slices
	 */
	void paintPie(Graphics2D g, Rectangle area, Slice[] slices) {
		double total = 0.0D;
		for (int i = 0; i < slices.length; i++) {
			total += slices[i].value;
		}
		double curValue = 0.0D;
		int startAngle = 0;
		for (int i = 0; i < slices.length; i++) {
			startAngle = (int) (curValue * 360 / total);
			int arcAngle = (int) (slices[i].value * 360 / total);
			g.setColor(slices[i].color);
			g.fillArc(area.x, area.y, area.width, area.height, startAngle,
					arcAngle);
			curValue += slices[i].value;
		}
	}

	private class Slice {
		double value;
		Color color;

		public Slice(double value, Color color) {
			this.value = value;
			this.color = color;
		}
	}

	public double getZoomFactor() {
		return this.zoomFactor;
	}

	public void setScaler(IScaler scaler) {
		this.scaler = scaler;
	}

	public Rectangle getCaptureRect() {
		return this.captureRect;
	}

	public void setCaptureRect(Rectangle r) {
		this.captureRect = r;
	}
}
