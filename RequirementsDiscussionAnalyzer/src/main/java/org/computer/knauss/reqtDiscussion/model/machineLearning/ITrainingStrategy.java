package org.computer.knauss.reqtDiscussion.model.machineLearning;

import java.util.HashMap;
import java.util.Map;

import oerich.nlputils.classifier.machinelearning.ILearningClassifier;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.DiscussionFactory;
import org.computer.knauss.reqtDiscussion.model.IClassificationFilter;
import org.computer.knauss.reqtDiscussion.model.machineLearning.metadata.DefaultMetricWrapper;
import org.computer.knauss.reqtDiscussion.model.machineLearning.metadata.IMetricWrapper;
import org.computer.knauss.reqtDiscussion.model.metric.AbstractDiscussionMetric;

public interface ITrainingStrategy {

	public final static ITrainingStrategy DEFAULT_STRAT = new ITrainingStrategy() {

		@Override
		public void trainClassifier(ILearningClassifier classifier,
				DiscussionEvent de, String referenceRaterName) {
			IClassificationFilter.NAME_FILTER.setName(referenceRaterName);

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

		@Override
		public void preprocess(Discussion d) {

		}
	};

	public static final ITrainingStrategy META_DATA_STRATEGY = new ITrainingStrategy() {

		private Map<AbstractDiscussionMetric, IMetricWrapper> metricwrappers = new HashMap<AbstractDiscussionMetric, IMetricWrapper>();

		@Override
		public void trainClassifier(ILearningClassifier classifier,
				DiscussionEvent de, String referenceRaterName) {
			IClassificationFilter.NAME_FILTER.setName(referenceRaterName);

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

		@SuppressWarnings("deprecation")
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
				// Double metricResult = m
				// .considerDiscussions(new Discussion[] { DiscussionFactory
				// .getInstance().getDiscussion(
				// de.getDiscussionID()) });
				Object metricResult = getMetricWrapper(m).measure(
						DiscussionFactory.getInstance().getDiscussion(
								de.getDiscussionID()));
				// we have only integer values in the standard metrics. Also,
				// the classifier would replace the dot by a space.
				// text.append(metricResult.intValue());
				text.append(metricResult);
			}
			// System.out.println(text.toString());
			return text.toString();
		}

		@Override
		public void preprocess(Discussion d) {
			for (AbstractDiscussionMetric m : AbstractDiscussionMetric.STANDARD_METRICS) {
				getMetricWrapper(m).preprocess(d);
			}
		}

		private IMetricWrapper getMetricWrapper(AbstractDiscussionMetric m) {
			IMetricWrapper ret = this.metricwrappers.get(m);
			if (ret == null) {
				ret = new DefaultMetricWrapper();
				ret.init(m, new Object[] { "00", "10", "20", "30", "40", "50",
						"60", "70", "80", "90" });
				this.metricwrappers.put(m, ret);
			}
			return ret;
		}
	};

	void trainClassifier(ILearningClassifier classifier, DiscussionEvent de,
			String referenceRaterName);

	String getStringForClassification(DiscussionEvent de);

	/**
	 * Allows to normalize metadata.
	 * 
	 * @param d
	 */
	void preprocess(Discussion d);
}
