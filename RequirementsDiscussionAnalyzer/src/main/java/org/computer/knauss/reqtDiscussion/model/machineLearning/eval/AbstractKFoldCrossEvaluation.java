package org.computer.knauss.reqtDiscussion.model.machineLearning.eval;

import org.computer.knauss.reqtDiscussion.io.IDAOProgressMonitor;
import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.DiscussionEventClassification;
import org.computer.knauss.reqtDiscussion.model.IClassificationFilter;
import org.computer.knauss.reqtDiscussion.model.IDiscussionFilter;
import org.computer.knauss.reqtDiscussion.model.machineLearning.IDiscussionEventClassifier;
import org.computer.knauss.reqtDiscussion.model.machineLearning.ITrainingStrategy;
import org.computer.knauss.reqtDiscussion.model.metric.PatternMetric;
import org.computer.knauss.reqtDiscussion.ui.ctrl.ExportVisualization;

public abstract class AbstractKFoldCrossEvaluation {

	public static final AbstractKFoldCrossEvaluation DISCUSSION_LEVEL = new AbstractKFoldCrossEvaluation() {

		@Override
		protected ConfusionMatrix evaluate(
				AbstractBucketBalancingStrategy buckets) {
			ConfusionMatrix confusionMatrix = new ConfusionMatrix();
			confusionMatrix.init(new String[] { "indifferent", "happy-ending",
					"discordant", "back-to-draft", "textbook-example",
					"procrastination", "unknown" });
			// System.out.println("ID \t Actual \t Predicted");
			for (int b = 0; b < buckets.getNumberOfBuckets(); b++) {
				for (Discussion discussion : buckets.getDiscussionsForBucket(b)) {
					if (getDiscussionFilter().accept(discussion)) {
						// workaround until the composite discussion has made it
						// through the whole system
						Discussion[] ds = new Discussion[] { discussion };
						getPatternMetric().initDiscussions(ds);
						IClassificationFilter.NAME_FILTER
								.setName(getReferenceRaterName());
						int reference = getPatternMetric().considerDiscussions(
								ds).intValue();
						IClassificationFilter.NAME_FILTER
								.setName(getClassifier().getClass()
										.getSimpleName());
						int classification = getPatternMetric()
								.considerDiscussions(ds).intValue();

						String referencePattern = getPatternMetric().decode(
								reference);
						String classifiedPattern = getPatternMetric().decode(
								classification);
						confusionMatrix.report(referencePattern,
								classifiedPattern);
						
						if(this.exporter != null && !referencePattern.equals(classifiedPattern))
							exporter.export(ds, referencePattern + "-" + classifiedPattern);

						// StringBuffer line = new StringBuffer();
						// line.append(discussion.getID());
						// line.append(',');
						// line.append("\t");
						// line.append(getPatternMetric().decode(reference));
						// line.append("\t");
						// line.append(getPatternMetric().decode(classification));
						// System.out.println(line.toString());
					}
				}
			}
			// Reset the name of the reference rater
			IClassificationFilter.NAME_FILTER.setName(getReferenceRaterName());
			return confusionMatrix;
		}
	};
	public static final AbstractKFoldCrossEvaluation DISCUSSION_EVENT_LEVEL = new AbstractKFoldCrossEvaluation() {

		@Override
		protected ConfusionMatrix evaluate(
				AbstractBucketBalancingStrategy buckets) {
			ConfusionMatrix cm = new ConfusionMatrix();
			cm.init(new String[] { "clari", "coord", "other", "autog", "no cl",
					"Solut", "unkno" });
			// System.out.println("ID \t Actual \t Predicted");
			for (int b = 0; b < buckets.getNumberOfBuckets(); b++) {
				for (DiscussionEvent de : buckets
						.getDiscussionEventsForBucket(b)) {
					IClassificationFilter.NAME_FILTER
							.setName(getReferenceRaterName());
					String reference = de.getReferenceClassification()
							.substring(0, 5);
					IClassificationFilter.NAME_FILTER.setName(getClassifier()
							.getClass().getSimpleName());
					String classification = de.getReferenceClassification()
							.substring(0, 5);

					cm.report(reference, classification);

					// StringBuffer line = new StringBuffer();
					// line.append(de.getID());
					// line.append("\t");
					// line.append(reference);
					// line.append("\t");
					// line.append(classification);
					// System.out.println(line.toString());
				}
			}
			// Reset the name of the reference rater
			IClassificationFilter.NAME_FILTER.setName(getReferenceRaterName());

			return cm;
		}
	};
	private AbstractBucketBalancingStrategy bucketAllocationStrat;
	private IDiscussionEventClassifier classifier;
	private String referenceRaterName;
	private boolean aggregateDiscussions;
	private PatternMetric metric;
	private IDiscussionFilter discussionFilter = new IDiscussionFilter() {

		@Override
		public boolean accept(Discussion d) {
			for (DiscussionEvent de : d.getDiscussionEvents())
				if (de.isClassified())
					return true;
			return false;
		}
	};
	private ITrainingStrategy trainingStrat = ITrainingStrategy.DEFAULT_STRAT;
	protected ExportVisualization exporter;

