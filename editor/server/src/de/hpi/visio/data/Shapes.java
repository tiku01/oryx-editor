package de.hpi.visio.data;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Shapes")
public class Shapes {
	
	@Element("Shape")
	public ArrayList<Shape> shapes;

}
