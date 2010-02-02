package de.hpi.bpmn2xpdl;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Lanes")
public class XPDLLanes extends XMLConvertable {

	@Element("Lane")
	protected ArrayList<XPDLLane> lanes;

	public void add(XPDLLane newLane) {
		initializeLanes();
		
		getLanes().add(newLane);
	}
	
	public ArrayList<XPDLLane> getLanes() {
		return lanes;
	}
	
	public void readJSONlanesunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "lanesunknowns");
	}

	public void setLanes(ArrayList<XPDLLane> lanes) {
		this.lanes = lanes;
	}
	
	public void writeJSONchildShapes(JSONObject modelElement) throws JSONException {
		ArrayList<XPDLLane> lanesList = getLanes();
		if (lanesList != null) {
			initializeChildShapes(modelElement);
			
			JSONArray childShapes = modelElement.getJSONArray("childShapes");
			for (int i = 0; i < lanesList.size(); i++) {
				JSONObject newLane = new JSONObject();
				lanesList.get(i).write(newLane);
				childShapes.put(newLane);
			}
		}
	}
	
	public void writeJSONlanesunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "lanesunknowns");
	}
	
	protected void initializeChildShapes(JSONObject modelElement) throws JSONException {
		if (modelElement.optJSONArray("childShapes") == null) {
			modelElement.put("childShapes", new JSONArray());
		}
	}
	
	protected void initializeLanes() {
		if (getLanes() == null) {
			setLanes(new ArrayList<XPDLLane>());
		}
	}
}
