package org.computer.knauss.reqtDiscussion.ui.ctrl;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.DiscussionEventClassification;
import org.computer.knauss.reqtDiscussion.model.IClassificationFilter;
import org.computer.knauss.reqtDiscussion.model.machineLearning.ClassifierManager;
import org.computer.knauss.reqtDiscussion.model.machineLearning.IDiscussionEventClassifier;

public class ClassifyDataCmd extends AbstractDiscussionIterationCommand {

	private static final long serialVersionUID = 1L;
	private IDiscussionEventClassifier classifier;

	// private InsertOrUpdateDiscussionEventClassification
	// insertOrUpdateCommand;

	public ClassifyDataCmd() {
		super("Classify discussions");
	}

	@Override
	protected void preProcessingHook() {
		classifier = ClassifierManager.getInstance().getClassifier();
		IClassificationFilter.NAME_FILTER.setName(classifier.getClass()
				.getSimpleName());
	}

	@Override
	protected void processDiscussionHook(Discussion d) {
		try {
			for (DiscussionEvent de : d.getDiscussionEvents()) {
				DiscussionEventClassification dec = new DiscussionEventClassification();

				double confidence = classifier.classify(de);
				dec.setClassifiedby(classifier.getClass().getSimpleName());
				dec.setConfidence(confidence);
				dec.setWorkitemcommentid(de.getID());

				if (classifier.getMatchValue() < confidence) {
					dec.setClassification("clarif");
					System.out.print('!');
				} else {
					dec.setClassification("other");
					System.out.print('.');
				}
				de.insertOrUpdateClassification(dec);

				// Currently no need to store classifications in database.
				// insertOrUpdateCommand.setWorkitemCommentClassification(dec);
				// insertOrUpdateCommand.actionPerformed(null);
			}
			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setInsertOrUpdateCommand(AbstractCommand insertOrUpdateCommand) {
		// this.insertOrUpdateCommand =
		// (InsertOrUpdateDiscussionEventClassification) insertOrUpdateCommand;
	}
}
