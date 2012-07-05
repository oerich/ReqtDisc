package org.computer.knauss.reqtDiscussion.io.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.computer.knauss.reqtDiscussion.io.DAOException;
import org.computer.knauss.reqtDiscussion.io.IDiscussionEventClassificationDAO;
import org.computer.knauss.reqtDiscussion.model.DiscussionEventClassification;

/**
 * Assume the following schema: discussionEventClassification(discussionEventId,
 * classifiedBy, classification, confidence, comment)
 * 
 * @author eknauss
 * 
 */
public class SQLDiscussionEventClassificationDAO extends AbstractSQLDAO
		implements IDiscussionEventClassificationDAO {

	public static final String DISCUSSION_EVENT_CLASSIFICATION_TABLE_NAME = "DISCUSSION_EVENT_CLASSIFICATION_TABLE_NAME";
	public static final String SELECT_DISCUSSION_EVENT_CLASSIFICATION_BY_DISCUSSION_EVENT_ID = "SELECT_DISCUSSION_EVENT_CLASSIFICATION_BY_DISCUSSION_EVENT_ID";
	public static final String INSERT_DISCUSSION_EVENT_CLASSIFICATION = "INSERT_DISCUSSION_EVENT_CLASSIFICATION";
	public static final String UPDATE_DISCUSSION_EVENT_CLASSIFICATION = "UPDATE_DISCUSSION_EVENT_CLASSIFICATION";
	public static final String EXISTS_DISCUSSION_EVENT_CLASSIFICATION = "EXISTS_DISCUSSION_EVENT_CLASSIFICATION";
	public static final String CREATE_DISCUSSION_EVENT_CLASSIFICATION_TABLE = "CREATE_DISCUSSION_EVENT_CLASSIFICATION_TABLE";
	public static final String DROP_DISCUSSION_EVENT_CLASSIFICATION_TABLE = "DROP_DISCUSSION_EVENT_CLASSIFICATION_TABLE";

	@Override
	public DiscussionEventClassification[] getClassificationsForDiscussionEvent(
			int discEventID) throws DAOException {
		try {
			if (!existsTable(this.properties
					.getProperty(DISCUSSION_EVENT_CLASSIFICATION_TABLE_NAME)))
				return new DiscussionEventClassification[0];
			PreparedStatement stat = getPreparedStatement(this.properties
					.getProperty(SELECT_DISCUSSION_EVENT_CLASSIFICATION_BY_DISCUSSION_EVENT_ID));
			List<DiscussionEventClassification> res = new LinkedList<DiscussionEventClassification>();
			stat.setInt(1, discEventID);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				DiscussionEventClassification dec = new DiscussionEventClassification();
				dec.setWorkitemcommentid(rs.getInt("discussionEventId"));
				dec.setClassifiedby(rs.getString("classifiedBy"));
				dec.setClassification(rs.getString("classification"));
				dec.setConfidence(rs.getDouble("confidence"));
				dec.setComment(rs.getString("comment"));
				res.add(dec);
			}
			return res.toArray(new DiscussionEventClassification[0]);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}

	@Override
	public void storeDiscussionEventClassification(
			DiscussionEventClassification classification) throws DAOException {
		try {
			if (!existsTable(this.properties
					.getProperty(DISCUSSION_EVENT_CLASSIFICATION_TABLE_NAME)))
				createSchema();
			PreparedStatement stat = getPreparedStatement(this.properties
					.getProperty(INSERT_DISCUSSION_EVENT_CLASSIFICATION));
			// TODO use update if this tuple already exists.
			stat.setInt(1, classification.getDiscussionEventID());
			stat.setString(2, classification.getClassifiedby());
			stat.setString(3, classification.getClassification());
			stat.setDouble(4, classification.getConfidence());
			stat.setString(5, classification.getComment());

			if (1 != stat.executeUpdate()) {
				// stat.close();
				// ConnectionManager.getInstance().closeConnection();
				throw new DAOException("INSERT should only affect one row");
			}
			// stat.close();
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			// ConnectionManager.getInstance().closeConnection();
		}
	}

	public void createSchema() throws SQLException {
		getPreparedStatement(
				this.properties
						.getProperty(CREATE_DISCUSSION_EVENT_CLASSIFICATION_TABLE))
				.executeUpdate();
		System.out
				.println("Created Table "
						+ this.properties
								.getProperty(DISCUSSION_EVENT_CLASSIFICATION_TABLE_NAME)
						+ ".");
	}

	public void dropSchema() throws SQLException {
		getPreparedStatement(
				this.properties
						.getProperty(DROP_DISCUSSION_EVENT_CLASSIFICATION_TABLE))
				.execute();
		System.out
				.println("Dropped Table "
						+ this.properties
								.getProperty(DISCUSSION_EVENT_CLASSIFICATION_TABLE_NAME)
						+ ".");
	}
}
