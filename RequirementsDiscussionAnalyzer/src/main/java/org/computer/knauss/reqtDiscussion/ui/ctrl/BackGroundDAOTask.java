package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import org.computer.knauss.reqtDiscussion.io.DAOException;
import org.computer.knauss.reqtDiscussion.io.IDAOProgressMonitor;

public class BackGroundDAOTask extends SwingWorker<Void, Void> implements
		IDAOProgressMonitor {

	private ProgressMonitor progressMonitor;
	private int totalSteps;
	private Subtask task;

	public BackGroundDAOTask(Subtask task) {
		this.task = task;
	}

	@Override
	public Void doInBackground() throws DAOException {
		progressMonitor = new ProgressMonitor(null,
				this.task.getName(), null, 0, 100);
		progressMonitor.setMillisToDecideToPopup(0);
		progressMonitor.setMillisToPopup(0);
		progressMonitor.setProgress(0);

		this.task.perform(this);

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
				errorType += "(" + causeCause.getClass().getSimpleName() + ")";
				errorMessage += "(" + causeCause.getMessage() + ")";
			}
			JOptionPane.showMessageDialog(null, "Could not load Discussions:\n"
					+ errorType + ":\n" + errorMessage,
					"Data Access Exception", JOptionPane.ERROR_MESSAGE);
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
