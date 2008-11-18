package org.mesh4j.sync.adapters.feed.atom;

import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.utils.DateHelper;

public class AtomSyndicationFormat implements ISyndicationFormat {

	private static final String NAME = "atom10";
	private static final String ATOM_ELEMENT_FEED = "feed";
	private static final String ATOM_ELEMENT_ENTRY = "entry";
	private static final String RSS_ELEMENT_TITLE = "title";
	private static final String RSS_ELEMENT_SUBTITLE = "subtitle";
	private static final String RSS_ELEMENT_LINK = "link";
	
	public static final AtomSyndicationFormat INSTANCE = new AtomSyndicationFormat();

	@SuppressWarnings("unchecked")
	@Override
	public List<Element> getRootElements(Element root) {
		return root.elements();
	}

	@Override
	public boolean isFeedItem(Element element) {
		return ATOM_ELEMENT_ENTRY.equals(element.getName());
	}

	@Override
	public Date parseDate(String dateAsString) {
		return dateAsString == null ? null : DateHelper.parseW3CDateTime(dateAsString);
	}

	@Override
	public String formatDate(Date date) {
		return date == null ? "" : DateHelper.formatW3CDateTime(date);
	}

	@Override
	public Element addRootElement(Document document) {
		Element rootElement = document.addElement(ATOM_ELEMENT_FEED);
		rootElement.addNamespace("", "http://www.w3.org/2005/Atom");
		rootElement.add(new Namespace(SX_PREFIX, NAMESPACE));
		return rootElement;
	}

	@Override
	public Element addFeedItemElement(Element root) {
		Element item = root.addElement(ATOM_ELEMENT_ENTRY);
		return item;
	}

	public static boolean isAtom(Document document) {
		return "feed".equals(document.getRootElement().getName());
	}

	public static boolean isAtom(String protocol) {
		return "feed".equals(protocol);
	}
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void addFeedInformation(Element rootElement, String title, String description, String link) {
		
		Element titleElement = rootElement.addElement(RSS_ELEMENT_TITLE);
		titleElement.setText(title);
		
		Element subtitleElement = rootElement.addElement(RSS_ELEMENT_SUBTITLE);
		subtitleElement.setText(description);
		
		Element linkElement = rootElement.addElement(RSS_ELEMENT_LINK);
		linkElement.setText(link);

	}

}
