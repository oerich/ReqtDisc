package org.computer.knauss.reqtDiscussion.ui.ctrl;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.metric.TrajectoryFeatures;

public class PrintTrajectoryFeatures extends AbstractDiscussionIterationCommand {

	private static final long serialVersionUID = 1L;
	private TrajectoryFeatures tf;

	public PrintTrajectoryFeatures() {
		super("Print trajectory features to sysout");
		this.tf = new TrajectoryFeatures();
	}

	@Override
	protected void processDiscussionHook(Discussion[] d) {
		tf.considerDiscussions(d);
	}

}
