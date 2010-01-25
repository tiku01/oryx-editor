package de.hpi.bpmn2xpdl;

import org.json.JSONObject;
import org.xmappr.Text;

public class XPDLTriggerConditional  extends XMLConvertable {

	@Text
	protected String condition;

	public String getCondition() {
		return condition;
	}
	
	public void readJSONcondition(JSONObject modelElement) {
		setCondition(modelElement.optString("condition"));
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}
}
