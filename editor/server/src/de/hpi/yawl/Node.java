package de.hpi.yawl;

import java.util.ArrayList;
import java.util.List;

public abstract class Node {
	
	protected String id = "";
	protected String name = "";
	protected List<FlowRelationship> incomingEdges;
	protected List<FlowRelationship> outgoingEdges;
	
	public Node(String ID, String name){
		setID(ID);
		setName(name);
	}
	
	public String getID() {
		return this.id;
	}
	
	public void setID(String anID) {
		//int tab = anID.indexOf('\t'); //determine the position of a tab
		//String name = tab < 0 ? anID : anID.substring(0, tab); //if no tab assign id, else take id without tab
		this.id = anID;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String nodeName){
		this.name = nodeName;
	}
	
	public List<FlowRelationship> getIncomingEdges() {
		if (incomingEdges == null)
			incomingEdges = new ArrayList<FlowRelationship>();
		return incomingEdges;
	}

	public List<FlowRelationship> getOutgoingEdges() {
		if (outgoingEdges == null)
			outgoingEdges = new ArrayList<FlowRelationship>();
		return outgoingEdges;
	}
}