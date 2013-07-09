package org.computer.knauss.reqtDiscussion.ui.ctrl;

import javax.swing.JOptionPane;

import org.computer.knauss.reqtDiscussion.Util;
import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.DiscussionEventClassification;

public class UpdateToNewSchema extends AbstractDiscussionIterationCommand {

	private static final long serialVersionUID = 1L;
	private int proceed = JOptionPane.CANCEL_OPTION;

	public UpdateToNewSchema() {
		super("Update to new database schema");
	}

	@Override
	protected void preProcessingHook() {
		proceed = JOptionPane.CANCEL_OPTION;
		proceed = JOptionPane
				.showConfirmDialog(
						null,
						"The old schema identified DiscussionEvents by their unique ID. The new schema identifies them by (DiscussionID, DiscussionEventID), the latter is only the rank of the event in the discussion. This will remove the unique ID and set the new dual key ID which could be desastrous if written to a database with the old schema. Proceed?",
						"Warning: Irreversible Action",
						JOptionPane.OK_CANCEL_OPTION);
	}

	@Override
	protected void processDiscussionHook(Discussion[] discussions) {
		if (JOptionPane.CANCEL_OPTION == proceed)
			return;
		
		for (Discussion d : discussions) {
			int discussionID = d.getID();
			
			DiscussionEvent[] des = d.getDiscussionEvents();
			Util.sortByDate(des);
			for (int discussionEventID = 0; discussionEventID < des.length; discussionEventID++) {
				DiscussionEvent de = des[discussionEventID];
				de.setDiscussionID(discussionID);
				de.setID(discussionEventID);
				for (DiscussionEventClassification dec : de.getDiscussionEventClassifications()) {
					dec.setDiscussionID(discussionID);
					dec.setDiscussionEventID(discussionEventID);
				}
			}
		}
	}

}
