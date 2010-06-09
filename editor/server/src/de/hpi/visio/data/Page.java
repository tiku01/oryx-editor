package de.hpi.visio.data;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Page")
public class Page {
	
	@Element("Shapes")
	public Shapes shapes;

}
