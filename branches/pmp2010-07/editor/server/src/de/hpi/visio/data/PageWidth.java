package de.hpi.visio.data;

import org.xmappr.RootElement;
import org.xmappr.Text;

/**
 * Class for xmappr - xml to java mapping.
 * 
 * @author Thamsen
 */
@RootElement("PageWidth")
public class PageWidth {

	@Text
	public Double content;

	public Double getWidth() {
		if (content == null)
			content = 0.0;
		return content;
	}

	public void setWidth(Double width) {
		content = width;
	}

}
