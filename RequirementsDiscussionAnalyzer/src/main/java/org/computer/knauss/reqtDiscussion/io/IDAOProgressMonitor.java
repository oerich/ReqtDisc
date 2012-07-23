package org.computer.knauss.reqtDiscussion.io;

public interface IDAOProgressMonitor {

	public static final IDAOProgressMonitor NULL_PROGRESS_MONITOR = new IDAOProgressMonitor() {

		@Override
		public void setTotalSteps(int steps) {

		}

		@Override
		public void setStep(int step, String message) {

		}

		@Override
		public void setStep(int step) {

		}

		@Override
		public boolean isCancelled() {
			return false;
		}
	};

	/**
	 * Set to -1, if you don't know
	 * 
	 * @param steps
	 */
	public void setTotalSteps(int steps);

	/**
	 * The step you are currently processing...
	 * 
	 * @param step
	 */
	public void setStep(int step);

	/**
	 * The step and a short message, you are currently processing
	 * 
	 * @param step
	 * @param message
	 */
	public void setStep(int step, String message);

	/**
	 * Please ask now and then if you should stop what you are doing.
	 * 
	 * @return
	 */
	public boolean isCancelled();

}
