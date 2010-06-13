package de.hpi.visio.data;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("PageProps")
public class PageProperties {

	@Element("PageWidth")
	public PageWidth width;
	
	@Element("PageHeight")
	public PageHeight height;
	
	public Double getWidth() {
		return width.getWidth();
	}
	
	public void setWidth(Double width) {
		this.width.setWidth(width);
	}
	
	public Double getHeight() {
		return height.getHeight();
	}
	
	public void setHeight(Double height) {
		this.height.setHeight(height);
	}
	

	
}
