package org.computer.knauss.reqtDiscussion.io.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.computer.knauss.reqtDiscussion.io.DAOException;
import org.computer.knauss.reqtDiscussion.io.IDiscussionEventClassificationDAO;
import org.computer.knauss.reqtDiscussion.io.IDiscussionEventDAO;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;

/**
 * Use properties to configure this DAO. If no username or password is given in
 * the properties, this is not a problem: A dialogue will query the user in this
 * case. You should however give a prepared statement for each of the KEYS
 * below. Where applicable, these PreparedStatements should work with the
 * following columns:
 * <ol>
 * <li>id</li>
 * <li>discussionId</li>
 * <li>content</li>
 * <li>creationDate</li>
 * <li>creator</li>
 * </ol>
 * 
 * @author eknauss
 * 
 */
public class SQLDiscussionEventDAO extends AbstractSQLDAO implements
		IDiscussionEventDAO {

	public static final String SELECT_DISCUSSION_EVENT_BY_DISCUSSION_ID = "SELECT_DISCUSSION_EVENT_BY_DISCUSSION_ID";
	public static final String INSERT_DISCUSSION_EVENT = "INSERT_DISCUSSION_EVENT";
	public static final String DELETE_DISCUSSION_EVENT = "DELETE_DISCUSSION_EVENT_OF_DISCUSSION";
	public static final String CREATE_DISCUSSION_EVENT_TABLE = "CREATE_DISCUSSION_EVENT_TABLE";
	public static final String DROP_DISCUSSION_EVENT_TABLE = "DROP_DISCUSSION_EVENT_TABLE";
	public static final String SELECT_NEW_DISCUSSION_EVENT_ID = "SELECT_NEW_DISCUSSION_EVENT_ID";
	public static final String DISCUSSION_EVENT_TABLE_NAME = "DISCUSSION_EVENT_TABLE_NAME";
	private IDiscussionEventClassificationDAO discEventClassDAO;

	public void setDiscussionEventClassificationDAO(
			IDiscussionEventClassificationDAO discEventClassDAO) {
		this.discEventClassDAO = discEventClassDAO;
	}

	@Override
	public DiscussionEvent[] getDiscussionEventsOfDiscussion(int discussionId)
			throws DAOException {
		try {
			if (!existsTable(this.properties
					.getProperty(DISCUSSION_EVENT_TABLE_NAME)))
				return new DiscussionEvent[0];
			PreparedStatement stat = getPreparedStatement(this.properties
					.getProperty(SELECT_DISCUSSION_EVENT_BY_DISCUSSION_ID));
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
				de.setDiscussionEventClassification(this.discEventClassDAO
						.getClassificationsForDiscussionEvent(de.getID()));
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
			if (!existsTable(this.properties
					.getProperty(DISCUSSION_EVENT_TABLE_NAME)))
				createSchema();

			// How many discussions do we have? (Discussion ID is part of the
			// PK!)
			Map<Integer, List<DiscussionEvent>> discussionMap = new HashMap<Integer, List<DiscussionEvent>>();
			for (DiscussionEvent e : des) {
				if (!discussionMap.containsKey(e.getDiscussionID())) {
					discussionMap.put(e.getDiscussionID(),
							new LinkedList<DiscussionEvent>());
				}
				discussionMap.get(e.getDiscussionID()).add(e);
			}

			// For each discussion: Check whether we are doing an update or
			// insert
			for (Integer i : discussionMap.keySet()) {
				PreparedStatement stat = null;
				if (existsDiscussion(i)) {
					stat = getPreparedStatement(this.properties
							.getProperty(DELETE_DISCUSSION_EVENT));
					stat.setInt(1, i);
					System.out.println("Updating " + stat.executeUpdate()
							+ " existing events for discussion " + i);
				}
				stat = getPreparedStatement(this.properties
						.getProperty(INSERT_DISCUSSION_EVENT));

				for (DiscussionEvent de : des) {
					int id = de.getID();

					if (id < 0)
						id = getNextDiscussionEventID();
					stat.setInt(1, id);
					stat.setInt(2, de.getDiscussionID());
					stat.setString(3, de.getContent());
					stat.setDate(4, de.getCreationDate());
					stat.setString(5, de.getCreator());

					if (1 != stat.executeUpdate()) {
						// stat.close();
						// ConnectionManager.getInstance().closeConnection();
						throw new DAOException(
								"INSERT should only affect one row");
					}
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
		getPreparedStatement(
				this.properties.getProperty(CREATE_DISCUSSION_EVENT_TABLE))
				.executeUpdate();
		System.out.println("Created Table "
				+ this.properties.getProperty(DISCUSSION_EVENT_TABLE_NAME)
				+ ".");
	}

	public void dropSchema() throws SQLException {
		getPreparedStatement(
				this.properties.getProperty(DROP_DISCUSSION_EVENT_TABLE))
				.execute();
		System.out.println("Dropped Table "
				+ this.properties.getProperty(DISCUSSION_EVENT_TABLE_NAME)
				+ ".");
	}

	private boolean existsDiscussion(int ID) throws SQLException {
		PreparedStatement ps = getPreparedStatement(this.properties
				.getProperty(SELECT_DISCUSSION_EVENT_BY_DISCUSSION_ID));
		ps.setInt(1, ID);
		ResultSet rs = ps.executeQuery();

		return rs.next();
	}

	private int getNextDiscussionEventID() throws SQLException {
		PreparedStatement ps = getPreparedStatement(this.properties
				.getProperty(SELECT_NEW_DISCUSSION_EVENT_ID));
		ResultSet rs = ps.executeQuery();
		if (!rs.next())
			return 1;
		return rs.getInt(1) + 1;
	}

	@Override
	protected Properties getDefaultProperties() {
		Properties p = new Properties();

		p.setProperty(SELECT_DISCUSSION_EVENT_BY_DISCUSSION_ID, "");
		p.setProperty(INSERT_DISCUSSION_EVENT, "");
		p.setProperty(DELETE_DISCUSSION_EVENT, "");
		p.setProperty(CREATE_DISCUSSION_EVENT_TABLE, "");
		p.setProperty(DROP_DISCUSSION_EVENT_TABLE, "");
		p.setProperty(SELECT_NEW_DISCUSSION_EVENT_ID, "");
		p.setProperty(DISCUSSION_EVENT_TABLE_NAME, "");

		return p;
	}

}
