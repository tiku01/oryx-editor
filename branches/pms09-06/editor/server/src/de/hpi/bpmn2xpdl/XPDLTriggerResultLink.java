package de.hpi.bpmn2xpdl;

import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Text;

public class XPDLTriggerResultLink extends XMLConvertable {

	@Attribute("CatchThrow")
	protected String catchThrow;
	@Text
	protected String link;
	
	public String getCatchThrow() {
		return catchThrow;
	}
	
	public String getLink() {
		return link;
	}
	
	public void readJSONlinkid(JSONObject modelElement) {
		setLink(modelElement.optString("linkid"));
	}
	
	public void setCatchThrow(String catchThrow) {
		this.catchThrow = catchThrow;
	}
	
	public void setLink(String linkValue) {
		link = linkValue;
	}
}
