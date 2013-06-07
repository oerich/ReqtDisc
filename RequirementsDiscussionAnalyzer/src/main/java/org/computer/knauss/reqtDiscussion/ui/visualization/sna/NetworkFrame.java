package org.computer.knauss.reqtDiscussion.ui.visualization.sna;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.sql.Date;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.computer.knauss.reqtDiscussion.Util;
import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.DiscussionFactory;
import org.computer.knauss.reqtDiscussion.model.VisualizationConfiguration;
import org.computer.knauss.reqtDiscussion.model.metric.AbstractNetworkMetric;
import org.computer.knauss.reqtDiscussion.model.partition.FixedNumberPartition;
import org.computer.knauss.reqtDiscussion.model.partition.IDiscussionOverTimePartition;
import org.computer.knauss.reqtDiscussion.model.partition.TimeIntervalPartition;
import org.computer.knauss.reqtDiscussion.model.socialNetwork.Node;
import org.computer.knauss.reqtDiscussion.model.socialNetwork.PartitionedSocialNetwork;
import org.computer.knauss.reqtDiscussion.model.socialNetwork.ProximitySocialNetwork;
import org.computer.knauss.reqtDiscussion.model.socialNetwork.SocialNetwork;
import org.computer.knauss.reqtDiscussion.ui.visualization.ZoomPanel;

public class NetworkFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private TimerTask timer = new TimerTask() {

