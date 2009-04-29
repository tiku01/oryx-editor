package de.hpi.yawl;

public class Task extends Node{
	
	public enum SplitJoinType {
		NONE, AND, OR, XOR
	}
	
	private SplitJoinType joinType = SplitJoinType.NONE;
	private SplitJoinType splitType = SplitJoinType.NONE;
	private String decomposesTo = null; // Name of subdecomposition.
	
	public Task(String ID, String name, SplitJoinType join, SplitJoinType split, String decomposesTo){
		super(ID, name);
		
		setJoinType(join);
		setSplitType(split);
		setDecomposition(decomposesTo);
	}
	
	public SplitJoinType getJoinType(){
		return this.joinType;
	}
	
	public void setJoinType(SplitJoinType join){
		this.joinType = join;
	}
	
	public SplitJoinType getSplitType(){
		return this.splitType;
	}
	
	public void setSplitType(SplitJoinType split){
		this.splitType = split;
	}
	
	public String getDecomposition() {
		return this.decomposesTo;
	}
	
	public void setDecomposition(String decomposesTo) {
		this.decomposesTo = decomposesTo == null ? "" : decomposesTo;
	}
	
	/**
	 * Export to YAWL file.
	 * @param phase Writing phase: 0 = inputCondition, 2 = outputCondition, 1 = rest.
	 * @return String The string to export for this YAWLDecompositon.
	 */
	public String writeToYAWL(int phase) {
		String s = "";
		if (phase == 1) {
			SplitJoinType st = getSplitType();
			SplitJoinType jt = getJoinType();
			
			if (st == SplitJoinType.NONE) {
				st = SplitJoinType.AND;
			}
			if (jt == SplitJoinType.NONE) {
				jt = SplitJoinType.XOR;
			}
			
			s +="\t\t\t\t<task\n";
			s +="\t\t\t\t\tid=\"Node" + getID() + "\"\n";
			s +="\t\t\t\t>\n";

			s +="\t\t\t\t\t<name>" + getName() + "</name>\n";
			s +="\t\t\t\t\t<documentation>" + getName() + "</documentation>\n";

			// First, normal edges
			for(FlowRelationship flow: this.getOutgoingEdges()){
				if (flow instanceof Edge){
					Edge edge = (Edge)flow;
					s += edge.writeToYAWL(this.splitType, Edge.EdgeType.NORMAL);
				}
			}

			// Second, join and split type
			s +="\t\t\t\t\t<join code=\"" + (jt == SplitJoinType.AND ? "and" : (jt == SplitJoinType.OR ? "or" :
					"xor")) + "\"/>\n";
			s +="\t\t\t\t\t<split code=\"" + (st == SplitJoinType.AND ? "and" : (st == SplitJoinType.OR ?
					"or" :
					"xor")) + "\"/>\n";

			// Third, reset edges.
			for(FlowRelationship flow: this.getOutgoingEdges()){
				if (flow instanceof Edge){
					Edge edge = (Edge)flow;
					s += edge.writeToYAWL(this.splitType, Edge.EdgeType.RESET);
				}
			}
			
            if (decomposesTo.length() > 0) {
                s += "\t\t\t\t\t<decomposesTo id=\"" + getDecomposition() + "\"/>\n";
            }
			s +="\t\t\t\t</task>\n";
		}
		return s;
	}
}
