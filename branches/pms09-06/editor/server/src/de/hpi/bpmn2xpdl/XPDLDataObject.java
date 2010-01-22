package de.hpi.bpmn2xpdl;

import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.RootElement;

@RootElement("DataObject")
public class XPDLDataObject extends XPDLThing {

	@Attribute("ProducedAtCompletion")
	protected boolean producedAtCompletion;
	@Attribute("RequiredForStart")
	protected boolean requiredForStart;
	@Attribute("State")
	protected String state;

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
