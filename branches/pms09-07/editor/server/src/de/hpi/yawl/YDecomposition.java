package de.hpi.yawl;

import java.util.*;

public class YDecomposition {
	
	public enum ExternalInteraction {
		MANUAL, AUTOMATED
	}
	
	private ArrayList<YNode> nodes = new ArrayList<YNode>();
	private ArrayList<YEdge> edges = new ArrayList<YEdge>();
	
    private String id; // The id of the decomposition
    private boolean isRootNet; // Whether this decomposition is the root
    private String xsiType; // the xsi:type of the decomposition
    
    private ArrayList<YVariable> inputParameters = new ArrayList<YVariable>();
    private ArrayList<YVariable> outputParameters = new ArrayList<YVariable>();
    private ArrayList<YVariable> localVariables = new ArrayList<YVariable>();
    
	private ExternalInteraction externalInteraction = ExternalInteraction.MANUAL;
	private String codelet = "";

    public String getID(){
    	return this.id;
    }
    
    public void setID(String ID){
    	this.id = ID;
    }
    
    /**
     * Create a new YAWL decomposition, given its name, whether it is the root, and its xsi:type.
     * @param id The given name
     * @param isRootNet Whether it is the root ("true") or not (anything else)
     * @param xsiType The xsi:type
     */
        
    public YDecomposition(String id, String isRootNet, String xsiType) {
        setID(id);
        setRootNet(isRootNet);
        setXSIType(xsiType);
    }
    
    public void setRootNet(String rootNet){
    	this.isRootNet = rootNet.equals("true");
    }
    
    public void setXSIType(String xsiType){
    	this.xsiType = xsiType;
    }
    
	public void setExternalInteraction(ExternalInteraction externalInteraction) {
		this.externalInteraction = externalInteraction;
	}

	public ExternalInteraction getExternalInteraction() {
		return externalInteraction;
	}

	public void setCodelet(String codelet) {
		this.codelet = codelet;
	}

	public String getCodelet() {
		return codelet;
	}

    /**
     * Returns whether root.
     * @return Whether root.
     */
    public boolean isRoot() {
        return isRootNet;
    }
    
    public List<YEdge> getEdges(){
    	if (edges == null)
			edges = new ArrayList<YEdge>();
		return edges;
    }
    
    public ArrayList<YNode> getNodes(){
    	if (nodes == null)
			nodes = new ArrayList<YNode>();
		return nodes;
    }
    
    /**
	 * Gets the set of edges from the first node to the second node.
	 * @param v1 the first node
	 * @param v2 the second node
	 * @return the set of edges from the first node to the second node
	 */
	public HashSet<YEdge> getEdgesBetween(YNode sourceNode, YNode targetNode) {
		HashSet<YEdge> result = new HashSet<YEdge>();
		
		for(YFlowRelationship flow: sourceNode.getOutgoingEdges()){
			if (flow instanceof YEdge){
				YEdge edge = (YEdge)flow;
				if (edge.getTarget() == targetNode) {
					result.add(edge);
				}
			}
		}
		
		return result;
	}
    
    public void addNode(YNode node){
    	nodes.add(node);
    }

    /**
     * Adds an input condition with given name.
     * @param name The given name
     */
    public YCondition addInputCondition(String id, String name) {
        YCondition condition = new YCondition(id, name, YCondition.ConditionType.IN);
        addNode(condition);
        return condition;
    }

    /**
     * Adds an output condition with given name.
     * @param name The given name
     */
    public YCondition addOutputCondition(String id, String name) {
        YCondition condition = new YCondition(id, name, YCondition.ConditionType.OUT);
        addNode(condition);
        return condition;
    }

    /**
     * Adds a (normal) condition with given name.
     * @param name The given name
     */
    public YCondition addCondition(String id, String name) {
        YCondition condition = new YCondition(id, name, YCondition.ConditionType.NONE);
        addNode(condition);
        return condition;
    }

    /**
     * Adds a task with given name, join type, split type, and subdecomposition name.
     * @param id The given identifier
     * @param name The given name
     * @param join The given join type (and, xor, or)
     * @param split The given split type (and, xor, or)
     * @param decomposesTo The given subdecomposition name.
     * @return the task created.
     */
    public YTask addTask(String id, String name, String join, String split,
                            YDecomposition decomposesTo) {
    	YTask.SplitJoinType joinType = YTask.SplitJoinType.XOR;
    	YTask.SplitJoinType splitType = YTask.SplitJoinType.AND;
    	
    	if(join.equalsIgnoreCase("and"))
    		joinType = YTask.SplitJoinType.AND;
    	else if(join.equalsIgnoreCase("xor"))
    		joinType = YTask.SplitJoinType.XOR;
    	else if(join.equalsIgnoreCase("or"))
    		joinType = YTask.SplitJoinType.OR;
        
    	if(split.equalsIgnoreCase("and"))
    		splitType = YTask.SplitJoinType.AND;
    	else if(split.equalsIgnoreCase("xor"))
    		splitType = YTask.SplitJoinType.XOR;
    	else if(split.equalsIgnoreCase("or"))
    		splitType = YTask.SplitJoinType.OR;
        
        YTask task = new YTask(id, name, joinType, splitType, decomposesTo);
        addNode(task);
        return task;
    }

