package org.computer.knauss.reqtDiscussion.io.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.computer.knauss.reqtDiscussion.io.IConfigurable;

public class ConnectionManager implements IConfigurable {

	private static final String PROP_URL = "url";
	private static final String PROP_USER = "user";
	private static final String PROP_PASS = "pass";
	private static ConnectionManager INSTANCE = null;
	private Connection connection;
	private Properties properties;

	private ConnectionManager() {
		// This is a Singleton
	}

	public static ConnectionManager getInstance() {
		if (INSTANCE == null)
			INSTANCE = new ConnectionManager();
		return INSTANCE;
	}

	public Connection getConnection() {
		if (this.connection == null) {
			String url = this.properties.getProperty(PROP_URL);
			try {
				if (url.indexOf("postgresql") > -1)
					Class.forName("org.postgresql.Driver");
				else if (url.indexOf("mysql") > -1)
					Class.forName("com.mysql.jdbc.Driver").newInstance();
			} catch (ClassNotFoundException e) {
				System.out.println("Where is your PostgreSQL JDBC Driver? "
						+ "Include in your library path!");
				e.printStackTrace();
				return null;
			} catch (InstantiationException e) {
				System.out.println("Where is your MySQL JDBC Driver? "
						+ "Include in your library path!");
				e.printStackTrace();
				return null;
			} catch (IllegalAccessException e) {
				System.out.println("Where is your MySQL JDBC Driver? "
						+ "Include in your library path!");
				e.printStackTrace();
				return null;
			}

			try {
				String user = this.properties.getProperty(PROP_USER);
				String pass = this.properties.getProperty(PROP_PASS);

				if (user == null || "".equals(user)) {
					this.connection = DriverManager.getConnection(url);
				} else {
					System.out.println("Create Connection for User " + user);
					if (pass == null)
						pass = "";
					this.connection = DriverManager.getConnection(url, user,
							pass);
				}
			} catch (SQLException e) {
				System.err
						.println("Connection Failed! Check output console. You might want to connect to the database: ssh -L 5432:localhost:5432 ballroom.segal.uvic.ca");
				e.printStackTrace();
				return null;

			}
		}
		return this.connection;
	}

	public void closeConnection() {
		if (this.connection == null)
			return;
		try {
			this.connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.connection = null;
	}

	public void configure(Properties p) {
		for (String key : p.stringPropertyNames()) {
			getConfiguration().setProperty(key, p.getProperty(key));
		}
	}

	@Override
	public Properties getConfiguration() {
		if (this.properties == null) {
			this.properties = new Properties();
			this.properties.setProperty(PROP_URL, "");
		}
		return this.properties;
	}

	@Override
	public Map<String, String> checkConfiguration() {
		Map<String, String> ret = new HashMap<String, String>();

		if ("".equals(getConfiguration().getProperty(PROP_URL)))
			ret.put(PROP_URL,
					"Missing: URL (e.g. jdbc:<dbms>://<url>:<port>/<database>");
		return ret;
	}
}
