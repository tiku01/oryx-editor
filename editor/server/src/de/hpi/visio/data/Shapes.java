package de.hpi.visio.data;

import java.util.ArrayList;
import java.util.List;

import org.xmappr.Element;
import org.xmappr.RootElement;

/**
 * Class for xmappr - xml to java mapping.
 * 
 * @author Thamsen
 */
@RootElement("Shapes")
public class Shapes {

	@Element("Shape")
	public List<Shape> shapes;

	public List<Shape> getShapes() {
		if (shapes == null)
			shapes = new ArrayList<Shape>();
		return shapes;
	}

	public void setShapes(List<Shape> shapes) {
		this.shapes = shapes;
	}

}
