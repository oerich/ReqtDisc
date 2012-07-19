package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import org.computer.knauss.reqtDiscussion.model.Discussion;

public abstract class AbstractDiscussionIterationCommand extends
		AbstractCommand implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;
	private ProgressMonitor progressMonitor;
	private ClassificationItemTask task;

	public AbstractDiscussionIterationCommand(String name) {
		super(name);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		progressMonitor = new ProgressMonitor(null,
				getValue(AbstractAction.NAME), null, 0, 100);
		progressMonitor.setProgress(0);
		task = new ClassificationItemTask();
		task.addPropertyChangeListener(this);
		task.execute();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			int progress = (Integer) evt.getNewValue();
			progressMonitor.setProgress(progress);
			if (progressMonitor.isCanceled()) {
				task.cancel(true);
			}
		}
	}

	protected void preProcessingHook() {

	}

	protected void postProcessingHook() {

	}

	protected abstract void processDiscussionHook(Discussion d);

	class ClassificationItemTask extends SwingWorker<Void, Void> {
		@Override
		public Void doInBackground() {
			preProcessingHook();

			Discussion[] discussions = getDiscussionTableModel()
					.getDiscussions();

			int progress = 0;
			int total = discussions.length;

			setProgress(0);

			try {
				for (Discussion d : discussions) {
					if (isCancelled()) {
						System.err.println(getClass().getSimpleName()
								+ ": Canceled.");
						break;
					}
					processDiscussionHook(d);
					setProgress((progress++ * 100) / total);
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, e.getClass()
						.getSimpleName() + ": " + e.getMessage(),
						"Error performing " + getValue(AbstractAction.NAME),
						JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
			// set progress to 100 to close progress monitor
			setProgress((progress++ * 100) / total);
			return null;
		}

		@Override
		public void done() {
			postProcessingHook();
		}
	}
}
