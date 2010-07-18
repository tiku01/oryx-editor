package de.hpi.visio.data;

import java.util.ArrayList;
import java.util.List;

import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.xmappr.Element;
import org.xmappr.RootElement;

/**
 * Class for xmappr - xml to java mapping.
 * 
 * @author Thamsen
 */
@RootElement("Page")
public class Page {

	@Element("Shapes")
	public Shapes shapes;

	@Element("PageSheet")
	public PageSheet pageSheet;

	public List<Shape> getShapes() {
		if (this.shapes == null)
			this.shapes = new Shapes();
		return shapes.getShapes();
	}

	public void setShapes(List<Shape> shapes) {
		if (this.shapes == null)
			this.shapes = new Shapes();
		this.shapes.setShapes(shapes);
	}

	public void removeShape(Shape shape) {
		this.getShapes().remove(shape);
	}

	public void addShape(Shape shape) {
		if (this.shapes == null)
			this.shapes = new Shapes();
		getShapes().add(shape);
	}

	public Double getWidth() {
		if (pageSheet == null)
			pageSheet = new PageSheet();
		return pageSheet.getWidth();
	}

	public void setWidth(Double width) {
		if (pageSheet == null)
			pageSheet = new PageSheet();
		pageSheet.setWidth(width);
	}

	public Double getHeight() {
		if (pageSheet == null)
			pageSheet = new PageSheet();
		return pageSheet.getHeight();
	}

	public void setHeight(Double height) {
		if (pageSheet == null)
			pageSheet = new PageSheet();
		pageSheet.setHeight(height);
	}

	public Bounds getBounds() {
		Point upperLeftPoint = new Point(0.0, 0.0);
		Point lowerRightPoint = new Point(getWidth(), getHeight());
		Bounds diagramBounds = new Bounds(lowerRightPoint, upperLeftPoint);
		return diagramBounds;
	}

	public List<Shape> getShapesByName(String name) {
		List<Shape> specifiedShapes = new ArrayList<Shape>();
		for (Shape shape : getShapes()) {
			if (shape.getName() == null)
				continue;
			if (shape.getName().equals(name))
				specifiedShapes.add(shape);
		}
		return specifiedShapes;
	}

}
