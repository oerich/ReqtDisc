package org.computer.knauss.reqtDiscussion.model.machineLearning;

import oerich.nlputils.classifier.machinelearning.ILearningClassifier;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.DiscussionFactory;
import org.computer.knauss.reqtDiscussion.model.IClassificationFilter;
import org.computer.knauss.reqtDiscussion.model.metric.AbstractDiscussionMetric;

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
					classifier.learnInClass(getStringForClassification(de));
					// System.out.print('!');
				} else {
					classifier.learnNotInClass(getStringForClassification(de));
					// System.out.print('.');
				}
			} else {
				// System.out.print('?');
			}
		}

		@Override
		public String getStringForClassification(DiscussionEvent de) {
			if (de == null)
				return null;
			return de.getContent();
		}
	};

	public static final ITrainingStrategy META_DATA_STRATEGY = new ITrainingStrategy() {

		@Override
		public void trainClassifier(ILearningClassifier classifier,
				DiscussionEvent de, String referenceRaterName) {
			IClassificationFilter.NAME_FILTER.setName(referenceRaterName);

			// TODO consider to use more attributes (e.g. creator,
			// length)
			if (de.isClassified()) {
				if (de.isInClass()) {
					classifier.learnInClass(getStringForClassification(de));
					// System.out.print('!');
				} else {
					classifier.learnNotInClass(getStringForClassification(de));
					// System.out.print('.');
				}
			} else {
				// System.out.print('?');
			}
		}

		@Override
		public String getStringForClassification(DiscussionEvent de) {
			if (de == null)
				return null;

			StringBuffer text = new StringBuffer();
			text.append(de.getContent());

			// add some metadata.
			text.append(" 0creator0");
			text.append(de.getCreator());
			text.append(" 0month0");
			text.append(String.valueOf(de.getCreationDate().getMonth()));
			text.append(String.valueOf(de.getCreationDate().getYear()));
			text.append(" 0discussionstatus0");
			text.append(DiscussionFactory.getInstance()
					.getDiscussion(de.getDiscussionID()).getStatus());
			text.append(" 0discussiontype0");
			text.append(DiscussionFactory.getInstance()
					.getDiscussion(de.getDiscussionID()).getType()
					.replace('.', '1'));

			for (AbstractDiscussionMetric m : AbstractDiscussionMetric.STANDARD_METRICS) {
				text.append(" 0discussion");
				text.append(m.getClass().getSimpleName());
				text.append("0");
				Double metricResult = m
						.considerDiscussions(new Discussion[] { DiscussionFactory
								.getInstance().getDiscussion(
										de.getDiscussionID()) });
				// we have only integer values in the standard metrics. Also,
				// the classifier would replace the dot by a space.
				text.append(metricResult.intValue());
			}
			// System.out.println(text.toString());
			return text.toString();
		}
	};

	void trainClassifier(ILearningClassifier classifier, DiscussionEvent de,
			String referenceRaterName);

	String getStringForClassification(DiscussionEvent de);
}
