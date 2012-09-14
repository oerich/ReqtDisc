package org.computer.knauss.reqtDiscussion.io.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.computer.knauss.reqtDiscussion.io.DAOException;
import org.computer.knauss.reqtDiscussion.io.IConfigurable;

public abstract class AbstractSQLDAO implements IConfigurable {

	private static final String EXISTS_TABLE = "EXISTS_TABLE";
	private Map<String, PreparedStatement> statementCache = new HashMap<String, PreparedStatement>();
	private transient Properties properties;

	public AbstractSQLDAO() {
		super();
	}

	public synchronized void configure(Properties p) throws DAOException {
		for (String key : p.stringPropertyNames()) {
			getConfiguration().setProperty(key, p.getProperty(key));
		}
		ConnectionManager.getInstance().configure(getConfiguration());
	}

	protected abstract Properties getDefaultProperties();

	protected boolean existsTable(String tableName) throws SQLException {
		PreparedStatement ps = getPreparedStatement(getConfiguration()
				.getProperty(EXISTS_TABLE));
		ps.setString(1, tableName);
		ps.execute();
		ResultSet rs = ps.getResultSet();
		if (!rs.next())
			return false;
		return rs.getInt(1) != 0;
	}

	protected PreparedStatement getPreparedStatement(String name)
			throws SQLException {
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

	public Properties getConfiguration() {
		// make sure that there is only one properties instance for the SQL DAOs
		// and the ConnectionManager
		if (this.properties == null) {
			this.properties = ConnectionManager.getInstance()
					.getConfiguration();
			Properties defaults = getDefaultProperties();

			for (String key : defaults.stringPropertyNames()) {
				this.properties.setProperty(key, defaults.getProperty(key));
			}
		}
		return this.properties;
	}

	@Override
	public Map<String, String> checkConfiguration() {
		System.out.println(getClass().getSimpleName() + ".check: "
				+ getConfiguration());
		Map<String, String> ret = ConnectionManager.getInstance()
				.checkConfiguration();

		Map<String, String> fields = getMandatoryPropertiesAndHints();
		for (String key : fields.keySet()) {
			if ("".equals(getConfiguration().getProperty(key))) {
				ret.put(key, "Missing: " + fields.get(key));
			}
		}

		return ret;
	}

	protected abstract Map<String, String> getMandatoryPropertiesAndHints();
}