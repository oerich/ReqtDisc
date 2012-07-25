package org.computer.knauss.reqtDiscussion.io;

import java.util.Properties;

import org.computer.knauss.reqtDiscussion.model.Incident;

public interface IIncidentDAO {

	public void configure(Properties p) throws DAOException;

	public Incident[] getIncidentsForDiscussion(int discussionID)
			throws DAOException;

	public void storeIncidents(Incident[] incidents) throws DAOException;

}
