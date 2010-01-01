package de.hpi.yawl;

import java.util.ArrayList;
import java.util.List;

public abstract class YNode implements FileWritingForYAWL {
	
	protected String id = ""; //the internal ID of a node
	protected String name = ""; //the name of a node that is shown in the editor
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
	
	public String writeToYAWL()
	{
		//implementation of the FileWritingForYAWL interface
		return "";
	}
}