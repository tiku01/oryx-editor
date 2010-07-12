package de.hpi.visio.data;

import java.util.List;
import java.util.Map;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("VisioDocument")
public class VisioDocument {
	
	@Element("Pages")
	public Pages pages;
	
	@Element("Masters")
	public Masters masters;
	
	private String stencilSet;
	
	public Map<String, String> getMasterIdToNameMapping() {
		return masters.getMasterIdToNameMapping();
	}
	
	public List<Page> getPages() {
		return pages.getPages();
	}
	
	public Page getFirstPage() {
		return pages.getFirstPage();
	}
	
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
