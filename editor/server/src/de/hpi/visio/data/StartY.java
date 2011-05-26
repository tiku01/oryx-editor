package de.hpi.visio.data;

import org.xmappr.RootElement;
import org.xmappr.Text;

/**
 * Class for xmappr - xml to java mapping.
 * 
 * @author Thamsen
 */
@RootElement("BeginY")
public class StartY {

	@Text
	public Double content;

	public Double getY() {
		return content;
	}

	public void setY(Double y) {
		content = y;
	}

}
