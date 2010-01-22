package de.hpi.bpmn2xpdl;

import java.util.ArrayList;

import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("GraphicsInfo")
public abstract class XPDLGraphicsInfo extends XMLConvertable {
	
	@Attribute("BorderColor")
	protected String borderColor = "#0,0,0";
	@Element("Coordinates")
	protected ArrayList<XPDLCoordinates> coordinates;
	@Attribute("FillColor")
	protected String fillColor;
	@Attribute("IsVisible")
	protected boolean isVisible = true;
	@Attribute("ToolId")
	protected String toolId = "Oryx";
	
	public XPDLGraphicsInfo() {
		setCoordinates(new ArrayList<XPDLCoordinates>());
	}
	
	public String getBorderColor() {
		return borderColor;
	}
	
	public ArrayList<XPDLCoordinates> getCoordinates() {
		return coordinates;
	}
	
	public String getFillColor() {
		return fillColor;
	}
	
	public boolean getIsVisible() {
		return isVisible;
	}
	
	public String getToolId() {
		return toolId;
	}
	
	public void readJSONbgcolor(JSONObject modelElement) {
		setFillColor(modelElement.optString("bgcolor"));
	}
	
	public void readJSONbounds(JSONObject modelElement) {		
	}
	
	public void setBorderColor(String color) {
		borderColor = color;
	}
	
	public void setCoordinates(ArrayList<XPDLCoordinates> coordinatesList) {
		coordinates = coordinatesList;
	}
	
	public void setFillColor(String color) {
		fillColor = color;
	}
	
	public void setIsVisible(boolean visibility) {
		isVisible = visibility;
	}
	
	public void setToolId(String tool) {
		toolId = tool;
	}
	
	protected XPDLCoordinates createCoordinates(JSONObject modelElement) {
		XPDLCoordinates createdCoordinates = new XPDLCoordinates();
		createdCoordinates.parse(modelElement);
		return createdCoordinates;
	}
	
	protected void initializeCoordinates() {
		if (getCoordinates() == null) {
			setCoordinates(new ArrayList<XPDLCoordinates>());
		}
	}
}
