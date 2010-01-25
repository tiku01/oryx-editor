package de.hpi.bpmn2xpdl;

import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.RootElement;

@RootElement("BlockActivity")
public class XPDLBlockActivity extends XMLConvertable {
	
	@Attribute("ActivitySetId")
	protected String activitySetId;
	@Attribute("View")
	protected String view;
	
	public String getActivitySetId() {
		return activitySetId;
	}
	
	public String getView() {
		return view;
	}
	
	public void readJSONid(JSONObject modelElement) {
		setActivitySetId(modelElement.optString("id") + "-activitySet");
	}
	
	public void readJSONsubprocesstype(JSONObject modelElement) {
		if (modelElement.optString("subprocesstype").contains("Collapsed")) {
			setView("COLLAPSED");
		} else {
			setView("EXTENDED");
		}
	}
	
	public void setActivitySetId(String activitySetId) {
		this.activitySetId = activitySetId;
	}
	
	public void setView(String view) {
		this.view = view;
	}
}
