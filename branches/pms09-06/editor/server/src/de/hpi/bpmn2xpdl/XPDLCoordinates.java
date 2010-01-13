package de.hpi.bpmn2xpdl;

import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class XPDLCoordinates extends XMLConvertable {
	protected double xCoordinate;
	protected double yCoordinate;

	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:Coordinates", XPDLCoordinates.class);

		xstream.useAttributeFor(XPDLCoordinates.class, "xCoordinate");
		xstream.aliasField("XCoordinate", XPDLCoordinates.class, "xCoordinate");
		xstream.useAttributeFor(XPDLCoordinates.class, "yCoordinate");
		xstream.aliasField("YCoordinate", XPDLCoordinates.class, "yCoordinate");
	}

	public double getX() {
		return xCoordinate;
	}

	public double getY() {
		return yCoordinate;
	}

	public void readJSONx(JSONObject modelElement) {
		setX(modelElement.optDouble("x", 0.0));
	}

	public void readJSONy(JSONObject modelElement) {
		setY(modelElement.optDouble("y", 0.0));
	}

	public void setX(double xValue) {
		xCoordinate = xValue;
	}

	public void setY(double yValue) {
		yCoordinate = yValue;
	}
}
