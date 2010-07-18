package de.hpi.visio.data;

import org.xmappr.RootElement;
import org.xmappr.Text;

/**
 * Class for xmappr - xml to java mapping.
 * 
 * @author Thamsen
 */
@RootElement("EndX")
public class EndX {

	@Text
	public Double content;

	public Double getX() {
		return content;
	}

	public void setX(Double x) {
		content = x;
	}

}
