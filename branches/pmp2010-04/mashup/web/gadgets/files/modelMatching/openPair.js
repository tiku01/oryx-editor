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

DIFFERENT_TYPE_PENALTY = 0.5; //the penalty given to the similarity of two nodes if their types are different
SEMI_DIFFERENT_TYPE_PENALTY = 0.25; //the penalty given to the similarity of two nodes if their types are different, but somehow similar (e.g. events in BPMN)
WEIGHT_STRUCTURAL_SIMILARITY = 1; 
WEIGHT_SYNTACTIC_SIMILARITY = 0.75;

/**
* This class constitutes a container for two Nodes.
* It calculates their similarity by using only information available in this two nodes
* AutoConnector uses methods where information about the whole graph is required e.g. for context similarity
* @class OpenPair
* @constructor
* @param {Node} node1
* @param {Node} node2
*/
OpenPair = function(node1, node2){
	this.nodes = new Array(node1, node2);
	this.similarity = this.similarity(node1, node2);

};

OpenPair.prototype = {
	/**
	 * Calculates the Similarity of two nodes 
	 * @param {Node} node1
	 * @param {Node} node2
	 * @return float value between 0..1	 
	 */
	similarity : function(node1, node2) {
		var typeSimilarity = this.typeSimilarity(node1, node2);
		var label1 = this.getNameForNode(node1);
		var label2 = this.getNameForNode(node2);		
		label1 = this.preprocessLabel(label1);
		label2 = this.preprocessLabel(label2);
		var syntacticSimilarity = this.syntacticSimilarity(label1, label2);
		return (typeSimilarity*WEIGHT_STRUCTURAL_SIMILARITY
				+ syntacticSimilarity*WEIGHT_SYNTACTIC_SIMILARITY) 
				/(WEIGHT_STRUCTURAL_SIMILARITY+WEIGHT_SYNTACTIC_SIMILARITY)
	},

	/**
	 * Calculates similarity between two strings
	 * implements Damerau-Levenshtein-Distance
	 * @param {String} label1
	 * @param {String} label2
	 * @return float value between 0..1	 
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
	
	/**
	 * normalizes labels to prepare them for optimal comparison
	 * gets rid of artifacts that do not contribute to meaning: translates all laters to lower case	
	 */
	preprocessLabel : function(label) {
	    label = label.toLowerCase();
	    //more to add... remove common words like "the", remove special charachters
	    return label;
	},
	
	/**
	 * Considers the type of a node, e.g. two nodes with the same type are more likely to match than 
	 * nodes with different types
	 * @param {Node} node1
	 * @param {Node} node2
	 * @return float value between 0..1	 
	 */
	typeSimilarity : function(node1, node2) {
		var type1 = node1.stencil.id;
		var type2 = node2.stencil.id;
		//same types
		if (type1==type2) return 1.00;
		//both subtype of event -> lower penalty 
		if (type1.search("Event")!=-1 && type2.search("Event")!=-1) return 1.00 - SEMI_DIFFERENT_TYPE_PENALTY;
		return 1.00 - DIFFERENT_TYPE_PENALTY * this.syntacticSimilarity(type1, type2);
		
	},
	
	/**
	 * Trys to determine the attribute that contains the name of this node
	 * @param {Node} node the given Node which can belong to a BPMN, EPC model or petri net and possbily more
	 * @return the name of the node
	 */
	getNameForNode : function (node) {
		if (node.properties.name || node.properties.name=="") {
			return node.properties.name;
		} else if (node.properties.title || node.properties.title==""){
			return node.properties.title;
		} else if (node.properties.caption || node.properties.caption==""){
			return node.properties.caption;
		} else if (node.properties.classname || node.properties.caption==""){
			return node.properties.classname;
		} else if (node.name || node.name=="") {
			return node.name;
		} else if (node.title || node.name=="") {
			return node.title;
		} else if (node.caption || node.caption=="") {
			return node.caption;
		} else if (node.resourceId || node.resourceId=="") {
			return node.resourceId;
		} else return "unknown";
	},
	

	//Tests
	
	testsyntacticSimilarity : function() {
		  var sim1 = this.syntacticSimilarity("Schuh","Schule");
		  var sim2 = this.syntacticSimilarity("Schuh","Schuhe");
		  assert(sim1<sim2);		 
	},
	
	testpreprocessLabel  : function() {
		var l1 = this.preprocessLabel("Schuh");
		assert(l1=="schuh");
	},
	
	testtypeSimilarity : function() {
		var n1 = {stencil : {id : "Event"}};
		var n2 = {stencil : {id : "Task"}};
		assert(this.typeSimilarity(n1, n2)==(1.00 - DIFFERENT_TYPE_PENALTY));
				
	}
	
	
	
}