package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.computer.knauss.reqtDiscussion.io.DAOException;
import org.computer.knauss.reqtDiscussion.io.IDAOProgressMonitor;
import org.computer.knauss.reqtDiscussion.model.IClassificationFilter;
import org.computer.knauss.reqtDiscussion.model.machineLearning.ClassifierManager;
import org.computer.knauss.reqtDiscussion.model.machineLearning.IDiscussionEventClassifier;
import org.computer.knauss.reqtDiscussion.model.machineLearning.eval.AbstractBucketBalancingStrategy;
import org.computer.knauss.reqtDiscussion.model.machineLearning.eval.AbstractKFoldCrossEvaluation;
import org.computer.knauss.reqtDiscussion.model.machineLearning.eval.ConfusionMatrix;
import org.computer.knauss.reqtDiscussion.model.machineLearning.eval.EventBasedRandomAllocationStrategy;
import org.computer.knauss.reqtDiscussion.model.machineLearning.eval.GreedyDiscussionEventAllocationStrategy;
import org.computer.knauss.reqtDiscussion.model.machineLearning.eval.LeaveOneOutAllocationStrategy;
import org.computer.knauss.reqtDiscussion.model.machineLearning.eval.RandomBucketAllocationStrategy;

public class KFoldCrossDiscussionEvaluationCmd extends AbstractCommand {

	public static final int LATEX_STYLE = 0;
	public static final int TAB_SEPARATED_STYLE = 1;
	private static final long serialVersionUID = 1L;

	public KFoldCrossDiscussionEvaluationCmd() {
		super("K-fold cross evaluation");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// Discussion or DiscussionEvent level?
		Object[] options = { "DiscussionEvent", "Discussion", "Cancel" };
		int n = JOptionPane.showOptionDialog(null,
				"Please select the level of evaluation.", "Evaluation Level",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, options, options[1]);
		if (n == 2)
			return;

		final AbstractKFoldCrossEvaluation eval;
		if (n == 0)
			eval = AbstractKFoldCrossEvaluation.DISCUSSION_EVENT_LEVEL;
		else
			eval = AbstractKFoldCrossEvaluation.DISCUSSION_LEVEL;

		// Which classifier to use?
		options = ClassifierManager.getInstance().getAvailableClassifiers();
		IDiscussionEventClassifier classifier = (IDiscussionEventClassifier) JOptionPane
				.showInputDialog(null,
						"Please select the classifier for evaluation.",
						"Classifier", JOptionPane.QUESTION_MESSAGE, null,
						options, options[0]);
		if (classifier == null)
			return;
		eval.setClassifier(classifier);

		// reference rater is currently fixed
		eval.setReferenceRaterName(IClassificationFilter.NAME_FILTER.getName());

		// Aggregate discussions?
		options = new Object[] { "Aggregate Discussions",
				"Separate Discussions", "Cancel" };
		n = JOptionPane.showOptionDialog(null,
				"Do you want to aggregate discussions?",
				"Aggregation of complex discussions",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, options, options[0]);
		if (n == 2)
			return;
		eval.setAggregateDiscussions(n == 0);

		// How to distribute discussions over k buckets?
		options = new AbstractBucketBalancingStrategy[] {
				new GreedyDiscussionEventAllocationStrategy(),
				new RandomBucketAllocationStrategy(),
				new EventBasedRandomAllocationStrategy(),
				new LeaveOneOutAllocationStrategy() };
		AbstractBucketBalancingStrategy alloc = (AbstractBucketBalancingStrategy) JOptionPane
				.showInputDialog(null,
						"Please select the classifier for evaluation.",
						"Classifier", JOptionPane.QUESTION_MESSAGE, null,
						options, options[0]);
		if (alloc == null)
			return;
		eval.setBucketAllocationStrategy(alloc);

		// If relevant: How many buckets do we need anyway?
		final int k = 10;

		// Print it to the cmd line
		options = new Object[] { "LaTeX", "Tabulator", "Both", "Cancel" };
		final int outputStyle = JOptionPane.showOptionDialog(null,
				"Please choose the output format.", "Evaluation Level",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, options, options[1]);
		if (outputStyle == 3)
			return;

		// Run it
		BackGroundDAOTask task = new BackGroundDAOTask(new Subtask(
				getDiscussionTableModel(), null) {

			@Override
			public void perform(IDAOProgressMonitor progressMonitor)
					throws DAOException {
				ConfusionMatrix cm = eval.evaluate(k, getDiscussionTableModel()
						.getDiscussions(), progressMonitor);
				printConfusionMatrix(outputStyle, cm);
				ConfusionMatrix cm2 = cm.collapseCategories("suspicious",
						new String[] { "back-to-draft", "happy-ending",
								"discordant" });
				cm2 = cm2.collapseCategories("unsuspicious", new String[] {
						"textbook-example", "indifferent" });
				cm2 = cm2.collapseCategories("unknown", new String[] {
						"unknown", "procrastination" });
				cm2 = cm2.collapseCategories("other", new String[] { "coord",
						"other", "no cl", "autog", "Solut" });
				printConfusionMatrix(outputStyle, cm2);
			}

			private void printConfusionMatrix(final int outputStyle,
					ConfusionMatrix cm) {
				if (outputStyle == LATEX_STYLE) {
					System.out.println(cm.layoutConfusionMatrix(" & ",
							" \\tabularnewline \n "));
				} else if (outputStyle == TAB_SEPARATED_STYLE) {
					System.out.println(cm.layoutConfusionMatrix(" \t ", "\n"));
				} else {
					System.out.println(cm.layoutConfusionMatrix(" & ",
							" \\tabularnewline \n "));
					System.out.println(cm.layoutConfusionMatrix(" \t ", "\n"));
				}
			}

			@Override
			public String getName() {
				return "K Fold Cross Evaluation";
			}
		});

		task.execute();
	}
}
