package org.computer.knauss.reqtDiscussion.model.machineLearning.eval;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import oerich.nlputils.classifier.machinelearning.ILearningClassifier;
import oerich.nlputils.classifier.machinelearning.NewBayesianClassifier;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.DiscussionEventClassification;
import org.computer.knauss.reqtDiscussion.model.IClassificationFilter;

public class HybridKFoldCrossDiscussionEventEvaluation {

	private static final String REFERENCE_CLASSIFIER = "gpoo,eric1";

	private ILearningClassifier clariClassifier;
	private ILearningClassifier coordClassifier;

	private class DiscussionEventBucket extends LinkedList<DiscussionEvent> {

		private static final long serialVersionUID = 1L;

	}

	public HybridKFoldCrossDiscussionEventEvaluation() {
		try {
			this.clariClassifier = new NewBayesianClassifier();
			this.coordClassifier = new NewBayesianClassifier();
			coordClassifier.init(new File("coordClassifier.txt"));
			clariClassifier.init(new File("clariClassifier.txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Do the evaluation and create the confusion matrix.
	 * 
	 * @param k
	 *            number of buckets (default: 10).
	 * @param discussions
	 *            array with all discussions that should be used in evaluation
	 *            (default: all you have).
	 * @param aggregateBuckets
	 *            switch to aggregate dependent stories (default: true).
	 * @return confusion matrix with prediction in columns and actual values in
	 *         rows. Size is PatternMetric.PATTERNS.length + 1 (we have the
	 *         unknown value to store, too)
	 */
	public ConfusionMatrix evaluate(int k, Discussion[] discussions) {
		DiscussionEventBucket[] buckets = distributedOverKBuckets(k,
				discussions);

		try {
			for (int i = 0; i < buckets.length; i++) {
				System.out.println("Bucket " + i + "/" + k);
				this.clariClassifier.clear();
				this.coordClassifier.clear();

				// use all buckets but i for training
				for (int j = 0; j < buckets.length; j++) {
					if (j != i) {
						trainClassifier(buckets[j]);
					}
				}

				classify(buckets[i]);
			}
			return evaluate(buckets);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private DiscussionEventBucket[] distributedOverKBuckets(int k,
			Discussion[] discussions) {

		Random r = new Random(System.currentTimeMillis());

		DiscussionEventBucket[] ret = new DiscussionEventBucket[k];

		for (int i = 0; i < ret.length; i++) {
			ret[i] = new DiscussionEventBucket();
		}

		List<DiscussionEvent> events = new LinkedList<DiscussionEvent>();
		for (Discussion d : discussions)
			for (DiscussionEvent de : d.getDiscussionEvents())
				events.add(de);

		while (events.size() > 0) {
			for (DiscussionEventBucket b : ret) {
				int size = events.size();
				if (size == 0)
					break;
				b.add(events.remove(r.nextInt(size)));
			}
		}

		return ret;
	}

	private void trainClassifier(DiscussionEventBucket bucket) {
		IClassificationFilter.NAME_FILTER.setName(REFERENCE_CLASSIFIER);

		for (DiscussionEvent de : bucket) {
			if (de.isClassified()) {
				if (de.isInClass()) {
					this.clariClassifier.learnInClass(de.getContent());
					this.coordClassifier.learnNotInClass(de.getContent());
					// System.out.print('!');
				} else {
					this.clariClassifier.learnNotInClass(de.getContent());
					this.coordClassifier.learnInClass(de.getContent());
					// System.out.print('.');
				}
			} else {
				// System.out.print('?');
			}
		}
		// System.out.println();
	}

	private void classify(DiscussionEventBucket bucket) {
		for (DiscussionEvent de : bucket) {
			DiscussionEventClassification dec = new DiscussionEventClassification();

			double confidence = clariClassifier.classify(de.getContent());
			dec.setClassifiedby(clariClassifier.getClass().getSimpleName());
			dec.setConfidence(confidence);
			dec.setWorkitemcommentid(de.getID());

			if (clariClassifier.getMatchValue() < confidence
					&& !coordClassifier.isMatch(de.getContent())) {
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

	public void printConfusionMatrix(ConfusionMatrix cm) {

		ConfigurableLayouter cl = new ConfigurableLayouter(" \t ", "\n");
		System.out.println(cl.layoutConfusionMatrix(cm));
		// int[][] confusionMatrix = cm.getConfusionMatrix();
		// System.out.println("predicted/actual \t clarif \t other ");
		// for (int row = 0; row < confusionMatrix.length; row++) {
		// if (row == 0)
		// System.out.print("clarif \t");
		// else if (row == 1)
		// System.out.print("other \t");
		// else
		// System.out.print("unknown row \t");
		// for (int col = 0; col < confusionMatrix[row].length; col++) {
		// System.out.print(confusionMatrix[row][col] + "\t");
		// }
		// System.out.println();
		// }
		// double precision = cm.getPrecision("clarif");
		// double recall = cm.getRecall("clarif");
		// double specificity = cm.getSpecificity("clarif");
		// double fMeasure = cm.getFMeasure("clarif");
		// System.out.println("recall \t precision \t f-measure \t specificity");
		// System.out.println(df.format(recall) + "\t" + df.format(precision)
		// + "\t" + df.format(fMeasure) + "\t" + df.format(specificity));
	}

	private ConfusionMatrix evaluate(DiscussionEventBucket[] buckets) {
		ConfusionMatrix confusionMatrix = new ConfusionMatrix();
		confusionMatrix.init(new String[] { "clari", "coord", "other", "no cl" });
		System.out.println("ID \t Actual \t Predicted");
		for (DiscussionEventBucket bucket : buckets) {
			for (DiscussionEvent de : bucket) {
				IClassificationFilter.NAME_FILTER.setName(REFERENCE_CLASSIFIER);
				String reference = de.getReferenceClassification().substring(0,
						5);
				IClassificationFilter.NAME_FILTER.setName(this.clariClassifier
						.getClass().getSimpleName());
				String classification = de.getReferenceClassification().substring(0,
						5);

				confusionMatrix.report(reference, classification);

				// metric result starts at -1 (unknown)

				StringBuffer line = new StringBuffer();
				line.append(de.getID());
				line.append("\t");
				line.append(reference);
				line.append("\t");
				line.append(classification);
				System.out.println(line.toString());
			}
		}

		return confusionMatrix;
	}

}
