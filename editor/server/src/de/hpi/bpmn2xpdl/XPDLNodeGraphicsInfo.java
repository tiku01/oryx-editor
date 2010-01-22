package de.hpi.bpmn2xpdl;

import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.RootElement;

@RootElement("NodeGraphicsInfo")
public class XPDLNodeGraphicsInfo extends XPDLGraphicsInfo {

	@Attribute("FillColor")
	protected String fillColor = "#255,255,255";
	@Attribute("Height")
	protected double height;
	@Attribute("LaneId")
	protected String laneId;
	@Attribute("Width")
	protected double width;

	public double getHeight() {
		return height;
	}

	public String getLaneId() {
		return laneId;
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

	public void setWidth(double widthValue) {
		width = widthValue;
	}
}
