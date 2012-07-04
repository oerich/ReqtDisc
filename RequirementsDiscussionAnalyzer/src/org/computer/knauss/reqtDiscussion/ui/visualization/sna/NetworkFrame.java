package org.computer.knauss.reqtDiscussion.ui.visualization.sna;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.ui.visualization.FixedNumberPartition;
import org.computer.knauss.reqtDiscussion.ui.visualization.IDiscussionOverTimePartition;
import org.computer.knauss.reqtDiscussion.ui.visualization.TimeIntervalPartition;

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

		this.weightSpinner = new JSpinner(new SpinnerNumberModel(0.0d, 0.0d, 100.0d, 0.1d));
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
		this.zoomSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				networkPanel.setZoomFactor(zoomSlider.getValue() / 10d);
			}
		});
		this.add(this.zoomSlider, BorderLayout.SOUTH);
		
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
		}
	}

	public static void main(String[] args) {
		NetworkFrame f = new NetworkFrame();

		Random r = new Random();
		int actorNumber = 7;
		DiscussionEvent[] wcs = new DiscussionEvent[20];
		for (int i =0; i < wcs.length; i++) {
			wcs[i] = new DiscussionEvent();
			wcs[i].setCreator("user"+r.nextInt(actorNumber));
			wcs[i].setCreationDate(new Date(System.currentTimeMillis() - r.nextInt(40) * TimeIntervalPartition.MILLIS_PER_DAY));
		}
		
		Arrays.sort(wcs, new Comparator<DiscussionEvent>() {

			@Override
			public int compare(DiscussionEvent o1, DiscussionEvent o2) {
				return o1.getCreationDate().compareTo(o2.getCreationDate());
			}
		});

		Discussion wi = new Discussion();
		wi.addComments(wcs);

		FixedNumberPartition p = new FixedNumberPartition();
		p.setTimeInterval(wcs[0].getCreationDate(), wcs[wcs.length-1].getCreationDate());

		f.setWorkitems(new Discussion[] { wi }, p);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setVisible(true);
	}
}
