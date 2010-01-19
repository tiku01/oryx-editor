package de.hpi.bpmn2xpdl;

import com.thoughtworks.xstream.XStream;

public class XPDLParticipantType extends XMLConvertable {

	protected String type;
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:ParticipantType", XPDLParticipantType.class);
		
		xstream.useAttributeFor(XPDLParticipantType.class, "type");
		xstream.aliasField("Type", XPDLParticipantType.class, "type");
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String typeValue) {
		type = typeValue;
	}
}
