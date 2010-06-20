package de.hpi.visio.data;

import org.xmappr.Element;
import org.xmappr.RootElement;
import org.xmappr.Text;

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
