package org.computer.knauss.reqtDiscussion.scripts;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.computer.knauss.reqtDiscussion.io.DAOException;
import org.computer.knauss.reqtDiscussion.io.csv.CSVDiscussionEventDAO;
import org.computer.knauss.reqtDiscussion.io.sql.mysql.MySQLDiscussionEventDAO;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;

public class PushCSVToMySQL {

	/**
	 * @param args
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException,
			IOException {

		Properties p = new Properties();
		// load url, username and password from a file that is not in github
		p.load(new FileInputStream("trento-mysql-properties.txt"));

		p.setProperty(CSVDiscussionEventDAO.PROP_FILENAME,
				"testfiles/example-workitemcomments.csv");
		p.setProperty(CSVDiscussionEventDAO.PROP_ID_COL, "4");
		p.setProperty(CSVDiscussionEventDAO.PROP_DISC_ID_COL, "1");
		p.setProperty(CSVDiscussionEventDAO.PROP_CONTENT_COL, "0");
		p.setProperty(CSVDiscussionEventDAO.PROP_CDATE_COL, "2");
		p.setProperty(CSVDiscussionEventDAO.PROP_CREATOR_COL, "3");
		p.setProperty(CSVDiscussionEventDAO.PROP_START_ROW, "1");

		CSVDiscussionEventDAO csvDAO = new CSVDiscussionEventDAO();
		MySQLDiscussionEventDAO sqlDAO = new MySQLDiscussionEventDAO();
		csvDAO.configure(p);
		sqlDAO.configure(p);

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
