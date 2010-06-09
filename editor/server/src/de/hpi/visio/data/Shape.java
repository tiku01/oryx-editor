package de.hpi.visio.data;

import java.util.ArrayList;

import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Shape")
public class Shape {

	@Attribute("NameU")
	public String nameU;
	
	@Element("XForm")
	public XForm xForm;
	
	@Element("Text")
	public Label label;
	
}
