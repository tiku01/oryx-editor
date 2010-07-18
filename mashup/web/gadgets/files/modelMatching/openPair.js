/**
 * Copyright (c) 2010
 * Uwe Hartmann
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/

DIFFERENT_TYPE_PENALTY = 0.5;
SEMI_DIFFERENT_TYPE_PENALTY = 0.25;
WEIGHT_STRUCTURAL_SIMILARITY = 1;
WEIGHT_SYNTACTIC_SIMILARITY = 0.75;

OpenPair = function(node1, node2){
	this.node1 = node1;
	this.node2 = node2;
	this.nodes = new Array(node1, node2);
	this.similarity = this.similarity(node1, node2);

};

OpenPair.prototype = {
	
	similarity : function(node1, node2) {
		var structuralSimilarity = this.structuralSimilarity(node1, node2);
		var label1 = this.preprocessLabel(node1.properties.name);
		var label2 = this.preprocessLabel(node2.properties.name);
		var syntacticSimilarity = this.syntacticSimilarity(label1, label2);
		return (structuralSimilarity*WEIGHT_STRUCTURAL_SIMILARITY
				+ syntacticSimilarity*WEIGHT_SYNTACTIC_SIMILARITY) 
				/(WEIGHT_STRUCTURAL_SIMILARITY+WEIGHT_SYNTACTIC_SIMILARITY)
	},

	/*
	 * implements Damerau-Levenshtein-Distance 
	 */		
	syntacticSimilarity : function(label1, label2) {
		
		//check if both label are empty to avoid division by zero
		if ((label1.length==0) && (label2.length==0)) return 1.00;
		
		var table = new Array();
		
		for (var i = 0; i<=label1.length; i++) {
			table[i] = new Array();
			table[i][0] = i;
		}
		for (var j = 0; j<=label2.length; j++) {
			table[0][j] = j;
		}
		
		for (var i = 1; i <= label1.length; i++){
			for (var j = 1; j <= label2.length; j++){
				
				if (label1[i - 1]==label2[j - 1])
					var cost = 0;
				else
					var cost = 1;
	
				table[i][j] =
					Math.min(table[i - 1][j] + 1,     // Deletion
					Math.min(table[i][j - 1] + 1,     // Insertion
							table[i - 1][j - 1] + cost));     // Substitution
				
				//Damerau's extension: consider switched letters
				if ((i > 1) && (j > 1) && (label1[i - 1]==(label2[j - 2])) && (label1[i - 2]==(label2[j - 1]))){
					table[i][j] = Math.min(table[i][j], table[i - 2][j - 2] + cost);
				}						
			}				
		}			
		// normalize between 0..1
		var n = table[label1.length][label2.length];
		var m = Math.max(label1.length, label2.length);
		return (1.00-n/m);
				
	},
	
	/*
	 * normalizes labels to prepare them for optimal comparison
	 * gets rid of artifacts: trims the label, translates all laters to lower case,
	 * removes special characters 
	 */
	preprocessLabel : function(label) {
	    label = label.toLowerCase();
	    //label = label.trim();
	    /*for (var i=0;i<label.length;i++)
	        if(!Character.isLetter(label.charAt(i)))
	        	label.deleteCharAt(i);*/
	    return label;
	},
	
	/*
	 * Considers the type of a node, e.g. two nodes with the same type are more likely to match than 
	 * nodes with different types
	 */
	structuralSimilarity : function(node1, node2) {
		var type1 = node1.stencil.id;
		var type2 = node2.stencil.id;
		//same types
		if (type1==type2) return 1.00;
		//both subtype of event -> lower penalty 
		if (type1.search("Event")!=-1 && type2.search("Event")!=-1) return 1.00 - SEMI_DIFFERENT_TYPE_PENALTY;
		return 1.00 - DIFFERENT_TYPE_PENALTY;
		
	}
	/*
	 * 
	 *   private double groupSimilarity (List<ProcessObject> mod1 , List<ProcessObject> mod2) {
        int s1=mod1.size();
        int s2=mod2.size();
        if (s1==0 && s2==0) return 0.25;
        if (s1==0 || s2==0) return 0.00;
        Mapping startMapping = new Mapping(mod1, mod2);
        startMapping.optimalMapping();
        double editDistance = startMapping.getGraphEditDistance();
        return editDistance/(4*Math.sqrt(s1)*(double)Math.sqrt(s2));
    }

    public double ContextSimilarity(ProcessModel ModelA, ProcessModel ModelB) {

        List<ProcessEdge>incommingEdges1 = ModelA.getIncomingEdges(ProcessEdge.class,(ProcessNode) object1);
        List<ProcessEdge>incommingEdges2 = ModelB.getIncomingEdges(ProcessEdge.class,(ProcessNode) object2);
        List<ProcessEdge>outgoingingEdges1 = ModelA.getOutgoingEdges(ProcessEdge.class,(ProcessNode) object1);
        List<ProcessEdge>outgoingingEdges2 = ModelB.getOutgoingEdges(ProcessEdge.class,(ProcessNode) object2);

        List<ProcessNode>succeedingNodes1 = ModelA.getSuccessors((ProcessNode) object1);
        List<ProcessNode>succeedingNodes2 = ModelB.getSuccessors((ProcessNode) object2);
        List<ProcessNode>precedingNodes1 = ModelA.getPredecessors((ProcessNode) object1);
        List<ProcessNode>precedingNodes2 = ModelB.getPredecessors((ProcessNode) object2);

        double simInEdges = groupSimilarity(new LinkedList<ProcessObject>(incommingEdges1), new LinkedList<ProcessObject>(incommingEdges2));
        double simOutEdges = groupSimilarity(new LinkedList<ProcessObject>(outgoingingEdges1), new LinkedList<ProcessObject>(outgoingingEdges2));
        double simOutNodes = groupSimilarity(new LinkedList<ProcessObject>(succeedingNodes1), new LinkedList<ProcessObject>(succeedingNodes2));
        double simInNodes = groupSimilarity(new LinkedList<ProcessObject>(precedingNodes1), new LinkedList<ProcessObject>(precedingNodes2));


     return simOutNodes+simInNodes+simOutEdges+simInEdges;

    }
	 */
	
	
	
	
}