package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("IntermediateEvent")
public class XPDLIntermediateEvent extends XMLConvertable {
	
	@Attribute("Trigger")
	protected String trigger;
	@Attribute("Implementation")
	protected String implementation;
	@Element("ResultError")
	protected XPDLResultError resultError;
	@Element("TriggerResultCompensation")
	protected XPDLTriggerResultCompensation triggerResultCompensation;
	@Element("TriggerConditional")
	protected XPDLTriggerConditional triggerConditional;
	@Element("TriggerResultMessage")
	protected XPDLTriggerResultMessage triggerResultMessage;
	@Element("TriggerResultSignal")
	protected XPDLTriggerResultSignal triggerResultSignal;
	@Element("TriggerTimer")
	protected XPDLTriggerTimer triggerTimer;
	@Element("TriggerResultLink")
	protected XPDLTriggerResultLink triggerResultLink;

	public String getImplementation() {
		return implementation;
	}
	
	public String getTrigger() {
		return trigger;
	}
	
	public XPDLTriggerResultCompensation getTriggerResultCompensation() {
		return triggerResultCompensation;
	}
	
	public XPDLTriggerConditional getTriggerConditional() {
		return triggerConditional;
	}

	public XPDLTriggerResultLink getTriggerResultLink() {
		return triggerResultLink;
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
	
	public XPDLResultError getResultError() {
		return resultError;
	}
	
	public void readJSONactivity(JSONObject modelElement) throws JSONException {
		JSONObject passObject = new JSONObject();
		passObject.put("activity", modelElement.optString("activity"));
		
		initializeTriggerResultCompensation();
		getTriggerResultCompensation().parse(passObject);
	}
	
	public void readJSONcondition(JSONObject modelElement) throws JSONException {
		JSONObject passObject = new JSONObject();
		passObject.put("condition", modelElement.optString("condition"));
		
		initializeTriggerConditional();
		getTriggerConditional().parse(passObject);
	}
	
	public void readJSONerrorcode(JSONObject modelElement) throws JSONException {
		JSONObject errorObject = new JSONObject();
		errorObject.put("errorcode", modelElement.optString("errorcode"));
		
		initializeResultError();
		getResultError().parse(errorObject);
	}
	
	public void readJSONimplementation(JSONObject modelElement) {
		setImplementation(modelElement.optString("implementation"));
	}
	
	public void readJSONlinkid(JSONObject modelElement) throws JSONException {
		JSONObject linkObject = new JSONObject();
		linkObject.put("linkid", modelElement.optString("linkid"));
		
		initializeTriggerResultLink();
		getTriggerResultLink().parse(linkObject);
	}
	
	public void readJSONmessage(JSONObject modelElement) throws JSONException {
		JSONObject passObject = new JSONObject();
		passObject.put("message", modelElement.optString("message"));
		
		initializeTriggerResultMessage();
		getTriggerResultMessage().parse(passObject);
	}
	
	public void readJSONstencil(JSONObject modelElement) {
		String stencil = modelElement.optString("stencil");
		String catchThrow = determineCatchThrow(stencil);
		
		if (stencil.equals("IntermediateMessageEventThrowing") || stencil.equals("IntermediateMessageEventCatching")) {
			initializeTriggerResultMessage();
			getTriggerResultMessage().setCatchThrow(catchThrow);
		} else if (stencil.equals("IntermediateSignalEventThrowing") || stencil.equals("IntermediateSignalEventCatching")) {
			initializeTriggerResultSignal();
			getTriggerResultSignal().setCatchThrow(catchThrow);
		} else if (stencil.equals("IntermediateLinkEventCatching") || stencil.equals("IntermediateLinkEventThrowing")) {
			initializeTriggerResultLink();
			getTriggerResultLink().setCatchThrow(catchThrow);
		}
	}
	
	public void readJSONsignalref(JSONObject modelElement) throws JSONException {
		JSONObject passObject = new JSONObject();
		passObject.put("signalref", modelElement.optString("signalref"));
		
		initializeTriggerResultSignal();
		getTriggerResultSignal().parse(passObject);
	}	
	
	public void readJSONtrigger(JSONObject modelElement) {
		String trigger = modelElement.optString("trigger");
		if (trigger.equals("Rule")) {
			setTrigger("Conditional");
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
	
	public void setResultError(XPDLResultError error) {
		resultError = error;
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
	
	public void setTriggerResultCompensation(
			XPDLTriggerResultCompensation triggerResultCompensation) {
		this.triggerResultCompensation = triggerResultCompensation;
	}

	public void setTriggerResultLink(XPDLTriggerResultLink link) {
		triggerResultLink = link;
	}
	
	public void setTriggerResultMessage(XPDLTriggerResultMessage message) {
		triggerResultMessage = message;
	}
	
	public void setTriggerResultSignal(XPDLTriggerResultSignal triggerResultSignal) {
		this.triggerResultSignal = triggerResultSignal;
	}
	
	public void setTriggerTimer(XPDLTriggerTimer timer) {
		triggerTimer = timer;
	}
	
	protected String determineCatchThrow(String stencil) {
		if (stencil.contains("Catching")) {
			return "CATCH";
		} else {
			return "THROW";
		}
	}
	
	protected void passInformationToTriggerTimer(JSONObject modelElement, String key) throws JSONException {
		JSONObject passObject = new JSONObject();
		passObject.put(key, modelElement.optString(key));
		
		initializeTriggerTimer();
		getTriggerTimer().parse(passObject);
	}
	
	protected void initializeResultError() {
		if (getResultError() == null) {
			setResultError(new XPDLResultError());
		}
	}
	
	protected void initializeTriggerConditional() {
		if (getTriggerConditional() == null) {
			setTriggerConditional(new XPDLTriggerConditional());
		}
	}
	
	protected void initializeTriggerResultCompensation() {
		if (getTriggerResultCompensation() == null) {
			setTriggerResultCompensation(new XPDLTriggerResultCompensation());
		}
	}
	
	protected void initializeTriggerResultLink() {
		if (getTriggerResultLink() == null) {
			setTriggerResultLink(new XPDLTriggerResultLink());
		}
	}
	
	protected void initializeTriggerResultMessage() {
		if (getTriggerResultMessage() == null) {
			setTriggerResultMessage(new XPDLTriggerResultMessage());
		}
	}
	
	protected void initializeTriggerResultSignal() {
		if (getTriggerResultSignal() == null) {
			setTriggerResultSignal(new XPDLTriggerResultSignal());
		}
	}
	
	protected void initializeTriggerTimer() {
		if (getTriggerTimer() == null) {
			setTriggerTimer(new XPDLTriggerTimer());
		}
	}
}
