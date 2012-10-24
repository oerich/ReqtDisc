package org.computer.knauss.reqtDiscussion.ui.visualization.sna;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class IScalerTest {

	@Test
	public void testDefault() {
		assertEquals(3, IScaler.DEFAULT_SCALER.scaleDown(3, 32), 0.01);
		assertEquals(16, IScaler.DEFAULT_SCALER.scaleDown(16, 32), 0.01);
		assertEquals(16, IScaler.DEFAULT_SCALER.scaleDown(17, 32), 0.01);
		assertEquals(16, IScaler.DEFAULT_SCALER.scaleDown(32, 32), 0.01);
		assertEquals(18, IScaler.DEFAULT_SCALER.scaleDown(64, 32), 0.01);
		assertEquals(30, IScaler.DEFAULT_SCALER.scaleDown(4096, 32), 0.01);
		assertEquals(32, IScaler.DEFAULT_SCALER.scaleDown(4097, 32), 0.01);
		assertEquals(32, IScaler.DEFAULT_SCALER.scaleDown(300000, 32), 0.01);
	}

}
