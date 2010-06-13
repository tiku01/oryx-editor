package de.hpi.visio.data;

import org.oryxeditor.server.diagram.Bounds;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Shape")
public class Shape {

	@Attribute("NameU")
	public String name;
	
	@Element("XForm")
	public XForm xForm;
	
	@Element("Text")
	public Label label;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
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
	
	public Bounds getBoundsForPage(Page visioPage){
		Bounds bounds = new Bounds(xForm.getLowerRightPointForPage(visioPage), 
				xForm.getUpperLeftPointForPage(visioPage));
		return bounds;
	}
	
}
