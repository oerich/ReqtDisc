package org.computer.knauss.reqtDiscussion.model.machineLearning;

import java.io.File;
import java.io.IOException;

import javax.swing.table.TableModel;

import oerich.nlputils.classifier.machinelearning.ILearningClassifier;
import oerich.nlputils.classifier.machinelearning.NewBayesianClassifier;

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
				this.inClassDelegate = new NewBayesianClassifier();
				((NewBayesianClassifier) this.inClassDelegate)
						.setAutosave(false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		;
		return this.inClassDelegate;
	}

	private ILearningClassifier getNotInClassDelegate() {
		if (this.notinClassDelegate == null)
			try {
				this.notinClassDelegate = new NewBayesianClassifier();

				((NewBayesianClassifier) this.notinClassDelegate)
						.setAutosave(false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		;
		return this.notinClassDelegate;
	}

	@Override
	public void init(File initData) throws Exception {
		getInClassDelegate().init(initData);
		File nicFile = new File("nic-" + initData.getName());
		getNotInClassDelegate().init(nicFile);
	}

	@Override
	public double classify(String text) throws IllegalArgumentException {
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
	public boolean isMatch(String text) throws IllegalArgumentException {
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

}
