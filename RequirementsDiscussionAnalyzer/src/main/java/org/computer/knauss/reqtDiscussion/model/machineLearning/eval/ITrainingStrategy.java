package org.computer.knauss.reqtDiscussion.model.machineLearning.eval;

import oerich.nlputils.classifier.machinelearning.ILearningClassifier;

import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.IClassificationFilter;

public interface ITrainingStrategy {

	public final static ITrainingStrategy DEFAULT_STRAT = new ITrainingStrategy() {

		@Override
		public void trainClassifier(ILearningClassifier classifier,
				DiscussionEvent de, String referenceRaterName) {
			IClassificationFilter.NAME_FILTER.setName(referenceRaterName);

			// TODO consider to use more attributes (e.g. creator,
			// length)
			if (de.isClassified()) {
				if (de.isInClass()) {
					classifier.learnInClass(de.getContent());
					// System.out.print('!');
				} else {
					classifier.learnNotInClass(de.getContent());
					// System.out.print('.');
				}
			} else {
				// System.out.print('?');
			}
		}
	};

	void trainClassifier(ILearningClassifier classifier, DiscussionEvent de,
			String referenceRaterName);
}
