package de.hpi.bpmn2xpdl;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Activities")
public class XPDLActivities extends XMLConvertable {

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
	
	protected void initializeActivities() {
		if (getActivities() == null) {
			setActivities(new ArrayList<XPDLActivity>());
		}
	}
}
