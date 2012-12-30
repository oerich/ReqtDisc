package org.computer.knauss.reqtDiscussion.model.machineLearning;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HybridBayesianClassifierTest {

	private File f1;
	private File f2;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		f1.delete();
		f2.delete();
	}

	@Test
	public void testInitFiles() throws Exception {
		f1 = new File("test");
		f1.deleteOnExit();

		assertEquals("test", f1.getPath());
		HybridBayesianClassifier hc = new HybridBayesianClassifier();

		hc.init(f1);

		f2 = new File("nic-test");
		f2.deleteOnExit();
		assertTrue(f2.exists());
	}

}
