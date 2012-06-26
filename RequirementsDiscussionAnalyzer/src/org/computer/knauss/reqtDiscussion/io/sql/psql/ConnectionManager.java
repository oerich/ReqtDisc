package org.computer.knauss.reqtDiscussion.io.sql.psql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionManager {

	private static ConnectionManager INSTANCE = null;
	private Connection connection;

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
			try {
				Class.forName("org.postgresql.Driver");
			} catch (ClassNotFoundException e) {
				System.out.println("Where is your PostgreSQL JDBC Driver? "
						+ "Include in your library path!");
				e.printStackTrace();
				return null;
			}
			this.connection = null;

			try {

				this.connection = DriverManager
						.getConnection("jdbc:postgresql://127.0.0.1:5432/discussionAnalysis");

			} catch (SQLException e) {
				System.err.println("Connection Failed! Check output console. You might want to connect to the database: ssh -L 5432:localhost:5432 ballroom.segal.uvic.ca");
				e.printStackTrace();
				return null;

			}
		}
		return this.connection;
	}
	
	public void closeConnection() {
		try {
			this.connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.connection = null;
	}

	public void configure(Properties p) {
		// TODO Auto-generated method stub
		
	}
}
