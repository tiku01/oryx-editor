package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;

public class XPDLTriggerResultMessage  extends XMLConvertable {

	@Attribute("CatchThrow")
	protected String catchThrow;
	@Element("Message")
	protected XPDLMessage message;
	
	public String getCatchThrow() {
		return catchThrow;
	}
	
	public XPDLMessage getMessage() {
		return message;
	}
	
	public void readJSONmessage(JSONObject modelElement) throws JSONException {
		JSONObject messageObject = new JSONObject();
		messageObject.put("message", modelElement.optString("message"));
		
		XPDLMessage passMessage = new XPDLMessage();
		passMessage.parse(messageObject);
		
		setMessage(passMessage);		
	}
	
	public void setCatchThrow(String catchThrow) {
		this.catchThrow = catchThrow;
	}
	
	public void setMessage(XPDLMessage message) {
		this.message = message;
	}
}
