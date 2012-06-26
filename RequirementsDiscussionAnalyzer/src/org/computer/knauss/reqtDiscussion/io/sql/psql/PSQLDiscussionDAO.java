package org.computer.knauss.reqtDiscussion.io.sql.psql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.computer.knauss.reqtDiscussion.io.DAOException;
import org.computer.knauss.reqtDiscussion.io.IDiscussionDAO;
import org.computer.knauss.reqtDiscussion.model.Discussion;

/**
 * discussion(id, topic, description, type, date, status, creator)
 * discussion_event(id, discussion_id, content, creator, date)
 * 
 * @author eknauss
 * 
 */
public class PSQLDiscussionDAO extends AbstractSQLDAO implements IDiscussionDAO {

	static final String DISCUSSION_TABLE = "discussion";
	private static final String SELECT_BY_ID = "SELECT * FROM "
			+ DISCUSSION_TABLE + " WHERE ID = ?";
	private static final String SELECT_ALL = "SELECT * FROM "
			+ DISCUSSION_TABLE;
	private static final String CREATE_DISCUSSION_TABLE = "CREATE TABLE "
			+ DISCUSSION_TABLE
			+ " (id INTEGER PRIMARY KEY, topic TEXT, description TEXT, type TEXT, date DATE, status TEXT, creator TEXT) ";
	private static final String INSERT_DISCUSSION = "INSERT INTO "
			+ DISCUSSION_TABLE + " values (?,?,?,?,?,?,?)";
	private static final String UPDATE_DISCUSSION = "UPDATE "
			+ DISCUSSION_TABLE
			+ " SET topic = ?, description = ?, type = ?, date = ?, status = ?, creator = ? WHERE id = ?";
	private static final String DROP_DISCUSSION_TABLE = "DROP TABLE "
			+ DISCUSSION_TABLE;
	@Override
	public Discussion getDiscussion(int discussionID) throws DAOException {
		try {
			if (!existsTable(DISCUSSION_TABLE))
				return null;
			PreparedStatement stat = getPreparedStatement(SELECT_BY_ID);
			stat.setInt(1, discussionID);
			ResultSet rs = stat.executeQuery();
			if (!rs.next())
				return null;
			// discussion(id, topic, description, type, date, status, creator)
			Discussion d = new Discussion();
			d.setId(rs.getInt(1));
			d.setSummary(rs.getString(2));
			d.setDescription(rs.getString(3));
			d.setType(rs.getString(4));
			d.setDateCreated(rs.getDate(5));
			d.setStatus(rs.getString(6));
			return d;
		} catch (SQLException e) {
			throw new DAOException(e);
		}
	}

	@Override
	public Discussion getNextDiscussion() throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Discussion[] getDiscussions() throws DAOException {
		try {
			if (!existsTable(DISCUSSION_TABLE))
				return new Discussion[0];
			PreparedStatement stat = getPreparedStatement(SELECT_ALL);
			ResultSet rs = stat.executeQuery();
			List<Discussion> tmp = new LinkedList<Discussion>();
			while (rs.next()) {
				// discussion(id, topic, description, type, date, status,
				// creator)
				Discussion d = new Discussion();
				d.setId(rs.getInt(1));
				d.setSummary(rs.getString(2));
				d.setDescription(rs.getString(3));
				d.setType(rs.getString(4));
				d.setDateCreated(rs.getDate(5));
				d.setStatus(rs.getString(6));
				tmp.add(d);
			}
			return tmp.toArray(new Discussion[0]);
		} catch (SQLException e) {
			throw new DAOException(e);
		}
	}

	@Override
	public void storeDiscussion(Discussion d) throws DAOException {
		// discussion(id, topic, description, type, date, status, creator)
		try {
			if (!existsTable(DISCUSSION_TABLE))
				createSchema();
			PreparedStatement ps = null;
			int i = 1;
			if (existsDiscussion(d.getID())) {
				ps = getPreparedStatement(UPDATE_DISCUSSION);
				ps.setInt(7, d.getID());
			} else {
				ps = getPreparedStatement(INSERT_DISCUSSION);
				ps.setInt(1, d.getID());
				i++;
			}
			ps.setString(i++, d.getSummary());
			ps.setString(i++, d.getDescription());
			ps.setString(i++, d.getType());
			ps.setDate(i++, d.getDateCreated());
			ps.setString(i++, d.getStatus());
			ps.setString(i++, d.getCreator());
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new DAOException(e);
		}
	}

	@Override
	public void storeDiscussions(Discussion[] ds) throws DAOException {
		for (Discussion d : ds)
			storeDiscussion(d);
	}

	private boolean existsDiscussion(int ID) throws SQLException {
		PreparedStatement ps = getPreparedStatement(SELECT_BY_ID);
		ps.setInt(1, ID);
		ResultSet rs = ps.executeQuery();
		if (!rs.next())
			return false;
		return rs.getInt(1) == ID;

	}

	void createSchema() throws SQLException {
		getPreparedStatement(CREATE_DISCUSSION_TABLE).executeUpdate();
		System.out.println("Created Table " + DISCUSSION_TABLE + ".");
	}

	void dropSchema() throws SQLException {
		getPreparedStatement(DROP_DISCUSSION_TABLE).execute();
		System.out.println("Dropped Table " + DISCUSSION_TABLE + ".");
	}

	public static void main(String[] args) throws DAOException, SQLException {
		// PSQLDiscussionDAO dao = new PSQLDiscussionDAO();
		//
		// Discussion d = dao.getDiscussion(1234);
		// System.out.println("should be null: " + d);
		//
		// d = new Discussion();
		// d.setId(1234);
		// d.setSummary("Test Discussion");
		// d.setDescription("This is a discussion inserted to test the Discussion DAO");
		// d.setStatus("open");
		// d.setType("Test dummy");
		// d.setDateCreated(new Date(System.currentTimeMillis()));
		// d.setCreator("Tester1");
		// dao.storeDiscussion(d);
		//
		// d = dao.getDiscussion(1234);
		// System.out.println("should not be null: " + d);
		//
		//
		// dao.dropSchema();
	}
}
