package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.util.LinkedList;
import java.util.Random;

import oerich.nlputils.classifier.machinelearning.ILearningClassifier;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.DiscussionEventClassification;
import org.computer.knauss.reqtDiscussion.model.IClassificationFilter;
import org.computer.knauss.reqtDiscussion.model.clarificationPatterns.BackToDraftPattern;
import org.computer.knauss.reqtDiscussion.model.clarificationPatterns.DiscordantPattern;
import org.computer.knauss.reqtDiscussion.model.clarificationPatterns.HappyEndingPattern;
import org.computer.knauss.reqtDiscussion.model.clarificationPatterns.IndifferentPattern;
import org.computer.knauss.reqtDiscussion.model.clarificationPatterns.ProcrastinationPattern;
import org.computer.knauss.reqtDiscussion.model.clarificationPatterns.TextbookPattern;
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
		// map from PatternMetric results (+1) to order of patterns in the
		// paper, e.g. PatternMetric returns -1 for "unknown", on index 0
		// (=-1+1) we refer to index 6, which is the position the unknown values
		// should have in the paper.
		// the order should be indifferent, happy-ending, discordant,
		// back-to-draft, text-book, procrastination, unknown
		// the order is unknown, Indifferent, Discordant, Procrastination,
		// BackToDraft, HappyEnding, Textbook
		int[] ordering = new int[] { 6, 0, 2, 5, 3, 1, 4 };

		int[][] confusionMatrix = createConfusionMatrix(buckets, ordering);

		printConfusionMatrix(ordering, confusionMatrix);
	}

	private void printConfusionMatrix(int[] ordering, int[][] confusionMatrix) {
		IConfusionMatrixLayouter layouter = new TabLayouter();
		System.out.println(layouter.layoutConfusionMatrix(confusionMatrix,
				ordering, metric));
		
		layouter = new ConfigurableLayouter("\t", "\n");
		System.out.println(layouter.layoutConfusionMatrix(confusionMatrix,
				ordering, metric));
		layouter = new ConfigurableLayouter(" & ", "\\tabularnewline \n");
		System.out.println(layouter.layoutConfusionMatrix(confusionMatrix,
				ordering, metric));	
		
	}

	private int[][] createConfusionMatrix(Bucket[] buckets, int[] ordering) {
		int[][] confusionMatrix = new int[7][7];
		System.out.println("ID \t Actual \t Predicted");
		for (Bucket bucket : buckets) {
			for (Discussion d : bucket) {
				if (considerDiscussion(d)) {

					this.metric.initDiscussions(new Discussion[] { d });
					IClassificationFilter.NAME_FILTER
							.setName(REFERENCE_CLASSIFIER);
					int reference = this.metric.considerDiscussions(
							new Discussion[] { d }).intValue();
					IClassificationFilter.NAME_FILTER.setName(this.classifier
							.getClass().getSimpleName());
					int classification = this.metric.considerDiscussions(
							new Discussion[] { d }).intValue();

					// metric result starts at -1 (unknown)
					confusionMatrix[ordering[classification + 1]][ordering[reference + 1]]++;

					System.out.println(d.getID() + "\t"
							+ this.metric.decode(reference) + "\t"
							+ this.metric.decode(classification));
				}
			}
		}
		return confusionMatrix;
	}

	private boolean considerDiscussion(Discussion d) {
		for (DiscussionEvent de : d.getDiscussionEvents())
			if (de.isClassified())
				return true;
		return false;
	}

	private int reverse(int index, int[] ordering) {
		for (int i = 0; i < ordering.length; i++) {
			if (ordering[i] == index)
				return i;
		}
		return -1;
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

	private interface IConfusionMatrixLayouter {
		String layoutConfusionMatrix(int[][] matrix, int[] ordering,
				PatternMetric metric);
	}

	private class TabLayouter implements IConfusionMatrixLayouter {

		@Override
		public String layoutConfusionMatrix(int[][] matrix, int[] ordering,
				PatternMetric metric) {
			// Print the heading of the confusion matrix
			String line = classifier.getClass().getSimpleName();
			line += "/" + REFERENCE_CLASSIFIER;
			for (int i = 0; i < 7; i++) {
				line += "\t" + metric.decode(reverse(i, ordering) - 1);
			}
			line += "\t true positive \t false positive \t false negative \t true negative \t recall \t precision \t f-measure \t specificity";

			int sum = 0;
			for (int i = 0; i < 7; i++) {
				for (int j = 0; j < 7; j++) {
					sum += matrix[i][j];
				}
			}

			for (int i = 0; i < 7; i++) {
				line += "\n" + metric.decode(reverse(i, ordering) - 1) + "\t";
				int tp = 0, fp = 0, fn = 0, tn = 0;
				for (int j = 0; j < 7; j++) {
					line += matrix[i][j] + "\t";
					if (i == j) {
						tp = matrix[i][j];
						for (int n = 0; n < 7; n++) {
							if (n != j)
								fn += matrix[n][j];
						}
					} else {
						fp += matrix[i][j];
					}
				}
				tn = sum - (tp + fp + fn);
				line += tp + "\t" + fp + "\t" + fn + "\t" + tn;
				// recall
				double recall = ((double) tp) / (((double) tp) + ((double) fn));
				line += "\t" + recall;
				// precision
				double precision = ((double) tp)
						/ (((double) tp) + ((double) fp));
				line += "\t" + precision;
				// f-measure
				line += "\t" + (2 * recall * precision) / (recall + precision);
				// specificity
				line += "\t" + ((double) tn) / (((double) tn) + ((double) fp));
			}
			return line;
		}

	}

	private class ConfigurableLayouter implements IConfusionMatrixLayouter {

		private String rowSep;
		private String colSep;

		public ConfigurableLayouter(String colSep, String rowSep) {
			this.colSep = colSep;
			this.rowSep = rowSep;
		}

		@Override
		public String layoutConfusionMatrix(int[][] matrix, int[] ordering,
				PatternMetric metric) {
			// Print the heading of the confusion matrix
			StringBuffer line = new StringBuffer();
			line.append(classifier.getClass().getSimpleName());
			line.append("/");
			line.append(REFERENCE_CLASSIFIER);
			for (int i = 0; i < 7; i++) {
				line.append(colSep);
				line.append(metric.decode(reverse(i, ordering) - 1));
			}
			line.append(colSep);
			line.append("true positive");
			line.append(colSep);
			line.append("false positive");
			line.append(colSep);
			line.append("false negative");
			line.append(colSep);
			line.append("true negative");
			line.append(colSep);
			line.append("recall");
			line.append(colSep);
			line.append("precision");
			line.append(colSep);
			line.append("f-measure");
			line.append(colSep);
			line.append("specificity");

			int sum = 0;
			for (int i = 0; i < 7; i++) {
				for (int j = 0; j < 7; j++) {
					sum += matrix[i][j];
				}
			}

			for (int i = 0; i < 7; i++) {
				line.append(rowSep);
				line.append(metric.decode(reverse(i, ordering) - 1));
				line.append(colSep);
				int tp = 0, fp = 0, fn = 0, tn = 0;
				for (int j = 0; j < 7; j++) {
					line.append(matrix[i][j]);
					line.append(colSep);
					if (i == j) {
						tp = matrix[i][j];
						for (int n = 0; n < 7; n++) {
							if (n != j)
								fn += matrix[n][j];
						}
					} else {
						fp += matrix[i][j];
					}
				}
				tn = sum - (tp + fp + fn);
				line.append(tp);
				line.append(colSep);
				line.append(fp);
				line.append(colSep);
				line.append(fn);
				line.append(colSep);
				line.append(tn);
				// recall
				double recall = ((double) tp) / (((double) tp) + ((double) fn));
				line.append(colSep);
				line.append(recall);
				// precision
				double precision = ((double) tp)
						/ (((double) tp) + ((double) fp));
				line.append(colSep);
				line.append(precision);
				// f-measure
				line.append(colSep);
				line.append((2 * recall * precision) / (recall + precision));
				// specificity
				line.append(((double) tn) / (((double) tn) + ((double) fp)));
			}
			return line.toString();
		}

	}

}
