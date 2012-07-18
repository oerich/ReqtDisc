package org.computer.knauss.reqtDiscussion.io.jazz.rest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.computer.knauss.reqtDiscussion.io.DAOException;
import org.computer.knauss.reqtDiscussion.io.IDiscussionDAO;
import org.computer.knauss.reqtDiscussion.io.IDiscussionEventDAO;
import org.computer.knauss.reqtDiscussion.io.Util;
import org.computer.knauss.reqtDiscussion.io.jazz.IJazzDAO;
import org.computer.knauss.reqtDiscussion.io.jazz.util.XPathHelper;
import org.computer.knauss.reqtDiscussion.io.jazz.util.ui.DialogBasedJazzAccessConfiguration;
import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.DiscussionFactory;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.JDOMException;

public class JazzJDOMDAO implements IJazzDAO, IDiscussionEventDAO,
		IDiscussionDAO {

	private static final String TEN_STORIES = "?oslc_cm.query=dc%3Atype=%22com.ibm.team.apt.workItemType.story%22&oslc_cm.pageSize=10";

	private XPathHelper xpathHelper;
	private IWebConnector webConnector;
	private int limit = 10;
	private String selectedProjectArea;
	private List<Object> projectAreaList;

	private XPathHelper changeRequestsXML;

	private XPathHelper commentsXML;

	private int discussionIndex;

	public JazzJDOMDAO(IJazzAccessConfiguration config) throws DAOException {
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
		if (this.selectedProjectArea == null) {
			StringBuffer b = new StringBuffer();
			for (String s : getProjectAreas()) {
				b.append("<");
				b.append(s);
				b.append(">,");
			}
			throw new IllegalArgumentException(
					"Select a project area first! Available: "
							+ b.toString().substring(0, b.length() - 1));
		}
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
		simpleQueryURI = simpleQueryURI.trim() + TEN_STORIES;
		// System.out.println("Query URL: "
		// + URLDecoder.decode(simpleQueryURI, "UTF-8"));
		// 2. create a simple query according to
		// http://open-services.net/bin/view/Main/CmQuerySyntaxV1
		r = this.webConnector.performHTTPSRequestXML(simpleQueryURI);

		// 3. make sense of the result and return it
		this.changeRequestsXML = new XPathHelper();
		this.changeRequestsXML.setDocument(r.getEntity().getContent());
		List<Object> crElements = this.changeRequestsXML
				.select("//ChangeRequest");
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
		Properties p = new Properties();
		p.load(new FileInputStream("jazz-properties.txt"));

		DialogBasedJazzAccessConfiguration config = new DialogBasedJazzAccessConfiguration();
		config.configure(p);
		JazzJDOMDAO dao = new JazzJDOMDAO(config);
		dao.setProjectArea("Rational Team Concert");

		for (String element : dao.getWorkitemsForType("any", false)) {
			System.out.println(element);
		}
	}

	@Override
	public void configure(Properties p) {
		this.webConnector.configure(p);
	}

	@Override
	public DiscussionEvent[] getDiscussionEventsOfDiscussion(int discussionId)
			throws DAOException {
		try {
			// 1. make sure that the change requests are already loaded.
			// Otherwise query them.
			if (this.changeRequestsXML == null) {
				getWorkitemsForType(null, false);
			}

			// 2. identify the change request with the discussion id and get the
			// comment-url
			String commentURL = ((Attribute) this.changeRequestsXML.select(
					"//ChangeRequest[identifier=" + discussionId
							+ "]/comments/@collref").get(0)).getValue();
			// System.out.println(commentURL);

			// 3. query the comments and transform them into DiscussionEvents
			if (this.commentsXML == null) {
				this.commentsXML = new XPathHelper();
			}
			this.commentsXML.setDocument(this.webConnector
					.performHTTPSRequestXML(commentURL).getEntity()
					.getContent());

			List<Object> comments = this.commentsXML.select("//Comment");
			DiscussionEvent[] ret = new DiscussionEvent[comments.size()];
			for (int i = 0; i < ret.length; i++) {
				ret[i] = new DiscussionEvent();
				ret[i].setDiscussionID(discussionId);
				ret[i].setCreator(((Attribute) this.commentsXML.select(
						comments.get(i), ".//creator/@resource").get(0))
						.getValue());
				ret[i].setContent(((Element) this.commentsXML.select(
						comments.get(i), ".//description").get(0)).getValue());
				String dateString = ((Element) this.commentsXML.select(
						comments.get(i), ".//created").get(0)).getValue();
				ret[i].setCreationDate(Util.parseDate(dateString));
			}

			return ret;

		} catch (JDOMException e) {
			throw new DAOException("Could not parse XML [" + e.getMessage()
					+ "]", e);
		} catch (IOException e) {
			throw new DAOException(
					"Could read stream [" + e.getMessage() + "]", e);
		} catch (Exception e) {
			throw new DAOException("General error reading discussion events ["
					+ e.getMessage() + "]", e);
		}
	}

	@Override
	public DiscussionEvent getDiscussionEvent(int id) throws DAOException {
		// that is not true, the have a resource-identifier. I am jsut not sure
		// how to deal with that
		throw new UnsupportedOperationException(
				"Not supported: Jazz does not have comment ids.");
	}

	@Override
	public void storeDiscussionEvent(DiscussionEvent de) throws DAOException {
		throw new UnsupportedOperationException("Read only DAO!");
	}

	@Override
	public void storeDiscussionEvents(DiscussionEvent[] des)
			throws DAOException {
		throw new UnsupportedOperationException("Read only DAO!");
	}

	@Override
	public Discussion getDiscussion(int discussionID) throws DAOException {
		try {
			// 1. make sure that the changerequests are already loaded.
			// Otherwise
			// query them.
			if (this.changeRequestsXML == null) {
				getWorkitemsForType(null, false);
			}

			// 2. identify the changerequest with the discussionid and get the
			// comment-url
			Object discussionElement = this.changeRequestsXML.select(
					"//ChangeRequest[identifier=" + discussionID + "]").get(0);

			Discussion d = createDiscussion(discussionElement);
			d.addComments(getDiscussionEventsOfDiscussion(d.getID()));

			return d;
		} catch (JDOMException e) {
			throw new DAOException("Could not parse XML [" + e.getMessage()
					+ "]", e);
		} catch (IOException e) {
			throw new DAOException(
					"Could read stream [" + e.getMessage() + "]", e);
		} catch (Exception e) {
			throw new DAOException("General error reading discussion events ["
					+ e.getMessage() + "]", e);
		}
	}

	private Discussion createDiscussion(Object discussionElement)
			throws DAOException {
		Discussion d = DiscussionFactory.getInstance().getDiscussion(
				Integer.parseInt(((Element) this.xpathHelper.select(
						discussionElement, "identifier").get(0)).getValue()));

		d.setDateCreated(Util.parseDate(((Element) this.xpathHelper.select(
				discussionElement, "created").get(0)).getValue()));
		d.setCreator(((Attribute) this.xpathHelper.select(discussionElement,
				"creator/@resource").get(0)).getValue());
		d.setDescription(((Element) this.xpathHelper.select(discussionElement,
				"description").get(0)).getValue());
		d.setSummary(((Element) this.xpathHelper.select(discussionElement,
				"title").get(0)).getValue());
		d.setType(((Attribute) this.xpathHelper.select(discussionElement,
				"type/@resource").get(0)).getValue());

		return d;
	}

	@Override
	public Discussion getNextDiscussion() throws DAOException {
		Discussion ret = null;

		if (this.discussionIndex < getDiscussions().length)
			ret = getDiscussions()[this.discussionIndex];
		else {
			// TODO
			// otherwise: get the link for the next results.
			// get the next discussions
			this.discussionIndex = this.discussionIndex
					- getDiscussions().length;
			ret = getNextDiscussion();
		}
		this.discussionIndex++;
		return ret;
	}

	@Override
	public Discussion[] getDiscussions() throws DAOException {
		try {
			// 1. make sure that the changerequests are already loaded.
			// Otherwise
			// query them.
			if (this.changeRequestsXML == null) {
				getWorkitemsForType(null, false);
			}

			// 2. identify the changerequest with the discussionid and get the
			// comment-url
			List<Object> discussionElements = this.changeRequestsXML
					.select("//ChangeRequest");

			Discussion[] ret = new Discussion[discussionElements.size()];
			for (int i = 0; i < ret.length; i++) {
				ret[i] = createDiscussion(discussionElements.get(i));
			}

			for (Discussion d : ret)
				d.addComments(getDiscussionEventsOfDiscussion(d.getID()));

			return ret;
		} catch (JDOMException e) {
			throw new DAOException("Could not parse XML [" + e.getMessage()
					+ "]", e);
		} catch (IOException e) {
			throw new DAOException(
					"Could read stream [" + e.getMessage() + "]", e);
		} catch (Exception e) {
			throw new DAOException("General error reading discussion events ["
					+ e.getMessage() + "]", e);
		}
	}

	@Override
	public void storeDiscussion(Discussion d) {
		throw new UnsupportedOperationException("Read only DAO!");
	}

	@Override
	public void storeDiscussions(Discussion[] ds) {
		throw new UnsupportedOperationException("Read only DAO!");
	}

}
