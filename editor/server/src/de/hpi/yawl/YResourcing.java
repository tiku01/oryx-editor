package de.hpi.yawl;

public class YResourcing implements FileWritingForYAWL {

	public enum InitiatorType { 
		USER, SYSTEM
	}
	
	private InitiatorType start;
	private InitiatorType allocate;
	private InitiatorType offer;
	
	public YResourcing() {
	
	}
	
	public void setStart(InitiatorType start) {
		this.start = start;
	}

	public InitiatorType getStart() {
		return start;
	}

	public void setAllocate(InitiatorType allocate) {
		this.allocate = allocate;
	}

	public InitiatorType getAllocate() {
		return allocate;
	}

	public void setOffer(InitiatorType offer) {
		this.offer = offer;
	}

	public InitiatorType getOffer() {
		return offer;
	}
	
	private String writeOfferToYAWL(String s) {
		s += String.format("\t<offer initiator=\"%s\" />\n", offer.toString().toLowerCase());
		return s;
	}
	
	private String writeAllocateToYAWL(String s) {
		s += String.format("\t<allocate initiator=\"%s\" />\n", allocate.toString().toLowerCase());
		return s;
	}
	
	private String writeStartToYAWL(String s) {
		s += String.format("\t<start initiator=\"%s\" />\n", start.toString().toLowerCase());
		return s;
	}

	@Override
	public String writeToYAWL() {
		String s = "";
		
		s += "<resourcing>\n";
		s = writeOfferToYAWL(s);
		s = writeAllocateToYAWL(s);
		s = writeStartToYAWL(s);
		s += "</resourcing>\n";
		
		return s;
	}

	
}
