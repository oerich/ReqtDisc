package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.awt.event.ActionEvent;

public class EvaluateClassifierCmd extends AbstractCommand {

	public EvaluateClassifierCmd() {
		super("Evaluate classify");
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO ask to store classifier first

		// add discussions to 10 buckets so that all buckets contain roughly the
		// same number of comments
		// do ten fold cross validation on buckets
		// print results for each bucket on command line

	}

}
