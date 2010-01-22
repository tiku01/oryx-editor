package de.hpi.bpmn2xpdl;

import org.xmappr.RootElement;
import org.xmappr.Text;

@RootElement("Vendor")
public class XPDLVendor extends XMLConvertable {

	@Text
	protected String content = "Hasso Plattner Institute";

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
