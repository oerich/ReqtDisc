package org.computer.knauss.reqtDiscussion.ui.visualization.sna;

public interface IScaler {

	public static final IScaler NULL_SCALER = new IScaler() {

		@Override
		public double scaleDown(double d, double bound) {
			return d;
		}

	};
	public static final IScaler DEFAULT_SCALER = new IScaler() {

		@Override
		public double scaleDown(double d, double bound) {
			double s = bound / 16;
			if (d > bound * 128)
				return bound;
			if (d > bound * 64)
				return bound - s;
			if (d > bound * 32)
				return bound - 2 * s;
			if (d > bound * 16)
				return bound - 3 * s;
			if (d > bound * 8)
				return bound - 4 * s;
			if (d > bound * 4)
				return bound - 5 * s;
			if (d > bound * 2)
				return bound - 6 * s;
			if (d > bound)
				return bound - 7 * s;
			if (d > bound / 2)
				return bound - 8 * s;
			return d;
		}
	};

	public double scaleDown(double d, double bound);

}
