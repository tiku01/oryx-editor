package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("StartEvent")
public class XPDLStartEvent extends XMLConvertable {

	@Attribute("Trigger")
	protected String trigger;
	@Attribute("Implementation")
	protected String implementation;
	@Element("TriggerConditional")
	protected XPDLTriggerConditional triggerConditional;
	@Element("TriggerResultMessage")
	protected XPDLTriggerResultMessage triggerResultMessage;
	@Element("TriggerResultSignal")
	protected XPDLTriggerResultSignal triggerResultSignal;
	@Element("TriggerTimer")
	protected XPDLTriggerTimer triggerTimer;
	
	public String getImplementation() {
		return implementation;
	}
	
	public String getTrigger() {
		return trigger;
	}

	public XPDLTriggerConditional getTriggerConditional() {
		return triggerConditional;
	}
	
	public XPDLTriggerResultMessage getTriggerResultMessage() {
		return triggerResultMessage;
	}

	public XPDLTriggerResultSignal getTriggerResultSignal() {
		return triggerResultSignal;
	}

	public XPDLTriggerTimer getTriggerTimer() {
		return triggerTimer;
	}
	
	public void readJSONconditionref(JSONObject modelElement) throws JSONException {
		JSONObject passObject = new JSONObject();
		passObject.put("condition", modelElement.optString("condition"));
		
		XPDLTriggerConditional condition = new XPDLTriggerConditional();
		condition.parse(passObject);
		
		setTriggerConditional(condition);
	}
	
	public void readJSONimplementation(JSONObject modelElement) {
		setImplementation(modelElement.optString("implementation"));
	}
	
	public void readJSONmessage(JSONObject modelElement) throws JSONException {
		JSONObject passObject = new JSONObject();
		passObject.put("message", modelElement.optString("message"));
		
		XPDLTriggerResultMessage message = new XPDLTriggerResultMessage();
		message.parse(passObject);
		
		setTriggerResultMessage(message);
	}
	
	public void readJSONsignalref(JSONObject modelElement) throws JSONException {
		JSONObject passObject = new JSONObject();
		passObject.put("signalref", modelElement.optString("signalref"));
		
		XPDLTriggerResultSignal signal = new XPDLTriggerResultSignal();
		signal.parse(passObject);
		
		setTriggerResultSignal(signal);
	}
	
	public void readJSONstencil(JSONObject modelElement) {
	}
	
	public void readJSONtrigger(JSONObject modelElement) {
		String trigger = modelElement.optString("trigger");
		if (trigger.equals("Rule")) {
			setTrigger("Conditional");
		} else if (modelElement.optString("stencil").equals("StartSignalEvent")) {
			setTrigger("Signal");
		} else {
			setTrigger(trigger);
		}
	}
	
	public void readJSONtimecycle(JSONObject modelElement) throws JSONException {
		passInformationToTriggerTimer(modelElement, "timecycle");
	}
	
	public void readJSONtimedate(JSONObject modelElement) throws JSONException {
		passInformationToTriggerTimer(modelElement, "timedate");
	}
	
	public void setImplementation(String implementation) {
		this.implementation = implementation;
	}
	
	public void setTrigger(String triggerValue) {
		trigger = triggerValue;
	}
	
	public void setTriggerConditional(XPDLTriggerConditional triggerConditional) {
		this.triggerConditional = triggerConditional;
	}

	public void setTriggerTimer(XPDLTriggerTimer timer) {
		triggerTimer = timer;
	}
	
	public void setTriggerResultMessage(XPDLTriggerResultMessage message) {
		triggerResultMessage = message;
	}
	
	public void setTriggerResultSignal(XPDLTriggerResultSignal triggerResultSignal) {
		this.triggerResultSignal = triggerResultSignal;
	}

	protected void passInformationToTriggerTimer(JSONObject modelElement, String key) throws JSONException {
		JSONObject passObject = new JSONObject();
		passObject.put(key, modelElement.optString(key));
		
		initializeTriggerTimer();
		getTriggerTimer().parse(passObject);
	}
	
	protected void initializeTriggerTimer() {
		if (getTriggerTimer() == null) {
			setTriggerTimer(new XPDLTriggerTimer());
		}
	}
}
