package de.hpi.yawl;

import java.util.ArrayList;
import java.util.List;

import de.hpi.petrinet.FlowRelationship;

public abstract class Node {
	
	protected String id = "";
	private List<FlowRelationship> incomingFlowRelationships;
	private List<FlowRelationship> outgoingFlowRelationships;
	
	public String getID() {
		return id;
	}
	
	public void setID(String anID) {
		int tab = anID.indexOf('\t'); //determine the position of a tab
		String name = tab < 0 ? anID : anID.substring(0, tab); //if no tab assign id, else take id without tab
		id = name;
	}
	
	public List<? extends FlowRelationship> getIncomingFlowRelationships() {
		if (incomingFlowRelationships == null)
			incomingFlowRelationships = new ArrayList();
		return incomingFlowRelationships;
	}

	public List<? extends FlowRelationship> getOutgoingFlowRelationships() {
		if (outgoingFlowRelationships == null)
			outgoingFlowRelationships = new ArrayList();
		return outgoingFlowRelationships;
	}
}
