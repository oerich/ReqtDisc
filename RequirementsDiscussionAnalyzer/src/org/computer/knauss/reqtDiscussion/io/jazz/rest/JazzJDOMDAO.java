package org.computer.knauss.reqtDiscussion.io.jazz.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.computer.knauss.reqtDiscussion.io.jazz.IJazzDAO;
import org.computer.knauss.reqtDiscussion.io.jazz.util.XPathHelper;
import org.computer.knauss.reqtDiscussion.io.jazz.util.ui.DialogBasedJazzAccessConfiguration;
import org.computer.knauss.reqtDiscussion.io.sql.DAOException;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.JDOMException;

public class JazzJDOMDAO implements IJazzDAO {

	private static final String TEN_STORIES = "?oslc_cm.query=dc:type=\"com.ibm.team.apt.workItemType.story\"&oslc_cm.pageSize=10";

	private XPathHelper xpathHelper;
	private IWebConnector webConnector;
	private int limit = 10;
	private String selectedProjectArea;
	private List<Object> projectAreaList;

	public JazzJDOMDAO(IJazzAccessConfiguration config) throws IOException {
		this(new FormBasedAuthenticatedConnector(config));
	}

	JazzJDOMDAO(IWebConnector connector) {
		this.xpathHelper = new XPathHelper();
		this.webConnector = connector;
	}

	void setWebConnector(IWebConnector connector) {
		this.webConnector = connector;
	}

	IWebConnector getWebConnector() {
		return this.webConnector;
	}

	public void readDocument(InputStream documentStream) {
		try {
			this.xpathHelper.setDocument(documentStream);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getChildAttribute(String childName, String attributeName) {
		return getAttributeValue(getChild(childName), attributeName);
	}

	private Object getChild(String name) {

		List<Object> selection = this.xpathHelper.select(name);
		if (selection.size() == 0)
			return null;
		return selection.get(0);

	}

	private String getAttributeValue(Object context, String attributeName) {
		List<Object> selection = this.xpathHelper.select(context, "@"
				+ attributeName);
		if (selection.size() == 0)
			return null;
		return selection.get(0).toString();
	}

	@Override
	public String[] getProjectAreas() throws Exception {
		List<Object> projectAreaElements = getProjectAreaList();

		String[] ret = new String[projectAreaElements.size()];
		for (int i = 0; i < ret.length; i++) {
			Element e = (Element) projectAreaElements.get(i);
			ret[i] = e.getChild("title").getValue();
			// ret[i] = ((Element) this.xpathHelper.select(e, "/title").get(0))
			// .getValue();
		}
		return ret;
	}

	private List<Object> getProjectAreaList() throws Exception, JDOMException,
			IOException {
		if (this.projectAreaList != null) {
			return this.projectAreaList;
		}
		// 1. get the rootservices and extract the catalog-URI
		HttpResponse response = this.webConnector
				.performHTTPSRequestXML("https://jazz.net/jazz/rootservices");
		this.xpathHelper.setDocument(response.getEntity().getContent());
		String catalogURI = ((Attribute) this.xpathHelper.select(
				"//cmServiceProviders/@resource").get(0)).getValue();
		response.getEntity().getContent().close();

		// 2. get the catalog and extract the project areas
		HttpResponse response2 = this.webConnector
				.performHTTPSRequestXML(catalogURI);
		this.xpathHelper.setDocument(response2.getEntity().getContent());
		this.projectAreaList = this.xpathHelper.select("//ServiceProvider");
		return this.projectAreaList;
	}

	@Override
	public void setProjectArea(String projArea) {
		this.selectedProjectArea = projArea;
	}

	@Override
	public void setLimit(int limit) {
		if (limit < 0)
			this.limit = 0;
		else
			this.limit = limit;
	}

	@Override
	public int getLimit() {
		return this.limit;
	}

	@Override
	public synchronized String[] getWorkitemsForType(String type, boolean deep)
			throws JDOMException, IOException, Exception {
		// 0. check if project area is set
		if (this.selectedProjectArea == null)
			throw new IllegalArgumentException("Select a project area first!");

		// 1. get the services.xml for project area and obtain the query URI
		String servicesURI = "";
		for (Object o : getProjectAreaList()) {
			Element e = ((Element) o);
			if (this.selectedProjectArea.equals(e.getChild("title").getValue())) {
				servicesURI = e.getChild("services").getAttribute("resource")
						.getValue();
				// System.out.println(servicesURI);
			}
		}
		HttpResponse r = this.webConnector.performHTTPSRequestXML(servicesURI);
		this.xpathHelper.setDocument(r.getEntity().getContent());
		String simpleQueryURI = ((Element) this.xpathHelper.select(
				"//simpleQuery/url").get(0)).getValue();
		simpleQueryURI = simpleQueryURI.trim()
				+ URLEncoder.encode(TEN_STORIES, "UTF-8");
		System.out.println("Query URL: "
				+ URLDecoder.decode(simpleQueryURI, "UTF-8"));
		// 2. create a simple query according to
		// http://open-services.net/bin/view/Main/CmQuerySyntaxV1
		r = this.webConnector.performHTTPSRequestXML(simpleQueryURI);

		// 3. make sense of the result and return it
		this.xpathHelper.setDocument(r.getEntity().getContent());
		List<Object> crElements = this.xpathHelper.select("//ChangeRequest");
		String[] ret = new String[crElements.size()];
		for (int i = 0; i < ret.length; i++) {
			Element e = (Element) crElements.get(i);
			// System.out.println(e.getChild("type").getAttribute("resource").getValue());
			ret[i] = convertElementToString(e);
		}

		return ret;
	}

	String convertElementToString(Element e) {
		return convertElementToString("", e);
	}

	private String convertElementToString(String offset, Element e) {
		StringBuffer ret = new StringBuffer(offset);
		ret.append("<");
		ret.append(e.getName());
		for (Attribute a : e.getAttributes()) {
			ret.append(' ');
			ret.append(a.getName());
			ret.append("=\"");
			ret.append(a.getValue());
			ret.append('"');
		}
		List<Element> childs = e.getChildren();
		if (childs.size() == 0)
			ret.append("/>");
		else {
			ret.append(">\n");
			for (Element c : childs) {
				ret.append(convertElementToString(offset + "  ", c));
				ret.append('\n');
			}
			ret.append(offset);
			ret.append("</");
			ret.append(e.getName());
			ret.append(">");
		}

		return ret.toString();
	}

	public static void main(String[] args) throws JDOMException, IOException,
			Exception {
		JazzJDOMDAO dao = new JazzJDOMDAO(
				new DialogBasedJazzAccessConfiguration());
		dao.setProjectArea("Rational Team Concert");

		for (String element : dao.getWorkitemsForType("any", false)) {
			System.out.println(element);
		}
	}

	@Override
	public void configure(Properties p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DiscussionEvent[] getDiscussionEventsOfDiscussion(int discussionId)
			throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DiscussionEvent getDiscussionEvent(int id) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void storeDiscussionEvent(DiscussionEvent de) throws DAOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void storeDiscussionEvents(DiscussionEvent[] des)
			throws DAOException {
		// TODO Auto-generated method stub
		
	}
}
