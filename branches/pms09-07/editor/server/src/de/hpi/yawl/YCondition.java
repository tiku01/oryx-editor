package de.hpi.yawl;

public class YCondition extends YNode {
	
	public YCondition(String ID, String name){
		super(ID, name);
	}
	
	/**
	 * Export to YAWL file.
	 * @return String The string to export for this YAWLDecompositon.
	 */
	public String writeToYAWL() {
		String s = "";
		s +="\t\t\t\t<condition id=\"" + getID() + "\">\n";
		s +="\t\t\t\t\t<name>" + getName() + "</name>\n";
		
		s = writeOutgoingEdgesToYAWL(s);
		s +="\t\t\t\t</condition>\n";
		return s;
	}

	/**
	 * @param s
	 * @return
	 */
	protected String writeOutgoingEdgesToYAWL(String s) {
		for(YFlowRelationship flow: this.getOutgoingEdges()){
			if (flow instanceof YEdge){
				YEdge edge = (YEdge)flow;
				s += edge.writeToYAWL(YTask.SplitJoinType.AND);
			}
		}
		return s;
	}
}
