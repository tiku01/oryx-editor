package de.hpi.visio.data;

import java.util.ArrayList;
import java.util.List;

import org.xmappr.Element;
import org.xmappr.RootElement;

/**
 * Class for xmappr - xml to java mapping.
 * 
 * @author Thamsen
 */
@RootElement("Pages")
public class Pages {

	@Element("Page")
	public List<Page> pages;

	public List<Page> getPages() {
		return pages;
	}

	public Page getFirstPage() {
		return pages.get(0);
	}

	public void setFirstAndOnlyPage(Page onlyPage) {
		pages = new ArrayList<Page>();
		pages.add(onlyPage);
	}

}
