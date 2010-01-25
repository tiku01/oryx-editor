package de.hpi.bpmn2xpdl;

import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.RootElement;
import org.xmappr.Text;

@RootElement("Message")
public class XPDLMessage extends XMLConvertable {

	@Attribute("Id")
	protected String id;
	@Text
	protected String message;
	
	public String getId() {
		return id;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void readJSONid(JSONObject modelElement) {
		setId(modelElement.optString("id")+"-message");
	}
	
	public void readJSONmessage(JSONObject modelElement) {
		setMessage(modelElement.optString("message"));
	}
	
	public void setId(String idValue) {
		id = idValue;
	}
	
	public void setMessage(String messageValue) {
		message = messageValue;
	}
}
