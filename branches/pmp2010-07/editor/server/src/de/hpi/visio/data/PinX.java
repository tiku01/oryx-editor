package de.hpi.visio.data;

import org.xmappr.RootElement;
import org.xmappr.Text;

@RootElement
public class PinX {

	@Text
	public Double content;
	
	public Double getX() {
		return content;
	}

	public void setX(Double x) {
		content = x;
	}
	
}