package org.computer.knauss.reqtDiscussion.ui.ctrl;

import org.computer.knauss.reqtDiscussion.io.IDAOProgressMonitor;

public class ProgressMonitorProbe implements IDAOProgressMonitor {

	public int totalSteps = 0;
	public int step = 0;
	public String message = "";

	@Override
	public void setTotalSteps(int steps) {
		this.totalSteps = steps;
	}

	@Override
	public void setStep(int step) {
		this.step = step;
	}

	@Override
	public void setStep(int step, String message) {
		this.step = step;
		this.message = message;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

}