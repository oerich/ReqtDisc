package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import org.computer.knauss.reqtDiscussion.io.DAOException;
import org.computer.knauss.reqtDiscussion.io.IDAOProgressMonitor;
import org.computer.knauss.reqtDiscussion.io.IDiscussionDAO;
import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.ui.uiModel.DiscussionTableModel;

public class LoadDiscussions extends AbstractCommand {

	private static final long serialVersionUID = 1L;

	private ClassificationItemTask task;

	public LoadDiscussions() {
		super("Load discussions...");
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		task = new ClassificationItemTask();
		// task.addPropertyChangeListener(this);
		task.execute();
	}

	class ClassificationItemTask extends SwingWorker<Void, Void> implements
			IDAOProgressMonitor {

		private ProgressMonitor progressMonitor;
		private int totalSteps;

		@Override
		public Void doInBackground() {
			progressMonitor = new ProgressMonitor(null,
					getValue(AbstractAction.NAME), null, 0, 100);
			progressMonitor.setMillisToDecideToPopup(0);
			progressMonitor.setMillisToPopup(0);
			progressMonitor.setProgress(0);

			DiscussionTableModel wtm = getDiscussionTableModel();

			Discussion[] data = new Discussion[0];

			try {
				IDiscussionDAO dao = getDiscussionDAO();
				data = dao.getDiscussions(this);
			} catch (DAOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"Could not load Discussions:\n "
								+ e.getCause().getClass().getSimpleName()
								+ ":\n" + e.getCause().getMessage(),
						"Data Access Exception", JOptionPane.ERROR_MESSAGE);
			}

			// System.out.println("Loaded data, setting it into the model.");
			wtm.setDiscussions(data);
			return null;
		}

		@Override
		public void done() {
			setProgress(100);
			progressMonitor.close();
		}

		@Override
		public void setTotalSteps(int steps) {
			this.totalSteps = steps;
			// progressMonitor.setMaximum(steps);
		}

		@Override
		public void setStep(int step) {
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
