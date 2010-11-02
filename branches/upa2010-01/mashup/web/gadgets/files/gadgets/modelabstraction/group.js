/**
 * Copyright (c) 2010 Christian Wiggert
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

var Group = function(gadget, name) {
	this.gadget = gadget;
	this.model = null;
	this.id = null;
	this.name = name;
	this.shapes = [];
	this.groupEl = null;
	this.init();
}

Group.prototype = {
	init : function() {
		this.id = this.gadget.getId();
	},
	
	/*
	Returns the JSON representation of the group.
	*/
	toJSON : function() {
		var result = {};
		result['model'] = this.model.url;
		result['name'] = this.name;
		result['shapes'] = this.shapes;
		return result;
	},
	
	/*
	Adds a shape to the internal list of shapes.
	Returns true if the shapes was successfully added.
	*/
	addShape : function(shape) {
		for (var i = 0; i < this.shapes.length; i++) {
			if (shape == this.shapes[i])
				return false;
		}
		this.shapes.push(shape);
		return true;
	},
	
	/*
	Removes the given from the internal list of shapes if it exists.
	Returns true if the shape was removed.
	*/
	removeShape : function(shape) {
		var i = 0; 
		var found = false;
		for (i; i < this.shapes.length; i++) {
			if (this.shapes[i] == shape) {
				found = true;
				break;
			}
		}
		if (found) {
			this.shapes.splice(i, 1);
		}
		return found;
	},
	
	/*
	Synchronises the given array of shapes with the local stored shapes and returns
	an object with a list of all added and all removed shapes.
	*/
	syncShapes : function(shapes) {
		var result = {"added":[], "removed":[]};
		// add all new shapes
		for (var i = 0; i < shapes.length; i++) {
			if (this.addShape(shapes[i])) {
				result["added"].push(shapes[i]);
			}
		}
		// find all shapes that have to be removed
		for (var i = 0; i < this.shapes.length; i++) {
			var found = false;
			for (var j = 0; j < shapes.length; j++) {
				if (this.shapes[i] == shapes[j]) {
					found = true;
					break;
				}
			}
			if (!found)
				result["removed"].push(this.shapes[i]);
		}
		// remove found shapes
		for (var i = 0; i < result['removed'].length; i++) {
			this.removeShape(result['removed'][i]);
		}
		return result;
	},
	
	setModel : function(model) {
		this.model = model;
	},
	
	/*
	Displays this group in the gadgets list of groups.
	*/
	display : function() {
		var el = document.createElement("li");
		el.id = "group_" + this.id;
		
		var elContainer = document.createElement("div");
		elContainer.className = "group-item";
		elContainer.onclick = this.highlightInViewer.bind(this);
		el.appendChild(elContainer);
		
		var elName = document.createElement("p");
		elName.innerHTML = this.name;
		elContainer.appendChild(elName);
		
		var elDelete = document.createElement("img");
		elDelete.src = this.gadget.GADGET_BASE + "modelabstraction/icons/delete.png";
		elDelete.onclick = this.remove.bind(this);
		el.appendChild(elDelete);
	
		var elClear = document.createElement("div");
		elClear.className = "clear";
		el.appendChild(elClear);

		document.getElementById("groups").appendChild(el);
	
		this.groupEl = elContainer;
		
		// deselect currently active group and select the new one
		var groups = this.gadget.groups;
		
		for (var i = 0; i < groups.length; i++){
			if (groups[i] && groups[i].isActive ){
				groups[i].deselect();
			}	
		}
		this.select();
	},
	
	remove: function(){
		
		if (this.isActive && this.gadget.viewer != null) {
			// TODO delete possible marking
			this.gadget.removeMarker(this.gadget.viewer, "all");
		}
		
		this.groupEl.ancestors()[0].remove();
		
		this.gadget.removeGroup(this);
	},
	
	/*
	 * mark all shapes in viewers that belong to the connection
	 */
	highlightInViewer : function(){
		
		this.isActive = true;
		
		// remove all markers and shadows in all models covered by the connection 
		// and mark shapes belonging to the connection
		this.markShapes(true);
	
		var groups = this.gadget.groups;
		
		for (var i = 0; i < groups.length; i++){
			if (groups[i] && groups[i].isActive ){
				groups[i].deselect();
			}	
		}
		this.select();
	},
	
	deselect : function(){
		this.isActive = false;
		this.groupEl.removeClassName("group-item-active");
	},
	
	select : function(){
		this.isActive = true;
		this.groupEl.addClassName("group-item-active");
	},
	
	/*
	 * in all models that belong to the connection mark the included shapes
	 * if reset is treu remove all other markers and shadows
	 */
	markShapes : function(reset){
		if (this.gadget.viewer) {
	   		if (reset){
	   			this.gadget.removeMarker(this.gadget.viewer, "all");
	   			this.gadget.undoGrey(this.gadget.viewer, "all");
	   		}
	   			
	   		//mark shapes of the current model that belong to the group
	   		this.gadget.markShapes(this.gadget.viewer, this.shapes);
		}
	},
}