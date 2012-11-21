package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.awt.event.ActionEvent;

public class LoadTrainingDataCmd extends AbstractCommand {

	private static final long serialVersionUID = 1L;

	public LoadTrainingDataCmd() {
		super("Load classifier");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO open a file dialogue, load trainings data and use it on
		// classifier
	}

}
