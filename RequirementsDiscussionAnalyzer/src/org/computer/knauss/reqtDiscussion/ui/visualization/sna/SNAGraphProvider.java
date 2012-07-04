package org.computer.knauss.reqtDiscussion.ui.visualization.sna;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.luh.pi.geometry.IBounds;
import de.luh.pi.geometry.ISize;
import de.luh.pi.geometry.Size;
import de.luh.pi.layouting.IGeometricalLayoutConstraint;
import de.luh.pi.layouting.IGraphProvider;

public class SNAGraphProvider implements
		IGraphProvider<SocialNetwork, Connection<Double, Node>, Node> {

	private Map<Node, Point2D.Double> positions = new HashMap<Node, Point2D.Double>();
	private double minWeight = 0;

	public void printMatrix(SocialNetwork graph) {
		List<Node> actors = getNodes(graph);
		for (int i = 0; i < actors.size(); i++) {
			System.out.println(i + " - " + actors.get(i).getLabel());
		}

		System.out.print(' ');
		for (int i = 0; i < actors.size(); i++) {
			System.out.print(i);
		}
		System.out.println();
		for (int i = 0; i < actors.size(); i++) {
			System.out.print(i);
			List<Connection<Double, Node>> cs = getSuccessors(graph,
					actors.get(i));
			for (int j = 0; j < actors.size(); j++) {
				boolean found = false;
				for (Connection<Double, Node> c : cs) {
					if (c.getTarget().equals(actors.get(j)))
						found = true;
				}
				if (found)
					System.out.print('x');
				else
					System.out.print(' ');
			}
			System.out.println();
		}
	}

	@Override
	public List<Node> getNodes(SocialNetwork graph) {
		return Arrays.asList(graph.getActors());
	}

	@Override
	public List<Connection<Double, Node>> getSuccessors(SocialNetwork graph,
			Node node) {
		Node[] actors = graph.getActors();
		List<Connection<Double, Node>> ret = new LinkedList<Connection<Double, Node>>();
		for (Node a : actors) {
			if (!node.equals(a)) {
				double weight = graph.getWeight(node, a);
				if (weight > minWeight) {
					Connection<Double, Node> c = new Connection<Double, Node>();
					c.setTarget(a);
					c.setWeight(weight);
					ret.add(c);
				}
			}
		}
		return ret;
	}

	@Override
	public Node getConnectionTarget(SocialNetwork graph,
			Connection<Double, Node> connection) {
		return connection.getTarget();
	}

	@Override
	public ISize getPreferredSize(SocialNetwork graph, Node node) {
		return new Size();
	}

	@Override
	public void placeNode(SocialNetwork graph, Node node, IBounds bounds) {
		if (!positions.containsKey(node)) {
			positions.put(node,
					new Point2D.Double(bounds.getX(), bounds.getY()));
		} else {
			Point2D.Double p = positions.get(node);
			p.x = bounds.getX();
			p.y = bounds.getY();
		}
	}

	@Override
	public void hideNode(SocialNetwork graph, Node node) {
		// TODO Auto-generated method stub

	}

	@Override
	public IGeometricalLayoutConstraint[] getLayoutConstraints(
			SocialNetwork graph, Node node) {
		return new IGeometricalLayoutConstraint[0];
	}
	
	public Point2D.Double getPosition(Node a) {
		return this.positions.get(a);
	}
	
	public void setMinWeight(double w) {
		this.minWeight = w;
	}
}