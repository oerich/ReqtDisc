package org.computer.knauss.reqtDiscussion.io.sql.psql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.computer.knauss.reqtDiscussion.io.DAOException;
import org.computer.knauss.reqtDiscussion.io.IDiscussionEventDAO;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;

public class PSQLDiscussionEventDAO extends AbstractSQLDAO implements
		IDiscussionEventDAO {

	private static final String DISCUSSION_EVENT_TABLENAME = "discussionevent";
	private static final String SELECT_BY_DISCUSSION_ID = "SELECT * FROM "
			+ DISCUSSION_EVENT_TABLENAME + " WHERE discussionId = ?;";
	private static final String INSERT_DISCUSSION_EVENT = "INSERT INTO "
			+ DISCUSSION_EVENT_TABLENAME
			+ " (id, discussionId, content, creationDate, creator) VALUES (?,?,?,?,?);";
	private static final String CREATE_DISCUSSION_EVENT_TABLE = "CREATE TABLE "
			+ DISCUSSION_EVENT_TABLENAME
			+ " (id INTEGER PRIMARY KEY, discussionId INTEGER, content TEXT, creationDate DATE, creator TEXT, CONSTRAINT discussionEvent_discussion_fkey FOREIGN KEY (discussionId) REFERENCES discussion (id))";
	private static final String DROP_DISCUSSION_TABLE = "DROP TABLE "
			+ DISCUSSION_EVENT_TABLENAME;

	@Override
	public void configure(Properties p) throws DAOException {
		ConnectionManager.getInstance().configure(p);
	}

	@Override
	public DiscussionEvent[] getDiscussionEventsOfDiscussion(int discussionId)
			throws DAOException {
		try {
			if (!existsTable(DISCUSSION_EVENT_TABLENAME))
				return new DiscussionEvent[0];
			PreparedStatement stat = getPreparedStatement(SELECT_BY_DISCUSSION_ID);
			List<DiscussionEvent> res = new LinkedList<DiscussionEvent>();
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
	public DiscussionEvent getDiscussionEvent(int id) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void storeDiscussionEvent(DiscussionEvent de) throws DAOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void storeDiscussionEvents(DiscussionEvent[] des)
			throws DAOException {
		try {
			if (!existsTable(DISCUSSION_EVENT_TABLENAME))
				createSchema();
			PreparedStatement stat = getPreparedStatement(INSERT_DISCUSSION_EVENT);

			for (DiscussionEvent de : des) {
				int id = de.getID();
				if (id <= 0)
					id = getNextDiscussionEventID();
				stat.setInt(1, id);
				stat.setInt(2, de.getDiscussionID());
				stat.setString(3, de.getContent());
				stat.setDate(4, de.getCreationDate());
				stat.setString(5, de.getCreator());

				if (1 != stat.executeUpdate()) {
					// stat.close();
					// ConnectionManager.getInstance().closeConnection();
					throw new DAOException("INSERT should only affect one row");
				}
			}
			// stat.close();
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			// ConnectionManager.getInstance().closeConnection();
		}
	}

	void createSchema() throws SQLException {
		getPreparedStatement(CREATE_DISCUSSION_EVENT_TABLE).executeUpdate();
		System.out.println("Created Table discussionEvent.");
	}

	void dropSchema() throws SQLException {
		getPreparedStatement(DROP_DISCUSSION_TABLE).execute();
		System.out.println("Dropped Table discussionEvent.");
	}

	private int getNextDiscussionEventID() throws SQLException {
		PreparedStatement ps = getPreparedStatement("SELECT max(id) from discussionEvent");
		ResultSet rs = ps.executeQuery();
		if (!rs.next())
			return 1;
		return rs.getInt(1) + 1;
	}

	// public static void main(String[] args) throws DAOException, SQLException
	// {
	// PSQLDiscussionEventDAO dao = new PSQLDiscussionEventDAO();
	//
	// DiscussionEvent[] des = dao.getDiscussionEventsOfDiscussion(1234);
	// System.out.println("Should find 0 discussionEvents, found "
	// + des.length);
	//
	// Discussion d = new Discussion();
	// d.setId(1234);
	//
	// PSQLDiscussionDAO ddao = new PSQLDiscussionDAO();
	// ddao.storeDiscussion(d);
	//
	// DiscussionEvent de = new DiscussionEvent();
	// de.setDiscussionID(1234);
	// de.setContent("Test comment");
	// de.setCreator("Tester1");
	// de.setCreationDate(new Date(System.currentTimeMillis()));
	//
	// dao.storeDiscussionEvents(new DiscussionEvent[] { de });
	//
	// des = dao.getDiscussionEventsOfDiscussion(1234);
	// System.out
	// .println("Should find 1 discussionEvent, found " + des.length);
	//
	// dao.dropSchema();
	// ddao.dropSchema();
	// }
}
