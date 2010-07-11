package de.hpi.visio.data;

import org.xmappr.RootElement;
import org.xmappr.Text;

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
