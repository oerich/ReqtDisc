package org.computer.knauss.reqtDiscussion.io.jazz.util.ui;

import static org.junit.Assert.*;

import org.junit.Test;

public class DialogBasedJazzAccessConfigurationTest {

	@Test
	public void test() {
		DialogBasedJazzAccessConfiguration jac = new DialogBasedJazzAccessConfiguration();
		assertEquals("ericKnauss", jac.getUsername());
		assertEquals("mist", jac.getPassword());
		assertEquals("jazz.net", jac.getHostname());
		assertEquals("https://jazz.net/jazz/oauth-authorize", jac.getJazzAuthURL().toString());
	}

}
