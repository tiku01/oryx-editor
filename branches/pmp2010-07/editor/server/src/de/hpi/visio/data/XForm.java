package de.hpi.visio.data;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Text")
public class XForm {
	
	@Element("PinX")
	public FormValue positionX;
	
	@Element("PinY")
	public FormValue positionY;
	
	@Element("Width")
	public FormValue width;
	
	@Element("Heigth")
	public FormValue heigth;

}
