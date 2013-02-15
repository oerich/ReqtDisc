package org.computer.knauss.reqtDiscussion.io.jazz.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

final class JazzAccessConfigurationDummy implements IJazzAccessConfiguration {

	String username = "test";
	String passwd = "test";
	String rootservicesURL = "https://jazz.net/jazz/rootservices";
	String hostname = "test";
	URI jazzAuthURI;

	public JazzAccessConfigurationDummy() {
		try {
			this.jazzAuthURI = new URI("https://jazz.net/auth/login");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getUsername() {
		return "test";
	}

	@Override
	public String getRootservicesURL() {
		return rootservicesURL;
	}

	@Override
	public String getPassword() {
		return "test";
	}

	@Override
	public URI getJazzAuthURL() {
		return this.jazzAuthURI;
	}

	@Override
	public String getHostname() {
		return this.hostname;
	}

	@Override
	public void configure(Properties properties) {

	}
}