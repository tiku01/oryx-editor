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

MIN_SIMILARITY = 0.0;
WEIGHT_SKIPPED_NODES = 1.0;
WEIGHT_SUBSTITUTED_NODES = 1.0;
WEIGHT_CONTEXT_SIMILARITY = 0.5;

AutoConnector = function(){
	this.nodesA = null;
	this.nodesB = null;
	this.NumberOfNodesInA = 0;
	this.NumberOfNodesInB = 0;
	this.shapesA = null;
	this.shapesB = null;
	this.useContextSimilarity = false;
	this.graphEditSimilarity = 0;

};

AutoConnector.prototype = {
		
		autoConnect : function(viewerA, viewerB){			
			this.shapesA = viewerA.canvas.shapes;
			this.shapesB = viewerB.canvas.shapes;	
			this.useContextSimilarity = true;
			return this.autoConnectNodes(viewerA.canvas.getNodes(), viewerB.canvas.getNodes());
		},
		
		
		autoConnectNodes : function(nodesA, nodesB){
			
			var isFunction = function isFunction(obj){				
				return Object.prototype.toString.call(obj) === "[object Function]";
			}
	
			var getArrayLength = function (array) {
				var result = 0;
				for ( key in array ) {
					if(array.hasOwnProperty(key) && !isFunction(array[key])) {				
						result++;
					}
				}
				return result;
			}			
			
			this.nodesA = nodesA;
			this.nodesB = nodesB;
			//get Nodes returns an object record, need to determine size manually
			this.NumberOfNodesInA = 0;
			this.NumberOfNodesInB = 0;
			var openPairs = [];
			var currentPair;
			this.NumberOfNodesInA = getArrayLength(this.nodesA);
			this.NumberOfNodesInB = getArrayLength(this.nodesB);
			for (var keyA in this.nodesA) {	
				for (var keyB in this.nodesB) {
					//getNodes() of the MOVI api is broken, it uses Array as base class for an simulated associative array/hashmap instead of Object
					//read more why this is bad e.g.: http://andrewdupont.net/2006/05/18/javascript-associative-arrays-considered-harmful/
					//thus I use a kind of workaround to avoid iterating over functions
					if(this.nodesA.hasOwnProperty(keyA) && !isFunction(nodesA[keyA]) && this.nodesB.hasOwnProperty(keyB) && !isFunction(nodesB[keyB])) {					
						if (this.NumberOfNodesInA!=0 && this.NumberOfNodesInB!=0) {					
							currentPair = new OpenPair(this.nodesA[keyA],this.nodesB[keyB]);
							if (this.useContextSimilarity) {
								var contextSimilarity = this.contextSimilarity(currentPair.nodes[0], currentPair.nodes[1]);								
								currentPair.similarity = (currentPair.similarity + contextSimilarity*WEIGHT_CONTEXT_SIMILARITY)/
															(1+WEIGHT_CONTEXT_SIMILARITY);
							}
							if (currentPair.similarity > MIN_SIMILARITY) openPairs.push(currentPair);
						}
					}
				}
			}
			this.mapping = this.optimalNodeMapping(openPairs);
			if (this.useContextSimilarity) {
				return this.mapping;
			}
			return this.mapping;
		},
		
		
		/*
		 * used to sort openPairs ascending by similarity
		 */
		sortSimilarity : function(a,b) {
			return  a.similarity - b.similarity;
		},
		
		/*
		 * //assumes openPairs is sorted by sortSimilarity
		 */
		maximalSimilarity : function(openPairs) {
			return openPairs[openPairs.length-1].similarity;
		},
		
		removePairsContaining : function(pairs, node1, node2) {
			var i = 0;
			while (i<pairs.length) {
				if ((pairs[i].node1.resourceId==node1.resourceId) || (pairs[i].node2.resourceId==node2.resourceId)) {
					pairs.splice(i,1);
					i--;
				}
				i++;
			}
		},
		
		/*
		 * implements an adaption of the Greedy Algorithm 
		 */
		optimalNodeMapping : function(openPairs) {
			// sort open pairs by Similarity
			if (this.useContextSimilarity) {
				var map = [];
			}
			var map = [];
			openPairs.sort(this.sortSimilarity);
			var totalNumberNodes = this.NumberOfNodesInA + this.NumberOfNodesInB;
			var lastDistance = 1.0; //just needs to bigger than new distance to enter loop
			var newDistance = 1.0;
			var mapDistance = 0.0; //absolute value
			do {				
				var choosenPair = openPairs.pop();
				if (!choosenPair) {					
					if (this.NumberOfNodesInA==0 && this.NumberOfNodesInB==0)
						lastDistance = 0;
					break;						
				}
				lastDistance = newDistance;
				newDistance = this.graphEditDistance(totalNumberNodes, map, mapDistance,choosenPair);
				if (newDistance<=lastDistance) {
					mapDistance = mapDistance + 1.0 - choosenPair.similarity;
					map.push(choosenPair);
					this.removePairsContaining(openPairs,choosenPair.node1,choosenPair.node2);
				}				
			} while (newDistance<lastDistance)
			this.graphEditSimilarity = 1.00 - lastDistance;
			return map;
	    	
	    },
	    
		/*
		 * Calculates a value between 0 and 1 representing the difference of two Graphs
		 */
		graphEditDistance : function(totalNumberNodes, map, mapDissimilarity, newPair) {
	    	//two times because there are two nodes in each connection
	    	var fractionSkippedNodes = (totalNumberNodes-2*(map.length+1))/totalNumberNodes;
	    	var fractionSubstitutedNodes = (mapDissimilarity + 1.0 - newPair.similarity)/(map.length+1);
	    	return (fractionSkippedNodes*WEIGHT_SKIPPED_NODES + fractionSubstitutedNodes*WEIGHT_SUBSTITUTED_NODES)
	    	/(WEIGHT_SKIPPED_NODES + WEIGHT_SUBSTITUTED_NODES);
	    },
		
		getOutgoingNodes : function(node, originShapes) {
			result = [];
			if (node.isNode()) {
				for (var i=0;i<node.outgoing.length;i++) {
					var currentEdge = originShapes[node.outgoing[i].resourceId];
					var currentNode = originShapes[currentEdge.outgoing[0].resourceId]
					if (currentNode)
						result.push(currentNode);
				}
				return result;
			}
			
		},
		
		getIncommingNodes : function(node, originShapes) {
			
			var getIncommingObjects = function(object, originShapes) {
				result = [];
				for (var key in originShapes) {
					for (var i=0;i<originShapes[key].outgoing.length;i++) {				
						if (originShapes[key].outgoing[i].resourceId==object.resourceId)
							result.push(originShapes[key]);
					}
				}
				return result;		
			}
			
			result = [];
			if (node.isNode()) {
				var incommingEdges = getIncommingObjects(node, originShapes);			
				for (var i=0;i<incommingEdges.length;i++) {				
					var incommingNodes = getIncommingObjects(incommingEdges[i],originShapes);
					if (incommingNodes.length>0) {
						result = result.concat(incommingNodes);
					}
				}			
				return result;	
			}
		},
		
		/*
		 * Considers the similarity of directly preceding and succeeding nodes, if they are more similar
		 * the nodes itself will be more similar
		 */
		contextSimilarity : function(node1, node2) {
			var outgoingNode1 = this.getOutgoingNodes(node1, this.shapesA);
			var outgoingNode2 = this.getOutgoingNodes(node2, this.shapesB);
			var incommingNode1 = this.getIncommingNodes(node1, this.shapesA);
			var incommingNode2 = this.getIncommingNodes(node2, this.shapesB);
			
			if (incommingNode1.length==4 && incommingNode2.length==2) {
				var c=0;
			}
			var incommingAutoConnector = new AutoConnector();
			incommingAutoConnector.autoConnectNodes(incommingNode1, incommingNode2);
			
			var outgoingAutoConnector = new AutoConnector();
			outgoingAutoConnector.autoConnectNodes(outgoingNode1, outgoingNode2);
			
			var similarity = incommingAutoConnector.graphEditSimilarity + outgoingAutoConnector.graphEditSimilarity;
			similarity = similarity/2.00
			return similarity;
			
		}
	    
	    
	    
	    
		
		
		
}



