package org.computer.knauss.reqtDiscussion.model.machineLearning;

import javax.swing.table.TableModel;

import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;

public interface IDiscussionEventClassifier {

	void trainDiscussionEvent(DiscussionEvent de);

	boolean inClass(DiscussionEvent de) throws Exception;

	double classify(DiscussionEvent de) throws Exception;

	void setTrainingStrategy(ITrainingStrategy strat);

	ITrainingStrategy getTrainingStrategy();

	void setName(String name);

	String getName();

	double getMatchValue();

	void clear();

	void trainDiscussionEvent(DiscussionEvent de, String referenceRaterName);

	void storeToFile();

	TableModel explainClassification(DiscussionEvent de);

}
