package de.hpi.yawl;

import java.util.ArrayList;
import java.util.List;

import de.hpi.petrinet.FlowRelationship;

public abstract class Node {
	
	protected String id = "";
	protected String name = "";
	private List<FlowRelationship> incomingFlowRelationships;
	private List<FlowRelationship> outgoingFlowRelationships;
	
	public Node(String ID, String name){
		setID(ID);
		setName(name);
	}
	
	public String getID() {
		return id;
	}
	
	public void setID(String anID) {
		int tab = anID.indexOf('\t'); //determine the position of a tab
		String name = tab < 0 ? anID : anID.substring(0, tab); //if no tab assign id, else take id without tab
		id = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String nodeName){
		this.name = nodeName;
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
