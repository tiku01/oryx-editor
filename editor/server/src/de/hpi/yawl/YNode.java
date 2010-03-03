package de.hpi.yawl;

import java.util.ArrayList;
import java.util.List;

public abstract class YNode implements FileWritingForYAWL {
	
	protected String id = ""; //the internal ID of a node
	protected String name = ""; //the name of a node that is shown in the editor
	protected List<YEdge> incomingEdges;
	protected List<YEdge> outgoingEdges;
	
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
	
	public List<YEdge> getIncomingEdges() {
		if (incomingEdges == null)
			incomingEdges = new ArrayList<YEdge>();
		return incomingEdges;
	}

	public List<YEdge> getOutgoingEdges() {
		if (outgoingEdges == null)
			outgoingEdges = new ArrayList<YEdge>();
		return outgoingEdges;
	}
	
	public String writeToYAWL()
	{
		//implementation of the FileWritingForYAWL interface
		return "";
	}
}