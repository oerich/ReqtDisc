package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.awt.event.ActionEvent;

public class StoreTrainingDataCmd extends AbstractCommand {

	private static final long serialVersionUID = 1L;

	public StoreTrainingDataCmd() {
		super("Store classifier");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO open a file dialogue, store the training data.

	}

}
