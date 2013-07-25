package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.util.concurrent.ExecutionException;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import org.computer.knauss.reqtDiscussion.io.DAOException;
import org.computer.knauss.reqtDiscussion.io.IConfigurable;
import org.computer.knauss.reqtDiscussion.io.IDAOProgressMonitor;

public class BackGroundDAOTask extends SwingWorker<Void, Void> implements
		IDAOProgressMonitor {

	private ProgressMonitor progressMonitor;
	private int totalSteps;
	private Subtask task;
	public Throwable cause;

	public BackGroundDAOTask(Subtask task) {
		this.task = task;
	}

	@Override
	public Void doInBackground() throws DAOException {
		progressMonitor = new ProgressMonitor(null, this.task.getName(), null,
				0, 100);
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
			String errorMessage = getErrorMessage(e);
			System.out.println(errorMessage);

			if (errorMessage
					.indexOf("The server requested password-based authentication, but no password was provided.") >= 0) {
				JTextField firstName = new JTextField();
				JPasswordField password = new JPasswordField();
				final JComponent[] inputs = new JComponent[] {
						new JLabel("User name"), firstName,
						new JLabel("Password"), password };
				JOptionPane.showMessageDialog(null, inputs,
						"Please provide username and password.",
						JOptionPane.PLAIN_MESSAGE);
				try {
					// Lets see. We probably need to set the password.
					this.task
							.getDiscussionDAO()
							.getConfiguration()
							.setProperty(IConfigurable.PROP_USER,
									firstName.getText());
					this.task
							.getDiscussionDAO()
							.getConfiguration()
							.setProperty(IConfigurable.PROP_PASS,
									new String(password.getPassword()));
					// Now start over.
					doInBackground();
				} catch (DAOException e2) {
					showError(e2, getErrorMessage(e2));
				}

			} else {
				showError(e, errorMessage);
			}
		}
	}

	void showError(Throwable e, String errorMessage) {
		e.printStackTrace();
		JOptionPane.showMessageDialog(null, "Could not load Discussions:\n"
				+ errorMessage, "Data Access Exception",
				JOptionPane.ERROR_MESSAGE);
	}

	String getErrorMessage(Throwable e) {
		cause = e.getCause();
		Throwable causeCause = cause.getCause();
		String errorType = cause.getClass().getSimpleName();
		String errorMessage = cause.getMessage();
		if (causeCause != null) {
			errorType += "(" + causeCause.getClass().getSimpleName() + ")";
			errorMessage += "\n(" + causeCause.getMessage() + ")";
		}
		errorMessage = errorType + ":\n" + errorMessage;
		return errorMessage;
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
