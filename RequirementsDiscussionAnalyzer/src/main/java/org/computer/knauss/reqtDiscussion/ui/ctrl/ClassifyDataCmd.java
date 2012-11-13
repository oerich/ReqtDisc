package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.awt.event.ActionEvent;


public class ClassifyDataCmd extends AbstractCommand {

	private static final long serialVersionUID = 1L;

	public ClassifyDataCmd() {
		super("classify data");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO apply classifier with current training on all comments

	}

}
