package de.hpi.yawl;

import java.util.*;

public class Decomposition {
	
	private ArrayList<Node> nodes = new ArrayList<Node>();
	private ArrayList<Edge> edges = new ArrayList<Edge>();
	
    private String id; // The id of the decomposition
    private boolean isRootNet; // Whether this decomposition is the root
    private String xsiType; // the xsi:type of the decomposition
    
    private ArrayList<Variable> inputParameters = new ArrayList<Variable>();
    private ArrayList<Variable> outputParameters = new ArrayList<Variable>();
    private ArrayList<Variable> localVariables = new ArrayList<Variable>();

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
        
    public Decomposition(String id, String isRootNet, String xsiType) {
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

    /**
     * Returns whether root.
     * @return Whether root.
     */
    public boolean isRoot() {
        return isRootNet;
    }
    
    public List<Edge> getEdges(){
    	if (edges == null)
			edges = new ArrayList<Edge>();
		return edges;
    }
    
    public ArrayList<Node> getNodes(){
    	if (nodes == null)
			nodes = new ArrayList<Node>();
		return nodes;
    }
    
    /**
	 * Gets the set of edges from the first node to the second node.
	 * @param v1 the first node
	 * @param v2 the second node
	 * @return the set of edges from the first node to the second node
	 */
	public HashSet<Edge> getEdgesBetween(Node sourceNode, Node targetNode) {
		HashSet<Edge> s = new HashSet<Edge>();
		
		for(FlowRelationship flow: sourceNode.getOutgoingEdges()){
			if (flow instanceof Edge){
				Edge edge = (Edge)flow;
				if (edge.getTarget() == targetNode) {
					s.add(edge);
				}
			}
		}
		
		return s;
	}

    /**
     * Returns whether any normal edge exists form the first node to the second node.
     * @param fromNode YAWLNode The given first node.
     * @param toNode YAWLNode The given second node.
     * @return boolean Returns true if any edge from the first to the second node is a normal edge.
     */
    public boolean hasNormalEdges(Node fromNode, Node toNode) {
        HashSet<Edge> edges = this.getEdgesBetween(fromNode, toNode);
        for (Edge edge : edges) {
            if (edge.isNormal()) {
                return true;
            }
        }
        return false;
    }
    
    public void addNode(Node node){
    	nodes.add(node);
    }

    /**
     * Adds an input condition with given name.
     * @param name The given name
     */
    public Condition addInputCondition(String id, String name) {
        Condition condition = new Condition(id, name, Condition.ConditionType.IN);
        addNode(condition);
        return condition;
    }

    /**
     * Adds an output condition with given name.
     * @param name The given name
     */
    public Condition addOutputCondition(String id, String name) {
        Condition condition = new Condition(id, name, Condition.ConditionType.OUT);
        addNode(condition);
        return condition;
    }

    /**
     * Adds a (normal) condition with given name.
     * @param name The given name
     */
    public Condition addCondition(String id, String name) {
        Condition condition = new Condition(id, name, Condition.ConditionType.NONE);
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
    public Task addTask(String id, String name, String join, String split,
                            String decomposesTo) {
        Task.SplitJoinType joinType = join.equals("and") ? Task.SplitJoinType.AND : join.equals("xor") ?
                       Task.SplitJoinType.XOR :
                       join.equals("or") ? Task.SplitJoinType.OR : Task.SplitJoinType.NONE;
        
        Task.SplitJoinType splitType = split.equals("and") ? Task.SplitJoinType.AND : split.equals("xor") ?
        		Task.SplitJoinType.XOR :
                        split.equals("or") ? Task.SplitJoinType.OR : Task.SplitJoinType.NONE;
        
        Task task = new Task(id, name, joinType, splitType, decomposesTo);
        addNode(task);
        return task;
    }

    public void removeNode(Node node) {
        nodes.remove(node);
    }
    
    public void removeEdge(Edge edge){
    	edge.getSource().getOutgoingEdges().remove(edge);
    	edge.getTarget().getIncomingEdges().remove(edge);
    	edges.remove(edge);
    }
    
    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    /**
     * Adds a normal edge from the given source node to the given destination node, given whether it is a default flow, given its predicate and its ordering.
     * @param fromName The name of the source node
     * @param toName The name of the destination node
     * @param isDefaultFLow Whether it is a default edge
     * @param predicate The given predicate
     * @param ordering The given predicate ordering
     */

    public Edge addNormalEdge(Node fromNode, Node toNode, boolean isDefaultFlow, String predicate,
           int ordering) {
    	
        Edge newEdge = new Edge(fromNode, toNode, Edge.EdgeType.NORMAL, isDefaultFlow, predicate, ordering);
        addEdge(newEdge);
        return newEdge;
    }
    
    public ArrayList<Variable> getInputParams(){
    	if (inputParameters == null)
    		inputParameters = new ArrayList<Variable>();
		return inputParameters;
    }
    
    public ArrayList<Variable> getOutputParams(){
    	if (outputParameters == null)
    		outputParameters = new ArrayList<Variable>();
		return outputParameters;
    }
    
    public ArrayList<Variable> getLocalVariables(){
    	if (localVariables == null)
    		localVariables = new ArrayList<Variable>();
		return localVariables;
    }

    /**
     * Export to YAWL file.
     * @return String The string to export for this YAWLDecompositon.
     */
    public String writeToYAWL() {
        String s = "";
        s += "\t\t<decomposition\n";
        s += "\t\t\tid=\"" + id + "\"\n";
        if (isRootNet) {
            s += "\t\t\tisRootNet=\"true\"\n";
        }
        s += "\t\t\txsi:type=\"" + xsiType + "\"\n";
        s += "\t\t>\n";
        
        if(getInputParams().size() > 0){
        	for(Variable var : getInputParams()){
        		s += "\t\t\t\t\t<inputParam>\n";
        		s += var.writeToYAWL();
        		s += "\t\t\t\t\t</inputParam>\n";
        	}
        }
        
        if(getOutputParams().size() > 0){
        	for(Variable var : getOutputParams()){
        		s += "\t\t\t\t\t<outputParam>\n";
        		s += var.writeToYAWL();
        		s += "\t\t\t\t\t</outputParam>\n";
        	}
        }
        
        if(getLocalVariables().size() > 0){
        	for(Variable var : getLocalVariables()){
        		s += "\t\t\t\t\t<localVariable>\n";
        		s += var.writeToYAWL();
        		s += "\t\t\t\t\t</localVariable>\n";
        	}
        }

        Iterator<Node> it = getNodes().iterator();
        if (it.hasNext()) {
            s += "\t\t\t<processControlElements>\n";
            for (int i = 0; i < 3; i++) {
                while (it.hasNext()) {
                    Object object = it.next();
                    if (object instanceof Task) {
                        s += ((Task) object).writeToYAWL(i);
                    } else if (object instanceof Condition) {
                        s += ((Condition) object).writeToYAWL(i);
                    }
                }
                it = getNodes().iterator();
            }
            s += "\t\t\t</processControlElements>\n";
        }

        s += "\t\t</decomposition>\n";
        return s;
    }
}
