package de.hpi.bpmn2xpdl;

import org.xmappr.RootElement;
import org.xmappr.Text;

@RootElement("ModificationDate")
public class XPDLModificationDate extends XMLConvertable {

	@Text
	protected String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
