package org.computer.knauss.reqtDiscussion.io.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionManager {

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
			String url = this.properties.getProperty("url");
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
				this.connection = DriverManager.getConnection(url);
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
		this.properties = p;
	}
}
