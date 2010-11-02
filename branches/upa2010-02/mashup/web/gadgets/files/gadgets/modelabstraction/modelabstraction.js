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
	this.msg = null;
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
			this.showModelLink();
		} else { 
			this.resetModel();
		}
		
		this.msg = new YAHOO.widget.Panel("msgpanel", {
			width: 300,
			close: false,
			visible: false,
			draggable: false,
			y: 100,
			x: 50
		});
		this.msg.render();
	},
	
	/*
	 * Handles the click on the create button.
	 * Creates a new group and 
	 * starts the selection mode if it isn't already running.
	 */
	createGroup : function() {
		if (!this.selectionMode) {
			var group = new Group(this, prompt("Enter a name for the new group:"));
			if (this.addGroup(group)) {
				this.buttons['save'].removeClass('hide');
				this.buttons['new'].addClass('hide');
				this.selectionMode = true;
				this.selector = new Selector(this, group, this.viewer);
			} else {
				this.showMessage("Please choose a unique group name!");
			}
		}
	},
	
	/*
	 * Adds a group to the internal list if it has a unique name.
	 * Returns true if the group was added successfully.
	 */
	addGroup : function(group) {
		if (group.name != null && group.name != "") {
			var exists = false;
			// compare the name of the group with all already exiting groups
			for (var i = 0; i < this.groups.length; i++) {
				if (this.groups[i].name == group.name) {
					exists = true;
					break;
				}
			}
			if (!exists) {
				// the name is unique
				this.groups.push(group);
				if (group.model == null && this.model != null)
					group.setModel(this.model);
				group.display();
				return true;
			}
		}
		return false;
	}, 
	
	/*
	 * Removes the group from the internal list. 
	 * Returns true if it was successfully removed.
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
			if (this.selectionMode && this.selector.group == group) 
				this.abortSelection();
		}
		return found;
	}, 
	
	/*
	 * Returns an integer which can be used as an id.
	 * Is used to give every group a unique id.
	 */
	getId : function() {
		return this.counter++;
	},
	
	/*
	 * Handles the click on the save button.
	 * Simply calls stopSelection if the selection mode is active.
	 */
	saveGroup : function() {
		if (this.selectionMode)
			this.stopSelection(false);
	},
	
	/*
	 * Aborts the selection mode if it is running.
	 */
	abortSelection : function() {
		if (this.selectionMode)
			this.stopSelection(true);
	},
	
	/*
	 * Stops the selection mode and changes the appearance of the buttons accordingly.
	 * The boolean parameter abort indicates whether the selection was aborted (or successful).
	 */
	stopSelection : function(abort) {
		this.buttons['save'].addClass('hide');
		this.buttons['new'].removeClass('hide');
		this.buttons['edit'].removeClass('hide');
		this.selector.stopSelection(abort);
		this.selector = null;
		this.selectionMode = false;
	},
	
	/*
	 * Handles the click on the edit button.
	 * Enables the selection mode for the current active group again.
	 */
	editGroup : function() {
		if (!this.selectionMode &&  this.groups.length > 0) { //this.viewer != null &&
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
			this.selector = new Selector(this, activeGroup, this.viewer);
		}
		
	},
	
	/*
	 * Handles the click on the reset button.
	 * Resets all groupings and the chosen model.
	 */
	resetGroups : function() {
		if (this.viewer != null) {
			this.removeMarker(this.viewer, "all");
		}
		this.viewer = null;
		this.resetModel();
		document.getElementById("groups").innerHTML = '';
		this.groups = [];
		this.counter = 0;
		if (this.selectionMode) {
			this.stopSelection(true);
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
			// TODO: send groups to the server and handle the response
			// therefore open a viewer with the returned model link
			// or show the according error message
		} else {
			// show error message
			this.showMessage("Can't do abstraction!");
		}	
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
		this.showModelLink();
	},
	
	showModelLink : function() {
		document.getElementById("model").innerHTML = '<a href="#" onClick="modelabstraction.displayModel();">' 
			+ this.model.url.gsub(this.SERVER_BASE, '').gsub(this.REPOSITORY_BASE, '') + '</a>' ;
	},
	
	// TODO: find a way to retrieve the according viewer for the opened model
	displayModel : function() {
		if (this.model != null) {
			gadgets.rpc.call("..", 
				'dispatcher.displayModel', 
				function(reply){return}, 
				this.model.url + "." + this.model.title);
		}
		return false;
	},
	
	setViewer : function(viewer) {
		this.viewer = viewer;
	},
	
	/*
	 * Shows a panel with the given message for 1.5 seconds.
	 */
	showMessage : function(msg) {
		this.msg.setBody(msg);
		this.msg.show();
		window.setTimeout(function(){
				this.msg.hide();
			}.bind(this), 1500);
	}
});