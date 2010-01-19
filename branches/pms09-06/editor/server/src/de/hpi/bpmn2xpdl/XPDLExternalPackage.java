package de.hpi.bpmn2xpdl;

import com.thoughtworks.xstream.XStream;

public class XPDLExternalPackage extends XPDLThing {
	
	protected String href;
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:ExternalPackage", XPDLExternalPackage.class);
		
		xstream.useAttributeFor(XPDLExternalPackage.class, "href");
		xstream.aliasField("href", XPDLExternalPackage.class, "href");
	}
	
	public String gethref() {
		return href;
	}
	
	public void sethref(String url) {
		href = url;
	}
}
