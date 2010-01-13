package de.hpi.bpmn2xpdl;

import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class XPDLMessageFlow extends XPDLThingConnectorGraphics{

	protected String source;
	protected String target;
	protected XPDLMessageType message;
	
	public static boolean handlesStencil(String stencil) {
		String[] types = {
				"MessageFlow"};
		return Arrays.asList(types).contains(stencil);
	}

	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:MessageFlow", XPDLMessageFlow.class);
		
		xstream.useAttributeFor(XPDLMessageFlow.class, "source");
		xstream.aliasField("Source", XPDLMessageFlow.class, "source");
		xstream.useAttributeFor(XPDLMessageFlow.class, "target");
		xstream.aliasField("Target", XPDLMessageFlow.class, "target");
		xstream.aliasField("xpdl2:MessageType", XPDLMessageFlow.class, "message");
	}
	
	public XPDLMessageType getMessage() {
		return message;
	}
	
	public String getSource() {
		return source;
	}
	
	public String getTarget() {
		return target;
	}
	
	public void readJSONmessage(JSONObject modelElement) throws JSONException {
		JSONObject messageType = new JSONObject();
		messageType.put("resourceId", getProperId(modelElement));
		
		XPDLMessageType newMessage = new XPDLMessageType();
		newMessage.parse(messageType);
		
		setMessage(newMessage);
	}
	
	public void readJSONsource(JSONObject modelElement) {
		setSource(modelElement.optString("source"));
	}
	
	public void readJSONtarget(JSONObject modelElement) throws JSONException {
		JSONObject target = modelElement.getJSONObject("target");
		setTarget(target.optString("resourceId"));
	}
	
	public void setMessage(XPDLMessageType messageValue) {
		message = messageValue;
	}
	
	public void setSource(String sourceValue) {
		source = sourceValue;
	}
	
	public void setTarget(String targetValue) {
		target = targetValue;
	}
}
