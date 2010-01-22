package de.hpi.bpmn2xpdl;

import org.xmappr.RootElement;
import org.xmappr.Text;

@RootElement("Version")
public class XPDLVersion extends XMLConvertable {

	@Text
	protected String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
