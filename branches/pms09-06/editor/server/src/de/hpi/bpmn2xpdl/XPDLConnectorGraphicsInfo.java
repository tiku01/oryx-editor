package de.hpi.bpmn2xpdl;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.RootElement;

@RootElement("ConnectorGraphicsInfo")
public class XPDLConnectorGraphicsInfo extends XPDLGraphicsInfo {

	@Attribute("FillColor")
	protected String fillColor = "#0,0,0";
	
	public void readJSONbounds(JSONObject modelElement) {
		initializeCoordinates();
		JSONObject bounds = modelElement.optJSONObject("bounds");
		
		XPDLCoordinates firstAnchor = createCoordinates(bounds.optJSONObject("upperLeft"));
		XPDLCoordinates secondAnchor = createCoordinates(bounds.optJSONObject("lowerRight"));
		
		getCoordinates().add(0,firstAnchor);
		getCoordinates().add(secondAnchor);
	}
	
	public void readJSONdockers(JSONObject modelElement) {
		JSONArray dockers = modelElement.optJSONArray("dockers");
		
		if (dockers != null) {
			initializeCoordinates();
			
			if (getCoordinates().size() == 0) {
				for (int i = 0; i < dockers.length(); i++) {
					getCoordinates().add(createCoordinates(dockers.optJSONObject(i)));
				}
			} else {
				for (int i = 0; i < dockers.length(); i++) {
					getCoordinates().add(getCoordinates().size()-2,createCoordinates(dockers.optJSONObject(i)));
				}
			}
		}
	}
}