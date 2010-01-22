package de.hpi.bpmn2xpdl;

import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.RootElement;

@RootElement("EndEvent")
public class XPDLEndEvent extends XMLConvertable {
	
	@Attribute("Result")
	protected String result;
	@Attribute("Implementation")
	protected String implementation;
	
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
