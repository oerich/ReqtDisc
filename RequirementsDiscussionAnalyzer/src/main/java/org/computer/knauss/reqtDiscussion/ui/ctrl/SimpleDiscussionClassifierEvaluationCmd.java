package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.util.LinkedList;
import java.util.Random;

import oerich.nlputils.classifier.machinelearning.ILearningClassifier;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.DiscussionEventClassification;
import org.computer.knauss.reqtDiscussion.model.IClassificationFilter;
import org.computer.knauss.reqtDiscussion.model.machineLearning.ClassifierManager;
import org.computer.knauss.reqtDiscussion.model.metric.PatternMetric;

public class SimpleDiscussionClassifierEvaluationCmd extends
		AbstractDiscussionIterationCommand {

	private static final String REFERENCE_CLASSIFIER = "gpoo,eric1";

	private Bucket[] buckets;
	private Random r = new Random();
	private ILearningClassifier classifier;
	private PatternMetric metric;

	public SimpleDiscussionClassifierEvaluationCmd() {
		super("Evaluate discussion classification");

		this.buckets = new Bucket[10];
		for (int i = 0; i < this.buckets.length; i++) {
			this.buckets[i] = new Bucket();
		}
	}

	private static final long serialVersionUID = 1L;

	@Override
	protected void processDiscussionHook(Discussion d) {
		this.buckets[r.nextInt(10)].add(d);
	}

	@Override
	protected void preProcessingHook() {
		this.classifier = ClassifierManager.getInstance().getClassifier();
		this.metric = new PatternMetric();
	}

	@Override
	protected void postProcessingHook() {
		// TODO balance buckets

		try {
			// i is the left out part.
			for (int i = 0; i < this.buckets.length; i++) {
				System.out.println("Bucket " + i + "/10");
				classifier.clear();

				// use all buckets but i for training
				for (int j = 0; j < this.buckets.length; j++) {
					if (j != i) {
						trainClassifier(this.buckets[j]);
					}
				}

				classify(this.buckets[i]);
			}
			evaluate(this.buckets);

		} catch (Exception e) {
			// We could not clear the classifier? That is odd.
			e.printStackTrace();
		}
	}

	private void evaluate(Bucket[] buckets) {
		// TODO output as tex
		// TODO sort output according to paper
		// TODO add true positive, false positive, false negative, and true negative
		// TODO add recall, precision, specificity, and f-measure
		
		// TODO review and double check results
		
		int[][] confusionMatrix = new int[7][7];
		System.out.println("ID \t Actual \t Predicted");
		for (Bucket bucket : buckets) {
			for (Discussion d : bucket) {
				this.metric.initDiscussions(new Discussion[] { d });
				IClassificationFilter.NAME_FILTER.setName(REFERENCE_CLASSIFIER);
				int reference = this.metric.considerDiscussions(
						new Discussion[] { d }).intValue();
				IClassificationFilter.NAME_FILTER.setName(this.classifier
						.getClass().getSimpleName());
				int classification = this.metric.considerDiscussions(
						new Discussion[] { d }).intValue();

				// metric result starts at -1 (unknown)
				confusionMatrix[classification + 1][reference + 1]++;

				System.out.println(d.getID() + "\t"
						+ this.metric.decode(reference) + "\t"
						+ this.metric.decode(classification));
			}
		}

		// Print the heading of the confusion matrix
		System.out.println(this.classifier.getClass().getSimpleName() + "/"
				+ REFERENCE_CLASSIFIER + "\t" + this.metric.decode(-1) + "\t"
				+ this.metric.decode(0) + "\t" + this.metric.decode(1) + "\t"
				+ this.metric.decode(2) + "\t" + this.metric.decode(3) + "\t"
				+ this.metric.decode(4) + "\t" + this.metric.decode(5) + "\t"
				+ this.metric.decode(6));

		for (int i = 0; i < 7; i++) {
			String line = this.metric.decode(i - 1) + "\t";
			for (int j = 0; j < 7; j++) {
				line += confusionMatrix[i][j] + "\t";
			}

			System.out.println(line);
		}
	}

	private void classify(Bucket bucket) {
		for (Discussion d : bucket) {
			for (DiscussionEvent de : d.getDiscussionEvents()) {
				DiscussionEventClassification dec = new DiscussionEventClassification();

				double confidence = classifier.classify(de.getContent());
				dec.setClassifiedby(classifier.getClass().getSimpleName());
				dec.setConfidence(confidence);
				dec.setWorkitemcommentid(de.getID());

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
	}

	private void trainClassifier(Bucket bucket) {
		IClassificationFilter.NAME_FILTER.setName(REFERENCE_CLASSIFIER);

		for (Discussion d : bucket) {
			for (DiscussionEvent de : d.getDiscussionEvents()) {
				// TODO consider to use more attributes (e.g. creator, length)
				if (de.isClassified()) {
					if (de.isInClass()) {
						this.classifier.learnInClass(de.getContent());
						// System.out.print('!');
					} else {
						this.classifier.learnNotInClass(de.getContent());
						// System.out.print('.');
					}
				} else {
					// System.out.print('?');
				}
			}
			// System.out.println();
		}
	}

	// work around an annoying feature of Java generics
	// (http://stackoverflow.com/questions/217065/cannot-create-an-array-of-linkedlists-in-java)
	private class Bucket extends LinkedList<Discussion> {

		private static final long serialVersionUID = 1L;

	}

}
