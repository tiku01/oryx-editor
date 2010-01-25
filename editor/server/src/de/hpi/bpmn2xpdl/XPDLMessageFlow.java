package de.hpi.bpmn2xpdl;

import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("MessageFlow")
public class XPDLMessageFlow extends XPDLThingConnectorGraphics {

	@Attribute("Source")
	protected String source;
	@Attribute("Target")
	protected String target;
	@Element("Message")
	protected XPDLMessage message;
	
	public static boolean handlesStencil(String stencil) {
		String[] types = {
				"MessageFlow"};
		return Arrays.asList(types).contains(stencil);
	}
	public XPDLMessage getMessage() {
		return message;
	}
	
	public String getSource() {
		return source;
	}
	
	public String getTarget() {
		return target;
	}
	
	public void readJSONmessage(JSONObject modelElement) throws JSONException {
		initializeMessage();
		
		JSONObject messageType = new JSONObject();
		messageType.put("id", getProperId(modelElement));
		messageType.put("message", modelElement.optString("message"));
		
		getMessage().parse(messageType);
	}
	
	public void readJSONsource(JSONObject modelElement) {
		setSource(modelElement.optString("source"));
	}
	
	public void readJSONtarget(JSONObject modelElement) throws JSONException {
		JSONObject target = modelElement.getJSONObject("target");
		setTarget(target.optString("resourceId"));
	}
	
	public void setMessage(XPDLMessage messageValue) {
		message = messageValue;
	}
	
	public void setSource(String sourceValue) {
		source = sourceValue;
	}
	
	public void setTarget(String targetValue) {
		target = targetValue;
	}
	
	protected void initializeMessage() {
		if (getMessage() == null) {
			setMessage(new XPDLMessage());
		}
	}
}
