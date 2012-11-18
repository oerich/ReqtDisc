package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.awt.event.ActionEvent;

import org.computer.knauss.reqtDiscussion.model.machineLearning.ClassifierManager;
import org.computer.knauss.reqtDiscussion.model.metric.eval.AbstractBucketBalancingStrategy;
import org.computer.knauss.reqtDiscussion.model.metric.eval.ConfigurableLayouter;
import org.computer.knauss.reqtDiscussion.model.metric.eval.HybridKFoldCrossDiscussionEvaluation;

public class KFoldCrossDiscussionEvaluationCmd extends AbstractCommand {

	public static final int LATEX_STYLE = 0;
	public static final int TAB_SEPARATED_STYLE = 1;
	private static final long serialVersionUID = 1L;
	private AbstractBucketBalancingStrategy alloc;
	private int style;
	private boolean aggregate;

	public KFoldCrossDiscussionEvaluationCmd(
			AbstractBucketBalancingStrategy alloc, int style, boolean aggregate) {
		super("K-fold cross evaluation on discussions ("
				+ alloc.getClass().getSimpleName() + ", LaTeX:" + (style == 0)
				+ ", aggregate:" + aggregate);
		this.alloc = alloc;
		this.style = style;
		this.aggregate = aggregate;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		HybridKFoldCrossDiscussionEvaluation eval = new HybridKFoldCrossDiscussionEvaluation();
		// KFoldCrossDiscussionEvaluation eval = new
		// KFoldCrossDiscussionEvaluation();

		// Configure it
		// eval.setClassifier(ClassifierManager.getInstance().getClassifier());
		eval.setBucketAllocationStrategy(alloc);

		if (style == LATEX_STYLE) {
			((ConfigurableLayouter) eval.getConfusionMatrixLayout())
					.setColSep(" & ");
			((ConfigurableLayouter) eval.getConfusionMatrixLayout())
					.setRowSep(" \\tabularnewline \n ");
		} else {
			((ConfigurableLayouter) eval.getConfusionMatrixLayout())
					.setColSep(" \t ");
			((ConfigurableLayouter) eval.getConfusionMatrixLayout())
					.setRowSep(" \n ");
		}

		// run it and print it to the cmd line
		eval.printConfusionMatrix(eval.evaluate(10, getDiscussionTableModel()
				.getDiscussions(), this.aggregate));
	}

}
