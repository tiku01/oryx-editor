package de.hpi.bpmn2xpdl;

import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.RootElement;

@RootElement("Assignment")
public class XPDLAssignment extends XMLConvertable {
	
	@Attribute("AssignTime")
	protected String assignTime;
	
	public String getAssignTime() {
		return assignTime;
	}
	
	public void readJSONassigntime(JSONObject modelElement) {
		setAssignTime(modelElement.optString("assigntime"));
	}
	
	public void setAssignTime(String time) {
		assignTime = time;
	}
}
