package de.hpi.yawl;

import java.util.ArrayList;
import java.util.List;

public abstract class YNode {
	
	protected String id = "";
	protected String name = "";
	protected List<YFlowRelationship> incomingEdges;
	protected List<YFlowRelationship> outgoingEdges;
	
	public YNode(String ID, String name){
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
	
	public List<YFlowRelationship> getIncomingEdges() {
		if (incomingEdges == null)
			incomingEdges = new ArrayList<YFlowRelationship>();
		return incomingEdges;
	}

	public List<YFlowRelationship> getOutgoingEdges() {
		if (outgoingEdges == null)
			outgoingEdges = new ArrayList<YFlowRelationship>();
		return outgoingEdges;
	}
}