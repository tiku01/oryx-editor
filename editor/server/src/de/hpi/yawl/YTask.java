package de.hpi.yawl;

import java.util.ArrayList;

public class YTask extends YNode{
	
	public enum SplitJoinType {
		NONE, AND, OR, XOR
	}
	
	public enum CreationMode {
		DYNAMIC, STATIC
	}
	
	private SplitJoinType joinType = SplitJoinType.NONE;
	private SplitJoinType splitType = SplitJoinType.NONE;
	private YDecomposition decomposesTo = null;
	private int minimum = 0;
	private int maximum = 0;
	private int threshold = 0;
	private CreationMode creationMode = CreationMode.STATIC;
	private boolean isMultipleTask = false;
	private ArrayList<YNode> cancellationSet;
	private ArrayList<YVariableMapping> startingMappings = new ArrayList<YVariableMapping>();
	private ArrayList<YVariableMapping> completedMappings = new ArrayList<YVariableMapping>();
	
	public YTask(String ID, String name, SplitJoinType join, SplitJoinType split, YDecomposition decomposesTo){
		super(ID, name);
		
		setJoinType(join);
		setSplitType(split);
		setDecomposition(decomposesTo);
	}
	
	public YTask(String ID, String name, SplitJoinType join, SplitJoinType split, YDecomposition decomposesTo, int min, int max, int threshold, CreationMode mode){
		this(ID, name, join, split, decomposesTo);
		
		setMinimum(min);
		setMaximum(max);
		setThreshold(threshold);
		setCreationMode(mode);
		setIsMultipleTask(true);
		
		YVariable nullVariable = new YVariable("null", "", "", "", false);
		
		YVariableMapping startingVarMap = new YVariableMapping("", nullVariable);
		YVariableMapping completedVarMap = new YVariableMapping("", nullVariable);
		
		startingMappings.add(startingVarMap);
		completedMappings.add(completedVarMap);
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
	
	public YDecomposition getDecomposition() {
		return this.decomposesTo;
	}
	
	public void setDecomposition(YDecomposition decomposesTo) {
		this.decomposesTo = decomposesTo;
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
	
	public ArrayList<YNode> getCancellationSet(){
		if (cancellationSet == null)
			cancellationSet = new ArrayList<YNode>();
		return cancellationSet;
	}
	
	public ArrayList<YVariableMapping> getStartingMappings(){
		if (startingMappings == null)
			startingMappings = new ArrayList<YVariableMapping>();
		return startingMappings;
	}
	
	public ArrayList<YVariableMapping> getCompletedMappings(){
		if (completedMappings == null)
			completedMappings = new ArrayList<YVariableMapping>();
		return completedMappings;
	}
	
	private void createCompletedNullMapping() {
		YVariable nullVariable = new YVariable("null", "", "", "", false);
		YVariableMapping completedVarMap = new YVariableMapping("", nullVariable);
		
		completedMappings.add(completedVarMap);
		
	}

	private void createStartingNullMapping() {
		YVariable nullVariable = new YVariable("null", "", "", "", false);
		YVariableMapping startingVarMap = new YVariableMapping("", nullVariable);
		
		startingMappings.add(startingVarMap);
		
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
			for(YFlowRelationship flow: this.getOutgoingEdges()){
				if (flow instanceof YEdge){
					YEdge edge = (YEdge)flow;
					s += edge.writeToYAWL(this.splitType, YEdge.EdgeType.NORMAL);
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
				for(YNode removeNode: getCancellationSet()){
					s += "\t\t\t\t\t<removesTokens id=\"" + removeNode.getID() + "\"/>\n";
				}
			}
			
			//if the task is a multiple task, the variable mappings may not be empty
			if(isMultipleTask()){
				if(getStartingMappings().size() == 0)
					createStartingNullMapping();
				
				if(getCompletedMappings().size() == 0)
					createCompletedNullMapping();
			}
			
			if (getStartingMappings().size() > 0){
				s += "\t\t\t\t\t<startingMappings>\n";
				for(YVariableMapping mapping : getStartingMappings()){
					s += mapping.writeToYAWL();
				}
				s += "\t\t\t\t\t</startingMappings>\n";
			}
			
			if (getCompletedMappings().size() > 0){
				s += "\t\t\t\t\t<completedMappings>\n";
				for(YVariableMapping mapping : getCompletedMappings()){
					s += mapping.writeToYAWL();
				}
				s += "\t\t\t\t\t</completedMappings>\n";
			}
			
            if (decomposesTo != null) {
                s += "\t\t\t\t\t<decomposesTo id=\"" + getDecomposition().getID() + "\"/>\n";
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
