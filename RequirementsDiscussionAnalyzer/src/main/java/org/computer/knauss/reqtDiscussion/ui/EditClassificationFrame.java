package org.computer.knauss.reqtDiscussion.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.DiscussionEventClassification;
import org.computer.knauss.reqtDiscussion.model.IClassificationFilter;
import org.computer.knauss.reqtDiscussion.model.clarificationPatterns.DiscussionListModel;
import org.computer.knauss.reqtDiscussion.ui.ctrl.AbstractCommand;
import org.computer.knauss.reqtDiscussion.ui.ctrl.InsertOrUpdateDiscussionEventClassification;

public class EditClassificationFrame extends JFrame implements
		ListSelectionListener {

	private static final long serialVersionUID = 1L;
	private JPanel northPanel;
	private JEditorPane workitemTextArea;
	private JPanel westPanel;
	private JSplitPane centerPanel;
	private JPanel classificationPanel;
	private JList commentList;
	private DiscussionListModel commentListModel;
	private JTextField workitemCommentDateField;
	private JTextField workitemCommentIDField;
	private JEditorPane workitemCommentContentTextView;
	private DiscussionEvent discussionEvent;
	private JTextField commentTextField;
	private JSpinner confidenceChooser;
	private JTextField classifiedByTextField;
	private JTextField classificationTextField;
	private DiscussionEventClassification classification;
	private JPanel southPanel;
	private InsertOrUpdateDiscussionEventClassification insertOrUpdateCommand;
	private JTextField workitemCommentCreatorField;
	private Discussion currentDiscussion;
	private Discussion[] discussions;

	public EditClassificationFrame() {
		super("Edit Classification");

		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		setLayout(new BorderLayout());
		add(getNorthPanel(), BorderLayout.NORTH);
		add(getWestPanel(), BorderLayout.WEST);
		add(getCenterPanel(), BorderLayout.CENTER);
		add(getSouthPanel(), BorderLayout.SOUTH);

		pack();
	}

	private JPanel getNorthPanel() {
		if (this.northPanel == null) {
			this.northPanel = new JPanel();
			this.northPanel.add(new JScrollPane(getWorkitemTextView()));
		}
		return this.northPanel;
	}

	private JEditorPane getWorkitemTextView() {
		if (this.workitemTextArea == null) {
			this.workitemTextArea = new JEditorPane();
			this.workitemTextArea.setContentType("text/html");
			// this.workitemTextArea.setWrapStyleWord(true);
			// this.workitemTextArea.setLineWrap(true);
			Dimension size = new Dimension(700, 150);
			this.workitemTextArea.setPreferredSize(size);
			this.workitemTextArea.setEditable(false);
		}
		return this.workitemTextArea;
	}

	private JPanel getWestPanel() {
		if (this.westPanel == null) {
			this.westPanel = new JPanel(new GridLayout(1, 1));
			this.westPanel.setBorder(BorderFactory
					.createTitledBorder("Comments"));
			this.westPanel.add(new JScrollPane(getCommentList()));
		}

		return this.westPanel;
	}

	private JList getCommentList() {
		if (this.commentList == null) {
			this.commentList = new JList(getCommentListModel());
			this.commentList.addListSelectionListener(this);
		}
		return this.commentList;
	}

	private DiscussionListModel getCommentListModel() {
		if (this.commentListModel == null) {
			this.commentListModel = new DiscussionListModel();
		}
		return this.commentListModel;
	}

	private JSplitPane getCenterPanel() {
		if (this.centerPanel == null) {
			JPanel commentPanel = new JPanel(new BorderLayout());
			commentPanel.setBorder(BorderFactory
					.createTitledBorder("WorkitemComment"));
			commentPanel.add(new JScrollPane(
					getDiscussionEventContentTextView()), BorderLayout.CENTER);

			JPanel meta = new JPanel(new GridLayout(1, 3));
			meta.add(getDiscussionEventIDField());
			meta.add(getDiscussionEventDateField());
			meta.add(getDiscussionEventCreatorField());

			commentPanel.add(meta, BorderLayout.NORTH);

			JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					commentPanel, getClassificationPanel());
			splitPane.setOneTouchExpandable(true);
			// splitPane.setDividerLocation(150);

			this.centerPanel = splitPane;
		}
		return this.centerPanel;
	}

	private JTextField getDiscussionEventCreatorField() {
		if (this.workitemCommentCreatorField == null) {
			this.workitemCommentCreatorField = new JTextField();
			this.workitemCommentCreatorField.setEditable(false);
		}
		return this.workitemCommentCreatorField;
	}

	private JTextField getDiscussionEventDateField() {
		if (this.workitemCommentDateField == null) {
			this.workitemCommentDateField = new JTextField();
			this.workitemCommentDateField.setEditable(false);
		}
		return this.workitemCommentDateField;
	}

	private JTextField getDiscussionEventIDField() {
		if (this.workitemCommentIDField == null) {
			this.workitemCommentIDField = new JTextField();
			this.workitemCommentIDField.setEditable(false);
		}
		return this.workitemCommentIDField;
	}

	private JEditorPane getDiscussionEventContentTextView() {
		if (this.workitemCommentContentTextView == null) {
			this.workitemCommentContentTextView = new JEditorPane();
			this.workitemCommentContentTextView.setEditable(false);
			Dimension d = new Dimension(300, 300);
			this.workitemCommentContentTextView.setPreferredSize(d);
			this.workitemCommentContentTextView.setContentType("text/html");
		}
		return this.workitemCommentContentTextView;
	}

	private JPanel getClassificationPanel() {
		if (this.classificationPanel == null) {
			this.classificationPanel = new JPanel(new GridLayout(4, 1));

			JPanel classiPane = new JPanel(new GridLayout(1, 1));
			classiPane.setBorder(BorderFactory
					.createTitledBorder("Classification"));
			classiPane.add(getClassificationTextField());
			JPanel confiPane = new JPanel(new GridLayout(1, 1));
			confiPane.setBorder(BorderFactory.createTitledBorder("Confidence"));
			confiPane.add(getConfidenceChooser());
			JPanel commentPane = new JPanel(new GridLayout(1, 1));
			commentPane.setBorder(BorderFactory.createTitledBorder("Comment"));
			commentPane.add(getDiscussionEventTextField());

			JButton updateOrInsert = new JButton("Update / Insert");
			updateOrInsert.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// put all data in the classification object
					classification.setDiscussionEventID(discussionEvent.getID());
					classification
							.setClassifiedby(IClassificationFilter.NAME_FILTER
									.getName());
					classification.setComment(getDiscussionEventTextField()
							.getText());
					classification
							.setConfidence((Double) getConfidenceChooser()
									.getValue());
					classification
							.setClassification(getClassificationTextField()
									.getText());

					discussionEvent
							.insertOrUpdateClassification(classification);
					// send it to the DAO.
					insertOrUpdateCommand
							.setWorkitemCommentClassification(classification);
					insertOrUpdateCommand.actionPerformed(arg0);

					valueChanged(null);
				}
			});
			JPanel buttonPanel = new JPanel();
			buttonPanel.add(updateOrInsert);

			this.classificationPanel.add(classiPane);
			this.classificationPanel.add(confiPane);
			this.classificationPanel.add(commentPane);
			this.classificationPanel.add(buttonPanel);
		}
		return this.classificationPanel;
	}

	private JPanel getSouthPanel() {
		if (this.southPanel == null) {
			this.southPanel = new JPanel();
			this.southPanel.add(new JLabel("Classified by:"));

			this.southPanel.add(getClassifiedByTextField());
		}
		return this.southPanel;
	}

	private JTextField getDiscussionEventTextField() {
		if (this.commentTextField == null) {
			this.commentTextField = new JTextField("");
			this.commentTextField.setColumns(20);
		}
		return this.commentTextField;
	}

	private JSpinner getConfidenceChooser() {
		if (this.confidenceChooser == null) {
			this.confidenceChooser = new JSpinner(new SpinnerNumberModel(1.0,
					0, 1, 0.1));
			this.confidenceChooser.setValue(0.999);
		}

		return this.confidenceChooser;
	}

	private JTextField getClassifiedByTextField() {
		if (this.classifiedByTextField == null) {
			this.classifiedByTextField = new JTextField("not specified");
			this.classifiedByTextField.setColumns(10);
			this.classifiedByTextField.setEditable(false);
		}
		return this.classifiedByTextField;
	}

	private JTextField getClassificationTextField() {
		if (this.classificationTextField == null) {
			this.classificationTextField = new JTextField("not classified");
			this.classificationTextField.setColumns(20);
		}
		return this.classificationTextField;
	}

	public void setDiscussions(Discussion[] discussions) {
		if (discussions == null || discussions.length == 0)
			// TODO clear content
			return;
		this.discussions = discussions;
		updateCurrentDiscussion(discussions[0]);

		DiscussionEvent[] events = getEvents(discussions);

		getCommentListModel().setComments(events);
		getCommentList().setSelectedIndex(0);
		valueChanged(null);
	}

	private void updateCurrentDiscussion(Discussion d) {
		if (this.currentDiscussion != null && this.currentDiscussion.equals(d))
			return;
		this.currentDiscussion = d;
		if (d == null) {
			getWorkitemTextView().setText("");
			return;
		}
		getWorkitemTextView().setText(
				"<h3>Summary (ID: " + this.currentDiscussion.getID()
						+ " Creator: " + this.currentDiscussion.getCreator()
						+ ")</h3>" + this.currentDiscussion.getSummary()
						+ "<h3>Description</h3>"
						+ this.currentDiscussion.getDescription());
		// getWorkitemTextArea().scrollRectToVisible(new Rectangle(0,0,10,10));
		getWorkitemTextView().setCaretPosition(0);
	}

	private DiscussionEvent[] getEvents(Discussion[] discussions) {
		List<DiscussionEvent> tmp = new LinkedList<DiscussionEvent>();
		for (Discussion d : discussions)
			Collections.addAll(tmp, d.getDiscussionEvents());
		Collections.sort(tmp, new Comparator<DiscussionEvent>() {
			@Override
			public int compare(DiscussionEvent o1, DiscussionEvent o2) {
				return o1.getCreationDate().compareTo(o2.getCreationDate());
			}
		});
		return tmp.toArray(new DiscussionEvent[0]);
	}

	@Override
	public void valueChanged(ListSelectionEvent event) {
		// get the selected discussionEvent and set the fields of this frame
		// accordingly
		getClassifiedByTextField().setText(
				IClassificationFilter.NAME_FILTER.getName());

		int i = getCommentList().getSelectedIndex();
		DiscussionEvent[] events = getEvents(this.discussions);
		if (i == -1 || i >= events.length) {
			// reset the fields
			String empty = "";
			getDiscussionEventIDField().setText(empty);
			getDiscussionEventDateField().setText(empty);
			getDiscussionEventCreatorField().setText(empty);
			getDiscussionEventContentTextView().setText(empty);
			getDiscussionEventContentTextView().setCaretPosition(0);
			getClassificationTextField().setText(empty);
			getConfidenceChooser().setValue(0);
			getDiscussionEventTextField().setText(empty);
			return;
		}
		this.discussionEvent = events[i];
		int discussionID = this.discussionEvent.getDiscussionID();
		for (Discussion d : this.discussions) {
			if (d.getID() == discussionID) {
				updateCurrentDiscussion(d);
			}
		}
		getDiscussionEventIDField().setText(
				String.valueOf(this.discussionEvent.getID()));
		getDiscussionEventDateField().setText(
				String.valueOf(this.discussionEvent.getCreationDate()));
		getDiscussionEventCreatorField().setText(
				this.discussionEvent.getCreator());
		getDiscussionEventContentTextView().setText(
				this.discussionEvent.getContent());
		getDiscussionEventContentTextView().setCaretPosition(0);
		this.classification = IClassificationFilter.NAME_FILTER
				.filterCommentClassifications(this.discussionEvent
						.getDiscussionEventClassifications());
		getClassificationTextField().setText(
				this.classification.getClassification());
		// getClassifiedByTextField().setText(
		// this.classification.getClassifiedby());

		getConfidenceChooser().setValue(this.classification.getConfidence());
		getDiscussionEventTextField().setText(this.classification.getComment());
	}

	public void setInsertOrUpdateCommand(AbstractCommand insertOrUpdateCommand) {
		this.insertOrUpdateCommand = (InsertOrUpdateDiscussionEventClassification) insertOrUpdateCommand;
	}
}
