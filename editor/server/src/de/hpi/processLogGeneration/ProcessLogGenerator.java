package de.hpi.processLogGeneration;

import de.hpi.petrinet.PetriNet;

public class ProcessLogGenerator {
	
	private PetriNet net;
	private CompletenessOption completeness;
	private int noise;
	private boolean propabilities;
	
	public ProcessLogGenerator(PetriNet net, CompletenessOption completeness, int noise, boolean propabilities) {
		this.net = net;
		this.completeness = completeness;
		this.noise = noise;
		this.propabilities = propabilities;
	}

	public String getSerializedLog() {
		// TODO Auto-generated method stub
		return "<xml ...><completeness selected =\""+completeness+"\"/>\n"+
		"<noise degree=\""+noise+"\"/>\n"+
		"<respect-propabilities selected=\""+propabilities+"\"/>";
	}

}
