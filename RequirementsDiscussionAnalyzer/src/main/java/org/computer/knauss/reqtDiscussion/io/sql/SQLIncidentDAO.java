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
import org.computer.knauss.reqtDiscussion.io.IIncidentDAO;
import org.computer.knauss.reqtDiscussion.model.Incident;

public class SQLIncidentDAO extends AbstractSQLDAO implements IIncidentDAO {
	public static final String SELECT_INCIDENT_BY_DISCUSSION_ID = "SELECT_INCIDENT_BY_DISCUSSION_ID";
	public static final String CREATE_INCIDENT_TABLE = "CREATE_INCIDENT_TABLE";
	public static final String DROP_INCIDENT_TABLE = "DROP_INCIDENT_TABLE";
	public static final String INCIDENT_TABLE_NAME = "INCIDENT_TABLE_NAME";

	@Override
	public Incident[] getIncidentsForDiscussion(int discussionID)
			throws DAOException {
		try {
			// if (!existsTable(getConfiguration()
			// .getProperty(INCIDENT_TABLE_NAME)))
			// return new Incident[0];
			PreparedStatement stat = getPreparedStatement(getConfiguration()
					.getProperty(SELECT_INCIDENT_BY_DISCUSSION_ID));
			List<Incident> res = new LinkedList<Incident>();
			stat.setInt(1, discussionID);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				Incident de = new Incident();
				de.setName(rs.getString("field"));
				de.setDate(rs.getDate("modifydate"));
				de.setSummary(rs.getString("oldcontent") + " => "
						+ rs.getString("newcontent"));

				res.add(de);
			}
			return res.toArray(new Incident[0]);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}

	@Override
	public void storeIncidents(Incident[] incidents) throws DAOException {
		// TODO Auto-generated method stub

	}

	@Override
	protected Properties getDefaultProperties() {
		Properties p = new Properties();

		p.setProperty(SELECT_INCIDENT_BY_DISCUSSION_ID, "");
		p.setProperty(CREATE_INCIDENT_TABLE, "");
		p.setProperty(DROP_INCIDENT_TABLE, "");
		p.setProperty(INCIDENT_TABLE_NAME, "");

		return p;
	}

	@Override
	protected Map<String, String> getMandatoryPropertiesAndHints() {
		Map<String, String> ret = new HashMap<String, String>();

		ret.put(INCIDENT_TABLE_NAME,
				"Name of the table with important incidents");
		ret.put(SELECT_INCIDENT_BY_DISCUSSION_ID,
				"SQL statement that selects important incidents for a given discussion");

		return ret;
	}

}
