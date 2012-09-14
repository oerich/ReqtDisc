package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import org.computer.knauss.reqtDiscussion.io.DAOException;
import org.computer.knauss.reqtDiscussion.io.IDAOProgressMonitor;
import org.computer.knauss.reqtDiscussion.ui.EditPropertiesFrame;
import org.computer.knauss.reqtDiscussion.ui.uiModel.DiscussionTableModel;

public class LoadDiscussions extends AbstractCommand {

	private static final long serialVersionUID = 1L;

	private ClassificationItemTask task;

	public LoadDiscussions() {
		super("Load discussions...");
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
				task = new ClassificationItemTask();
				// task.addPropertyChangeListener(this);
				task.execute();
			}
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	class ClassificationItemTask extends SwingWorker<Void, Void> implements
			IDAOProgressMonitor {

		private ProgressMonitor progressMonitor;
		private int totalSteps;

		@Override
		public Void doInBackground() throws DAOException {
			progressMonitor = new ProgressMonitor(null,
					getValue(AbstractAction.NAME), null, 0, 100);
			progressMonitor.setMillisToDecideToPopup(0);
			progressMonitor.setMillisToPopup(0);
			progressMonitor.setProgress(0);

			DiscussionTableModel wtm = getDiscussionTableModel();

			wtm.setDiscussions(getDiscussionDAO().getDiscussions(this));
			return null;
		}

		@Override
		public void done() {
			setProgress(100);
			progressMonitor.close();
			try {
				get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
				Throwable cause = e.getCause();
				Throwable causeCause = cause.getCause();
				String errorType = cause.getClass().getSimpleName();
				String errorMessage = cause.getMessage();
				if (causeCause != null) {
					errorType += "(" + causeCause.getClass().getSimpleName()
							+ ")";
					errorMessage += "(" + causeCause.getMessage() + ")";
				}
				JOptionPane.showMessageDialog(null,
						"Could not load Discussions:\n" + errorType + ":\n"
								+ errorMessage, "Data Access Exception",
						JOptionPane.ERROR_MESSAGE);
			}
		}

		@Override
		public void setTotalSteps(int steps) {
			this.totalSteps = steps;
			// progressMonitor.setMaximum(steps);
		}

		@Override
		public void setStep(int step) {
			if (this.totalSteps != 0)
				progressMonitor.setProgress((step * 100) / this.totalSteps);
		}

		@Override
		public void setStep(int step, String message) {
			// System.out.println(message + ": " + step + "/" + totalSteps);
			setStep(step);
			progressMonitor.setNote(message);
		}
	}
}
