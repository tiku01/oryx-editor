package de.hpi.visio.data;

import org.xmappr.RootElement;
import org.xmappr.Text;

/**
 * Class for xmappr - xml to java mapping.
 * 
 * @author Thamsen
 */
@RootElement("PageHeight")
public class PageHeight {

	@Text
	public Double content;

	public Double getHeight() {
		if (content == null)
			content = 0.0;
		return content;
	}

	public void setHeight(Double height) {
		content = height;
	}

}
