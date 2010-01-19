package de.hpi.bpmn2xpdl;

import com.thoughtworks.xstream.XStream;

public class XPDLExternalReference extends XMLConvertable {

	protected String location;
	protected String namespace;
	protected String xref;
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:ExternalReference", XPDLExternalReference.class);
		
		xstream.useAttributeFor(XPDLExternalReference.class, "location");
		xstream.aliasField("Location", XPDLExternalReference.class, "location");
		xstream.useAttributeFor(XPDLExternalReference.class, "namespace");
		xstream.aliasField("Namespace", XPDLExternalReference.class, "namespace");
		xstream.useAttributeFor(XPDLExternalReference.class, "xref");
		xstream.aliasField("xref", XPDLExternalReference.class, "xref");
	}
	
	public String getLocation() {
		return location;
	}
	
	public String getNamespace() {
		return namespace;
	}
	
	public String getxref() {
		return xref;
	}
	
	public void setLocation(String locationValue) {
		location = locationValue;
	}
	
	public void setNamespace(String namespaceValue) {
		namespace = namespaceValue;
	}
	
	public void setxref(String reference) {
		xref = reference;
	}
}
