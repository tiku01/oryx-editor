package de.hpi.bpmn2xpdl;

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

	public void setStyle(String styleValue) {
		style = styleValue;
	}
}