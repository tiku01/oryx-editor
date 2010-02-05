package de.hpi.yawl;

import de.hpi.yawl.resourcing.InitiatorType;
import de.hpi.yawl.resourcing.DistributionSet;

public class YResourcing implements FileWritingForYAWL {

	private InitiatorType start;
	private InitiatorType allocate;
	private DistributionSet allocateDistributionSet;
	private InitiatorType offer;
	private DistributionSet offerDistributionSet;
	
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

	public void setAllocateDistributionSet(DistributionSet allocateDistributionSet) {
		this.allocateDistributionSet = allocateDistributionSet;
	}

	public DistributionSet getAllocateDistributionSet() {
		return allocateDistributionSet;
	}

	public void setOffer(InitiatorType offer) {
		this.offer = offer;
	}

	public InitiatorType getOffer() {
		return offer;
	}
	
	public void setOfferDistributionSet(DistributionSet offerDistributionSet) {
		this.offerDistributionSet = offerDistributionSet;
	}

	public DistributionSet getOfferDistributionSet() {
		return offerDistributionSet;
	}

	private String writeOfferToYAWL(String s) {
		s += String.format("\t<offer initiator=\"%s\" ", offer.toString().toLowerCase());
		if((offer == InitiatorType.SYSTEM) && (offerDistributionSet != null) 
				&& (offerDistributionSet.getInitialSetList().size() > 0)){
			s += ">\n";
			s += offerDistributionSet.writeToYAWL();
			s += "\t</offer>\n";
		}else
			s += "/>\n";
		
		return s;
	}
	
	private String writeAllocateToYAWL(String s) {
		s += String.format("\t<allocate initiator=\"%s\" ", allocate.toString().toLowerCase());
		if((allocate == InitiatorType.SYSTEM) && (allocateDistributionSet != null) 
				&& (allocateDistributionSet.getInitialSetList().size() > 0)){
			s += ">\n";
			s += allocateDistributionSet.writeToYAWL();
			s += "\t</allocate>\n";
		}else
			s += "/>\n";
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
