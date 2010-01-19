package de.hpi.bpmn2xpdl;

import com.thoughtworks.xstream.XStream;

public class XPDLApplication extends XPDLThing {

	protected XPDLExternalReference externalReference;
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:Application", XPDLApplication.class);
	
		xstream.aliasField("xpdl2:ExternReference", XPDLApplication.class, "externalReference");
	}
}
