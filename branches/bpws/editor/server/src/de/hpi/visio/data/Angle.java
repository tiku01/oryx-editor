package de.hpi.visio.data;

import org.xmappr.RootElement;
import org.xmappr.Text;

/**
 * Class for xmappr - xml to java mapping. Angle: Some stencils come with an
 * angle of about 90° and therefore height and width are swapped, so that this
 * has to be corrected (e.g., pool header).
 * 
 * @author Thamsen
 */
@RootElement
public class Angle {

	@Text
	public Double content;

	public Double getAngle() {
		return content;
	}

	public void setAngle(Double angle) {
		content = angle;
	}

}
