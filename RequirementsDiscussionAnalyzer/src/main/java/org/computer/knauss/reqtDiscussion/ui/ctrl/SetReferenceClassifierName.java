package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.computer.knauss.reqtDiscussion.model.IClassificationFilter;

public class SetReferenceClassifierName extends AbstractCommand {

	private static final long serialVersionUID = 1L;

	public SetReferenceClassifierName() {
		super("Set rater name for reference");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		IClassificationFilter.NAME_FILTER.setName(JOptionPane.showInputDialog(
				null, "Please enter the name of the rater that should be used as reference.",
				IClassificationFilter.NAME_FILTER.getName()));

	}

}
