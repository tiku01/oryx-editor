package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class XPDLEvent extends XMLConvertable {
	
	protected XPDLEndEvent endEvent;
	protected XPDLIntermediateEvent intermediateEvent;
	protected XPDLStartEvent startEvent;
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:Event", XPDLEvent.class);
		
		xstream.aliasField("xpdl2:EndEvent", XPDLEvent.class, "endEvent");
		xstream.aliasField("xpdl2:IntermediateEvent", XPDLEvent.class, "intermediateEvent");
		xstream.aliasField("xpdl2:StartEvent", XPDLEvent.class, "startEvent");
	}

	public XPDLEndEvent getEndEvent() {
		return endEvent;
	}

	public XPDLIntermediateEvent getIntermediateEvent() {
		return intermediateEvent;
	}

	public XPDLStartEvent getStartEvent() {
		return startEvent;
	}

	public void readJSONeventtype(JSONObject modelElement) {
		String typeOfEvent = modelElement.optString("eventtype");
		if (typeOfEvent.equals("End")) {
			initializeEndEvent();
		} else if (typeOfEvent.equals("Intermediate")) {
			initializeIntermediateEvent();
		} else if (typeOfEvent.equals("Start")) {
			initializeStartEvent();
		}
	}
	
	public void readJSONimplementation(JSONObject modelElement) throws JSONException {
		String typeOfEvent = modelElement.optString("eventtype");
		if (typeOfEvent.equals("End")) {
			passInformationToEndEvent(modelElement, "trigger");
		} else if (typeOfEvent.equals("Intermediate")) {
			passInformationToIntermediateEvent(modelElement, "trigger");
		} else if (typeOfEvent.equals("Start")) {
			passInformationToStartEvent(modelElement, "trigger");
		}
	}
	
	public void readJSONresult(JSONObject modelElement) throws JSONException {
		passInformationToEndEvent(modelElement, "result");
	}
	
	public void readJSONtrigger(JSONObject modelElement) throws JSONException {
		String typeOfEvent = modelElement.optString("eventtype");
		if (typeOfEvent.equals("Intermediate")) {
			passInformationToIntermediateEvent(modelElement, "trigger");
		} else if (typeOfEvent.equals("Start")) {
			passInformationToStartEvent(modelElement, "trigger");
		}
	}
	
	public void setEndEvent(XPDLEndEvent endEvent) {
		this.endEvent = endEvent;
	}

	public void setIntermediateEvent(XPDLIntermediateEvent intermediateEvent) {
		this.intermediateEvent = intermediateEvent;
	}

	public void setStartEvent(XPDLStartEvent startEvent) {
		this.startEvent = startEvent;
	}
	
	protected void initializeEndEvent() {
		if (getEndEvent() ==  null) {
			setEndEvent(new XPDLEndEvent());
		}
	}
	
	protected void initializeIntermediateEvent() {
		if (getIntermediateEvent() ==  null) {
			setIntermediateEvent(new XPDLIntermediateEvent());
		}
	}
	
	protected void initializeStartEvent() {
		if (getStartEvent() ==  null) {
			setStartEvent(new XPDLStartEvent());
		}
	}
	
	protected void passInformationToEndEvent(JSONObject modelElement, String key) throws JSONException {
		initializeEndEvent();
		
		JSONObject passObject = new JSONObject();
		passObject.put(key, modelElement.optString(key));
		
		getEndEvent().parse(passObject);
	}
	
	protected void passInformationToIntermediateEvent(JSONObject modelElement, String key) throws JSONException {
		initializeIntermediateEvent();
		
		JSONObject passObject = new JSONObject();
		passObject.put(key, modelElement.optString(key));
		
		getIntermediateEvent().parse(passObject);
	}
	
	protected void passInformationToStartEvent(JSONObject modelElement, String key) throws JSONException {
		initializeStartEvent();
		
		JSONObject passObject = new JSONObject();
		passObject.put(key, modelElement.optString(key));
		
		getStartEvent().parse(passObject);
	}
}
