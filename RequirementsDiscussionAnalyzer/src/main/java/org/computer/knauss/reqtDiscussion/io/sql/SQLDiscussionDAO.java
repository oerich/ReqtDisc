package org.computer.knauss.reqtDiscussion.io.sql;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.computer.knauss.reqtDiscussion.io.DAOException;
import org.computer.knauss.reqtDiscussion.io.IDAOProgressMonitor;
import org.computer.knauss.reqtDiscussion.io.IDiscussionDAO;
import org.computer.knauss.reqtDiscussion.io.IDiscussionEventDAO;
import org.computer.knauss.reqtDiscussion.io.IIncidentDAO;
import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionFactory;
import org.computer.knauss.reqtDiscussion.ui.ctrl.HighlightRelatedDiscussions;

/**
 * discussion(id, topic, description, type, date, status, creator)
 * discussion_event(id, discussion_id, content, creator, date)
 * 
 * @author eknauss
 * 
 */
public class SQLDiscussionDAO extends AbstractSQLDAO implements IDiscussionDAO {

	private static final String DISCUSSION_TABLE = "DISCUSSION_TABLE";
	private static final String SELECT_DISCUSSION_BY_ID = "SELECT_DISCUSSION_BY_ID";
	private static final String SELECT_ALL_DISCUSSIONS = "SELECT_ALL_DISCUSSIONS";
	private static final String NUMBER_OF_ALL_DISCUSSIONS = "NUMBER_OF_ALL_DISCUSSIONS";
	private static final String UPDATE_DISCUSSION = "UPDATE_DISCUSSION";
	private static final String INSERT_DISCUSSION = "INSERT_DISCUSSION";
	private static final String CREATE_DISCUSSION_TABLE = "CREATE_DISCUSSION_TABLE";
	private static final String DROP_DISCUSSION_TABLE = "DROP_DISCUSSION_TABLE";
	private IDiscussionEventDAO deDAO;
	private IIncidentDAO inDAO;

	public void setDiscussionEventDAO(IDiscussionEventDAO deDAO) {
		this.deDAO = deDAO;
	}

	public void setIncidentDAO(IIncidentDAO inDAO) {
		this.inDAO = inDAO;
	}

