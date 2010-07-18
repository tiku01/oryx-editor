package de.hpi.visio.data;

import java.util.List;
import java.util.Map;

import org.xmappr.Element;
import org.xmappr.RootElement;

/**
 * Class for xmappr - xml to java mapping. VisioDocument is the root element of
 * a vdx-xml. Contains of all master shapes (all shapes are linked by id to a
 * master) and all pages of the visio .vdx.
 * 
 * @author Thamsen
 */
@RootElement("VisioDocument")
public class VisioDocument {

	@Element("Pages")
	public Pages pages;

	@Element("Masters")
	public Masters masters;

	private String stencilSet;

	/**
	 * @return a map to resolve shape's master id to the given master's
	 *         normalized nameU.
	 */
	public Map<String, String> getMasterIdToNameMapping() {
		return masters.getMasterIdToNameMapping();
	}

	public List<Page> getPages() {
		return pages.getPages();
	}

	public Page getFirstPage() {
		return pages.getFirstPage();
	}

	/**
	 * Used to merge all pages into one page, if the document has more than one.
	 * This is done because the import is directly into the editor.
	 */
	public void setFirstAndOnlyPage(Page onlyPage) {
		pages.setFirstAndOnlyPage(onlyPage);
	}

	public void setStencilSet(String stencilSet) {
		this.stencilSet = stencilSet;
	}

	public String getStencilSet() {
		return stencilSet;
	}

}
