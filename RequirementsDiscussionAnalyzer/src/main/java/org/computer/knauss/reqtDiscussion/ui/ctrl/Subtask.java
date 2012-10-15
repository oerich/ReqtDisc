package org.computer.knauss.reqtDiscussion.ui.ctrl;

import org.computer.knauss.reqtDiscussion.io.DAOException;
import org.computer.knauss.reqtDiscussion.io.IDAOProgressMonitor;
import org.computer.knauss.reqtDiscussion.io.IDiscussionDAO;
import org.computer.knauss.reqtDiscussion.ui.uiModel.DiscussionTableModel;

abstract class Subtask {

	private IDiscussionDAO discussionDAO;
	private DiscussionTableModel discussionTableModel;

	Subtask() {

	}

	Subtask(DiscussionTableModel dtm, IDiscussionDAO dDao) {
		this.discussionTableModel = dtm;
		this.discussionDAO = dDao;
	}

	public IDiscussionDAO getDiscussionDAO() {
		return discussionDAO;
	}

	public void setDiscussionDAO(IDiscussionDAO discussionDAO) {
		this.discussionDAO = discussionDAO;
	}

	public DiscussionTableModel getDiscussionTableModel() {
		return discussionTableModel;
	}

	public void setDiscussionTableModel(
			DiscussionTableModel discussionTableModel) {
		this.discussionTableModel = discussionTableModel;
	}

	public abstract void perform(IDAOProgressMonitor progressMonitor) throws DAOException;

	public abstract String getName();
}