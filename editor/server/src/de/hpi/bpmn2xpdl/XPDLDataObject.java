package de.hpi.bpmn2xpdl;

import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class XPDLDataObject extends XPDLThing {

	protected boolean producedAtCompletion;
	protected boolean requiredForStart;
	protected String state;

	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:DataObject", XPDLDataObject.class);

		xstream.useAttributeFor(XPDLDataObject.class, "producedAtCompletion");
		xstream.aliasField("ProducedAtCompletion", XPDLDataObject.class, "producedAtCompletion");
		xstream.useAttributeFor(XPDLDataObject.class, "requiredForStart");
		xstream.aliasField("RequiredForStart", XPDLDataObject.class, "requiredForStart");
		xstream.useAttributeFor(XPDLDataObject.class, "state");
		xstream.aliasField("State", XPDLDataObject.class, "state");
	}

	public boolean getProducedAtCompletion() {
		return producedAtCompletion;
	}

	public boolean getRequiredForStart() {
		return requiredForStart;
	}

	public String getState() {
		return state;
	}

	public void readJSONartifacttype(JSONObject modelElement) {
	}
	
	public void readJSONitems(JSONObject modelElement) {
	}
	
	public void readJSONproducedatcompletion(JSONObject modelElement) {
		setProducedAtCompletion(modelElement.optBoolean("producedatcompletion"));
	}

	public void readJSONrequiredforstart(JSONObject modelElement) {
		setRequiredForStart(modelElement.optBoolean("requiredforstart"));
	}

	public void readJSONstate(JSONObject modelElement) {
		setState(modelElement.optString("state"));
	}
	
	public void readJSONtotalCount(JSONObject modelElement) {
	}

	public void setProducedAtCompletion(boolean isProduced) {
		producedAtCompletion = isProduced;
	}

	public void setRequiredForStart(boolean isRequired) {
		requiredForStart = isRequired;
	}

	public void setState(String stateValue) {
		state = stateValue;
	}
}
