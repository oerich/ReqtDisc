package org.computer.knauss.reqtDiscussion.model.machineLearning;

import java.io.File;

import javax.swing.table.TableModel;

import oerich.nlputils.classifier.machinelearning.ILearningClassifier;

public class HybridBayesianClassifier implements ILearningClassifier {

	private ILearningClassifier inClassDelegate;
	private ILearningClassifier notinClassDelegate;

	@Override
	public double getMinimalValue() {
		return this.inClassDelegate.getMinimalValue();
	}

	@Override
	public double getMaximumValue() {
		return this.inClassDelegate.getMinimalValue();
	}

	@Override
	public void init(File initData) throws Exception {
		this.inClassDelegate.init(initData);
		File nicFile = new File(initData.getParentFile().getPath() + "nic-"
				+ initData.getName());
		this.notinClassDelegate.init(nicFile);
	}

	@Override
	public double classify(String text) throws IllegalArgumentException {
		double pInClass = this.inClassDelegate.classify(text);
		double pNinClass = this.notinClassDelegate.classify(text);

		if (pNinClass <= this.notinClassDelegate.getMatchValue()
				&& pInClass > this.inClassDelegate.getMatchValue()) {
			// everything is fine: We have a hit.
			return pInClass;
		}
		if (pNinClass <= this.notinClassDelegate.getMatchValue()
				&& pInClass <= this.inClassDelegate.getMatchValue()) {
			// funny: It is neither in nor out.
			return 0.4;
		}
		if (pNinClass > this.notinClassDelegate.getMatchValue()
				&& pInClass <= this.inClassDelegate.getMatchValue()) {
			// everything is fine: This thing is most likely not in class.
			return pInClass;
		}
		if (pNinClass > this.notinClassDelegate.getMatchValue()
				&& pInClass > this.inClassDelegate.getMatchValue()) {
			// funny: it is both in and out.
			return 0.6;
		}
		// this cannot happen, I suppose. But don't want to destroy the
		// structure above at this stage.
		return -1;
	}

	@Override
	public boolean isMatch(String text) throws IllegalArgumentException {
		return this.inClassDelegate.isMatch(text)
				&& !this.notinClassDelegate.isMatch(text);
	}

	@Override
	public void setMatchValue(double value) {
		this.inClassDelegate.setMatchValue(value);
		this.notinClassDelegate.setMatchValue(value);
	}

	@Override
	public double getMatchValue() {
		return this.inClassDelegate.getMatchValue();
	}

	@Override
	public TableModel explainClassification(String text) {
		return this.inClassDelegate.explainClassification(text);
	}

	@Override
	public void learnInClass(String text) {
		this.inClassDelegate.learnInClass(text);
		this.notinClassDelegate.learnNotInClass(text);
	}

	@Override
	public void learnNotInClass(String text) {
		this.inClassDelegate.learnNotInClass(text);
		this.notinClassDelegate.learnInClass(text);
	}

	@Override
	public int thingsInClass() {
		return this.inClassDelegate.thingsInClass();
	}

	@Override
	public int thingsNotInClass() {
		return this.inClassDelegate.thingsNotInClass();
	}

	@Override
	public void clear() throws Exception {
		this.inClassDelegate.clear();
		this.notinClassDelegate.clear();
	}

}