	public ConfusionMatrix evaluate(int k, Discussion[] discussions,
			IDAOProgressMonitor progressMonitor) {
		getBucketAllocationStrategy().distributedOverKBuckets(k, discussions,
				isAggregateDiscussions());

		// allow the strategy to change the k
		k = getBucketAllocationStrategy().getNumberOfBuckets();

		progressMonitor.setTotalSteps(2 * k + 1);
		progressMonitor.setStep(0, "Preparing...");
		try {
			for (int i = 0; i < k; i++) {
				progressMonitor.setStep(2 * i, "Training...");
				getClassifier().clear();
				// use all buckets but i for training
				for (int j = 0; j < k; j++) {
					if (j != i) {
						trainClassifier(getBucketAllocationStrategy()
								.getDiscussionEventsForBucket(j));
					}
				}

				progressMonitor.setStep(2 * i + 1, "Classifying...");
				classify(getBucketAllocationStrategy()
						.getDiscussionEventsForBucket(i));
			}
			progressMonitor.setStep(2 * k, "Evaluating...");
			return evaluate(getBucketAllocationStrategy());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	private void trainClassifier(DiscussionEvent[] des) {
		for (DiscussionEvent de : des) {
			getClassifier().trainDiscussionEvent(de, getReferenceRaterName());
		}
	}

	public String getReferenceRaterName() {
		return this.referenceRaterName;
	}

	public void setReferenceRaterName(String raterName) {
		System.out.println(getClass().getName()
				+ ".setReferenceRaterName(" + raterName + ")");
		this.referenceRaterName = raterName;
	}

	private void classify(DiscussionEvent[] des) throws Exception {
		for (DiscussionEvent de : des) {
			DiscussionEventClassification dec = new DiscussionEventClassification();

			double confidence = classifier.classify(de);
			dec.setClassifiedby(classifier.getClass().getSimpleName());
			dec.setConfidence(confidence);
			dec.setDiscussionEventID(de.getID());

			if (classifier.getMatchValue() < confidence) {
				dec.setClassification("clarif");
				// System.out.print('!');
			} else {
				dec.setClassification("other");
				// System.out.print('.');
			}
			de.insertOrUpdateClassification(dec);
		}
		// System.out.println();
	}

	protected abstract ConfusionMatrix evaluate(
			AbstractBucketBalancingStrategy buckets);

	public PatternMetric getPatternMetric() {
		if (this.metric == null)
			this.metric = new PatternMetric();
		return this.metric;
	}

	public IDiscussionEventClassifier getClassifier() {
		return this.classifier;
	}

	public void setClassifier(
			IDiscussionEventClassifier iDiscussionEventClassifier) {
		this.classifier = iDiscussionEventClassifier;
	}

	public boolean isAggregateDiscussions() {
		return this.aggregateDiscussions;
	}

	public void setAggregateDiscussions(boolean arg) {
		this.aggregateDiscussions = arg;
	}

	public AbstractBucketBalancingStrategy getBucketAllocationStrategy() {
		return this.bucketAllocationStrat;
	}

	public void setBucketAllocationStrategy(
			AbstractBucketBalancingStrategy bucketingStrat) {
		this.bucketAllocationStrat = bucketingStrat;
	}

	public void setDiscussionFilter(IDiscussionFilter discussionFilter) {
		this.discussionFilter = discussionFilter;
	}

	public IDiscussionFilter getDiscussionFilter() {
		return this.discussionFilter;
	}

	public ITrainingStrategy getTrainingStrategy() {
		return trainingStrat;
	}

	public void setTrainingStrategy(ITrainingStrategy trainingStrat) {
		this.trainingStrat = trainingStrat;
	}

	/**
	 * If no null, this exporter will be used for storing automatically created
	 * trajectories during evaluation to compare them later.
	 * 
	 * @param exporter
	 */
	public void setVisualizationExporter(ExportVisualization exporter) {
		this.exporter = exporter;
	}
}
