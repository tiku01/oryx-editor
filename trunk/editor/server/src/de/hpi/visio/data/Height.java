package de.hpi.visio.data;

import org.xmappr.RootElement;
import org.xmappr.Text;

/**
 * Class for xmappr - xml to java mapping.
 * 
 * @author Thamsen
 */
@RootElement
public class Height {

	@Text
	public Double content;

	public Double getHeight() {
		return content;
	}

	public void setHeight(Double height) {
		content = height;
	}

}