    public void removeNode(YNode node) {
        nodes.remove(node);
    }
    
    public void removeEdge(YEdge edge){
    	edge.getSource().getOutgoingEdges().remove(edge);
    	edge.getTarget().getIncomingEdges().remove(edge);
    	edges.remove(edge);
    }
    
    /**
     * Adds an edge to the edges list of the decomposition.
     * @param edge The edge object
     */
    
    public void addEdge(YEdge edge) {
        edges.add(edge);
    }

    /**
     * Adds an edge from the given source node to the given destination node, given whether it is a default flow, given its predicate and its ordering.
     * @param fromNode The name of the source node
     * @param toNode The name of the destination node
     * @param isDefaultFLow Whether it is a default edge
     * @param predicate The given predicate
     * @param ordering The given predicate ordering
     */

    public YEdge addEdge(YNode fromNode, YNode toNode, boolean isDefaultFlow, String predicate,
           int ordering) {
    	
        YEdge newEdge = new YEdge(fromNode, toNode, isDefaultFlow, predicate, ordering);
        addEdge(newEdge);
        return newEdge;
    }
    
    /**
     * Adds an edge from the given source node to the given destination node.
     * @param fromNode The name of the source node
     * @param toNode The name of the destination node
     */
    
    public YEdge addEdge(YNode fromNode, YNode toNode) {
     	
         YEdge newEdge = new YEdge(fromNode, toNode);
         addEdge(newEdge);
         return newEdge;
     }
    
    public ArrayList<YVariable> getInputParams(){
    	if (inputParameters == null)
    		inputParameters = new ArrayList<YVariable>();
		return inputParameters;
    }
    
    public ArrayList<YVariable> getOutputParams(){
    	if (outputParameters == null)
    		outputParameters = new ArrayList<YVariable>();
		return outputParameters;
    }
    
    public ArrayList<YVariable> getLocalVariables(){
    	if (localVariables == null)
    		localVariables = new ArrayList<YVariable>();
		return localVariables;
    }

    /**
     * Export to YAWL file.
     * @return String The string to export for this YAWLDecompositon.
     */
    public String writeToYAWL() {
        String s = "";
        s += "\t\t<decomposition ";
        s += "id=\"" + id + "\" ";
        if (isRootNet) {
            s += "isRootNet=\"true\" ";
        }
        s += "xsi:type=\"" + xsiType + "\" >\n";
        
        if(getInputParams().size() > 0){
        	Boolean isParam = true;
        	
        	for(YVariable var : getInputParams()){
        		s += "\t\t\t\t\t<inputParam>\n";
        		s += var.writeToYAWL(isParam);
        		s += "\t\t\t\t\t</inputParam>\n";
        	}
        }
        
        if(getOutputParams().size() > 0){
        	Boolean isParam = true;
        	
        	for(YVariable var : getOutputParams()){
        		s += "\t\t\t\t\t<outputParam>\n";
        		s += var.writeToYAWL(isParam);
        		s += "\t\t\t\t\t</outputParam>\n";
        	}
        }
        
        if(getLocalVariables().size() > 0){
        	Boolean isParam = true;
        	
        	for(YVariable var : getLocalVariables()){
        		s += "\t\t\t\t\t<localVariable>\n";
        		s += var.writeToYAWL(isParam);
        		s += "\t\t\t\t\t</localVariable>\n";
        	}
        }

        Iterator<YNode> it = getNodes().iterator();
        if (it.hasNext()) {
            s += "\t\t\t<processControlElements>\n";
            for (int i = 0; i < 3; i++) {
                while (it.hasNext()) {
                    Object object = it.next();
                    if (object instanceof YTask) {
                        s += ((YTask) object).writeToYAWL(i);
                    } else if (object instanceof YCondition) {
                        s += ((YCondition) object).writeToYAWL(i);
                    }
                }
                it = getNodes().iterator();
            }
            s += "\t\t\t</processControlElements>\n";
        }
        
        if(xsiType.equals("WebServiceGatewayFactsType")){
        	if(!codelet.isEmpty())
        		s += String.format("\t\t\t<codelet>%s</codelet>\n", codelet);
        	
        	s += String.format("\t\t\t<externalInteraction>%s</externalInteraction>\n", externalInteraction.toString().toLowerCase(Locale.ENGLISH));
        }

        s += "\t\t</decomposition>\n";
        return s;
    }
}
