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

var ModelAbstraction = function() {
	ModelAbstraction.superclass.constructor.call(this, "modelAbstraction");
	this.viewer = null;
	this.model = null;
	this.selector = null;
	this.counter = 0;
	this.groups = [];
	this.buttons = {};	
	this.selectionMode = false;
	this.init();
}

YAHOO.lang.extend( ModelAbstraction, AbstractGadget, {	
	init : function() {
		var layout = new YAHOO.widget.Layout({ 
			units: [ 
	            { position: 'top', header: 'Model Abstaction - Menu', height: '80px', resize: false, body: 'top', gutter: '5px', collapse: true}, 
	            { position: 'center', body: 'center', header: 'Groups', gutter: '5px', scroll: true} 
	            ] 
	     }); 
		
		layout.render();
		
		// used to add a new group
		var newButton = new YAHOO.widget.Button({
			id :		"newButton", 
			container :	"button_group", 
			title : 	"create a new group of shapes" 
		});
		newButton.on("click", this.createGroup.bind(this));
		newButton.setStyle("background", "url('" + this.GADGET_BASE + "modelabstraction/icons/add.png') no-repeat center");
		newButton.className ="button";
		this.buttons['new'] = newButton;
		
		// saves the choice after the selection is done
		var saveButton = new YAHOO.widget.Button({
			id : 		"saveButton", 
			container : "button_group",
			title : 	"save your choice"
		});
		saveButton.on("click", this.saveGroup.bind(this));
		saveButton.setStyle("background", "url('" + this.GADGET_BASE + "modelabstraction/icons/disk.png') no-repeat center");
		saveButton.className ="button";
		saveButton.addClass("hide");
		this.buttons['save'] = saveButton;
		
		// used to edit an existing group
		var editButton = new YAHOO.widget.Button({
			id :		"editButton", 
			container :	"button_group", 
			title : 	"edit an existing group of shapes" 
		});
		editButton.on("click", this.editGroup.bind(this));
		editButton.setStyle("background", "url('" + this.GADGET_BASE + "modelabstraction/icons/pencil.png') no-repeat center");
		editButton.className ="button";
		this.buttons['edit'] = editButton;
		
		// used to remove all existing groups
		var resetButton = new YAHOO.widget.Button({
			id :		"resetButton", 
			container :	"button_group", 
			title : 	"reset the list of groups" 
		});
		resetButton.on("click", this.resetGroups.bind(this));
		resetButton.setStyle("background", "url('" + this.GADGET_BASE + "modelabstraction/icons/cancel.png') no-repeat center");
		resetButton.className ="button";
		
		// used to send the selected groups to server
		var abstractButton = new YAHOO.widget.Button({
			id :		"abstractButton", 
			container :	"button_group", 
			title : 	"start the abstraction" 
		});
		abstractButton.on("click", this.abstract.bind(this));
		abstractButton.setStyle("background", "url('" + this.GADGET_BASE + "modelabstraction/icons/cog_go.png') no-repeat center");
		abstractButton.className ="button";
		
		if (this.model != null) {
			document.getElementById("model").innerHTML = this.model.url.gsub(this.SERVER_BASE, '').gsub(this.REPOSITORY_BASE, '');
		} else { 
			this.resetModel();
		}
	},
	
	/*
	 *
	 */
	createGroup : function() {
		if (!this.selectionMode) {
			this.selectionMode = true;
			this.buttons['save'].removeClass('hide');
			this.buttons['new'].addClass('hide');
			this.selector = new Selector(this, this.viewer, null);
		}
	},
	
	/*
	 *
	 */
	addGroup : function(group) {
		this.groups.push(group);
		if (group.model == null && this.model != null)
			group.setModel(this.model);
		group.display();
	}, 
	
	/*
	 *
	 */
	removeGroup : function(group) {
		var i = 0;
		var found = false;
		for (i; i < this.groups.length; i++) {
			if (group == this.groups[i]) {
				found = true;
				break;
			}
		}
		if (found) {
			this.groups.splice(i, 1);
		}
		return found;
	}, 
	
	/*
	 *
	 */
	getId : function() {
		return this.counter++;
	},
	
	/*
	 *
	 */
	saveGroup : function() {
		if (this.selectionMode) {
			this.buttons['save'].addClass('hide');
			this.buttons['new'].removeClass('hide');
			this.buttons['edit'].removeClass('hide');
			
			this.selector.stopSelection();
			this.selector = null;
			this.selectionMode = false;
		}
	},
	
	/*
	 *
	 */
	editGroup : function() {
		if (!this.selectionMode && this.viewer != null && this.groups.length > 0) {
			this.selectionMode = true;
			this.buttons['edit'].addClass('hide');
			this.buttons['save'].removeClass('hide');
			var activeGroup = null;
			for (var i = 0; i < this.groups.length; i++) {
				if (this.groups[i].isActive) {
					activeGroup = this.groups[i];
					break;
				}
			}
			this.selector = new Selector(this, this.viewer, activeGroup);
		}
		
	},
	
	/*
	 * Resets all groupings and the chosen model.
	 */
	resetGroups : function() {
		if (this.viewer != null) {
			this.undoGrey(this.viewer, "all");
			this.removeMarker(this.viewer, "all");
		}
		this.viewer = null;
		this.resetModel();
		document.getElementById("groups").innerHTML = '';
		this.groups = [];
		this.counter = 0;
		if (this.selectionMode) {
			this.buttons['save'].addClass('hide');
			this.buttons['new'].removeClass('hide');
			this.buttons['edit'].removeClass('hide');
			this.selector.stopSelection();
			this.selector = null;
			this.selectionMode = false;
		}
	},
	
	/*
	 * Sends the groupings to server which does the abstraction and opens a viewer with the abstracted model. 
	 * Does only work if a model is chosen and at least one grouping exists.
	 */ 
	abstract : function() {
		if (this.model != null && this.groups.length > 0) {
			var groups = [];
			for (var i=0; i < this.groups.length; i++) {
				groups.push(this.groups[i].toJSON());
			}
			var result = {
				'model'	: this.model.url,
				'groups': groups
			};
			console.log('Abstraction Groups: ');
			console.log(result);
		} else {
			console.log("abstraction does not work here");
			console.log(gadgets);
			console.log(gadgets.MiniMessage);
			console.log(__MODULE_ID__);
			msg = new gadgets.MiniMessage(__MODULE_ID__);
			msg.createTimerMessage("There is nothing to abstract!", 3);
		}
		// TODO: send groups to the server and handle the response
		// therefore open a viewer with the returned model link
		// or show the according error message
	},
	
	/*
	 * Resets the model attribute and changes the label in the menu to the default value.
	 */
	resetModel : function() {
		document.getElementById("model").innerHTML = '<i>None</i>';
		this.model = null;
	},
	
	/*
	 * A simple setter for the model attribute which additionally changes the label in the menu.
	 */ 
	setModel : function(model) {
		this.model = model;
		document.getElementById("model").innerHTML = this.model.url.gsub(this.SERVER_BASE, '').gsub(this.REPOSITORY_BASE, '');
	},
	
	setViewer : function(viewer) {
		this.viewer = viewer;
	}
});