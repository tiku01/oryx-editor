package de.hpi.bpmn2xpdl;

import com.thoughtworks.xstream.XStream;

public class XPDLPage extends XMLConvertable {
	
	protected String height;
	protected String id;
	protected String name;
	protected String width;

	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:Page", XPDLPage.class);
		
		xstream.useAttributeFor(XPDLPage.class, "height");
		xstream.aliasField("Height", XPDLPage.class, "height");
		xstream.useAttributeFor(XPDLPage.class, "id");
		xstream.aliasField("Id", XPDLPage.class, "id");
		xstream.useAttributeFor(XPDLPage.class, "name");
		xstream.aliasField("Name", XPDLPage.class, "name");
		xstream.useAttributeFor(XPDLPage.class, "width");
		xstream.aliasField("Width", XPDLPage.class, "width");
	}
	
	public String getHeight() {
		return height;
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getWidth() {
		return width;
	}
	
	public void setHeight(String heightValue) {
		height = heightValue;
	}
	
	public void setId(String idValue) {
		id = idValue;
	}
	
	public void setName(String nameValue) {
		name = nameValue;
	}
	
	public void setWidth(String widthValue) {
		width = widthValue;
	}
}
