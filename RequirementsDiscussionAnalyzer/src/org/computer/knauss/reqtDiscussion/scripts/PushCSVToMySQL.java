package org.computer.knauss.reqtDiscussion.scripts;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.computer.knauss.reqtDiscussion.io.DAOException;
import org.computer.knauss.reqtDiscussion.io.IDiscussionEventDAO;
import org.computer.knauss.reqtDiscussion.io.csv.CSVDiscussionEventDAO;
import org.computer.knauss.reqtDiscussion.io.sql.SQLDAOManager;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;

public class PushCSVToMySQL {

	/**
	 * @param args
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws DAOException 
	 */
	public static void main(String[] args) throws FileNotFoundException,
			IOException, DAOException {

		Properties p = new Properties();

		p.setProperty(CSVDiscussionEventDAO.PROP_FILENAME,
				"testfiles/example-workitemcomments.csv");
		p.setProperty(CSVDiscussionEventDAO.PROP_ID_COL, "4");
		p.setProperty(CSVDiscussionEventDAO.PROP_DISC_ID_COL, "1");
		p.setProperty(CSVDiscussionEventDAO.PROP_CONTENT_COL, "0");
		p.setProperty(CSVDiscussionEventDAO.PROP_CDATE_COL, "2");
		p.setProperty(CSVDiscussionEventDAO.PROP_CREATOR_COL, "3");
		p.setProperty(CSVDiscussionEventDAO.PROP_START_ROW, "1");

		CSVDiscussionEventDAO csvDAO = new CSVDiscussionEventDAO();
		SQLDAOManager sdm = new SQLDAOManager("trento-mysql-properties.txt", "mysql-default-schema-queries.txt");
		IDiscussionEventDAO sqlDAO = sdm.getDiscussionEventDAO();
		csvDAO.configure(p);

		try {
			DiscussionEvent[] des = csvDAO
					.getDiscussionEventsOfDiscussion(32654);
			sqlDAO.storeDiscussionEvents(des);
			des = csvDAO.getDiscussionEventsOfDiscussion(44088);
			sqlDAO.storeDiscussionEvents(des);
			des = csvDAO.getDiscussionEventsOfDiscussion(46097);
			sqlDAO.storeDiscussionEvents(des);
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
