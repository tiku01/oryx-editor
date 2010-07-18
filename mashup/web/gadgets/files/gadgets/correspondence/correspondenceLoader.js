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






correspondenceLoader = function(){
	this.loadedConnectionCollection = null;
	this.urls = [];
	this.gadget = null;
	this.timeWaited = 0;
	this.data = null;
	this.onLoadingComplete = null;
};

correspondenceLoader.prototype = {

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
			for (var i=0;i<this.data.length;i++) {
				for (var j=0;j<this.data[i].models.length;j++) {
					var currentUrl = this.data[i].models[j].url;
					if (!contains(this.urls,currentUrl))
						this.urls.push(currentUrl);						
				}
			}
			this.gadget.executeWithAllViewers(this.loadRequiredModels.bind(this));
				
				
				/*
				var comment = data[i].comment;				
				var newConnection = new Connection(gadget, comment);
				this.loadedConnectionCollection.addModel()
				data[i].gadget = gadget;				
				data[i].editing = false;
				data[i].elComment = null;
				//determine all involved models
				for (var j=0;j<data[i].models.length;j++) {
					var currentUrl = data[i].models[j].url;
					if (!contains(urls,currentUrl))
						urls.push(currentUrl);					
				}
			
			
			gadget.executeWithAllViewers(this.waitUntilRequiredViewersAreAvailable);	
				 	*/
			
		},
		
		loadViewer: function(args){ 
			
			gadgets.rpc.call(
				null, 
				'dispatcher.displayModel', 
				function(reply){return}, 
				args );
	    },
	    
	    containsUri : function(array, element) {
			for (var k=0;k<array.length;k++) {
				if (array[k].viewer.getModelUri()==element)
					return true;
			}
			return false;
		},
		
		loadRequiredModels : function() {
		    	
	    	for (var i=0;i<this.urls.length;i++) {	    		
	    		if (!this.containsUri(this.gadget.availableModelViewers,this.urls[i]))
	    			this.loadViewer(this.urls[i]+'.JOB_1');
	    	}
	    	
	    	this.waitForLoadingComplete();
	    	
		},
		
		waitForLoadingComplete : function() {
			this.timeWaited = this.timeWaited + 1500;
			setTimeout(this.getAllViewersAndCheckLoadingComplete.bind(this),1500);						
		},
		
		getAllViewersAndCheckLoadingComplete : function() {			
			this.gadget.executeWithAllViewers(this.testLoadingComplete.bind(this));			
		},
		
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
		
		//determine modelviewer index by searching for URL of Model
		getIndexByURL : function(url) {
			for (var i=0;i<this.gadget.availableModelViewers.length;i++) {
				if (this.gadget.availableModelViewers[i].viewer.getModelUri()==url) {
					return this.gadget.availableModelViewers[i].index;
				}				
			}
			alert("Loading failed, viewer not found.")
		},
		
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
		
		createConnectionCollection : function() {			
			for (var i=0;i<this.data.length;i++) {
				var comment = this.data[i].comment;
				var connection = new Connection(this.gadget,comment);				
				for (var j=0;j<this.data[i].models.length;j++) {
					var currentUrl = this.data[i].models[j].url;
					var title = this.data[i].models[j].title;
					var index = this.getIndexByURL(currentUrl);
					var nodes = this.getNodesByResourceIDs(this.data[i].models[j].nodes, index);
					connection.addModel(index, title, currentUrl, nodes);								
				}
				this.loadedConnectionCollection.addConnection(connection);
			}
			if (this.onLoadingComplete!=null) {
				this.onLoadingComplete(this.loadedConnectionCollection);
			}
		}
		

};