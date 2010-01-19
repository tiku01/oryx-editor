package de.hpi.bpmn2xpdl;

import java.util.ArrayList;

import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public abstract class XPDLGraphicsInfo extends XMLConvertable {
	
	protected String borderColor = "#0,0,0";
	protected ArrayList<XPDLCoordinates> coordinates;
	protected String fillColor;
	protected boolean isVisible = true;
	protected String page;
	protected String pageId;
	protected String toolId = "Oryx";
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("GraphicsInfo", XPDLGraphicsInfo.class);

		xstream.useAttributeFor(XPDLGraphicsInfo.class, "borderColor");
		xstream.aliasField("BorderColor", XPDLGraphicsInfo.class, "borderColor");
		xstream.useAttributeFor(XPDLGraphicsInfo.class, "fillColor");
		xstream.aliasField("FillColor", XPDLGraphicsInfo.class, "fillColor");
		xstream.useAttributeFor(XPDLGraphicsInfo.class, "page");
		xstream.aliasField("Page", XPDLGraphicsInfo.class, "page");
		xstream.useAttributeFor(XPDLGraphicsInfo.class, "pageId");
		xstream.aliasField("PageId", XPDLGraphicsInfo.class, "pageId");
		xstream.useAttributeFor(XPDLGraphicsInfo.class, "toolId");
		xstream.aliasField("ToolId", XPDLGraphicsInfo.class, "toolId");
		xstream.useAttributeFor(XPDLGraphicsInfo.class, "isVisible");
		xstream.aliasField("IsVisible", XPDLGraphicsInfo.class, "isVisible");
		
		xstream.addImplicitCollection(XPDLGraphicsInfo.class, "coordinates");
	}
	
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
	
	public String getPage() {
		return page;
	}

	public String getPageId() {
		return pageId;
	}
	
	public String getToolId() {
		return toolId;
	}
	
	public void readJSONbgcolor(JSONObject modelElement) {
		setFillColor(rgbToNumber(modelElement.optString("bgcolor")));
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
	
	public void setPage(String pageValue) {
		page = pageValue;
	}
	
	public void setPageId(String pageIdValue) {
		pageId = pageIdValue;
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
	
	protected String numberToRGB(String numberString) {
		String[] numberValues = numberString.split(",");
		String resultString = "#";
		for (int i = 0; i < numberValues.length; i++) {
			Integer parsedNumber = Integer.parseInt(numberValues[i]);
			if (parsedNumber < 16) {
				resultString += "0" + Integer.toHexString(parsedNumber);
			} else {
				resultString += Integer.toHexString(parsedNumber);
			}
		}
		return resultString;
	}

	protected String rgbToNumber(String rgbString) {
		String resultString = Integer.parseInt(rgbString.substring(1, 3), 16) + ",";
		resultString += Integer.parseInt(rgbString.substring(3, 5), 16) + ",";
		resultString += Integer.parseInt(rgbString.substring(5, 7), 16);
		return resultString;
	}
}
