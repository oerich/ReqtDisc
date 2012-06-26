package org.computer.knauss.reqtDiscussion.scripts;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.computer.knauss.reqtDiscussion.io.DAOException;
import org.computer.knauss.reqtDiscussion.io.IDiscussionDAO;
import org.computer.knauss.reqtDiscussion.io.IDiscussionEventDAO;
import org.computer.knauss.reqtDiscussion.io.jazz.rest.JazzJDOMDAO;
import org.computer.knauss.reqtDiscussion.io.jazz.util.ui.DialogBasedJazzAccessConfiguration;
import org.computer.knauss.reqtDiscussion.io.sql.psql.PSQLDiscussionDAO;
import org.computer.knauss.reqtDiscussion.io.sql.psql.PSQLDiscussionEventDAO;
import org.computer.knauss.reqtDiscussion.model.Discussion;

public class FetchJazzToPSQL {

	/**
	 * @param args
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws DAOException
	 */
	public static void main(String[] args) throws FileNotFoundException,
			IOException, DAOException {
		Properties p = new Properties();
		p.load(new FileInputStream("jazz-properties.txt"));

		DialogBasedJazzAccessConfiguration config = new DialogBasedJazzAccessConfiguration();
		config.configure(p);
		IDiscussionDAO jazzDiscussions = new JazzJDOMDAO(config);
		((JazzJDOMDAO) jazzDiscussions)
				.setProjectArea("Rational Team Concert");
		IDiscussionDAO psqlDiscussions = new PSQLDiscussionDAO();

		IDiscussionEventDAO jazzDiscussionEvents = (IDiscussionEventDAO) jazzDiscussions;
		IDiscussionEventDAO psqlDiscussionEvents = new PSQLDiscussionEventDAO();

		for (Discussion d : jazzDiscussions.getDiscussions()) {
			psqlDiscussions.storeDiscussion(d);
			psqlDiscussionEvents.storeDiscussionEvents(jazzDiscussionEvents
					.getDiscussionEventsOfDiscussion(d.getID()));
		}
	}

}
