package de.hpi.visio.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

/**
 * Class for xmappr - xml to java mapping.
 * A stencil on the visio canvas. Information are highly encapsulated because of the xml import mapping,
 * so that all needed information are provided trough delegation...
 * From xml imported information:
 * 	- name (nameU) 	: stencil type
 * 	- masterId 		: link to master (with correct stencilId)
 * 	- xForm			: boundaries through width, height and central point + angle of the shape
 * 	- xForm1D		: edges have in addition a startpoint and an endpoint (with correct direction)
 * 	- label			: text label of the shape, used as condition exp., title or name in oryx
 * 	- geom			: edges also have additional dockers
 * 
 * @author Thamsen
 */
@RootElement("Shape")
public class Shape {

	private List<Shape> outgoings;

	private List<Shape> incomings;

	private Shape target;

	private Shape source;

	private String shapeId;

	@Attribute("NameU")
	public String name;

	@Attribute("Master")
	public String masterId;

	@Element("XForm")
	public XForm xForm;

	@Element("XForm1D")
	public xFormStartAndEnd xFormStartAndEnd;

	@Element("Text")
	public Label label;

	@Element("Geom")
	public Geom geom;

	public Map<String, String> properties;

	public void setOutgoings(List<Shape> outgoings) {
		this.outgoings = outgoings;
	}

	public List<Shape> getOutgoings() {
		if (outgoings == null)
			outgoings = new ArrayList<Shape>();
		return outgoings;
	}

	public void addOutgoing(Shape outgoing) {
		if (outgoings == null)
			outgoings = new ArrayList<Shape>();
		outgoings.add(outgoing);
	}

	public void setIncomings(List<Shape> incomings) {
		this.incomings = incomings;
	}

	public List<Shape> getIncomings() {
		if (incomings == null)
			incomings = new ArrayList<Shape>();
		return incomings;
	}

	public void addIncoming(Shape incoming) {
		if (incomings == null)
			incomings = new ArrayList<Shape>();
		incomings.add(incoming);
	}

	public void setTarget(Shape target) {
		this.target = target;
	}

	public Shape getTarget() {
		return target;
	}

	public void setSource(Shape source) {
		this.source = source;
	}

	public Shape getSource() {
		return source;
	}

	public void setShapeId(String shapeId) {
		this.shapeId = shapeId;
	}

	public String getShapeId() {
		return shapeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMasterId() {
		return masterId;
	}

	public XForm getXForm() {
		return xForm;
	}

	public void setLabel(String label) {
		this.label.text = label;
	}

	public String getLabel() {
		if (label == null)
			label = new Label();
		return label.getLabel();
	}

	public Double getWidth() {
		return Math.abs(xForm.getWidth());
	}

	public Double getHeight() {
		return Math.abs(xForm.getHeight());
	}

	public void setWidth(Double width) {
		if (xForm == null)
			xForm = new XForm();
		xForm.setWidth(width);
	}

	public void setHeight(Double height) {
		if (xForm == null)
			xForm = new XForm();
		xForm.setHeight(height);
	}

	public Map<String, String> getProperties() {
		if (properties == null)
			properties = new HashMap<String, String>();
		return properties;
	}

	public void putProperty(String key, String value) {
		if (properties == null)
			properties = new HashMap<String, String>();
		properties.put(key, value);
	}

	public String getPropertyValueByKey(String key) {
		if (properties == null)
			properties = new HashMap<String, String>();
		return properties.get(key);
	}

	public Bounds getBoundsOnPage(Page visioPage) {
		Bounds bounds = new Bounds(xForm.getLowerRightPointForPage(visioPage), xForm
				.getUpperLeftPointForPage(visioPage));
		return bounds;
	}

	public Bounds getVisioBounds() {
		Bounds bounds = new Bounds(xForm.getLowerRightVisioPoint(), xForm.getUpperLeftVisioPoint());
		return bounds;
	}

	public Point getCentralPin() {
		return xForm.getCentralPin();
	}

	public Point getCentralPinForPage(Page visioPage) {
		return xForm.getCentralPinForPage(visioPage);
	}

	public void setCentralPin(Point newPin) {
		if (xForm == null)
			xForm = new XForm();
		xForm.setCentralPin(newPin);
	}

	public Point getStartPoint() {
		if (xFormStartAndEnd == null)
			return xForm.getUpperLeftVisioPoint();
		return xFormStartAndEnd.getStartPoint();
	}

	public Point getStartPointForPage(Page visioPage) {
		if (xFormStartAndEnd == null)
			return xForm.getUpperLeftPointForPage(visioPage);
		return xFormStartAndEnd.getStartPointForPage(visioPage);
	}

	public void setStartPoint(Point startPoint) {
		if (xFormStartAndEnd == null)
			xFormStartAndEnd = new xFormStartAndEnd();
		xFormStartAndEnd.setStartPoint(startPoint);
	}

	public Point getEndPoint() {
		if (xFormStartAndEnd == null)
			return xForm.getLowerRightVisioPoint();
		return xFormStartAndEnd.getEndPoint();
	}

	public Point getEndPointForPage(Page visioPage) {
		if (xFormStartAndEnd == null)
			return xForm.getLowerRightPointForPage(visioPage);
		return xFormStartAndEnd.getEndPointForPage(visioPage);
	}

	public void setEndPoint(Point endPoint) {
		xFormStartAndEnd.setEndPoint(endPoint);
	}

	public Double getArea() {
		if (this.getHeight() == null || this.getWidth() == null)
			return null;
		return this.getHeight() * this.getWidth();
	}

	public List<Point> getDockerPoints() {
		if (geom != null) {
			return geom.getDockerPointsInRightOrder();
		}
		return new ArrayList<Point>();
	}

}
