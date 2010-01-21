package de.hpi.bpmn2xpdl;

import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class XPDLEndEvent extends XMLConvertable {
	
	protected String result;
	protected String implementation;
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:EndEvent", XPDLEndEvent.class);
		
		xstream.useAttributeFor(XPDLEndEvent.class, "result");
		xstream.aliasField("Result", XPDLEndEvent.class, "result");
		xstream.useAttributeFor(XPDLEndEvent.class, "implementation");
		xstream.aliasField("Implementation", XPDLEndEvent.class, "implementation");
	}
	
	public String getImplementation() {
		return implementation;
	}
	
	public String getResult() {
		return result;
	}
	
	public void readJSONimplementation(JSONObject modelElement) {
		setImplementation(modelElement.optString("implementation"));
	}
	
	public void readJSONresult(JSONObject modelElement) {
		setResult(modelElement.optString("result"));
	}
	
	public void setImplementation(String implementation) {
		this.implementation = implementation;
	}
	
	public void setResult(String resultValue) {
		result = resultValue;
	}
}
