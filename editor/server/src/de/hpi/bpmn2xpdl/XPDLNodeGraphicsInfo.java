package de.hpi.bpmn2xpdl;

import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class XPDLNodeGraphicsInfo extends XPDLGraphicsInfo {

	protected String fillColor = "#255,255,255";
	protected double height;
	protected String laneId;
	protected String shape;
	protected double width;

	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:NodeGraphicsInfo", XPDLNodeGraphicsInfo.class);

		xstream.useAttributeFor(XPDLNodeGraphicsInfo.class, "height");
		xstream.aliasField("Height", XPDLNodeGraphicsInfo.class, "height");
		xstream.useAttributeFor(XPDLNodeGraphicsInfo.class, "width");
		xstream.aliasField("Width", XPDLNodeGraphicsInfo.class, "width");
		xstream.useAttributeFor(XPDLNodeGraphicsInfo.class, "shape");
		xstream.aliasField("Shape", XPDLNodeGraphicsInfo.class, "shape");
		xstream.useAttributeFor(XPDLNodeGraphicsInfo.class, "laneId");
		xstream.aliasField("LaneId", XPDLNodeGraphicsInfo.class, "laneId");
	}

	public double getHeight() {
		return height;
	}

	public String getLaneId() {
		return laneId;
	}

	public String getShape() {
		return shape;
	}
	
	public double getWidth() {
		return width;
	}

	public void readJSONbounds(JSONObject modelElement) {
		JSONObject bounds = modelElement.optJSONObject("bounds");
		JSONObject upperLeft = bounds.optJSONObject("upperLeft");
		JSONObject lowerRight = bounds.optJSONObject("lowerRight");
	
		getCoordinates().add(createCoordinates(upperLeft));
		
		setHeight(lowerRight.optDouble("y") - upperLeft.optDouble("y"));
		setWidth(lowerRight.optDouble("x") - upperLeft.optDouble("x"));
	}

	public void setHeight(double heightValue) {
		height = heightValue;
	}

	public void setLaneId(String lane) {
		laneId = lane;
	}
	
	public void setShape(String shapeValue) {
		shape = shapeValue;
	}

	public void setWidth(double widthValue) {
		width = widthValue;
	}
}
