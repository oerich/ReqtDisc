package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.computer.knauss.reqtDiscussion.Util;

public class SetRandomSeed extends AbstractCommand {

	public SetRandomSeed() {
		super("Set random seed");
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent event) {
		String answer = JOptionPane
				.showInputDialog("Please provide a random seed (long).");
		if (answer != null) {
			Util.useSeed(Long.parseLong(answer));
		}
	}

}
