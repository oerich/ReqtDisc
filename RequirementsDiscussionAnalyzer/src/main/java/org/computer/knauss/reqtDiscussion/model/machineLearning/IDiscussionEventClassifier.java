package org.computer.knauss.reqtDiscussion.model.machineLearning;

import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;

public interface IDiscussionEventClassifier {

	void trainDiscussionEvent(DiscussionEvent de);

	boolean inClass(DiscussionEvent de);

	double classify(DiscussionEvent de);

	void setTrainingStrategy(ITrainingStrategy strat);

	ITrainingStrategy getTrainingStrategy();

	void setName(String name);

	String getName();

	double getMatchValue();

	void clear();

	void trainDiscussionEvent(DiscussionEvent de, String referenceRaterName);

}
