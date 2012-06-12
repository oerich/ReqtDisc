package org.computer.knauss.reqtDiscussion.scripts;

import java.util.Properties;

import org.computer.knauss.reqtDiscussion.io.csv.CSVDiscussionEventDAO;
import org.computer.knauss.reqtDiscussion.io.sql.DAOException;
import org.computer.knauss.reqtDiscussion.io.sql.SQLDiscussionEventDAO;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;


public class PushCSVToMySQL {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Properties p = new Properties();
		p.setProperty(SQLDiscussionEventDAO.PROP_URL,
				"jdbc:mysql://db.disi.unitn.it/developers");
		p.setProperty(SQLDiscussionEventDAO.PROP_USER, "mist");
		p.setProperty(SQLDiscussionEventDAO.PROP_PWD, "mist");
		p.setProperty(CSVDiscussionEventDAO.PROP_FILENAME,
				"testfiles/example-workitemcomments.csv");
		p.setProperty(CSVDiscussionEventDAO.PROP_ID_COL,
				"4");
		p.setProperty(CSVDiscussionEventDAO.PROP_DISC_ID_COL,
				"1");
		p.setProperty(CSVDiscussionEventDAO.PROP_CONTENT_COL,
				"0");
		p.setProperty(CSVDiscussionEventDAO.PROP_CDATE_COL,
				"2");
		p.setProperty(CSVDiscussionEventDAO.PROP_CREATOR_COL,
				"3");
		p.setProperty(CSVDiscussionEventDAO.PROP_START_ROW, "1");
		
		CSVDiscussionEventDAO csvDAO = new CSVDiscussionEventDAO();
		SQLDiscussionEventDAO sqlDAO = new SQLDiscussionEventDAO();
		csvDAO.configure(p);
		sqlDAO.configure(p);
		
		try {
			DiscussionEvent[] des = csvDAO.getDiscussionEventsOfDiscussion(32654);
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
