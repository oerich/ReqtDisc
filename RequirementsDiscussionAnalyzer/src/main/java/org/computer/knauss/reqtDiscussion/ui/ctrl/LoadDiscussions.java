package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.awt.event.ActionEvent;
import java.util.Map;

import org.computer.knauss.reqtDiscussion.io.DAOException;
import org.computer.knauss.reqtDiscussion.io.IDAOProgressMonitor;
import org.computer.knauss.reqtDiscussion.ui.EditPropertiesFrame;

public class LoadDiscussions extends AbstractCommand {

	private static final String NAME = "Load discussions...";
	private static final long serialVersionUID = 1L;
	private BackGroundDAOTask task;

	public LoadDiscussions() {
		super(NAME);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		// Let's see if the configuration is okay...
		try {
			// System.out.println("Let's check the configuration");
			Map<String, String> remarks = getDiscussionDAO()
					.checkConfiguration();
			// System.out.println("We have " + remarks.size() +
			// " configuration errors");
			// for (String key : remarks.keySet())
			// System.out.println(" - " + key + ": " + remarks.get(key));

			if (remarks.size() > 0) {
				EditPropertiesFrame epf = new EditPropertiesFrame();
				epf.setProperties(getDiscussionDAO().getConfiguration());
				epf.setRemarks(remarks);
				epf.pack();
				epf.setVisible(true);
			} else {
				task = new BackGroundDAOTask(new Subtask(
						getDiscussionTableModel(), getDiscussionDAO()) {

					@Override
					public void perform(IDAOProgressMonitor progressMonitor)
							throws DAOException {
						getDiscussionTableModel().setDiscussions(
								getDiscussionDAO().getDiscussions(
										progressMonitor));
					}

					@Override
					public String getName() {
						return NAME;
					}

				});
				// task.addPropertyChangeListener(this);
				task.execute();
			}
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
