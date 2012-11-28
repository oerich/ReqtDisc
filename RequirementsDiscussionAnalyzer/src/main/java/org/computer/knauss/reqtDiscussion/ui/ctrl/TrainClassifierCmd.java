package org.computer.knauss.reqtDiscussion.ui.ctrl;

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
		classifier = ClassifierManager.getInstance().getClassifier();
	}

	@Override
	protected void processDiscussionHook(Discussion d) {
		if (classifier == null)
			return;
		// use currently loaded discussions for training.
		for (DiscussionEvent de : d.getDiscussionEvents()) {
			this.classifier.trainDiscussionEvent(de);
		}
		classifier.storeToFile();
		System.out.println();
	}

}
