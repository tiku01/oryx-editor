package de.hpi.bpmn2xpdl;

import org.xmappr.RootElement;
import org.xmappr.Text;

@RootElement("XPDLVersion")
public class XPDLXPDLVersion extends XMLConvertable {

	@Text
	protected String content = "2.1";

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
