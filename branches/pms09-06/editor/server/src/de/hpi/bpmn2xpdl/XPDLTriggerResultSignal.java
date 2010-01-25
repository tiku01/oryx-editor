package de.hpi.bpmn2xpdl;

import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Text;

public class XPDLTriggerResultSignal  extends XMLConvertable {

	@Attribute("CatchThrow")
	protected String catchThrow;
	@Text
	protected String signal;
	
	public String getCatchThrow() {
		return catchThrow;
	}
	
	public String getSignal() {
		return signal;
	}
	
	public void readJSONsignalref(JSONObject modelElement) {
		setSignal(modelElement.optString("signalref"));
	}
	
	public void setCatchThrow(String catchThrow) {
		this.catchThrow = catchThrow;
	}
	
	public void setSignal(String signal) {
		this.signal = signal;
	}
}
