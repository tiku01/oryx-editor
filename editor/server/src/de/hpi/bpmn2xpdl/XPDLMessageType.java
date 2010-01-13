package de.hpi.bpmn2xpdl;

import com.thoughtworks.xstream.XStream;

public class XPDLMessageType extends XPDLThing {

	/* TODO:
	 * From
	 * To
	 * FaultName
	 * DataMapping
	 * ActualParameters
	 */
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:MessageType", XPDLMessageType.class);
	}
}
