package org.computer.knauss.reqtDiscussion.io;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.Iteration;

public interface IIterationDAO {

	public Iteration getIterationForDiscussion(Discussion d) throws DAOException;
	
	public void storeIteration(Iteration it) throws DAOException;
	
}
