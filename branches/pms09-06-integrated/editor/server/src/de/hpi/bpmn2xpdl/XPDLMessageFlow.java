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
		passInformationToMessage(modelElement, "message");
	}
	
	public void readJSONmessageunknowns(JSONObject modelElement) throws JSONException {
		passInformationToMessage(modelElement, "messageunknowns");
	}
	
	public void readJSONsource(JSONObject modelElement) {
		createExtendedAttribute("source", modelElement.optString("source"));
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
	
	public void writeJSONmessage(JSONObject modelElement) throws JSONException {
		XPDLMessage messageObject = getMessage();
		if (messageObject != null) {
			initializeProperties(modelElement);
			messageObject.write(getProperties(modelElement));
		}
	}
	
	public void writeJSONsource(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "source", "");
	}
	
	public void writeJSONstencil(JSONObject modelElement) throws JSONException {
		JSONObject stencil = new JSONObject();
		stencil.put("id", "MessageFlow");
		
		modelElement.put("stencil", stencil);
	}
	
	public void writeJSONtarget(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "target", "");
		
		String targetValue = getTarget();
		if (targetValue != null) {
			
		}
	}
	
	protected void initializeMessage() {
		if (getMessage() == null) {
			setMessage(new XPDLMessage());
		}
	}
	
	protected void passInformationToMessage(JSONObject modelElement, String key) throws JSONException {
		initializeMessage();
		
		JSONObject passObject = new JSONObject();
		passObject.put("id", getProperId(modelElement));
		passObject.put(key, modelElement.optString(key));
		
		getMessage().parse(passObject);
	}
}