		@Override
		public void run() {
			if (play) {
				networkPanel.getLayouter().layout(1);
				networkPanel.repaint();
			}
		}

	};
	private JButton playButton;
	private JComboBox socialNetworkBox;
	private JLabel metricLabel;
	private JSlider cutoffSlider;
	private JSpinner weightSpinner;

	private NetworkPanel networkPanel;
	private Discussion[] discussions;
	private boolean play;

	private VisualizationConfiguration configuration;
	private JCheckBox scaleElementsBox;
	private ZoomPanel zoomPanel;
	private JScrollPane jScrollPane;
	private JPanel networkPropertiesPanel;
	private JPanel ctrlPanel;
	private JMenuBar menu;

	private boolean showNetworkProperties = true;
	private boolean showZoomPanel = true;

	public NetworkFrame(VisualizationConfiguration configuration) {
		super("Social Network Analysis");

		this.configuration = configuration;

		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setLayout(new BorderLayout());

		addElements();
		setJMenuBar(getMenu());

		pack();
		Timer t = new Timer();
		t.schedule(this.timer, 50, 100);
	}

	private void addElements() {
		add(getScrollPane(), BorderLayout.CENTER);

		ctrlPanel = new JPanel();
		add(ctrlPanel, BorderLayout.WEST);
		ctrlPanel.setLayout(new BoxLayout(ctrlPanel, BoxLayout.PAGE_AXIS));

		if (this.showNetworkProperties) {
			JPanel tmp = new JPanel();
			tmp.add(getNetworkPropertiesPanel());
			ctrlPanel.add(tmp);
		}
		if (this.showZoomPanel) {
			JPanel tmp = new JPanel();
			tmp.add(getZoomPanel());
			ctrlPanel.add(tmp);
		}

		this.metricLabel = new JLabel("");
		add(this.metricLabel, BorderLayout.SOUTH);

	}

	private JScrollPane getScrollPane() {
		if (this.jScrollPane == null)
			jScrollPane = new JScrollPane(getNetworkPanel());
		return this.jScrollPane;
	}

	private NetworkPanel getNetworkPanel() {
		if (this.networkPanel == null)
			this.networkPanel = new NetworkPanel();
		return this.networkPanel;
	}

	private JSlider getCutoffSlider() {
		if (this.cutoffSlider == null) {
			this.cutoffSlider = new JSlider();
			this.cutoffSlider.setPaintLabels(true);
			this.cutoffSlider.setValue(0);
			this.cutoffSlider.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent arg0) {
					weightSpinner.setValue((double) cutoffSlider.getValue());
					setWorkitems(discussions);
				}
			});
		}
		return cutoffSlider;
	}

	private JCheckBox getScaleElementsBox() {
		if (scaleElementsBox == null) {
			scaleElementsBox = new JCheckBox("Scale elements");
			scaleElementsBox.setSelected(true);
			scaleElementsBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (scaleElementsBox.isSelected())
						networkPanel.setScaler(IScaler.DEFAULT_SCALER);
					else
						networkPanel.setScaler(IScaler.NULL_SCALER);
				}
			});
		}
		return scaleElementsBox;
	}

	private ZoomPanel getZoomPanel() {
		if (this.zoomPanel == null) {
			zoomPanel = new ZoomPanel();
			zoomPanel.setBorder(BorderFactory.createTitledBorder("Zoom"));
			zoomPanel.setLayout(new GridLayout(3, 1));
			zoomPanel.setZoomable(getNetworkPanel());
			zoomPanel.setZoomableParent(getScrollPane());

			networkPanel.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						networkPanel.setZoomFactor(2 * networkPanel
								.getZoomFactor());
					}

				}

			});

			// networkPanel.addMouseMotionListener(new MouseMotionAdapter() {
			// Point start = new Point();
			//
			// @Override
			// public void mouseMoved(MouseEvent me) {
			// // XXX move this to NetworkPanel or AbstractZoomable!
			// Rectangle captureRect = networkPanel.getCaptureRect();
			// if (captureRect != null) {
			// captureRect = new Rectangle(networkPanel.getCaptureRect());
			// // where is the rectangle without zoom?
			// captureRect.height /= networkPanel.getZoomFactor();
			// captureRect.width /= networkPanel.getZoomFactor();
			// captureRect.x /= networkPanel.getZoomFactor();
			// captureRect.y /= networkPanel.getZoomFactor();
			//
			// // use the initial rectangle
			// networkPanel.zoomToFillRect(networkPanel.getCaptureRect());
			//
			// // where is the rectangle with the new zoom?
			// captureRect.height *= networkPanel.getZoomFactor();
			// captureRect.width *= networkPanel.getZoomFactor();
			// captureRect.x *= networkPanel.getZoomFactor();
			// captureRect.y *= networkPanel.getZoomFactor();
			// networkPanel.scrollRectToVisible(captureRect);
			//
			// networkPanel.setCaptureRect(null);
			// }
			// start = me.getPoint();
			// networkPanel.repaint();
			// }
			//
			// @Override
			// public void mouseDragged(MouseEvent me) {
			// Point end = me.getPoint();
			// networkPanel.setCaptureRect(new Rectangle(start, new Dimension(
			// end.x - start.x, end.y - start.y)));
			// networkPanel.repaint();
			// }
			//
			// });
			zoomPanel.add(getScaleElementsBox());
		}
		return this.zoomPanel;
	}

	private JPanel getNetworkPropertiesPanel() {
		if (networkPropertiesPanel == null) {
			networkPropertiesPanel = new JPanel();
			networkPropertiesPanel.setLayout(new BoxLayout(
					networkPropertiesPanel, BoxLayout.PAGE_AXIS));
			playButton = new JButton("start layouting");
			playButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0e) {
					play = !play;

					if (play)
						playButton.setText("stop layouting");
					else
						playButton.setText("start layouting");
				}
			});
			networkPropertiesPanel.add(playButton);
			networkPropertiesPanel.add(getSocialNetworkBox());
			networkPropertiesPanel.add(getWeightSpinner());
			networkPropertiesPanel.add(getCutoffSlider());
		}
		return networkPropertiesPanel;
	}

	private JSpinner getWeightSpinner() {
		if (this.weightSpinner == null) {
			this.weightSpinner = new JSpinner(new SpinnerNumberModel(0.0d,
					-1.0d, 100.0d, 0.1d));
			this.weightSpinner.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent arg0) {
					setWorkitems(discussions);
					double v = (Double) weightSpinner.getValue();
					cutoffSlider.setValue((int) v);
				}
			});
		}
		return this.weightSpinner;
	}

	private JComboBox getSocialNetworkBox() {
		if (this.socialNetworkBox == null) {
			this.socialNetworkBox = new JComboBox(new Object[] {
					new PartitionedSocialNetwork(),
					new ProximitySocialNetwork() });
			this.socialNetworkBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent event) {
					setWorkitems(discussions);
				}
			});
		}
		return this.socialNetworkBox;
	}

	JMenuBar getMenu() {
		if (this.menu == null) {
			this.menu = new JMenuBar();

			JMenu viewMenu = new JMenu("View");
			JMenuItem toggleZoomPanel = new JMenuItem("Zoom panel");
			toggleZoomPanel.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					showZoomPanel = !showZoomPanel;
					removeAll();
					addElements();
					pack();
					invalidate();
					repaint();
				}
			});
			viewMenu.add(toggleZoomPanel);
			JMenuItem toggleNetworkProperties = new JMenuItem(
					"Network properties");
			toggleNetworkProperties.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					showNetworkProperties = !showNetworkProperties;
					removeAll();
					addElements();
					pack();
					invalidate();
					repaint();
				}
			});
			viewMenu.add(toggleNetworkProperties);

			this.menu.add(viewMenu);
		}
		return this.menu;
	}

	public void setWorkitems(Discussion[] discussions) {
		this.discussions = discussions;

		if (discussions != null
				&& this.configuration.getDiscussionPartition() != null) {
			SocialNetwork sn = (SocialNetwork) getSocialNetworkBox()
					.getSelectedItem();
			sn.setDiscussionData(discussions,
					this.configuration.getDiscussionPartition());
			sn.setEdgeCutoffWeight((Double) getWeightSpinner().getValue());
			this.configuration.setSocialNetwork(sn);
			this.networkPanel.setNetwork(sn);
			// this.networkPanel.repaint();
			getZoomPanel().fitIntoParentBounds();

			computeNetworkMetrics(discussions,
					configuration.getDiscussionPartition(), sn);
			updateCutoffSlider(sn);
		}
	}

	private void updateCutoffSlider(SocialNetwork sn) {
		List<Double> weights = new LinkedList<Double>();
		double maxWeight = 0;
		for (Node n1 : sn.getActors()) {
			for (Node n2 : sn.getActors()) {
				double weight = sn.getWeight(n1, n2);
				if (weight > 0) {
					weights.add(weight);
					if (weight > maxWeight)
						maxWeight = weight;
				}
			}
		}

		Collections.sort(weights);
		int[] bucketAmounts = new int[(int) maxWeight + 1];
		// 1. divide the maxWeight by bucketAmounts.length, and count how many
		// weights we
		// have for each bucket
		int i = 0;
		for (Double w : weights) {
			while (w > i + 1) {
				i++;
			}
			bucketAmounts[i]++;
		}

		// 2. divide the maxAmount per bucket by the height we might use
		int maxHeight = 100;

		// 3. create pictures for each bucket and add them to the slider
		Dictionary<Integer, JComponent> labels = new Hashtable<Integer, JComponent>();
		for (int j = 0; j < bucketAmounts.length; j++) {
			int amount = bucketAmounts[j];
			int maxAmount = weights.size();
			if (maxHeight < weights.size()) {
				amount = (amount * maxHeight) / weights.size();
				maxAmount = maxHeight;
			} else if (weights.size() == 0)
				maxAmount = 1;
			BufferedImage bi = new BufferedImage(5, maxAmount,
					BufferedImage.TYPE_INT_RGB);
			// System.out.println(j + " = " + amount + "/" + weights.size());
			Graphics graphics = bi.getGraphics();
			graphics.fillRect(0, 0, 5, maxAmount);
			graphics.setColor(Color.BLUE);

			graphics.fillRect(0, 0, 5, amount);
			JLabel weightLabel = new JLabel(new ImageIcon(bi));
			weightLabel.setToolTipText(amount + " edges have weight < " + j);
			labels.put(j, weightLabel);
		}
		getCutoffSlider().setMinimum(0);
		getCutoffSlider().setMaximum((int) maxWeight);
		getCutoffSlider().setLabelTable(labels);
	}

	private void computeNetworkMetrics(Discussion[] discussions,
			IDiscussionOverTimePartition partition, SocialNetwork sn) {
		StringBuffer sb = new StringBuffer();
		for (AbstractNetworkMetric anm : AbstractNetworkMetric.STANDARD_METRICS) {
			sb.append(" | ");
			sb.append(anm.getName());
			sb.append(" = ");
			anm.setPartition(partition);
			anm.setSocialNetwork(sn);
			anm.setMinWeight((Double) weightSpinner.getValue());
			sb.append(String.valueOf(anm.getDecimalFormat().format(
					anm.considerDiscussions(discussions))));
		}
		sb.append(" | ");
		this.metricLabel.setText(sb.toString());
	}

	public static void main(String[] args) {
		VisualizationConfiguration vc = new VisualizationConfiguration();
		final FixedNumberPartition p = new FixedNumberPartition();
		p.setPartitionCount(8);
		vc.setPartition(p);
		final NetworkFrame f = new NetworkFrame(vc);

		JMenuItem starNet = new JMenuItem("generate star");
		JMenuItem randomNet = new JMenuItem("generate random");
		JMenuItem doubleStar = new JMenuItem("generate 2 stars");

		JMenu createExamples = new JMenu("Create Examples");
		f.getMenu().add(createExamples);
		createExamples.add(starNet);
		createExamples.add(doubleStar);
		createExamples.add(randomNet);

		randomNet.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int actorNumber = 15;
				DiscussionEvent[] wcs = new DiscussionEvent[20];
				for (int i = 0; i < wcs.length; i++) {
					wcs[i] = new DiscussionEvent();
					wcs[i].setCreator("user"
							+ Util.getRandom().nextInt(actorNumber));
					wcs[i].setCreationDate(new Date(System.currentTimeMillis()
							- Util.getRandom().nextInt(40)
							* TimeIntervalPartition.MILLIS_PER_DAY));
				}

				Arrays.sort(wcs, new Comparator<DiscussionEvent>() {

					@Override
					public int compare(DiscussionEvent o1, DiscussionEvent o2) {
						return o1.getCreationDate().compareTo(
								o2.getCreationDate());
					}
				});

				p.setTimeInterval(wcs[0].getCreationDate(),
						wcs[wcs.length - 1].getCreationDate());
				Discussion d = DiscussionFactory.getInstance().getDiscussion(1);

				d.addDiscussionEvents(wcs);
				d.setCreationDate(wcs[0].getCreationDate());

				f.setWorkitems(new Discussion[] { d });
			}
		});
		starNet.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int actorNumber = 8;
				DiscussionEvent[] wcs = new DiscussionEvent[2 * actorNumber];
				for (int i = 0; i < wcs.length; i++) {
					int r = i % 2;
					int n = i / 2;
					if (r != 0)
						n = 1;

					wcs[i] = new DiscussionEvent();
					wcs[i].setCreator("user" + n);
					wcs[i].setCreationDate(new Date(System.currentTimeMillis()
							- (wcs.length - i)
							* TimeIntervalPartition.MILLIS_PER_DAY));
				}

				p.setTimeInterval(wcs[0].getCreationDate(),
						wcs[wcs.length - 1].getCreationDate());

				Discussion d = DiscussionFactory.getInstance().getDiscussion(1);

				d.addDiscussionEvents(wcs);
				d.setCreationDate(wcs[0].getCreationDate());

				f.setWorkitems(new Discussion[] { d });
			}
		});
		doubleStar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int actorNumber = 8;
				DiscussionEvent[] wcs1 = new DiscussionEvent[2 * actorNumber];
				DiscussionEvent[] wcs2 = new DiscussionEvent[2 * actorNumber];

				for (int i = 0; i < wcs1.length; i++) {
					int r = i % 2;
					int n = i / 2;
					if (r != 0)
						n = 1;

					wcs1[i] = new DiscussionEvent();
					wcs1[i].setCreator("userA" + n);
					wcs1[i].setCreationDate(new Date(System.currentTimeMillis()
							- (wcs1.length - i)
							* TimeIntervalPartition.MILLIS_PER_DAY));
					wcs2[i] = new DiscussionEvent();
					wcs2[i].setCreator("userB" + n);
					wcs2[i].setCreationDate(new Date(System.currentTimeMillis()
							- (wcs2.length - i)
							* TimeIntervalPartition.MILLIS_PER_DAY));
				}

				p.setTimeInterval(wcs1[0].getCreationDate(),
						wcs1[wcs1.length - 1].getCreationDate());

				Discussion d1 = DiscussionFactory.getInstance()
						.getDiscussion(1);
				d1.addDiscussionEvents(wcs1);
				d1.setCreationDate(wcs1[0].getCreationDate());
				Discussion d2 = DiscussionFactory.getInstance()
						.getDiscussion(1);
				d2.addDiscussionEvents(wcs2);
				d2.setCreationDate(wcs2[0].getCreationDate());

				f.setWorkitems(new Discussion[] { d1, d2 });
			}
		});
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setVisible(true);
	}
}
