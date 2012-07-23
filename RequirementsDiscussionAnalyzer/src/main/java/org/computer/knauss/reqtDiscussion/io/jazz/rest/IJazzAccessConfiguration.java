package org.computer.knauss.reqtDiscussion.io.jazz.rest;

import java.net.URI;
import java.util.Properties;

public interface IJazzAccessConfiguration {

	public abstract String getHostname();

	public abstract URI getJazzAuthURL();

	public abstract String getUsername();

	public abstract String getPassword();
	
	public abstract void configure(Properties properties);

}