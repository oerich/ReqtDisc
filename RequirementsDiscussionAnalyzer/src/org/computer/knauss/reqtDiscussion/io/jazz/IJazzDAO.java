package org.computer.knauss.reqtDiscussion.io.jazz;

import java.io.IOException;

import org.computer.knauss.reqtDiscussion.io.IDiscussionEventDAO;
import org.jdom2.JDOMException;

public interface IJazzDAO extends IDiscussionEventDAO {

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

	/**
	 * For simplicity, we start to return workitems as xml fragments with discussion.
	 * @param type defaults to story
	 * @param deep should we follow links to subitems?
	 * @return workitems as xml fragments with discussion
	 * @throws Exception 
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public String[] getWorkitemsForType(String type, boolean deep) throws JDOMException, IOException, Exception;

}
