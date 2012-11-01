package org.computer.knauss.reqtDiscussion.ui.visualization.sna;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.sql.Date;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
	private JScrollPane jScrollPane;
	private JSlider cutoffSlider;
	private JSpinner weightSpinner;

	private NetworkPanel networkPanel;
	private Discussion[] discussions;
	private boolean play;

	private VisualizationConfiguration configuration;

	private JCheckBox scaleElementsBox;

	public NetworkFrame(VisualizationConfiguration configuration) {
		super("Social Network Analysis");

		this.configuration = configuration;

		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setLayout(new BorderLayout());

		JPanel buttonPanel = new JPanel();
		add(buttonPanel, BorderLayout.NORTH);
		playButton = new JButton("start");
		playButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0e) {
				play = !play;

				if (play)
					playButton.setText("stop");
				else
					playButton.setText("start");
			}
		});
		buttonPanel.add(playButton);
		this.socialNetworkBox = new JComboBox(new Object[] {
				new PartitionedSocialNetwork(), new ProximitySocialNetwork() });
		this.socialNetworkBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				setWorkitems(discussions);
			}
		});
		buttonPanel.add(this.socialNetworkBox);

		this.weightSpinner = new JSpinner(new SpinnerNumberModel(0.0d, -1.0d,
				100.0d, 0.1d));
		this.weightSpinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				setWorkitems(discussions);
				double v = (Double) weightSpinner.getValue();
				cutoffSlider.setValue((int) v);
			}
		});
		buttonPanel.add(this.weightSpinner);

		this.networkPanel = new NetworkPanel();
		jScrollPane = new JScrollPane(this.networkPanel);
		add(jScrollPane, BorderLayout.CENTER);

		JPanel ctrlPanel = new JPanel(new GridLayout(2, 1));

		ZoomPanel zoomPanel = new ZoomPanel();
		zoomPanel.setBorder(BorderFactory.createTitledBorder("Zoom"));
		zoomPanel.setLayout(new GridLayout(3, 1));
		zoomPanel.setZoomable(this.networkPanel);
		zoomPanel.setZoomableParent(jScrollPane);

		add(ctrlPanel, BorderLayout.WEST);

		ctrlPanel.add(zoomPanel, BorderLayout.WEST);

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
		zoomPanel.add(scaleElementsBox);

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
		ctrlPanel.add(this.cutoffSlider);

		this.metricLabel = new JLabel("");
		add(this.metricLabel, BorderLayout.SOUTH);

		pack();

		Timer t = new Timer();
		t.schedule(this.timer, 50, 100);
	}

	public void setWorkitems(Discussion[] discussions) {
		this.discussions = discussions;

		if (discussions != null
				&& this.configuration.getDiscussionPartition() != null) {
			SocialNetwork sn = (SocialNetwork) this.socialNetworkBox
					.getSelectedItem();
			sn.setDiscussionData(discussions,
					this.configuration.getDiscussionPartition());
			sn.setEdgeCutoffWeight((Double) weightSpinner.getValue());
			this.configuration.setSocialNetwork(sn);
			this.networkPanel.setNetwork(sn);
			// this.networkPanel.repaint();
			this.networkPanel.zoomToFitRect(this.jScrollPane.getBounds());

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
		this.cutoffSlider.setMinimum(0);
		this.cutoffSlider.setMaximum((int) maxWeight);
		this.cutoffSlider.setLabelTable(labels);
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
		FixedNumberPartition p = new FixedNumberPartition();
		vc.setPartition(p);
		NetworkFrame f = new NetworkFrame(vc);

		Random r = new Random();
		int actorNumber = 7;
		DiscussionEvent[] wcs = new DiscussionEvent[20];
		for (int i = 0; i < wcs.length; i++) {
			wcs[i] = new DiscussionEvent();
			wcs[i].setCreator("user" + r.nextInt(actorNumber));
			wcs[i].setCreationDate(new Date(System.currentTimeMillis()
					- r.nextInt(40) * TimeIntervalPartition.MILLIS_PER_DAY));
		}

		p.setTimeInterval(wcs[0].getCreationDate(),
				wcs[wcs.length - 1].getCreationDate());

		Arrays.sort(wcs, new Comparator<DiscussionEvent>() {

			@Override
			public int compare(DiscussionEvent o1, DiscussionEvent o2) {
				return o1.getCreationDate().compareTo(o2.getCreationDate());
			}
		});

		Discussion d = DiscussionFactory.getInstance().getDiscussion(1);

		d.addDiscussionEvents(wcs);
		d.setCreationDate(wcs[0].getCreationDate());

		f.setWorkitems(new Discussion[] { d });
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setVisible(true);
	}
}
