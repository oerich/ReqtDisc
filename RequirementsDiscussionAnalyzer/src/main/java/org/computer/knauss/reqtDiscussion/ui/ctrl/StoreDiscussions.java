package org.computer.knauss.reqtDiscussion.ui.ctrl;

import org.computer.knauss.reqtDiscussion.io.DAOException;
import org.computer.knauss.reqtDiscussion.model.Discussion;

public class StoreDiscussions extends AbstractDiscussionIterationCommand {

	private static final long serialVersionUID = 1L;

	public StoreDiscussions() {
		super("Store discussions...");
	}

	@Override
	protected void processDiscussionHook(Discussion[] discussions) {
		try {
			for (Discussion d : discussions)
				getDiscussionDAO().storeDiscussion(d);
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
