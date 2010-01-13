package de.hpi.bpmn2xpdl;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class XPDLConnectorGraphicsInfo extends XPDLGraphicsInfo {

	protected String fillColor = "#0,0,0";
	protected String style;

	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:ConnectorGraphicsInfo", XPDLConnectorGraphicsInfo.class);

		xstream.useAttributeFor(XPDLConnectorGraphicsInfo.class, "style");
		xstream.aliasField("Style", XPDLConnectorGraphicsInfo.class, "style");
	}

	public String getStyle() {
		return style;
	}
	
	public void readJSONbounds(JSONObject modelElement) {
		initializeCoordinates();
		JSONObject bounds = modelElement.optJSONObject("bounds");
		
		XPDLCoordinates firstAnchor = createCoordinates(bounds.optJSONObject("upperLeft"));
		XPDLCoordinates secondAnchor = createCoordinates(bounds.optJSONObject("lowerRight"));
		
		getCoordinates().add(0,firstAnchor);
		getCoordinates().add(secondAnchor);
	}

	public void readJSONdockers(JSONObject modelElement) throws JSONException {
		/* TODO: dockers are different now - rethink it */
		JSONArray dockers = modelElement.getJSONArray("dockers");
		ArrayList<XPDLCoordinates> coordinatesList = getCoordinates();
		XPDLCoordinates lastElement = null;

		if (getCoordinates().size() != 0) {
			lastElement = coordinatesList.get(coordinatesList.size() - 1);
			coordinatesList.remove(coordinatesList.size() - 1);
		}
		for (int i = 0; i < dockers.length(); i++) {
			XPDLCoordinates nextDocker = new XPDLCoordinates();
			nextDocker.parse(dockers.getJSONObject(i));
			coordinatesList.add(nextDocker);
		}
		if (lastElement != null) {
			coordinatesList.add(lastElement);
		}
	}

	public void setStyle(String styleValue) {
		style = styleValue;
	}
}