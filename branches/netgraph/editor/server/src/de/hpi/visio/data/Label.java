package de.hpi.visio.data;

import org.xmappr.RootElement;
import org.xmappr.Text;

/**
 * Class for xmappr - xml to java mapping. Label: In oryx according to the
 * stencil this properties key is: title, name, condition, expression,...
 * 
 * @author Thamsen
 */
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
