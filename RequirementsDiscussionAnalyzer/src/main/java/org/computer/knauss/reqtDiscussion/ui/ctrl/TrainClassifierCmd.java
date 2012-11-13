package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.awt.event.ActionEvent;

import oerich.nlputils.classifier.machinelearning.ILearningClassifier;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.machineLearning.ClassifierManager;

public class TrainClassifierCmd extends AbstractDiscussionIterationCommand {
	private static final long serialVersionUID = 1L;
	private ILearningClassifier classifier;

	public TrainClassifierCmd() {
		super("Train classifier");
		// TODO use only selected discussions?
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

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
			// TODO consider to use more attributes (e.g. creator, length)
			if (de.isInClass())
				this.classifier.learnInClass(de.getContent());
			else
				this.classifier.learnNotInClass(de.getContent());
		}
	}

}
