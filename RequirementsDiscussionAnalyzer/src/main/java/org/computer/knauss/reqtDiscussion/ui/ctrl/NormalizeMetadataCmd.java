package org.computer.knauss.reqtDiscussion.ui.ctrl;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.machineLearning.ITrainingStrategy;

public class NormalizeMetadataCmd extends AbstractDiscussionIterationCommand {

	public NormalizeMetadataCmd() {
		super("Normalize metadata");
	}

	private static final long serialVersionUID = 1L;

	@Override
	protected void processDiscussionHook(Discussion[] discussions) {
		for (Discussion d : discussions)
			ITrainingStrategy.META_DATA_STRATEGY.preprocess(d);
	}

}
