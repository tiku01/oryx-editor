package de.hpi.yawl;

public class YEdge extends YFlowRelationship {
	
	private boolean defaultEdge;
    private String predicate = "";
    private int ordering = 0;
    
    public YEdge(YNode edgeSource, YNode edgeTarget)
    {
    	setSource(edgeSource);
    	setTarget(edgeTarget);
    	setEdgeType(false, "", 0);
    }
    
    public YEdge(YNode edgeSource, YNode edgeTarget, boolean defaultEdge, String predicate, int ordering){
    	setSource(edgeSource);
    	setTarget(edgeTarget);
    	setEdgeType(defaultEdge, predicate, ordering);
    }

	public void setEdgeType(boolean defaultEdge, String predicate, int ordering) {
		setDefault(defaultEdge);
		setPredicate(predicate);
		setOrdering(ordering);
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
		if(givenPredicate.equals("true()"))
			this.defaultEdge = true;
		else
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
    public String writeToYAWL(YTask.SplitJoinType splitType) {
        String s = "";

        s += "\t\t\t\t\t<flowsInto>\n";
        s += String.format("\t\t\t\t\t\t<nextElementRef id=\"%s\"/>\n", getTarget().getID());
        
        if (predicate != null && predicate.length() > 0) {
        	boolean hasPredicate = false;
            if (splitType == YTask.SplitJoinType.XOR) {
            	s += String.format("\t\t\t\t\t\t<predicate ordering=\"%s\">", ordering);
                hasPredicate = true;
            } else if (splitType == YTask.SplitJoinType.OR) {
            	s += "\t\t\t\t\t\t<predicate>";
                hasPredicate = true;
            }
            if (hasPredicate) {
            	// Predicate might contain special characters. Have them replaced.
                s += predicate.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
                s += "</predicate>\n";
            }
        }
        if (isDefault()) {
        	s += "\t\t\t\t\t\t<isDefaultFlow/>\n";
        }
        s += "\t\t\t\t\t</flowsInto>\n";
        return s;
    }
}
