package org.computer.knauss.reqtDiscussion.ui.visualization.sna;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
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
import org.computer.knauss.reqtDiscussion.model.metric.AbstractNetworkMetric;
import org.computer.knauss.reqtDiscussion.model.partition.FixedNumberPartition;
import org.computer.knauss.reqtDiscussion.model.partition.IDiscussionOverTimePartition;
import org.computer.knauss.reqtDiscussion.model.partition.TimeIntervalPartition;
import org.computer.knauss.reqtDiscussion.model.socialNetwork.PartitionedSocialNetwork;
import org.computer.knauss.reqtDiscussion.model.socialNetwork.ProximitySocialNetwork;
import org.computer.knauss.reqtDiscussion.model.socialNetwork.SocialNetwork;

public class NetworkFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private NetworkPanel networkPanel;
	private boolean play;

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
	private JSpinner weightSpinner;
	private IDiscussionOverTimePartition partition;
	private Discussion[] discussions;
	private JSlider zoomSlider;
	private JLabel metricLabel;

	public NetworkFrame() {
		super("Social Network Analysis");

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
				setWorkitems(discussions, partition);
			}
		});
		buttonPanel.add(this.socialNetworkBox);

		this.weightSpinner = new JSpinner(new SpinnerNumberModel(0.0d, 0.0d,
				100.0d, 0.1d));
		this.weightSpinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				setWorkitems(discussions, partition);
			}
		});
		buttonPanel.add(this.weightSpinner);

		this.networkPanel = new NetworkPanel();
		add(new JScrollPane(this.networkPanel), BorderLayout.CENTER);

		this.zoomSlider = new JSlider(2, 20, 10);
		Dictionary<Integer, JComponent> labels = new Hashtable<Integer, JComponent>();
		labels.put(2, new JLabel("-"));
		labels.put(20, new JLabel("+"));
		this.zoomSlider.setLabelTable(labels);
		this.zoomSlider.setPaintLabels(true);
		this.zoomSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				networkPanel.setZoomFactor(zoomSlider.getValue() / 10d);
			}
		});
		this.add(this.zoomSlider, BorderLayout.WEST);

		this.metricLabel = new JLabel("");
		add(this.metricLabel, BorderLayout.SOUTH);

		pack();

		Timer t = new Timer();
		t.schedule(this.timer, 50, 100);
	}

	public void setWorkitems(Discussion[] discussions,
			IDiscussionOverTimePartition partition) {
		this.discussions = discussions;
		this.partition = partition;

		if (discussions != null && partition != null) {
			SocialNetwork sn = (SocialNetwork) this.socialNetworkBox
					.getSelectedItem();
			sn.setDiscussionData(discussions, partition);
			this.networkPanel.setMinWeight((Double) weightSpinner.getValue());
			this.networkPanel.setNetwork(sn);
			this.networkPanel.repaint();

			computeNetworkMetrics(discussions, partition, sn);
		}
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
		NetworkFrame f = new NetworkFrame();

		Random r = new Random();
		int actorNumber = 7;
		DiscussionEvent[] wcs = new DiscussionEvent[20];
		for (int i = 0; i < wcs.length; i++) {
			wcs[i] = new DiscussionEvent();
			wcs[i].setCreator("user" + r.nextInt(actorNumber));
			wcs[i].setCreationDate(new Date(System.currentTimeMillis()
					- r.nextInt(40) * TimeIntervalPartition.MILLIS_PER_DAY));
		}

		Arrays.sort(wcs, new Comparator<DiscussionEvent>() {

			@Override
			public int compare(DiscussionEvent o1, DiscussionEvent o2) {
				return o1.getCreationDate().compareTo(o2.getCreationDate());
			}
		});

		Discussion d = DiscussionFactory.getInstance().getDiscussion(1);

		d.addComments(wcs);
		d.setDateCreated(wcs[0].getCreationDate());
		FixedNumberPartition p = new FixedNumberPartition();
		p.setTimeInterval(wcs[0].getCreationDate(),
				wcs[wcs.length - 1].getCreationDate());

		f.setWorkitems(new Discussion[] { d }, p);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setVisible(true);
	}
}
