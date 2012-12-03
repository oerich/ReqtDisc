package org.computer.knauss.reqtDiscussion.model.machineLearning;

import java.io.IOException;

import javax.swing.table.TableModel;

import oerich.nlputils.classifier.machinelearning.ILearningClassifier;
import oerich.nlputils.classifier.machinelearning.NewBayesianClassifier;

import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;

public class LearningClassifierWrapper implements IDiscussionEventClassifier {

	private ILearningClassifier delegate;
	private ITrainingStrategy trainingStrategy;
	private String name;

	public void setLearningClassifier(ILearningClassifier classifier) {
		this.delegate = classifier;
	}

	@Override
	public void trainDiscussionEvent(DiscussionEvent de) {
		trainDiscussionEvent(de, "gpoo,eric1");
	}

	@Override
	public boolean inClass(DiscussionEvent de) {
		return this.delegate.isMatch(this.trainingStrategy
				.getStringForClassification(de));
	}

	@Override
	public double classify(DiscussionEvent de) {
		return this.delegate.classify(this.trainingStrategy
				.getStringForClassification(de));
	}

	@Override
	public void setTrainingStrategy(ITrainingStrategy strat) {
		this.trainingStrategy = strat;
	}

	@Override
	public ITrainingStrategy getTrainingStrategy() {
		return this.trainingStrategy;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		if (this.getName() == null)
			return this.delegate.getClass().getSimpleName();
		return this.getName();
	}

	@Override
	public double getMatchValue() {
		return this.delegate.getMatchValue();
	}

	@Override
	public void clear() {
		try {
			this.delegate.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void trainDiscussionEvent(DiscussionEvent de,
			String referenceRaterName) {
		this.trainingStrategy.trainClassifier(this.delegate, de,
				referenceRaterName);
	}

	@Override
	public void storeToFile() {
		if (this.delegate instanceof NewBayesianClassifier)
			try {
				((NewBayesianClassifier) this.delegate).storeToFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	@Override
	public TableModel explainClassification(DiscussionEvent de) {
		return this.delegate.explainClassification(this.trainingStrategy
				.getStringForClassification(de));
	}
}
