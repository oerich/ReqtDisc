package org.computer.knauss.reqtDiscussion.model.machineLearning;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import oerich.nlputils.classifier.machinelearning.ILearningClassifier;
import oerich.nlputils.classifier.machinelearning.NewBayesianClassifier;

public class ClassifierManager {

	private static ClassifierManager instance;
	private List<ILearningClassifier> classifiers;
	private ILearningClassifier classifier;

	private ClassifierManager() {

	}

	public synchronized static ClassifierManager getInstance() {
		if (instance == null) {
			instance = new ClassifierManager();
			try {
				NewBayesianClassifier classifier = new NewBayesianClassifier();
				classifier.init(new File("classifier.txt"));
				classifier.setAutosave(false);
				instance.registerClassifier(classifier);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return instance;
	}

	public void registerClassifier(ILearningClassifier classifier) {
		if (this.classifiers == null) {
			this.classifiers = new Vector<ILearningClassifier>();
			this.classifier = classifier;
		}
		this.classifiers.add(classifier);
	}

	public ILearningClassifier[] getAvailableClassifiers() {
		ILearningClassifier[] ret = new ILearningClassifier[0];
		if (this.classifiers == null)
			return ret;
		return this.classifiers.toArray(ret);
	}

	public ILearningClassifier getClassifier() {
		return this.classifier;
	}

	public void setClassifier(ILearningClassifier classifier) {
		if (this.classifiers == null || !this.classifiers.contains(classifier))
			registerClassifier(classifier);
		this.classifier = classifier;
	}
}
