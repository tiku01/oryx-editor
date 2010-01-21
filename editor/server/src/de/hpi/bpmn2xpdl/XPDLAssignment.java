package de.hpi.bpmn2xpdl;

import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class XPDLAssignment extends XMLConvertable {
	
	protected String assigntime;
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:Assignment", XPDLAssignment.class);
		
		xstream.useAttributeFor(XPDLAssignment.class, "assigntime");
		xstream.aliasField("AssignTime", XPDLAssignment.class, "assigntime");
	}
	
	public String getAssignTime() {
		return assigntime;
	}
	
	public void readJSONassigntime(JSONObject modelElement) {
		setAssignTime(modelElement.optString("assigntime"));
	}
	
	public void setAssignTime(String time) {
		assigntime = time;
	}
}
