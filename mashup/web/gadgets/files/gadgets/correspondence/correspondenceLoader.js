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





/**
 * This class is responsible for loading a Connection Collection from a file,
 * determines viewers already opened and loads required viewers and displays them
 * @class correspondenceLoader
 * @constructor
 */
correspondenceLoader = function(){
	this.loadedConnectionCollection = null;
	this.urls = [];
	this.gadget = null;
	this.timeWaited = 0;
	this.data = null;
	this.onLoadingComplete = null;
};

correspondenceLoader.prototype = {
		
		/**
		 * Loads a jsonText into a connection Collection
		 * @param {String} jsonText The JSON to load from
		 * @param {Correspondence} gadget The gadget the connection should belong to
		 * @param {ConnectionCollection} connectionCollection The connectionCollection to load to data in.
		 */
		load : function(jsonText, gadget, connectionCollection) {	
	
			var contains = function(array, element) {
				for (var k=0;k<array.length;k++) {
					if (array[k]==element)
						return true;
				}
				return false;
			};
			this.loadedConnectionCollection = connectionCollection;
			this.gadget = gadget;
			this.data = YAHOO.lang.JSON.parse(jsonText);			
			//determine required models
			if (this.data!==null) {
				for (var i=0;i<this.data.length;i++) {
					if (this.data[i]!==null) {
						for (var j=0;j<this.data[i].models.length;j++) {
							if (this.data[i].models[j]!==null) {
								var currentUrl = this.data[i].models[j].url;
								if (!contains(this.urls,currentUrl))
									this.urls.push(currentUrl);	
							}
						}
					}
				}
			}
			this.gadget.executeWithAllViewers(this.loadRequiredModels.bind(this));			

			
		},
		/**
		 * Loads a viewer and displays it in the mashup
		 * @param {String} args The URL of the viewer to load plus job e.g. url + '.JOB_1'		 * 
		 */
		loadViewer: function(args){ 
			
			gadgets.rpc.call(
				null, 
				'dispatcher.displayModel', 
				function(reply){return}, 
				args );
	    },
	    
	    /**
	     * Checks whether element in contained in array
	     * @return {boolean}
	     */
	    containsUri : function(array, element) {
			for (var k=0;k<array.length;k++) {
				if (array[k].viewer.getModelUri()==element)
					return true;
			}
			return false;
		},
		
		/**
		 * Loads the viewer which are required to display the connection
		 */
		loadRequiredModels : function() {
		    	
	    	for (var i=0;i<this.urls.length;i++) {	    		
	    		if (!this.containsUri(this.gadget.availableModelViewers,this.urls[i]))
	    			this.loadViewer(this.urls[i]+'.JOB_1');
	    	}
	    	
	    	this.waitForLoadingComplete();
	    	
		},
		
		/**
		 * waits and Checks whether all required models have completed loading
		 */
		waitForLoadingComplete : function() {
			this.timeWaited = this.timeWaited + 1500;
			setTimeout(this.getAllViewersAndCheckLoadingComplete.bind(this),1500);						
		},
		
		/**
		 * tests whether all viewers completed loading
		 */
		getAllViewersAndCheckLoadingComplete : function() {			
			this.gadget.executeWithAllViewers(this.testLoadingComplete.bind(this));			
		},
		
		/**
		 * Checks whether all required viewers are contained in local array
		 */
		allViewersAvailable : function() {
	    	for (var i=0;i<this.urls.length;i++) {	    		
	    		if (!this.containsUri(this.gadget.availableModelViewers,this.urls[i]))
	    			return false;
	    	}
			return true;
		},
		
		testLoadingComplete : function() {
			if (this.allViewersAvailable()) {
				this.createConnectionCollection()
			} else {
				if (this.timeWaited<15000) {
					this.waitForLoadingComplete();
				} else {
					alert("Could not load required Models")
				}				

			}		
		},
		
		/**
		 * determine modelviewer index by searching for URL of Model
		 */
		getIndexByURL : function(url) {
			for (var i=0;i<this.gadget.availableModelViewers.length;i++) {
				if (this.gadget.availableModelViewers[i].viewer.getModelUri()==url) {
					return this.gadget.availableModelViewers[i].index;
				}				
			}
			alert("Loading failed, viewer not found.")
		},
		
		/**
		 * Get the Nodes Object by using their Resssource IDs
		 * @param {Array of Integer}resourceIDs The resourceIDs of the nodes to load
		 * @param {viewerIndex}  The Index of the Viewer to load the Nodes from
		 * @return an Array with the requested Nodes
		 */
		getNodesByResourceIDs : function(resourceIDs, viewerIndex) {
			for (var i=0;i<this.gadget.availableModelViewers.length;i++) {
				if (this.gadget.availableModelViewers[i].index==viewerIndex) {
					var nodes = this.gadget.availableModelViewers[i].viewer.canvas.getNodes();
					break;
				}
			}			
			var resultNodes = [];
			for (var i=0;i<resourceIDs.length;i++) {	
				for (var key in nodes) {
					var n1 = nodes[key].resourceId;
					var n2 = resourceIDs[i].resourceId;
					if (n1==n2) {
						resultNodes.push(nodes[key]);														
					}
				}
			}
			return resultNodes;
		},
		/**
		 * assemebles the ConnectionCollection
		 */
		createConnectionCollection : function() {	
			var hasContent = false;
			if (this.data!==null) {
				for (var i=0;i<this.data.length;i++) {
					if (this.data[i]!==null) {
						var comment = this.data[i].comment;
						var connection = new Connection(this.gadget,comment);				
						for (var j=0;j<this.data[i].models.length;j++) {
							if (this.data[i].models[j]!==null) {
								var currentUrl = this.data[i].models[j].url;
								var title = this.data[i].models[j].title;
								var index = this.getIndexByURL(currentUrl);
								var nodes = this.getNodesByResourceIDs(this.data[i].models[j].nodes, index);
								if (index!==null && currentUrl!==null) {
									connection.addModel(index, title, currentUrl, nodes);
									hasContent = true;
								}							
							}
						}	
						if (hasContent) {
							this.loadedConnectionCollection.addConnection(connection);
						}							
						hasContent = false;
					}
				}
			}
			if (this.loadedConnectionCollection.connections.size===0) {
				alert("Input File in wrong format.")
			}
			this.gadget.connectionCollection = this.loadedConnectionCollection;			
			if (this.onLoadingComplete!==null) {
				this.onLoadingComplete(this.loadedConnectionCollection);
			}
		}
		

};