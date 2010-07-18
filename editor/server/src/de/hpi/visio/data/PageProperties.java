package de.hpi.visio.data;

import org.xmappr.Element;
import org.xmappr.RootElement;

/**
 * Class for xmappr - xml to java mapping.
 * 
 * @author Thamsen
 */
@RootElement("PageProps")
public class PageProperties {

	@Element("PageWidth")
	public PageWidth width;

	@Element("PageHeight")
	public PageHeight height;

	public Double getWidth() {
		if (this.width == null)
			this.width = new PageWidth();
		return width.getWidth();
	}

	public void setWidth(Double width) {
		if (this.width == null)
			this.width = new PageWidth();
		this.width.setWidth(width);
	}

	public Double getHeight() {
		if (this.height == null)
			this.height = new PageHeight();
		return height.getHeight();
	}

	public void setHeight(Double height) {
		if (this.height == null)
			this.height = new PageHeight();
		this.height.setHeight(height);
	}

}
