package org.computer.knauss.reqtDiscussion.ui.ctrl;

import javax.swing.AbstractAction;

import org.computer.knauss.reqtDiscussion.io.DAOException;
import org.computer.knauss.reqtDiscussion.io.DAORegistry;
import org.computer.knauss.reqtDiscussion.io.IDiscussionDAO;
import org.computer.knauss.reqtDiscussion.io.IDiscussionEventClassificationDAO;
import org.computer.knauss.reqtDiscussion.model.VisualizationConfiguration;
import org.computer.knauss.reqtDiscussion.ui.uiModel.DiscussionTableModel;

public abstract class AbstractCommand extends AbstractAction {

	private static final long serialVersionUID = 1L;
	private DiscussionTableModel workitemTableModel;
	private DAORegistry daoRegistry;
	private VisualizationConfiguration configuration;

	public AbstractCommand(String name) {
		super(name);
	}

	public IDiscussionDAO getDiscussionDAO() throws DAOException {
		return this.daoRegistry.getSelectedDAOManager().getDiscussionDAO();
	}

	public void setDiscussionTableModel(DiscussionTableModel wtm) {
		this.workitemTableModel = wtm;
	}

	public DiscussionTableModel getDiscussionTableModel() {
		return this.workitemTableModel;
	}

	public IDiscussionEventClassificationDAO getDiscussionEventClassificationDAO()
			throws DAOException {
		return this.daoRegistry.getSelectedDAOManager()
				.getDiscussionEventClassificationDAO();
	}

	public void setDAORegistry(DAORegistry dr) {
		this.daoRegistry = dr;
	}

	public void setVisualizationConfiguration(
			VisualizationConfiguration configuration) {
		this.configuration = configuration;
	}

	public VisualizationConfiguration getVisualizationConfiguration() {
		return this.configuration;
	}
}
