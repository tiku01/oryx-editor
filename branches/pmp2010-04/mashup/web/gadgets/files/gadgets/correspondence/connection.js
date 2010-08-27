/**
 * Copyright (c) 2010
 * Uwe Hartmann, Helen Kaltegaertner
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
     * Associates process nodes in a number of process models.    
     * @class Connection
     * @constructor
	 * @param {Correspondence} gadget The Correspondence gadget in which the connection should be displayed.
	 * @param {String} comment The comment that will be show with the connection.
     */

Connection = function(gadget, comment){
	this.comment = comment;
	this.gadget = gadget;
	this.models = [];
	this.elComment = null;
	
};

Connection.prototype = {
		
	/**
	 * Adds a new Processmodel including url and some of its nodes to the connection	
	 * @param {integer} index The index of the Viewer gadget currently displayed on the screen, which contains the model
	 * @param {ModelViewer} model The model containing nodes
	 * @param {String} url The URL to load the the ModelViewer
	 * @param {Node Array} nodes An Array of the nodes which should be part of the connection and are contained in the given model
	 */
	
	addModel : function(index, model, url, nodes){		
		this.models[index] = new ConnectionModel (model, url, nodes);		
	},
	
	/**
	 * Returns a human readable explanation of the connection for the UI.
	 * @return The description of the connection as a String
	 */
	getInfoString : function() {
		var result = this.comment + ': '
		for (var i = 0; i < (this.models.length); i++){ 
			var resourceIds = [];
			if (this.models[i]) {
			for (var j = 0; j < (this.models[i].nodes.length); j++){
				result = result + this.humanReadableName(this.models[i].nodes[j]);
				if (j<(this.models[i].nodes.length-1)) {				
					result = result + " ; "
				}
				
				resourceIds.push(this.models[i].nodes[j].resourceId)
			}
			/*
			for (var j = 0; j < (resourceIds.length); j++){
				this.comment = this.comment +" ; " + resourceIds[j]
			}*/
			if (i!=this.models.length-1) result = result + ' <-> ';
			}			
		}
		return result;
	},
	
	/**
	 * Refreshes the InfoString in the UI.	 
	 */
	updateInfoString : function() {
		this.elComment.innerHTML = this.getInfoString();
	},
	
	
	
	/**
	 * Adds the connection in correspondece gadget and displays it.
	 */
	display : function(){
		
		var el = document.createElement("li");
		el.className = "connection";
		
		var elContainer = document.createElement("div");
		elContainer.className = "connection-item";
		elContainer.onclick = this.highlightInViewer.bind(this);
		el.appendChild(elContainer);

		
		this.elComment = document.createElement("p");
		this.updateInfoString();
		elContainer.appendChild(this.elComment);
		
		var elDelete = document.createElement("img");
		elDelete.src = this.gadget.GADGET_BASE + "correspondence/icons/delete.png";
		elDelete.onclick = this.remove.bind(this);
		elDelete.title="delete correspondence";
		el.appendChild(elDelete);
		
		var elEdit = document.createElement("img");
		elEdit.src = this.gadget.GADGET_BASE + "correspondence/icons/pencil.png";
		elEdit.onclick = this.edit.bind(this);
		elEdit.title="edit correspondence";
		el.appendChild(elEdit);
	
		var elClear = document.createElement("div");
		elClear.className = "clear";
		el.appendChild(elClear);

		$("connections").appendChild(el);
		
		this.connectionEl = elContainer;
		
		// deselect currently active connection and select the new one
		var connections = this.gadget.connectionCollection.connections;
		
		for (var i = 0; i < connections.length; i++){
			if (connections[i] && connections[i].isActive ){
				connections[i].deselect();
			}	
		}
		this.select();

	},
	
	/**
	 * Returns a name of an ID of a node, depending on what is available.
	 * @param {Node | {name}} A node to get the name.
	 * @return Returns a name of an ID of a node, depending on what is available.
	 */
	humanReadableName : function(node){	
		if (node.properties.name || node.properties.name=="") {
			return node.properties.name;
		} else if (node.properties.title || node.properties.title==""){
			return node.properties.title;
		} else if (node.properties.caption || node.properties.caption==""){
			return node.properties.caption;
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

	/**
	 * Removes this connection form the gadget.
	 */
	remove: function(){
		
		if (this.isActive){
			for (var i = 0; i < this.models.length; i++){
				if (this.models[i]){
					this.gadget.undoGrey(i, "all");
					
					//mark shapes of the current model that belong to the connection
					var resourceIds = [];
					for (var j = 0; j < this.models[i].nodes.length; j++){
						resourceIds.push(this.models[i].nodes[j].resourceId)
					}
					this.gadget.removeMarker( i, resourceIds );
				}
			}
		}
		
		this.connectionEl.ancestors()[0].remove();
		
		var connections = this.gadget.connectionCollection.connections;
		
		for (var i = 0; i < connections.length; i++){
			if (connections[i] && connections[i] == this)
				connections[i] = null;
		}
		
	},
	
	/**
	 *  Stops the editing mode. Resets all the markings in the viewers.
	 */
	stopEditing: function() {		
		this.gadget.unregisterSelectionChanged();
		this.gadget.resetModels();		
	},
	
	
	/**
	 * Starts the edit Mode. Disables the gadget, marks the viewers accordingly.
	 */
	edit: function() {
		var onSuccess = function() {
			var commentTextBox = document.getElementById("commentTextBox");
			this.comment = commentTextBox.value;
			this.updateInfoString();
			this.stopEditing();
			this.highlightInViewer();
			this.gadget.enterDiscoveryMode();

		};
		//this.gadget.stopDiscoveryMode();
		this.select();
		this.editing = true;
		this.gadget.disable("Edit Mode", '<div> Please select the desired shapes in the model viewers to edit the correspondence. </div>  <label for="Comment">Comment:</label><input id="commentTextBox" type="textbox" name="Comment" />', onSuccess.bind(this));
		document.getElementById("commentTextBox").value = this.comment;
		this.selectShapes();
		this.gadget.registerSelectionChanged("all");
		this.gadget.registerRPC("handleSelection", "", "", this.updateConnection, this);
	},
	
	/**
	 * Updates the selection of nodes 
	 * @param {{Integer,[Node]}}
	 */
	updateConnection: function(reply){
		
		var index = reply.index;		
		var nodes = reply.selected;
		var nodeArray = [];
		
		var resourceIds = [];
		for (var key in nodes) {
			resourceIds.push(key);
			nodeArray.push(nodes[key]);
		}
		// remove shadow
		this.gadget.undoGrey(index, resourceIds);
		
		if (this.models[index]){
			//check if selected nodes are already contained 
			this.models[index].nodes = nodeArray;	
			this.updateInfoString();
		}
		
	},
	/**
	 *  Removes all markers and selections from all other models.
	 */
	clearModels : function() {
		var resetModels = function(viewers){			
			for (var i = 0; i < viewers.length; i++){
				this.gadget.resetSelection( viewers[i] );
				if ( ! this.models[ viewers[i] ] ){
					this.gadget.removeMarker(viewers[i], "all");
					this.gadget.undoGrey(viewers[i], "all");					
				}
			}
		};			
		this.gadget.sendViewers(resetModels, this);
	},
	
	
	/**
	 * mark all shapes in viewers that belong to the connection
	 */
	highlightInViewer : function(){
		
		this.isActive = true;
		
		// remove all markers and shadows in all models covered by the connection 
		// and mark shapes belonging to the connection
		this.markShapes(true);
		this.select();
	},
	
	/**
	 * Deselects this connection in the UI.
	 */
	deselect : function(){
		this.isActive = false;
		this.connectionEl.removeClassName("connection-item-active");
	},
	
	/**
	 * Selects this connection in the UI.
	 */	
	select : function(){
		var connections = this.gadget.connectionCollection.connections;	
		for (var i = 0; i < connections.length; i++){
			if (connections[i] && connections[i].isActive ){
				connections[i].deselect();
			}	
		}
		this.isActive = true;
		this.connectionEl.addClassName("connection-item-active");
	},
	
	/**
	 * in all models that belong to the connection mark the included shapes
	 * @param {Boolean} reset If reset is true remove all other markers and shadows
	 */
	markShapes : function(reset){
		
		for (var i = 0; i < this.models.length; i++){
			if (this.models[i]){
				
				if (reset){
					this.gadget.removeMarker(i, "all");
					this.gadget.undoGrey(i, "all");
				}
				
				//mark shapes of the current model that belong to the connection
				var resourceIds = [];
				for (var j = 0; j < this.models[i].nodes.length; j++){
					resourceIds.push(this.models[i].nodes[j].resourceId)
				}
				this.gadget.markShapes( i, resourceIds );

			}
		}
		
	},
	
	/**
	 * in all models that belong to the connection select the included shapes	 
	 * the difference to markShapes is that selected shapes can be removed with one click
	 * @param {Boolean} reset If reset is true remove all other markers and shadows
	 */
	selectShapes : function(reset){
		
		for (var i = 0; i < this.models.length; i++){
			if (this.models[i]){
				
				if (reset){
					this.gadget.removeMarker(i, "all");
					this.gadget.undoGrey(i, "all");
				}
				
				//mark shapes of the current model that belong to the connection
				var resourceIds = [];
				for (var j = 0; j < this.models[i].nodes.length; j++){
					resourceIds.push(this.models[i].nodes[j].resourceId)
				}
				this.gadget.setSelectedShapes( i, resourceIds );

			}
		}
		
	},
	
	/**
	 * checks whether a shape specified by its resourceId and the viewer belongs to the connection
	 * @param {Integer} viewer The index of the Viewer gadget
	 * @param {String} resourceId The resourceId of the shape to test.
	 */
	includesShape: function(viewer, resourceId){
		if (this.models[viewer]){
			for (var i = 0; i < this.models[viewer].nodes.length; i++){
				if (this.models[viewer].nodes[i].resourceId == resourceId)
					return true;
			}
		}
		return false;
		
	},
	
	/**
	 * Returns a JSON Representation of the connection
	 * @return {String} Returns a JSON Representation of the connection
	 */
	toJSON: function() {
		var obj = {
			comment : this.comment,
			models	: this.models
		};
			
		return Object.toJSON(obj);
	}
		
	
		
		
};
