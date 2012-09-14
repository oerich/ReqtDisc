package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.awt.event.ActionEvent;

import org.computer.knauss.reqtDiscussion.io.DAOException;
import org.computer.knauss.reqtDiscussion.ui.EditPropertiesFrame;

public class EditDatasourceCommand extends AbstractCommand {

	public EditDatasourceCommand() {
		super("Edit data source configuration");
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent arg0) {
		try {
			EditPropertiesFrame epf = new EditPropertiesFrame();
			epf.setProperties(getDiscussionDAO().getConfiguration());
			epf.setRemarks(getDiscussionDAO().checkConfiguration());
			epf.pack();
			epf.setVisible(true);

		} catch (DAOException e) {
			e.printStackTrace();
		}
	}
}
