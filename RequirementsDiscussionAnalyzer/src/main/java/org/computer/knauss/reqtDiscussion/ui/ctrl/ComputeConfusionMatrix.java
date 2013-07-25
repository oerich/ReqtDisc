package org.computer.knauss.reqtDiscussion.ui.ctrl;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.IClassificationFilter;
import org.computer.knauss.reqtDiscussion.model.machineLearning.eval.ConfusionMatrix;

public class ComputeConfusionMatrix extends AbstractDiscussionIterationCommand {

	private static final long serialVersionUID = 1L;
	private String baselineRater;
	private String predictRater;
	private ConfusionMatrix confusionMatrix;

	public ComputeConfusionMatrix() {
		super("Compare raters (confusion matrix)");
	}

	@Override
	protected void preProcessingHook() {
		JTextField firstName = new JTextField();
		JTextField secondName = new JTextField();
		final JComponent[] inputs = new JComponent[] {
				new JLabel("Rater (baseline)"), firstName,
				new JLabel("Rater (prediction)"), secondName };
		JOptionPane.showMessageDialog(null, inputs,
				"Please provide the names of the raters to compare.",
				JOptionPane.PLAIN_MESSAGE);
		this.baselineRater = firstName.getText();
		this.predictRater = secondName.getText();

		this.confusionMatrix = new ConfusionMatrix();
		this.confusionMatrix.init(new String[] { "clari", "coord", "other",
				"autog", "no cl", "solut", "unkno" });
	}

	@Override
	protected void postProcessingHook() {
		ConfusionMatrix cm = this.confusionMatrix.collapseCategories("other",
				new String[] { "coord", "other", "autog", "no cl", "Solut",
						"unkno" });
		System.out.println(this.confusionMatrix.layoutConfusionMatrix("\t",
				"\n"));
		System.out.println(cm.layoutConfusionMatrix("\t", "\n"));
	}

	@Override
	protected void processDiscussionHook(Discussion[] d) {
		for (DiscussionEvent de : getDiscussionEvents(d)) {
			IClassificationFilter.NAME_FILTER.setName(this.baselineRater);
			if (de.isClassified()) {
				String baseline = de.getReferenceClassification();
				IClassificationFilter.NAME_FILTER.setName(this.predictRater);
				if (de.isClassified())
					this.confusionMatrix.report(clean(baseline),
							clean(de.getReferenceClassification()));
			}
		}
	}

	private String clean(String str) {
		return str.substring(0, 5).toLowerCase();
	}

}
