package org.computer.knauss.reqtDiscussion.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
	private Discussion workitem;
	private JTextField workitemCommentDateField;
	private JTextField workitemCommentIDField;
	private JEditorPane workitemCommentContentTextView;
	private DiscussionEvent workitemComment;
	private JTextField commentTextField;
	private JSpinner confidenceChooser;
	private JTextField classifiedByTextField;
	private JTextField classificationTextField;
	private DiscussionEventClassification classification;
	private JPanel southPanel;
	private InsertOrUpdateDiscussionEventClassification insertOrUpdateCommand;
	private JTextField workitemCommentCreatorField;

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
			commentPanel.add(new JScrollPane(getCommentContentTextView()),
					BorderLayout.CENTER);

			JPanel meta = new JPanel(new GridLayout(1, 3));
			meta.add(getWorkitemCommentIDField());
			meta.add(getWorkitemCommentDateField());
			meta.add(getWorkitemCommentCreatorField());

			commentPanel.add(meta, BorderLayout.NORTH);

			JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					commentPanel, getClassificationPanel());
			splitPane.setOneTouchExpandable(true);
			// splitPane.setDividerLocation(150);

			this.centerPanel = splitPane;
		}
		return this.centerPanel;
	}

	private JTextField getWorkitemCommentCreatorField() {
		if (this.workitemCommentCreatorField == null) {
			this.workitemCommentCreatorField = new JTextField();
			this.workitemCommentCreatorField.setEditable(false);
		}
		return this.workitemCommentCreatorField;
	}

	private JTextField getWorkitemCommentDateField() {
		if (this.workitemCommentDateField == null) {
			this.workitemCommentDateField = new JTextField();
			this.workitemCommentDateField.setEditable(false);
		}
		return this.workitemCommentDateField;
	}

	private JTextField getWorkitemCommentIDField() {
		if (this.workitemCommentIDField == null) {
			this.workitemCommentIDField = new JTextField();
			this.workitemCommentIDField.setEditable(false);
		}
		return this.workitemCommentIDField;
	}

	private JEditorPane getCommentContentTextView() {
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
			commentPane.add(getCommentTextField());

			JButton updateOrInsert = new JButton("Update / Insert");
			updateOrInsert.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// put all data in the classification object
					classification.setWorkitemcommentid(workitemComment.getID());
					classification
							.setClassifiedby(IClassificationFilter.NAME_FILTER
									.getName());
					classification.setComment(getCommentTextField().getText());
					classification
							.setConfidence((Double) getConfidenceChooser()
									.getValue());
					classification
							.setClassification(getClassificationTextField()
									.getText());

					workitemComment
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

	private JTextField getCommentTextField() {
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

	public void setWorkitem(Discussion currentWorkitem) {
		this.workitem = currentWorkitem;
		getWorkitemTextView().setText(
				"<h3>Summary (ID: " + this.workitem.getID() + " Creator: "
						+ this.workitem.getCreator() + ")</h3>"
						+ this.workitem.getSummary() + "<h3>Description</h3>"
						+ this.workitem.getDescription());
		// getWorkitemTextArea().scrollRectToVisible(new Rectangle(0,0,10,10));
		getWorkitemTextView().setCaretPosition(0);
		getCommentListModel().setComments(this.workitem.getDiscussionEvents());
		getCommentList().setSelectedIndex(0);
		valueChanged(null);
	}

	@Override
	public void valueChanged(ListSelectionEvent event) {
		// get the selected workitemComment and set the fields of this frame
		// accordingly
		getClassifiedByTextField().setText(
				IClassificationFilter.NAME_FILTER.getName());

		int i = getCommentList().getSelectedIndex();
		if (i == -1 || i >= this.workitem.getDiscussionEvents().length) {
			// reset the fields
			String empty = "";
			getWorkitemCommentIDField().setText(empty);
			getWorkitemCommentDateField().setText(empty);
			getWorkitemCommentCreatorField().setText(empty);
			getCommentContentTextView().setText(empty);
			getCommentContentTextView().setCaretPosition(0);
			getClassificationTextField().setText(empty);
			getConfidenceChooser().setValue(0);
			getCommentTextField().setText(empty);
			return;
		}
		this.workitemComment = this.workitem.getDiscussionEvents()[i];
		getWorkitemCommentIDField().setText(
				String.valueOf(this.workitemComment.getID()));
		getWorkitemCommentDateField().setText(
				String.valueOf(this.workitemComment.getCreationDate()));
		getWorkitemCommentCreatorField().setText(
				this.workitemComment.getCreator());
		getCommentContentTextView().setText(this.workitemComment.getContent());
		getCommentContentTextView().setCaretPosition(0);
		this.classification = IClassificationFilter.NAME_FILTER
				.filterCommentClassifications(this.workitemComment
						.getCommentClassifications());
		getClassificationTextField().setText(
				this.classification.getClassification());
		// getClassifiedByTextField().setText(
		// this.classification.getClassifiedby());

		getConfidenceChooser().setValue(this.classification.getConfidence());
		getCommentTextField().setText(this.classification.getComment());
	}

	public void setInsertOrUpdateCommand(AbstractCommand insertOrUpdateCommand) {
		this.insertOrUpdateCommand = (InsertOrUpdateDiscussionEventClassification) insertOrUpdateCommand;
	}
}
