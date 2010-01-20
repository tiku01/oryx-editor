package de.hpi.bpmn2xpdl;

import com.thoughtworks.xstream.XStream;

public class XPDLExtendedAttribute {

	protected String name;
	protected String value;
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:ExtendedAttribute", XPDLExtendedAttribute.class);
		
		xstream.useAttributeFor(XPDLExtendedAttribute.class, "name");
		xstream.aliasField("Name", XPDLExtendedAttribute.class, "name");
		xstream.useAttributeFor(XPDLExtendedAttribute.class, "value");
		xstream.aliasField("Value", XPDLExtendedAttribute.class, "value");
	}
	
	public String getName() {
		return name;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setName(String nameParameter) {
		name = nameParameter;
	}
	
	public void setValue(String valueParameter) {
		value = valueParameter;
	}
}
