package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.io.IOException;

import oerich.nlputils.classifier.machinelearning.ILearningClassifier;
import oerich.nlputils.classifier.machinelearning.NewBayesianClassifier;

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
			if (de.isClassified()) {
				if (de.isInClass()) {
					this.classifier.learnInClass(de.getContent());
					System.out.print('!');
				} else {
					this.classifier.learnNotInClass(de.getContent());
					System.out.print('.');
				}
			} else {
				System.out.print('?');
			}
		}
		try {
			((NewBayesianClassifier)classifier).storeToFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println();
	}

}
