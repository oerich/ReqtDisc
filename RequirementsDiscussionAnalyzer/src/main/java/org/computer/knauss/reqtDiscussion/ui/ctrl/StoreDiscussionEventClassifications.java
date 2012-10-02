package org.computer.knauss.reqtDiscussion.ui.ctrl;

import org.computer.knauss.reqtDiscussion.io.DAOException;
import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.DiscussionEventClassification;

public class StoreDiscussionEventClassifications extends
		AbstractDiscussionIterationCommand {

	private static final long serialVersionUID = 1L;

	public StoreDiscussionEventClassifications() {
		super("Store classifications...");
	}

	@Override
	protected void processDiscussionHook(Discussion d) {
		try {
			for (DiscussionEvent e : d.getDiscussionEvents())
				for (DiscussionEventClassification c : e
						.getCommentClassifications())
					getDiscussionEventClassificationDAO()
							.storeDiscussionEventClassification(c);
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
