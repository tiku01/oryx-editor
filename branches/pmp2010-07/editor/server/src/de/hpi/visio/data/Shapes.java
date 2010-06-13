package de.hpi.visio.data;

import java.util.List;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Shapes")
public class Shapes {
	
	@Element("Shape")
	public List<Shape> shapes;

}
