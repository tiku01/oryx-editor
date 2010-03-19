package de.hpi.bpmn2xpdl;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Activities")
public class XPDLActivities extends XMLConvertible {

	@Element("Activity")
	protected ArrayList<XPDLActivity> activities;

	public void add(XPDLActivity newActivity) {
		initializeActivities();
		
		getActivities().add(newActivity);
	}
	
	public ArrayList<XPDLActivity> getActivities() {
		return activities;
	}
	
	public void readJSONactivitiesunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "activitiesunknowns");
	}

	public void setActivities(ArrayList<XPDLActivity> activities) {
		this.activities = activities;
	}
	
	public void writeJSONactivitiesunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "activitiesunknowns");
	}
	
	public void writeJSONchildShapes(JSONObject modelElement) throws JSONException {
		ArrayList<XPDLActivity> activitiesList = getActivities();
		if (activitiesList != null) {
			initializeChildShapes(modelElement);
			
			JSONArray childShapes = modelElement.getJSONArray("childShapes");
			for (int i = 0; i < activitiesList.size(); i++) {
				JSONObject newActivity = new JSONObject();
				activitiesList.get(i).write(newActivity);
				childShapes.put(newActivity);
			}
		}
	}
	
	protected void initializeActivities() {
		if (getActivities() == null) {
			setActivities(new ArrayList<XPDLActivity>());
		}
	}
	
	protected void initializeChildShapes(JSONObject modelElement) throws JSONException {
		if (modelElement.optJSONArray("childShapes") == null) {
			modelElement.put("childShapes", new JSONArray());
		}
	}
}
