package org.computer.knauss.reqtDiscussion.io.jazz;

import java.io.IOException;

import org.computer.knauss.reqtDiscussion.io.IDiscussionDAO;
import org.computer.knauss.reqtDiscussion.io.IDiscussionEventDAO;
import org.jdom2.JDOMException;

public interface IJazzDAO extends IDiscussionDAO, IDiscussionEventDAO {

	public static final String JAZZ_USR = "jazz.usr";
	public static final String JAZZ_PWD = "jazz.pwd";
	public static final String JAZZ_AUT = "jazz.aut";
	public static final String JAZZ_URL = "jazz.url";

	/**
	 * Workitems are organized in project areas and we need to decide which ones
	 * we want to go for.
	 * 
	 * @return
	 * @throws Exception
	 */
	public String[] getProjectAreas() throws Exception;

	/**
	 * Workitems are organized in project areas and we need to decide which ones
	 * we want to go for.
	 */
	public void setProjectArea(String projArea);

	/**
	 * The OSLC interface seems to be kind of slow. Thus, we should set a limit.
	 * Defaults to 10.
	 * 
	 * @param limit
	 */
	public void setLimit(int limit);

	/**
	 * The OSLC interface seems to be kind of slow. Thus, we should set a limit.
	 * Defaults to 10.
	 */
	public int getLimit();

}
