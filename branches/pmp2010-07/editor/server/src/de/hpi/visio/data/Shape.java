package de.hpi.visio.data;

import java.util.HashMap;
import java.util.Map;

import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Shape")
public class Shape {

	@Attribute("NameU")
	public String name;
	
	@Attribute("Master")
	public String masterId;
	
	@Element("XForm")
	public XForm xForm;
	
	@Element("Text")
	public Label label;
	
	public Map<String, String> properties;

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
	
	public String getLabel() {
		if (label == null)
			label = new Label();
		return label.getLabel();
	}
	
	public Double getWidth() {
		return xForm.getWidth();
	}
	
	public Double getHeight() {
		return xForm.getHeight();
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
	
	public Bounds getBoundsOnPage(Page visioPage){
		Bounds bounds = new Bounds(xForm.getLowerRightPointForPage(visioPage), 
				xForm.getUpperLeftPointForPage(visioPage));
		return bounds;
	}
	
	public Bounds getVisioBounds() {
		Bounds bounds = new Bounds(xForm.getLowerRightVisioPoint(), xForm.getUpperLeftVisioPoint());
		return bounds;
	}
	
	
	public Point getCentralPin() {
		return xForm.getCentralPin();
	}
	
	public void setCentralPin(Point newPin) {
		xForm.setCentralPin(newPin);
	}
	
}
