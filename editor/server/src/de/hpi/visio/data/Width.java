package de.hpi.visio.data;

import org.xmappr.RootElement;
import org.xmappr.Text;

/**
 * Class for xmappr - xml to java mapping.
 * 
 * @author Thamsen
 */
@RootElement
public class Width {

	@Text
	public Double content;

	public Double getWidth() {
		return content;
	}

	public void setWidth(Double width) {
		content = width;
	}

}