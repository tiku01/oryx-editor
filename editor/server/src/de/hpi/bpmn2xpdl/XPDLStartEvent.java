package de.hpi.bpmn2xpdl;

import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class XPDLStartEvent extends XMLConvertable {

	protected String trigger;
	protected String implementation;
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:StartEvent", XPDLStartEvent.class);
		
		xstream.useAttributeFor(XPDLStartEvent.class, "trigger");
		xstream.aliasField("Trigger", XPDLStartEvent.class, "trigger");
		xstream.useAttributeFor(XPDLStartEvent.class, "implementation");
		xstream.aliasField("Implementation", XPDLStartEvent.class, "implementation");
	}
	
	public String getTrigger() {
		return trigger;
	}
	
	public String getImplementation() {
		return implementation;
	}
	
	public void readJSONimplementation(JSONObject modelElement) {
		setImplementation(modelElement.optString("implementation"));
	}
	
	public void readJSONtrigger(JSONObject modelElement) {
		setTrigger(modelElement.optString("trigger"));
	}
	
	public void setImplementation(String implementation) {
		this.implementation = implementation;
	}
	
	public void setTrigger(String triggerValue) {
		trigger = triggerValue;
	}
}
