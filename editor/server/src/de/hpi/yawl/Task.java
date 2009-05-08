package de.hpi.yawl;

import java.util.ArrayList;

public class Task extends Node{
	
	public enum SplitJoinType {
		NONE, AND, OR, XOR
	}
	
	public enum CreationMode {
		DYNAMIC, STATIC
	}
	
	private SplitJoinType joinType = SplitJoinType.NONE;
	private SplitJoinType splitType = SplitJoinType.NONE;
	private String decomposesTo = null; // Name of subdecomposition.
	private int minimum = 0;
	private int maximum = 0;
	private int threshold = 0;
	private CreationMode creationMode = CreationMode.STATIC;
	private boolean isMultipleTask = false;
	private ArrayList<Node> cancellationSet;
	private ArrayList<VariableMapping> startingMappings = new ArrayList<VariableMapping>();
	private ArrayList<VariableMapping> completedMappings = new ArrayList<VariableMapping>();
	
	public Task(String ID, String name, SplitJoinType join, SplitJoinType split, String decomposesTo){
		super(ID, name);
		
		setJoinType(join);
		setSplitType(split);
		setDecomposition(decomposesTo);
	}
	
	public Task(String ID, String name, SplitJoinType join, SplitJoinType split, String decomposesTo, int min, int max, int threshold, CreationMode mode){
		this(ID, name, join, split, decomposesTo);
		
		setMinimum(min);
		setMaximum(max);
		setThreshold(threshold);
		setCreationMode(mode);
		setIsMultipleTask(true);
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
	
	public int getMinimum(){
		return this.minimum;
	}
	
	public void setMinimum(int min){
		this.minimum = min >= 0 ? min : 0;
	}
	
	public int getMaximum(){
		return this.maximum;
	}
	
	public void setMaximum(int max){
		this.maximum = max >= this.minimum ? max : 0;
	}
	
	public int getThreshold(){
		return this.threshold;
	}
	
	public void setThreshold(int threshold){
		this.threshold = threshold >= 0 ? threshold : 0;
	}
	
	public CreationMode getCreationMode(){
		return this.creationMode;
	}
	
	public void setCreationMode(CreationMode mode){
		this.creationMode = mode;
	}
	
	public boolean isMultipleTask(){
		return this.isMultipleTask;
	}
	
	public void setIsMultipleTask(boolean multiple){
		this.isMultipleTask = multiple;
	}
	
	public ArrayList<Node> getCancellationSet(){
		if (cancellationSet == null)
			cancellationSet = new ArrayList<Node>();
		return cancellationSet;
	}
	
	public ArrayList<VariableMapping> getStartingMappings(){
		if (startingMappings == null)
			startingMappings = new ArrayList<VariableMapping>();
		return startingMappings;
	}
	
	public ArrayList<VariableMapping> getCompletedMappings(){
		if (completedMappings == null)
			completedMappings = new ArrayList<VariableMapping>();
		return completedMappings;
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
			
			s +="\t\t\t\t<task id=\"Node" + getID() + "\">\n";

			s +="\t\t\t\t\t<name>" + getName() + "</name>\n";

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

			// Third, reset set
			if (getCancellationSet().size() > 0){
				for(Node removeNode: getCancellationSet()){
					s += "\t\t\t\t\t<removesTokens id=\"" + removeNode.getID() + "\"/>\n";
				}
			}
			
			if (getStartingMappings().size() > 0){
				s += "\t\t\t\t\t<startingMappings>\n";
				for(VariableMapping mapping : getStartingMappings()){
					mapping.writeToYAWL();
				}
				s += "\t\t\t\t\t</startingMappings>\n";
			}
			
			if (getCompletedMappings().size() > 0){
				s += "\t\t\t\t\t<completedMappings>\n";
				for(VariableMapping mapping : getCompletedMappings()){
					mapping.writeToYAWL();
				}
				s += "\t\t\t\t\t</completedMappings>\n";
			}
			
            if (decomposesTo.length() > 0) {
                s += "\t\t\t\t\t<decomposesTo id=\"" + getDecomposition() + "\"/>\n";
            }
            if (isMultipleTask()) {
                s += "\t\t\t\t\t<minimum>" + getMinimum() + "</minimum>\n";
                s += "\t\t\t\t\t<maximum>" + getMaximum() + "</maximum>\n";
                s += "\t\t\t\t\t<threshold>" + getThreshold() + "</threshold>\n";            
                s += "\t\t\t\t\t<creationMode code=\"" + (getCreationMode() == CreationMode.DYNAMIC ? "dynamic" : "static") + "\" />\n";
            }
            
			s +="\t\t\t\t</task>\n";
		}
		return s;
	}
}
