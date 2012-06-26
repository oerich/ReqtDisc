package org.computer.knauss.reqtDiscussion.io.sql.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.computer.knauss.reqtDiscussion.io.DAOException;
import org.computer.knauss.reqtDiscussion.io.IDiscussionEventDAO;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;


public class MySQLDiscussionEventDAO implements IDiscussionEventDAO {

	public static final String PROP_URL = "url";
	public static final String PROP_USER = "user";
	public static final String PROP_PWD = "pass";

	private static final String SELECT_BY_DISCUSSION_ID = "SELECT * FROM discussionEvent WHERE discussionId = ?;";
	private static final String INSERT_DISCUSSION_EVENT = "INSERT INTO discussionEvent (id,discussionId, content, creationDate, creator) VALUES (?,?,?,?,?);";

	private boolean initialized = false;
	private Properties properties;
	private Connection connection;
	private Map<String, PreparedStatement> statementCache = new HashMap<String, PreparedStatement>();

	@Override
	public void configure(Properties p) {
		this.properties = p;
	}

	@Override
	public DiscussionEvent[] getDiscussionEventsOfDiscussion(int discussionId)
			throws DAOException {
		try {
			PreparedStatement stat = getPreparedStatement(SELECT_BY_DISCUSSION_ID);
			List<DiscussionEvent> res = new Vector<DiscussionEvent>();
			stat.setInt(1, discussionId);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				DiscussionEvent de = new DiscussionEvent();
				de.setID(rs.getInt("id"));
				de.setDiscussionID(rs.getInt("discussionId"));
				de.setContent(rs.getString("content"));
				de.setCreationDate(rs.getDate("creationDate"));
				de.setCreator(rs.getString("creator"));
				res.add(de);
			}
			return res.toArray(new DiscussionEvent[0]);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DAOException(e);
		}

	}

	@Override
	public DiscussionEvent getDiscussionEvent(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void storeDiscussionEvent(DiscussionEvent de) {
		// TODO Auto-generated method stub

	}

	@Override
	public void storeDiscussionEvents(DiscussionEvent[] des)
			throws DAOException {
		try {
			PreparedStatement stat = getPreparedStatement(INSERT_DISCUSSION_EVENT);

			for (DiscussionEvent de : des) {
				stat.setInt(1, de.getID());
				stat.setInt(2, de.getDiscussionID());
				stat.setString(3, de.getContent());
				stat.setDate(4, de.getCreationDate());
				stat.setString(5, de.getCreator());

				if (1 != stat.executeUpdate()) {
					stat.close();
					this.connection.close();
					throw new DAOException("INSERT should only affect one row");
				}
			}
			stat.close();
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			try {
				this.connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
	}
	
	private PreparedStatement getPreparedStatement(String name)
			throws SQLException {
		// Connection still valid?
		if (this.connection == null || this.connection.isClosed()) {
			this.connection = getConnection();
			this.statementCache.clear();
		}

		if (!this.statementCache.containsKey(name)) {
			PreparedStatement stat = this.connection.prepareStatement(name);
			this.statementCache.put(name, stat);
		}
		return this.statementCache.get(name);
	}

	private Connection getConnection() throws SQLException {
		if (!initialized) {
			try {
				Class.forName("com.mysql.jdbc.Driver").newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
			initialized = true;
		}
		return DriverManager.getConnection(
				this.properties.getProperty(PROP_URL),
				this.properties.getProperty(PROP_USER),
				this.properties.getProperty(PROP_PWD));
	}

}
