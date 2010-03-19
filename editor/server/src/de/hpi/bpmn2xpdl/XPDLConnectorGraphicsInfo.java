package de.hpi.bpmn2xpdl;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
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
				for (int i = 1; i < dockers.length()-1; i++) {
					getCoordinates().add(createCoordinates(dockers.optJSONObject(i)));
				}
			} else {
				for (int i = 1; i < dockers.length()-1; i++) {
					getCoordinates().add(getCoordinates().size()-2,createCoordinates(dockers.optJSONObject(i)));
				}
			}
		}
	}
	
	public void readJSONgraphicsinfounknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "graphicsinfounknowns");
	}
	
	public void writeJSONbounds(JSONObject modelElement) throws JSONException {
		ArrayList<XPDLCoordinates> coordinatesList = getCoordinates();
		if (coordinatesList != null) {
			if (coordinatesList.size() > 0) {
				XPDLCoordinates firstCoordinates = coordinatesList.get(0);
				JSONObject upperLeft = new JSONObject();
				upperLeft.put("x", firstCoordinates.getXCoordinate());
				upperLeft.put("y", firstCoordinates.getYCoordinate());
				
				XPDLCoordinates lastCoordinates = coordinatesList.get(coordinatesList.size()-1);
				JSONObject lowerRight = new JSONObject();
				lowerRight.put("x", lastCoordinates.getXCoordinate());
				lowerRight.put("y", lastCoordinates.getYCoordinate());
				
				JSONObject bounds = new JSONObject();
				bounds.put("upperLeft", upperLeft);
				bounds.put("lowerRight", lowerRight);
				
				modelElement.put("bounds", bounds);				
			} else {
				writeEmptyBounds(modelElement);
			}
		} else {
			writeEmptyBounds(modelElement);
		}
	}
	
	public void writeJSONdockers(JSONObject modelElement) throws JSONException {
		JSONArray dockers = new JSONArray();
		ArrayList<XPDLCoordinates> coordinatesList = getCoordinates();
		if (coordinatesList != null) {
			for (int i = 0; i < coordinatesList.size(); i++) {
				XPDLCoordinates coordinate = coordinatesList.get(i);
				
				JSONObject docker = new JSONObject();
				docker.put("x", coordinate.getXCoordinate());
				docker.put("y", coordinate.getYCoordinate());
				
				dockers.put(docker);
			}
		}
		modelElement.put("dockers", dockers);		
	}
	
	public void writeJSONgraphicsinfounknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "graphicsinfounknowns");
	}
}