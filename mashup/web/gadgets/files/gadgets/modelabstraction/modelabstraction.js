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
	this.selectionMode = false;
	this.sliderValue = 100;
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
		
		this.initMenu();
		
		if (this.model != null) {
			this.showModelLink();
		} else { 
			this.resetModel();
		}
		
		// displays error messages
		this.msg = new YAHOO.widget.Panel("msgpanel", {
			width: 300,
			close: false,
			visible: false,
			draggable: false,
			y: 100,
			x: 50,
			zIndex: 102
		});
		this.msg.render();
		
		this.initOptionPanel();
		this.initEditPanel();
		this.initLoadingAnimation();
	},
	
	/*
	 * Creates the menu.
	 */
	initMenu : function() {
		// used to add a new group
		var newButton = new YAHOO.widget.Button({
			id :		"newButton", 
			container :	"button_group", 
			title : 	"create a new group of shapes" 
		});
		newButton.on("click", this.createGroup.bind(this));
		newButton.setStyle("background", "url('" + this.GADGET_BASE + "modelabstraction/icons/add.png') no-repeat center");
		newButton.className ="button";
	
		// used to display the option panel
		var optionButton = new YAHOO.widget.Button({
			id :		"optionButton", 
			container :	"button_group", 
			title : 	"show options" 
		});
		optionButton.on("click", this.showOptions.bind(this));
		optionButton.setStyle("background", "url('" + this.GADGET_BASE + "modelabstraction/icons/wrench.png') no-repeat center");
		optionButton.className ="button";
		
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
	},
	
	/*
	 * Shows some options.
	 */
	initOptionPanel : function() {
		this.optionPanel = new YAHOO.widget.Panel("option_panel", {
			width: 250,
			height: 160,
			close: false,
			visible: false,
			draggable: false,
			y: 50,
			x: 75,
			zIndex: 101
		});
		this.optionPanel.setHeader("Options");
		this.optionPanel.render();
		var optionCloseButton = new YAHOO.widget.Button({
			id : 		"optionCloseButton", 
			container : "option_buttons",
			title : 	"close options",
			label : 	"Close"
		});
		optionCloseButton.on("click", this.hideOptions.bind(this));
		
		// used to give the user the choice to prefer either cycles or parallelism
		/*
		this.cycleChoice = new YAHOO.widget.ButtonGroup({
			id : 		"cycleChoice",
			name : 		"cycleChoice",
			container :	"radio_buttons"
		});
		this.cycleChoice.addButtons([
			{label: "Cycles", value: "0", type: "radio"},
			{label: "Parallelism", value: "1", type: "radio"}//, checked: true
		]);
		this.cycleChoice.check(1);
		*/
		// slider and ...
		var img = document.createElement("img");
		img.setAttribute("src", this.GADGET_BASE + "modelabstraction/icons/thumb-n.gif");
		img.setAttribute("alt", "Slider Thumb");
		img.setAttribute("width", "20");
		img.setAttribute("height", "20");
		$('slider_thumb').appendChild(img);
		this.slider = new YAHOO.widget.Slider.getHorizSlider("slider", "slider_thumb", 0, 200);
		this.slider.subscribe('change', function(offset) {
			this.sliderValue = offset;
			$('threshold_value').value = this.getThreshold();
		}.bind(this));
		// ...input field for the threshold
		$('threshold_value').observe('change', function() {
			var value = parseFloat($('threshold_value').value);
			if (!isNaN(value)) {
				var sliderValue = Math.min(value, 1.0);
				sliderValue = Math.max(sliderValue, 0.005);
				this.slider.setValue(sliderValue * 200);
			}
		}.bind(this));
	},
	
	/*
	 * creation and edit dialog
	 */
	initEditPanel : function() {
		this.editPanel = new YAHOO.widget.Panel("edit_panel", {
			width: 250,
			height: 150,
			close: false,
			visible: false,
			draggable: false,
			y: 50,
			x: 75,
			zIndex: 101
		});
		this.editPanel.render();
		var editSaveButton = new YAHOO.widget.Button({
			id : 		"editSaveButton", 
			container : "edit_buttons",
			title : 	"save your choice",
			label : 	"Save"
		});
		editSaveButton.on("click", this.saveGroup.bind(this));
		var editCancelButton = new YAHOO.widget.Button({
			id : 		"editCancelButton", 
			container : "edit_buttons",
			title : 	"cancel the selection",
			label : 	"Cancel"
		});
		editCancelButton.on("click", this.abortSelection.bind(this));		
	},
	
	/*
	 * busy animation showing that the abstraction process is running
	 */
	initLoadingAnimation : function() {
		this.loadingPanel = new YAHOO.widget.Panel("loading_anim", {
			width: 50,
			height: 50,
			close: false,
			visible: false,
			draggable: false,
			y: 130,
			x: 175,
			zIndex: 101
		});
		var ani = document.createElement("img");
		ani.setAttribute("src", this.GADGET_BASE + "modelabstraction/icons/ajax-loader.gif");
		ani.setAttribute("alt", "Loading...");
		this.loadingPanel.setBody(ani);
		this.loadingPanel.render();
	},
	
	/*
	 * Shows the loading animation.
	 */
	showLoading : function() {
		$("overlay").setStyle({'visibility': 'visible'});
		this.loadingPanel.show();
	},
	
	/*
	 * Hides the loading animation.
	 */
	hideLoading : function() {
		$("overlay").setStyle({'visibility': 'hidden'});	
		this.loadingPanel.hide();
	},
	
	showOptions : function() {
		$("overlay").setStyle({'visibility': 'visible'});	
		this.optionPanel.show();
		this.slider.setValue(this.sliderValue);
	},
	
	hideOptions : function() {
		$("overlay").setStyle({'visibility': 'hidden'});	
		this.optionPanel.hide();
	},
	
	/*
	 * Handles the click on the create button.
	 * Creates a new group and 
	 * starts the selection mode if it isn't already running.
	 */
	createGroup : function() {
		if (!this.selectionMode) {
			var group = new Group(this, "");
			this.selectionMode = true;
			this.selector = new Selector(this, group, this.viewer);
			this.openEditDialog(group, true);
		}
	},
	
	/*
	 * Opens the create/edit dialog.
	 * The first parameter has to be a group 
	 * and the second parameter indicates whether
	 * a new group is created (true or false for editing).
	 */ 
	openEditDialog : function(group, create) {
		var input = $("edit_name");
		input.value = group.name;
		$("overlay").setStyle({'visibility': 'visible'});
		if (create)
			this.editPanel.setHeader("Create new Group");
		else
			this.editPanel.setHeader("Edit Group");
		this.editPanel.show();
	},
	
	/*
	 * Closes the create/edit dialog again.
	 */
	closeEditDialog : function() {
		$("overlay").setStyle({'visibility': 'hidden'});
		this.editPanel.hide();
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
	
	groupExists : function(group) {
		for (var i=0; i < this.groups.length; i++) {
			if (group == this.groups[i]) {
 				return true;
			}
		}
		return false;
	},
	
	groupNameIsInUse : function(group) {
		for (var i=0; i < this.groups.length; i++) {
			if (group != this.groups[i] && group.name == this.groups[i].name) {
 				return true;
			}
		}
		return false;
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
		if (this.selectionMode) {
			var group = this.selector.group;
			var temp = group.name;
			group.name = $('edit_name').getValue();
			if (this.groupNameIsInUse(group) || group.name === "") {
				group.name = temp;
				this.showMessage("Please choose a unique group name!");
			} else {
				if (!this.groupExists(group)) 
					this.addGroup(group)
				else 
					group.refresh();
				this.stopSelection(false);
			}
		}
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
		this.closeEditDialog();
		this.selector.stopSelection(abort);
		this.selector = null;
		this.selectionMode = false;
	},
	
	/*
	 * Handles the click on the edit button.
	 * Enables the selection mode for the current active group again.
	 */
	editGroup : function() {
		if (!this.selectionMode &&  this.groups.length > 0) { 
			this.selectionMode = true;
			var activeGroup = null;
			for (var i = 0; i < this.groups.length; i++) {
				if (this.groups[i].isActive) {
					activeGroup = this.groups[i];
					break;
				}
			}
			this.selector = new Selector(this, activeGroup, this.viewer);
			this.openEditDialog(activeGroup, false);
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
	 * Sends the groupings and the original model (JSON) 
	 * to the server, where a new model is derived. Afterwards the new 
	 * model will be stored again in the oryx backend and opened in a viewer gadget.
	 *
	 * This is the first step, which simply loads the JSON representation of 
	 * the model from the oryx backend
	 *
	 * Does only work if a model is chosen and at least one grouping exists.
	 */ 
	abstract : function() {
		if (this.model != null && this.groups.length > 0) {
			this.showLoading();
			new Ajax.Request(this.model.url + "/json", 
				{
					method 			: "get",
					onSuccess		: function(response) {
						this.runAbstraction(response.responseText);
					}.bind(this),
					onFailure		: function(response) {
						this.hideLoading();
						this.showMessage('Server communication failed!');
					}.bind(this)
				});
			
		} else {
			// show error message
			this.showMessage("Can't do abstraction!");
		}	
	},
	
	/*
	 * Retrieves and normalizes the value of the threshold slider.
	 */
	getThreshold : function() {
		var value;
		if (this.sliderValue == 0) 
			value = 1;
		else
			value = this.sliderValue;
		return (value / 200).toString();
	},
	
	/*
	 * Second step, sends the loaded model JSON to the 
	 * mashup server, where the real abstraction is processed.
	 */
	runAbstraction : function(data) {
		var groups = [];
		for (var i=0; i < this.groups.length; i++) {
			groups.push(this.groups[i].toJSON());
		}
		var result = {
			'model'	: data,
			'groups': groups.toJSON(),
			'preference': 1,//this.cycleChoice.get('value'),
			'threshold' : this.getThreshold()
		};
		new Ajax.Request("/mashup/generate", 
			 {
				method			: "post",
				onSuccess		: function(response){
					this.saveModel(response.responseText);
				}.bind(this),
				onFailure		: function(response){
					this.hideLoading();
					if (response.status == 500)
						this.showMessage(response.responseText);
					else
						this.showMessage('Server communication failed!');
				}.bind(this),
				parameters 		: result
			});
	},
		
	/*
	 * Fetches the list of private models of the user 
	 * and hands them over to the given callback.
	 */
	fetchModelLinks : function(callback) {
		var params = {access:'owner,read,write'};
		new Ajax.Request("/backend/poem/filter", 
			{
				method			: "get",
				parameters		: params,
				onSuccess		: function(response) {
					callback.call(this, response.responseText.evalJSON());
				},
				onFailure		: function(){
					this.hideLoading();
					this.showMessage('Server communication failed!');
				}.bind(this)
			});
	},	
		
	/*
	 * Last abstraction step. Takes the generated model and stores it
	 * in the oryx backend. Afterwards a new viewer is opened with the according model.
	 */
	saveModel : function(data) {
		var container = data.evalJSON();
		var content = {
			'data' : Object.toJSON(container.model),
			'summary' : 'This model was derived from: '.concat(this.model.model),
			'svg' : container.svg,
			'title' : 'Abstracted '.concat(this.model.model),
			'type' : container.model.stencilset.namespace,
		};
		var stencilSet = container.model.stencilset.url.match(/\/?stencilsets\/.*/);
		if (stencilSet.indexOf("/") != 0)
			stencilSet = "/".concat(stencilSet);
		new Ajax.Request("/backend/poem/new?stencilset=".concat(stencilSet), 
			 {
				method			: "post",
				onSuccess		: function(response){
					this.hideLoading();
					this.openViewer(response.getResponseHeader('location').replace(/\/self$/, ""));
				}.bind(this),
				onFailure		: function(){
					this.hideLoading();
					this.showMessage('Server communication failed!');
				}.bind(this),
				parameters 		: content
			});
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
	
	/*
	 * Renders a link in the menu with the model id.
	 */
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
				this.model.url + "." + this.model.model);
		}
		return false;
	},
	
	/*
	 * Opens a new viewer for the given model url.
	 */
	openViewer : function(url) {
		gadgets.rpc.call("..",
			"dispatcher.displayModel",
			function(reply) {return},
			url.concat(".-"));
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