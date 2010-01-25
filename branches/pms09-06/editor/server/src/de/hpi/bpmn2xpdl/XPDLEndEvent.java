package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("EndEvent")
public class XPDLEndEvent extends XMLConvertable {
	
	@Attribute("Result")
	protected String result;
	@Attribute("Implementation")
	protected String implementation;
	@Element("ResultError")
	protected XPDLResultError resultError;
	@Element("TriggerResultSignal")
	protected XPDLTriggerResultSignal triggerResultSignal;
	
	public String getImplementation() {
		return implementation;
	}
	
	public String getResult() {
		return result;
	}
	
	public XPDLResultError getResultError() {
		return resultError;
	}
	
	public XPDLTriggerResultSignal getTriggerResultSignal() {
		return triggerResultSignal;
	}
	
	public void readJSONerrorcode(JSONObject modelElement) throws JSONException {
		JSONObject errorObject = new JSONObject();
		errorObject.put("errorcode", modelElement.optString("errorcode"));
		
		XPDLResultError error = new XPDLResultError();
		error.parse(errorObject);
		
		setResultError(error);
	}
	
	public void readJSONimplementation(JSONObject modelElement) {
		setImplementation(modelElement.optString("implementation"));
	}
	
	public void readJSONresult(JSONObject modelElement) {
		setResult(modelElement.optString("result"));
	}
	
	public void readJSONsignalref(JSONObject modelElement) throws JSONException {
		JSONObject passObject = new JSONObject();
		passObject.put("signalref", modelElement.optString("signalref"));
		
		XPDLTriggerResultSignal signal = new XPDLTriggerResultSignal();
		signal.parse(passObject);
		
		setTriggerResultSignal(signal);
	}
	
	public void setImplementation(String implementation) {
		this.implementation = implementation;
	}
	
	public void setResult(String resultValue) {
		result = resultValue;
	}
	
	public void setResultError(XPDLResultError error) {
		resultError = error;
	}
	
	public void setTriggerResultSignal(XPDLTriggerResultSignal triggerResultSignal) {
		this.triggerResultSignal = triggerResultSignal;
	}
}
