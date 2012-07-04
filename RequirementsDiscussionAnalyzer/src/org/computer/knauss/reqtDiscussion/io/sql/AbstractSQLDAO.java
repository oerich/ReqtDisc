package org.computer.knauss.reqtDiscussion.io.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.computer.knauss.reqtDiscussion.io.DAOException;

public class AbstractSQLDAO {

	private static final String EXISTS_TABLE = "EXISTS_TABLE";
	private Map<String, PreparedStatement> statementCache = new HashMap<String, PreparedStatement>();
	protected Properties properties;

	public AbstractSQLDAO() {
		super();
	}

	public void configure(Properties p) throws DAOException {
		ConnectionManager.getInstance().configure(p);
		this.properties = p;
	}
	
	protected boolean existsTable(String tableName) throws SQLException {
		PreparedStatement ps = getPreparedStatement(this.properties.getProperty(EXISTS_TABLE));
		ps.setString(1, tableName);
		ps.execute();
		ResultSet rs = ps.getResultSet();
		if (!rs.next())
			return false;
		return rs.getInt(1) != 0;
	}

	protected PreparedStatement getPreparedStatement(String name) throws SQLException {
		// Connection still valid?
		Connection c = ConnectionManager.getInstance().getConnection();
		if (c.isClosed()) {
			ConnectionManager.getInstance().closeConnection();
			c = ConnectionManager.getInstance().getConnection();
			this.statementCache.clear();
		}
	
		if (!this.statementCache.containsKey(name)) {
			PreparedStatement stat = c.prepareStatement(name);
			this.statementCache.put(name, stat);
		}
		return this.statementCache.get(name);
	}

}