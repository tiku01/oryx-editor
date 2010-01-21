package de.hpi.bpmn2xpdl;

import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class XPDLIntermediateEvent extends XMLConvertable {
	
	protected String trigger;
	protected String implementation;
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:IntermediateEvent", XPDLIntermediateEvent.class);
		
		xstream.useAttributeFor(XPDLIntermediateEvent.class, "trigger");
		xstream.aliasField("Trigger", XPDLIntermediateEvent.class, "trigger");
		xstream.useAttributeFor(XPDLIntermediateEvent.class, "implementation");
		xstream.aliasField("Implementation", XPDLIntermediateEvent.class, "implementation");
	}

	public String getImplementation() {
		return implementation;
	}
	
	public String getTrigger() {
		return trigger;
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
