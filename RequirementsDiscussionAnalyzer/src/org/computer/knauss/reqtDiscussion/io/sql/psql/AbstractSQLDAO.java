package org.computer.knauss.reqtDiscussion.io.sql.psql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AbstractSQLDAO {

	private static final String EXISTS_TABLE = "SELECT c.oid FROM pg_catalog.pg_class c LEFT JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace WHERE c.relname ~ ? AND pg_catalog.pg_table_is_visible(c.oid)";
	private Map<String, PreparedStatement> statementCache = new HashMap<String, PreparedStatement>();

	public AbstractSQLDAO() {
		super();
	}

	protected boolean existsTable(String tableName) throws SQLException {
		PreparedStatement ps = getPreparedStatement(EXISTS_TABLE);
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