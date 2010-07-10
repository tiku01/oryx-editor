package de.hpi.visio.data;

import org.xmappr.RootElement;
import org.xmappr.Text;

@RootElement("Text")
public class Label {
	
	@Text
	public String text;
	
	public Label() {
		text = "";
	}
	
	public String getLabel() {
		return this.text;
	}
	
	public void setLabel(String label) {
		text = label;
	}

}
