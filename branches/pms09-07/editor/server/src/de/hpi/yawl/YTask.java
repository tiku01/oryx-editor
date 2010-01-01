package de.hpi.yawl;

import java.util.ArrayList;
import java.util.Locale;

public class YTask extends YNode{
	
	public enum SplitJoinType {
		AND, OR, XOR
	}
	
	private SplitJoinType joinType = SplitJoinType.XOR;
	private SplitJoinType splitType = SplitJoinType.AND;
	private YDecomposition decomposesTo = null;
	private String xsiType = "";
	private YMultiInstanceParam miParam = null;

	private boolean isMultipleTask = false;
	private ArrayList<YNode> cancellationSet;
	private ArrayList<YVariableMapping> startingMappings = new ArrayList<YVariableMapping>();
	private ArrayList<YVariableMapping> completedMappings = new ArrayList<YVariableMapping>();
	private YTimer timer = null;
	
	public YTimer getTimer() {
		return timer;
	}

	public void setTimer(YTimer timer) {
		this.timer = timer;
	}

	public YTask(String ID)
	{
		super(ID, "");
	}
	
	public YTask(String ID, String name)
	{
		super(ID, name);
	}
	
	public YTask(String ID, String name, SplitJoinType join, SplitJoinType split, YDecomposition decomposesTo){
		super(ID, name);
		
		setJoinType(join);
		setSplitType(split);
		setDecomposesTo(decomposesTo);
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
	
	public YDecomposition getDecomposesTo() {
		return this.decomposesTo;
	}
	
	public void setDecomposesTo(YDecomposition decomposesTo) {
		this.decomposesTo = decomposesTo;
	}
	
	public void setXsiType(String xsiType) {
		this.xsiType = xsiType;
	}

	public String getXsiType() {
		return xsiType;
	}

	public void setMiParam(YMultiInstanceParam miParam) {
		this.miParam = miParam;
	}

	public YMultiInstanceParam getMiParam() {
		return miParam;
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

	/**
	 * @param s
	 * @return
	 */
	private String writeOutgoingEdgesToYAWL(String s) {
		for(YFlowRelationship flow: this.getOutgoingEdges()){
			if (flow instanceof YEdge){
				YEdge edge = (YEdge)flow;
				s += edge.writeToYAWL(this.splitType);
			}
		}
		return s;
	}

	/**
	 * @param s
	 * @return
	 */
	private String writeCompletedMappingsToYAWL(String s) {
		if (getCompletedMappings().size() > 0){
			s += "\t\t\t\t\t<completedMappings>\n";
			for(YVariableMapping mapping : getCompletedMappings()){
				s += mapping.writeToYAWL();
			}
			s += "\t\t\t\t\t</completedMappings>\n";
		}
		return s;
	}

	/**
	 * @param s
	 * @return
	 */
	private String writeStartingMappingsToYAWL(String s) {
		if (getStartingMappings().size() > 0){
			s += "\t\t\t\t\t<startingMappings>\n";
			for(YVariableMapping mapping : getStartingMappings()){
				s += mapping.writeToYAWL();
			}
			s += "\t\t\t\t\t</startingMappings>\n";
		}
		return s;
	}

	/**
	 * @param s
	 * @return
	 */
	private String writeCancellationSetToYAWL(String s) {
		if (getCancellationSet().size() > 0){
			for(YNode removeNode: getCancellationSet()){
				s += "\t\t\t\t\t<removesTokens id=\"" + removeNode.getID() + "\"/>\n";
			}
		}
		return s;
	}
	
	/**
	 * @param s
	 * @return
	 */
	private String writeMiParamToYAWL(String s) {
		if (isMultipleTask()) {
            s += getMiParam().writeToYAWL();
        }
		return s;
	}

	/**
	 * @param s
	 * @return
	 */
	private String writeDecomposesToToYAWL(String s) {
		if (decomposesTo != null) {
            s += String.format("\t\t\t\t\t<decomposesTo id=\"%s\"/>\n", getDecomposesTo().getID());
        }
		return s;
	}

	/**
	 * @param s
	 * @return
	 */
	private String writeTimerToYAWL(String s) {
		if (timer != null){
			s += timer.writeToYAWL();
		}
		return s;
	}

	/**
	 * @param s
	 * @return
	 */
	private String writeSplitJoinTypeToYAWL(String s) {
		s += String.format("\t\t\t\t\t<join code=\"%s\"/>\n", getJoinType().toString().toLowerCase(Locale.ENGLISH));
		s += String.format("\t\t\t\t\t<split code=\"%s\"/>\n", getSplitType().toString().toLowerCase(Locale.ENGLISH));
		return s;
	}
	
	/**
	 * Export to YAWL file.
	 * @param phase Writing phase: 0 = inputCondition, 2 = outputCondition, 1 = rest.
	 * @return String The string to export for this YTask.
	 */
	public String writeToYAWL() {
		String s = "";
			
		if(!getXsiType().isEmpty())
			s += String.format("\t\t\t\t<task id=\"%s\" xsi:type=\"%s\">\n", getID(), getXsiType());
		else
			s += String.format("\t\t\t\t<task id=\"%s\">\n", getID());

		s += String.format("\t\t\t\t\t<name>%s</name>\n", getName());

		// First, normal edges
		s = writeOutgoingEdgesToYAWL(s);

		// Second, join and split type
		s = writeSplitJoinTypeToYAWL(s);

		// Third, reset set
		s = writeCancellationSetToYAWL(s);
			
		s = writeStartingMappingsToYAWL(s);
			
		s = writeCompletedMappingsToYAWL(s);
			
		s = writeTimerToYAWL(s);
			
        s = writeDecomposesToToYAWL(s);
        s = writeMiParamToYAWL(s);
            
		s +="\t\t\t\t</task>\n";
		return s;
	}
}
