package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.awt.event.ActionEvent;

import org.computer.knauss.reqtDiscussion.model.machineLearning.ClassifierManager;

public class ClearClassifierCmd extends AbstractCommand {

	public ClearClassifierCmd() {
		super("Clear classifier");
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent arg0) {
		try {
			ClassifierManager.getInstance().getClassifier().clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
