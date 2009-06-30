package de.unihannover.se.infocup2008.bpmn.model;

import org.json.JSONArray;
import org.json.JSONObject;

public class BPMNElementJSON extends BPMNAbstractElement {

	private JSONObject boundsJSON;
	private JSONArray dockersJSON;
	
	@Override
	public void updateDataModel() {
		
	}

	public void setBoundsJSON(JSONObject boundsJSON) {
		this.boundsJSON = boundsJSON;
	}

	public JSONObject getBoundsJSON() {
		return boundsJSON;
	}

	public void setDockersJSON(JSONArray dockers) {
		this.dockersJSON = dockers;
	}

	public JSONArray getDockersJSON() {
		return dockersJSON;
	}

}