	@Override
	public Discussion getDiscussion(int discussionID) throws DAOException {
		try {
			if (!existsTable(this.properties.getProperty(DISCUSSION_TABLE)))
				return null;
			PreparedStatement stat = getPreparedStatement(this.properties
					.getProperty(SELECT_DISCUSSION_BY_ID));
			stat.setInt(1, discussionID);
			ResultSet rs = stat.executeQuery();
			if (!rs.next())
				return null;
			Discussion d = createDiscussion(rs);
			addRelatedItems(d);
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
		return getDiscussions(IDAOProgressMonitor.NULL_PROGRESS_MONITOR);
	}

	private List<Discussion> addReferencedDiscussions(
			List<Discussion> discussions, IDAOProgressMonitor monitor,
			int progress) throws DAOException {
		List<Discussion> ret = new LinkedList<Discussion>();
		try {
			HighlightRelatedDiscussions hrd = new HighlightRelatedDiscussions();
			monitor.setStep(progress, "Loading related discussions...");
			for (Discussion d : discussions) {
				monitor.setStep(progress++);
				int[] relatedIDs = hrd.getRelatedDiscussionIDs(d.getID());
				for (int i : relatedIDs) {
					if (!DiscussionFactory.getInstance().exists(i))
						ret.add(getDiscussion(i));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		discussions.addAll(ret);
		return discussions;
	}

	private void addRelatedItems(Discussion d) throws DAOException {
		d.addDiscussionEvents(this.deDAO.getDiscussionEventsOfDiscussion(d
				.getID()));
		d.addIncidents(this.inDAO.getIncidentsForDiscussion(d.getID()));
	}

	private Discussion createDiscussion(ResultSet rs) throws SQLException {
		Discussion d = DiscussionFactory.getInstance().getDiscussion(
				rs.getInt(1));
		d.setSummary(rs.getString(2));
		d.setDescription(rs.getString(3));
		d.setType(rs.getString(4));
		d.setCreationDate(rs.getDate(5));
		d.setStatus(rs.getString(6));
		d.setCreator(rs.getString(7));
		return d;
	}

	@Override
	public void storeDiscussion(Discussion d) throws DAOException {
		// discussion(id, topic, description, type, date, status, creator)
		try {
			if (!existsTable(this.properties.getProperty(DISCUSSION_TABLE)))
				createSchema();
			PreparedStatement ps = null;
			int i = 1;
			if (existsDiscussion(d.getID())) {
				ps = getPreparedStatement(this.properties
						.getProperty(UPDATE_DISCUSSION));
				ps.setInt(7, d.getID());
			} else {
				ps = getPreparedStatement(this.properties
						.getProperty(INSERT_DISCUSSION));
				ps.setInt(1, d.getID());
				i++;
			}
			ps.setString(i++, d.getSummary());
			ps.setString(i++, d.getDescription());
			ps.setString(i++, d.getType());
			ps.setDate(i++, d.getCreationDate());
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
		PreparedStatement ps = getPreparedStatement(this.properties
				.getProperty(SELECT_DISCUSSION_BY_ID));
		ps.setInt(1, ID);
		ResultSet rs = ps.executeQuery();
		if (!rs.next())
			return false;
		return rs.getInt(1) == ID;

	}

	void createSchema() throws SQLException {
		getPreparedStatement(
				this.properties.getProperty(CREATE_DISCUSSION_TABLE))
				.executeUpdate();
		System.out.println("Created Table "
				+ this.properties.getProperty(DISCUSSION_TABLE) + ".");
	}

	public void dropSchema() throws SQLException {
		getPreparedStatement(this.properties.getProperty(DROP_DISCUSSION_TABLE))
				.execute();
		System.out.println("Dropped Table "
				+ this.properties.getProperty(DISCUSSION_TABLE) + ".");
	}

	@Override
	public Discussion[] getDiscussions(IDAOProgressMonitor progressMonitor)
			throws DAOException {
		try {
			if (!existsTable(this.properties.getProperty(DISCUSSION_TABLE)))
				return new Discussion[0];

			progressMonitor.setTotalSteps(numberOfAllDiscussions() * 3);

			PreparedStatement stat = getPreparedStatement(this.properties
					.getProperty(SELECT_ALL_DISCUSSIONS));
			ResultSet rs = stat.executeQuery();
			List<Discussion> tmp = new LinkedList<Discussion>();

			int i = 0;
			progressMonitor.setStep(0, "Loading Discussions...");
			while (rs.next()) {
				// discussion(id, topic, description, type, date, status,
				// creator)
				Discussion d = createDiscussion(rs);
				tmp.add(d);
				progressMonitor.setStep(i++);
				// Thread.yield();
			}
			progressMonitor.setStep(i, "Loading Discussion Events...");
			for (Discussion d : tmp) {
				addRelatedItems(d);
				progressMonitor.setStep(i++);
			}

			return addReferencedDiscussions(tmp, progressMonitor, i).toArray(
					new Discussion[0]);
		} catch (SQLException e) {
			throw new DAOException(e);
		}
	}

	private int numberOfAllDiscussions() throws SQLException {
		if (!existsTable(this.properties.getProperty(DISCUSSION_TABLE)))
			return 0;
		String numberQuery = this.properties
				.getProperty(NUMBER_OF_ALL_DISCUSSIONS);
		if (numberQuery == null)
			return -1;
		PreparedStatement stat = getPreparedStatement(numberQuery);
		ResultSet rs = stat.executeQuery();

		if (rs.next())
			return rs.getInt(1);

		return 0;
	}

}
