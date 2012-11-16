package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.awt.event.ActionEvent;

import org.computer.knauss.reqtDiscussion.model.machineLearning.ClassifierManager;
import org.computer.knauss.reqtDiscussion.model.metric.eval.AbstractBucketBalancingStrategy;
import org.computer.knauss.reqtDiscussion.model.metric.eval.KFoldCrossDiscussionEvaluation;

public class KFoldCrossDiscussionEvaluationCmd extends AbstractCommand {

	private static final long serialVersionUID = 1L;

	public KFoldCrossDiscussionEvaluationCmd() {
		super("K-fold cross evaluation on discussions");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		KFoldCrossDiscussionEvaluation eval = new KFoldCrossDiscussionEvaluation();

		// Configure it
		eval.setClassifier(ClassifierManager.getInstance().getClassifier());
		eval.setBucketAllocationStrategy(AbstractBucketBalancingStrategy.GREEDY_DISCUSSION_EVENT);
		// eval.setBucketAllocationStrategy(AbstractBucketBalancingStrategy.RANDOM_BUCKET);

		// run it and print it to the cmd line
		eval.printConfusionMatrix(eval.evaluate(10, getDiscussionTableModel()
				.getDiscussions(), true));
	}

}
