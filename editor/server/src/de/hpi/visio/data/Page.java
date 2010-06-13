package de.hpi.visio.data;

import java.util.List;

import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Page")
public class Page {
	
	@Element("Shapes")
	public Shapes shapes;
	
	@Element("PageSheet")
	public PageSheet pageSheet;
	
	public List<Shape> getShapes() {
		return shapes.shapes;
	}
	
	public void setShapes(List<Shape> shapes) {
		this.shapes.shapes = shapes;
	}
	
	public Double getWidth() {
		if (pageSheet == null) 
			pageSheet = new PageSheet();
		return pageSheet.getWidth();
	}
	
	public void setWidth(Double width) {
		pageSheet.setWidth(width);
	}
	
	public Double getHeight() {
		if (pageSheet == null) 
			pageSheet = new PageSheet();
		return pageSheet.getHeight();
	}	
	
	public void setHeight(Double height) {
		pageSheet.setHeight(height);
	}
	
	public Bounds getBounds() {
		Point upperLeftPoint = new Point(0.0, 0.0);
		Point lowerRightPoint = new Point(getWidth(), getHeight());
		Bounds diagramBounds = new Bounds(lowerRightPoint, upperLeftPoint);
		return diagramBounds;
	}

}
