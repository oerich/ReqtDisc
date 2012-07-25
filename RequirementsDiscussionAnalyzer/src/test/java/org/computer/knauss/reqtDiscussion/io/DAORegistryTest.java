package org.computer.knauss.reqtDiscussion.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DAORegistryTest {

	@Test
	public void test() {
		DAORegistry dr = DAORegistry.getInstance();
		DAOManagerDummy mgr = new DAOManagerDummy();
		DAOManagerDummy mgr2 = new DAOManagerDummy();

		assertEquals(0, dr.availableDAOManagers().length);
		dr.register("test", mgr);
		assertEquals(1, dr.availableDAOManagers().length);
		assertEquals("test", dr.availableDAOManagers()[0]);
		assertEquals(mgr, dr.getDAOManager("test"));
		assertEquals(mgr, dr.getSelectedDAOManager());

		dr.register("test2", mgr2);
		assertEquals(2, dr.availableDAOManagers().length);
		assertEquals("test", dr.availableDAOManagers()[0]);
		assertEquals("test2", dr.availableDAOManagers()[1]);
		assertEquals(mgr, dr.getDAOManager("test"));
		assertEquals(mgr2, dr.getDAOManager("test2"));
		assertEquals(mgr, dr.getSelectedDAOManager());

		dr.selectDAOManager("test2");
		assertEquals(mgr2, dr.getDAOManager("test2"));
		assertEquals(mgr2, dr.getSelectedDAOManager());

		assertFalse(mgr.connectionClosed);
		assertFalse(mgr2.connectionClosed);
		dr.closeAllConnections();
		assertTrue(mgr.connectionClosed);
		assertTrue(mgr2.connectionClosed);
	}

	public class DAOManagerDummy implements IDAOManager {

		boolean connectionClosed = false;

		@Override
		public IDiscussionDAO getDiscussionDAO() throws DAOException {
			return null;
		}

		@Override
		public IDiscussionEventDAO getDiscussionEventDAO() throws DAOException {
			return null;
		}

		@Override
		public IDiscussionEventClassificationDAO getDiscussionEventClassificationDAO()
				throws DAOException {
			return null;
		}

		@Override
		public void closeAllConnections() {
			connectionClosed = true;
		}

		@Override
		public IIncidentDAO getIncidentDAO() throws DAOException {
			// TODO Auto-generated method stub
			return null;
		}

	}

}
