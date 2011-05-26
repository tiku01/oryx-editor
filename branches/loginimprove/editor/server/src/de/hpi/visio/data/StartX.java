package de.hpi.visio.data;

import org.xmappr.RootElement;
import org.xmappr.Text;

/**
 * Class for xmappr - xml to java mapping.
 * 
 * @author Thamsen
 */
@RootElement("BeginX")
public class StartX {

	@Text
	public Double content;

	public Double getX() {
		return content;
	}

	public void setX(Double x) {
		content = x;
	}

}
