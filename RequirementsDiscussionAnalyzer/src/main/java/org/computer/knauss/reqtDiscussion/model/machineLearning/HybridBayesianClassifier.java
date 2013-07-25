package org.computer.knauss.reqtDiscussion.model.machineLearning;

import java.io.File;
import java.io.IOException;

import javax.swing.table.TableModel;

import oerich.nlputils.NLPInitializationException;
import oerich.nlputils.classifier.machinelearning.ILearningClassifier;
import oerich.nlputils.classifier.machinelearning.NewBayesianClassifier;
import oerich.nlputils.text.DuplicateFilter;

public class HybridBayesianClassifier implements ILearningClassifier {

	private ILearningClassifier inClassDelegate;
	private ILearningClassifier notinClassDelegate;

	@Override
	public double getMinimalValue() {
		return getInClassDelegate().getMinimalValue();
	}

	@Override
	public double getMaximumValue() {
		return getInClassDelegate().getMinimalValue();
	}

	private ILearningClassifier getInClassDelegate() {
		if (this.inClassDelegate == null)
			try {
				this.inClassDelegate = constructClassifier();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		return this.inClassDelegate;
	}

	private ILearningClassifier getNotInClassDelegate() {
		if (this.notinClassDelegate == null)
			try {
				NewBayesianClassifier classifier = constructClassifier();
				classifier.setProClassBias(2);
				// classifier.setUnknownWordValue(0.6);
				this.notinClassDelegate = classifier;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		return this.notinClassDelegate;
	}

	private NewBayesianClassifier constructClassifier() throws IOException {
		NewBayesianClassifier classifier = new NewBayesianClassifier();
		classifier.setAutosave(false);
		// set default values
		classifier.setProClassBias(1);
		classifier.setUnknownWordValue(0.5);
		// Count each word only once per discussion event
		classifier.setStopWordFilter(new DuplicateFilter());
		return classifier;
	}

	@Override
	public void init(File initData) throws Exception {
		getInClassDelegate().init(initData);
		File nicFile = new File("nic-" + initData.getName());
		getNotInClassDelegate().init(nicFile);
	}

	@Override
	public double classify(String text) throws NLPInitializationException {
		double pInClass = getInClassDelegate().classify(text);
		double pNinClass = getNotInClassDelegate().classify(text);

		if (pNinClass <= getNotInClassDelegate().getMatchValue()
				&& pInClass > getInClassDelegate().getMatchValue()) {
			// everything is fine: We have a hit.
			return pInClass;
		}
		if (pNinClass <= getNotInClassDelegate().getMatchValue()
				&& pInClass <= getInClassDelegate().getMatchValue()) {
			// funny: It is neither in nor out.
			return 0.4;
		}
		if (pNinClass > getNotInClassDelegate().getMatchValue()
				&& pInClass <= getInClassDelegate().getMatchValue()) {
			// everything is fine: This thing is most likely not in class.
			return pInClass;
		}
		if (pNinClass > getNotInClassDelegate().getMatchValue()
				&& pInClass > getInClassDelegate().getMatchValue()) {
			// funny: it is both in and out.
			return 0.6;
		}
		// this cannot happen, I suppose. But don't want to destroy the
		// structure above at this stage.
		return -1;
	}

	@Override
	public boolean isMatch(String text) throws NLPInitializationException {
		return getInClassDelegate().isMatch(text)
				&& !getNotInClassDelegate().isMatch(text);
	}

	@Override
	public void setMatchValue(double value) {
		getInClassDelegate().setMatchValue(value);
		getNotInClassDelegate().setMatchValue(value);
	}

	@Override
	public double getMatchValue() {
		return getInClassDelegate().getMatchValue();
	}

	@Override
	public TableModel explainClassification(String text) {
		return getInClassDelegate().explainClassification(text);
	}

	@Override
	public void learnInClass(String text) {
		getInClassDelegate().learnInClass(text);
		getNotInClassDelegate().learnNotInClass(text);
	}

	@Override
	public void learnNotInClass(String text) {
		getInClassDelegate().learnNotInClass(text);
		getNotInClassDelegate().learnInClass(text);
	}

	@Override
	public int thingsInClass() {
		return getInClassDelegate().thingsInClass();
	}

	@Override
	public int thingsNotInClass() {
		return getInClassDelegate().thingsNotInClass();
	}

	@Override
	public void clear() throws Exception {
		getInClassDelegate().clear();
		getNotInClassDelegate().clear();
	}

	public void storeToFile() {
		ILearningClassifier[] classifiers = new ILearningClassifier[] {
				inClassDelegate, notinClassDelegate };
		for (ILearningClassifier classifier : classifiers)
			if (classifier instanceof NewBayesianClassifier)
				try {
					NewBayesianClassifier nbc = (NewBayesianClassifier) classifier;
					nbc.storeToFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
	}

}
