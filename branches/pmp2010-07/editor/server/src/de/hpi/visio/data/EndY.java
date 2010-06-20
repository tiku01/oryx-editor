package de.hpi.visio.data;

import org.xmappr.RootElement;
import org.xmappr.Text;

@RootElement("EndY")
public class EndY {
	
	@Text
	public Double content;
	
	public Double getY() {
		return content;
	}

	public void setY(Double y) {
		content = y;
	}


}
