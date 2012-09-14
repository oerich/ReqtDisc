package org.computer.knauss.reqtDiscussion.io;

import java.util.Map;
import java.util.Properties;

public interface IConfigurable {
	public void configure(Properties properties) throws DAOException;

	/**
	 * Returns the configuration set by configure or a default configuration
	 * that contains all relevant keys and default values.
	 * 
	 * @return
	 */
	public Properties getConfiguration();

	/**
	 * Checks the configuration and returns a map with the property keys and
	 * problem description. If there is no problem, the map should be empty.
	 * 
	 * @return
	 */
	public Map<String, String> checkConfiguration();
}
