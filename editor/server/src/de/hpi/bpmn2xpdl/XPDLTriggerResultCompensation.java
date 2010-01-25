package de.hpi.bpmn2xpdl;

import org.json.JSONObject;
import org.xmappr.Attribute;

public class XPDLTriggerResultCompensation  extends XMLConvertable {

	@Attribute("AttributeId")
	protected String attributeId;
	@Attribute("CatchThrow")
	protected String catchThrow;

	public String getAttributeId() {
		return attributeId;
	}
	
	public String getCatchThrow() {
		return catchThrow;
	}
	
	public void readJSONactivity(JSONObject modelElement) {
		setAttributeId(modelElement.optString("activity"));
	}

	public void setAttributeId(String attributeId) {
		this.attributeId = attributeId;
	}
	
	public void setCatchThrow(String catchThrow) {
		this.catchThrow = catchThrow;
	}
}
