package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.io.IOException;

import oerich.nlputils.classifier.machinelearning.NewBayesianClassifier;

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
		try {
			((NewBayesianClassifier) classifier).storeToFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println();
	}

}
