package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.awt.event.ActionEvent;

public class TrainClassifierCmd extends AbstractCommand {
	private static final long serialVersionUID = 1L;

	public TrainClassifierCmd() {
		super("Train classifier");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Ask if current classifier should be stored
		// use currently loaded discussions for training.

	}

}
