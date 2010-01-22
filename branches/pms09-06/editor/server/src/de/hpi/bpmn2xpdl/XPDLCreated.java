package de.hpi.bpmn2xpdl;

import org.xmappr.RootElement;
import org.xmappr.Text;

@RootElement("Created")
public class XPDLCreated extends XMLConvertable {

	@Text
	protected String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
