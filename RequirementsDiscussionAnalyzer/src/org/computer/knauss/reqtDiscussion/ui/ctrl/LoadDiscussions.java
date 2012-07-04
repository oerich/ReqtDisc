package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.computer.knauss.reqtDiscussion.io.DAOException;
import org.computer.knauss.reqtDiscussion.io.IDiscussionDAO;
import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.ui.uiModel.DiscussionTableModel;

public class LoadDiscussions extends AbstractCommand {

	private static final long serialVersionUID = 1L;

	public LoadDiscussions() {
		super("Load discussions...");
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		DiscussionTableModel wtm = getWorkitemTableModel();

		Discussion[] data = new Discussion[0];

		try {
			IDiscussionDAO dao = getWorkitemDAO();
			data = dao.getDiscussions();
		} catch (DAOException e) {
			JOptionPane.showMessageDialog(null, "Could not load Discussions:\n "
					+ e.getCause().getClass().getSimpleName() + ":\n"
					+ e.getCause().getMessage(), "Data Access Exception",
					JOptionPane.ERROR_MESSAGE);
		}
		wtm.setDiscussions(data);
	}

}
