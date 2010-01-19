package de.hpi.bpmn2xpdl;

import com.thoughtworks.xstream.XStream;

public class XPDLLayoutInfo extends XMLConvertable {
	
	protected String pixelsPerMillimeter;
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:LayoutInfo", XPDLLayoutInfo.class);
		
		xstream.useAttributeFor(XPDLLayoutInfo.class, "pixelsPerMillimeter");
		xstream.aliasField("PixelsPerMillimeter", XPDLPackage.class, "pixelsPerMillimeter");
	}
	
	public String getPixelsPerMillimeter() {
		return pixelsPerMillimeter;
	}
	
	public void setPixelsPerMillimeter(String pixels) {
		pixelsPerMillimeter = pixels;
	}
}
