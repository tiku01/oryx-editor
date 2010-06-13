package de.hpi.visio.data;

import org.xmappr.RootElement;
import org.xmappr.Text;

@RootElement
public class PinY {

	@Text
	public Double content;
	
	public Double getY() {
		return content;
	}
	
}