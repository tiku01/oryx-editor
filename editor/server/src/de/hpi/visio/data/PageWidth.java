package de.hpi.visio.data;

import org.xmappr.RootElement;
import org.xmappr.Text;

@RootElement("PageWidth")
public class PageWidth {
	
	@Text
	public Double content;
	
	public Double getWidth() {
		return content;
	}
	
	public void setWidth(Double width) {
		content = width;
	}

}
