package org.computer.knauss.reqtDiscussion.ui.visualization;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ZoomPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private IZoomable zoomable;
	private Component zoomableParent;
	private JSlider zoomSlider;

	public ZoomPanel() {
		JButton fitToViewBtn = new JButton("fit to view");
		add(fitToViewBtn);
		fitToViewBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ZoomPanel.this.zoomable
						.zoomToFitRect(ZoomPanel.this.zoomableParent
								.getBounds());
				zoomSlider.setValue((int) (ZoomPanel.this.zoomable
						.getZoomFactor() * 10));
			}
		});

		this.zoomSlider = new JSlider(2, 20, 10);
		Dictionary<Integer, JComponent> labels = new Hashtable<Integer, JComponent>();
		labels.put(2, new JLabel("-"));
		labels.put(20, new JLabel("+"));
		this.zoomSlider.setLabelTable(labels);
		this.zoomSlider.setPaintLabels(true);
		this.zoomSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				ZoomPanel.this.zoomable.setZoomFactor(zoomSlider.getValue() / 10d);
			}
		});
		add(this.zoomSlider);
	}

	public void setZoomable(IZoomable zoomable) {
		this.zoomable = zoomable;
	}

	public void setZoomableParent(Component zoomableParent) {
		this.zoomableParent = zoomableParent;
	}

}
