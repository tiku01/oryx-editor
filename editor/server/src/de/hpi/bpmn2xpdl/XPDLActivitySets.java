package de.hpi.bpmn2xpdl;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("ActivitySets")
public class XPDLActivitySets extends XMLConvertable {

	@Element("ActivitySet")
	protected ArrayList<XPDLActivitySet> actvitySets;

	public void add(XPDLActivitySet set) {
		initializeActivitySets();
		
		actvitySets.add(set);
	}
	
	public ArrayList<XPDLActivitySet> getActvitySets() {
		return actvitySets;
	}
	
	public void readJSONactivitysetsunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "activitysetsunknowns");
	}
	
	public void setActvitySets(ArrayList<XPDLActivitySet> actvitySets) {
		this.actvitySets = actvitySets;
	}
	
	public void writeJSONactivitysetsunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "activitysetsunknowns");
	}
	
	public void writeJSONchildShapes(JSONObject modelElement) throws JSONException {
		ArrayList<XPDLActivitySet> activitySetsList = getActvitySets();
		if (activitySetsList != null) {
			initializeChildShapes(modelElement);
			
			JSONArray childShapes = modelElement.getJSONArray("childShapes");
			for (int i = 0; i < activitySetsList.size(); i++) {
				JSONObject newActivitySet = new JSONObject();
				activitySetsList.get(i).write(newActivitySet);
				childShapes.put(newActivitySet);
			}
		}
	}
	
	protected void initializeActivitySets() {
		if (getActvitySets() == null) {
			setActvitySets(new ArrayList<XPDLActivitySet>());
		}
	}
		
	protected void initializeChildShapes(JSONObject modelElement) throws JSONException {
		if (modelElement.optJSONArray("childShapes") == null) {
			modelElement.put("childShapes", new JSONArray());
		}
	}
}
