package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.computer.knauss.reqtDiscussion.model.DiscussionEventClassification;

public class InsertOrUpdateDiscussionEventClassification extends AbstractCommand {

	private static final long serialVersionUID = 1L;
	private DiscussionEventClassification wcc;

	public InsertOrUpdateDiscussionEventClassification() {
		super("Insert or Update Comment Classification");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			getWorkitemCommentClassificationDAO().insertOrUpdate(this.wcc);
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null, e1.getMessage(),
					"Error by Insert or Update", JOptionPane.ERROR_MESSAGE);
			e1.printStackTrace();
		}
	}

	public void setWorkitemCommentClassification(
			DiscussionEventClassification wcc) {
		this.wcc = wcc;
	}
}
