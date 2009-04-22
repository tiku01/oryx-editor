package de.hpi.yawl;

public class Edge extends FlowRelationship {

	public enum EdgeType {
		NORMAL, RESET
	}
	
	private boolean defaultEdge;
    private String predicate = "";
    private int ordering = 0;
    protected EdgeType edgeType = EdgeType.NORMAL;
    
    public Edge(Node edgeSource, Node edgeTarget, EdgeType type, boolean defaultEdge, String predicate, int ordering){
    	setSource(edgeSource);
    	setTarget(edgeTarget);
    	setEdgeType(type, defaultEdge, predicate, ordering);
    }
    
    public EdgeType getEdgeType() {
		return edgeType;
	}

	public void setEdgeType(EdgeType edgeType, boolean defaultEdge, String predicate, int ordering) {
		switch(edgeType){
		case NORMAL:{
			setEdgeToNormal(defaultEdge, predicate, ordering);
			break;
		}
		
		case RESET:{
			setEdgeToReset();
			break;
		}
		}
	}
	
	public void setEdgeToNormal(boolean defaultEdge, String predicate, int ordering){
		this.edgeType = EdgeType.NORMAL;
		
		setDefault(defaultEdge);
		setPredicate(predicate);
		setOrdering(ordering);
	}
	
	public void setEdgeToReset(){
		this.edgeType = EdgeType.RESET;
		setPredicate(null);
		setOrdering(0);
	}
	
	public boolean isNormal(){
		return this.edgeType == EdgeType.NORMAL;
	}
	
	public boolean isDefault() {
		return defaultEdge;
	}

	public void setDefault(boolean value) {
		this.defaultEdge = value;
	}
	
	public String getPredicate() {
		return predicate;
	}

	public void setPredicate(String givenPredicate) {
		this.predicate = givenPredicate;
	}
	
	public int getOrdering() {
		return ordering;
	}
	
	public void setOrdering(int predicateOrdering) {
		this.ordering = predicateOrdering;
	}
	
	/**
     * Export to YAWL file.
     * @param splitType int The split type of the originating YAWL node.
     * @return String The string to export for this YAWLDecompositon.
     */
    public String writeToYAWL(Task.SplitJoinType splitType, EdgeType type) {
        String s = "";
        if (type != this.edgeType) {
            return "";
        } else if (type == EdgeType.NORMAL) {
            s += "\t\t\t\t\t<flowsInto>\n";
            s += "\t\t\t\t\t\t<nextElementRef id=\"Node" + getTarget().getID() +
                    "\"/>\n";
            if (predicate != null && predicate.length() > 0) {
                boolean hasPredicate = false;
                if (splitType == Task.SplitJoinType.XOR) {
                    s += "\t\t\t\t\t\t<predicate ordering=\"" + ordering +
                            "\">";
                    hasPredicate = true;
                } else if (splitType == Task.SplitJoinType.OR) {
                    s += "\t\t\t\t\t\t<predicate>";
                    hasPredicate = true;
                }
                if (hasPredicate) {
                    // Predicate might contain special characters. Have them replaced.
                    s += predicate.replaceAll("&", "&amp;").replaceAll("<",
                            "&lt;").replaceAll(">",
                                               "&gt;");
                    s += "</predicate>\n";
                }
            }
            if (this.defaultEdge) {
                s += "\t\t\t\t\t\t<isDefaultFlow/>\n";
            }
            s += "\t\t\t\t\t</flowsInto>\n";
        } else if (type == EdgeType.RESET) {
            s += "\t\t\t\t\t<removesTokens id=\"Node" + getTarget().getID() +
                    "\"/>\n";
        }
        return s;
    }
}
