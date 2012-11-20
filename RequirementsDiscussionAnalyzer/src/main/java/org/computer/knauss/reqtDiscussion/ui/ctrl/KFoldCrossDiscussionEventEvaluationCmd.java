package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.awt.event.ActionEvent;

import org.computer.knauss.reqtDiscussion.model.machineLearning.ClassifierManager;
import org.computer.knauss.reqtDiscussion.model.machineLearning.eval.HybridKFoldCrossDiscussionEventEvaluation;
import org.computer.knauss.reqtDiscussion.model.machineLearning.eval.KFoldCrossDiscussionEventEvaluation;

public class KFoldCrossDiscussionEventEvaluationCmd extends AbstractCommand {

	public static final int LATEX_STYLE = 0;
	public static final int TAB_SEPARATED_STYLE = 1;
	private static final long serialVersionUID = 1L;

	public KFoldCrossDiscussionEventEvaluationCmd() {
		super("K-fold cross evaluation on discussion events");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// HybridKFoldCrossDiscussionEvaluation eval = new
		// HybridKFoldCrossDiscussionEvaluation();

		// Start with the single classifier:
		KFoldCrossDiscussionEventEvaluation eval = new KFoldCrossDiscussionEventEvaluation();

		eval.setClassifier(ClassifierManager.getInstance().getClassifier());

		// run it and print it to the cmd line
		eval.printConfusionMatrix(eval.evaluate(10, getDiscussionTableModel()
				.getDiscussions()));

		// Now use the hybrid version:
		HybridKFoldCrossDiscussionEventEvaluation heval = new HybridKFoldCrossDiscussionEventEvaluation();
		heval.printConfusionMatrix(heval.evaluate(10, getDiscussionTableModel()
				.getDiscussions()));
	}

}
