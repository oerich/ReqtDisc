package org.computer.knauss.reqtDiscussion.model.machineLearning;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import oerich.nlputils.classifier.machinelearning.NewBayesianClassifier;

public class ClassifierManager {

	private static ClassifierManager instance;
	private List<IDiscussionEventClassifier> classifiers;
	private IDiscussionEventClassifier classifier;

	private ClassifierManager() {

	}

	public synchronized static ClassifierManager getInstance() {
		if (instance == null) {
			instance = new ClassifierManager();
			try {
				NewBayesianClassifier classifier = new NewBayesianClassifier();
				classifier.init(new File("classifier.txt"));
				classifier.setAutosave(false);

				LearningClassifierWrapper wrapper = new LearningClassifierWrapper();
				wrapper.setLearningClassifier(classifier);
				wrapper.setTrainingStrategy(ITrainingStrategy.DEFAULT_STRAT);

				instance.registerClassifier(wrapper);

				HybridBayesianClassifier hclass = new HybridBayesianClassifier();
				hclass.init(new File("hybrid-classifier.txt"));
				LearningClassifierWrapper hwrapper = new LearningClassifierWrapper();
				hwrapper.setLearningClassifier(hclass);
				hwrapper.setTrainingStrategy(ITrainingStrategy.DEFAULT_STRAT);

				instance.registerClassifier(hwrapper);
				
				MultiClassDiscussionEventClassifier mclass = new MultiClassDiscussionEventClassifier();
				mclass.setTrainingStrategy(ITrainingStrategy.DEFAULT_STRAT);
				instance.registerClassifier(mclass);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return instance;
	}

	public void registerClassifier(IDiscussionEventClassifier classifier) {
		if (this.classifiers == null) {
			this.classifiers = new Vector<IDiscussionEventClassifier>();
			this.classifier = classifier;
		}
		this.classifiers.add(classifier);
	}

	public IDiscussionEventClassifier[] getAvailableClassifiers() {
		IDiscussionEventClassifier[] ret = new IDiscussionEventClassifier[0];
		if (this.classifiers == null)
			return ret;
		return this.classifiers.toArray(ret);
	}

	public IDiscussionEventClassifier getClassifier() {
		return this.classifier;
	}

	public void setClassifier(IDiscussionEventClassifier classifier) {
		if (this.classifiers == null || !this.classifiers.contains(classifier))
			registerClassifier(classifier);
		this.classifier = classifier;
	}
}
