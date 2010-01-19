package de.hpi.bpmn2xpdl;

import com.thoughtworks.xstream.XStream;

public class XPDLParticipant extends XPDLThing {

	protected XPDLExternalReference externalReference;
	protected XPDLParticipantType participantType;
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:Participant", XPDLParticipant.class);
		
		xstream.aliasField("xpdl2:ExternalReference", XPDLParticipant.class, "externalReference");
		xstream.aliasField("xpdl2:ParticipantType", XPDLParticipant.class, "participantType");
	}
	
	public XPDLExternalReference getExternalReference() {
		return externalReference;
	}
	
	public XPDLParticipantType getParticipantType() {
		return participantType;
	}
	
	public void setExternalReference(XPDLExternalReference reference) {
		externalReference = reference;
	}
	
	public void setParticipantType(XPDLParticipantType type) {
		participantType = type;
	}
}
