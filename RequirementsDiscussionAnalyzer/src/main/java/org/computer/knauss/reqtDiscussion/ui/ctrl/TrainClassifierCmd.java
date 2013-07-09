package org.computer.knauss.reqtDiscussion.ui.ctrl;

import javax.swing.JOptionPane;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.machineLearning.ClassifierManager;
import org.computer.knauss.reqtDiscussion.model.machineLearning.IDiscussionEventClassifier;

public class TrainClassifierCmd extends AbstractDiscussionIterationCommand {
	private static final long serialVersionUID = 1L;
	private IDiscussionEventClassifier classifier;

	public TrainClassifierCmd() {
		super("Train classifier");
		// TODO use only selected discussions?
	}

	@Override
	protected void preProcessingHook() {
		// Which classifier to use?
		IDiscussionEventClassifier[] options = ClassifierManager.getInstance()
				.getAvailableClassifiers();
		IDiscussionEventClassifier classifier = (IDiscussionEventClassifier) JOptionPane
				.showInputDialog(null,
						"Please select the classifier for evaluation.",
						"Classifier", JOptionPane.QUESTION_MESSAGE, null,
						options, options[0]);
		if (classifier == null)
			return;

		this.classifier = classifier;
	}

	@Override
	protected void processDiscussionHook(Discussion[] d) {
		if (classifier == null)
			return;
		// use currently loaded discussions for training.
		for (DiscussionEvent de : getDiscussionEvents(d)) {
			this.classifier.trainDiscussionEvent(de);
		}
		classifier.storeToFile();
//		System.out.println();
	}

}
