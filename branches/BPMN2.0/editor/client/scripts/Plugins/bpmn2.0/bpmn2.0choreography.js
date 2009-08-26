/**
 * Copyright (c) 2009
 * Sven Wagner-Boysen
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
   @namespace Oryx name space for plugins
   @name ORYX.Plugins
*/
 if(!ORYX.Plugins)
	ORYX.Plugins = new Object();
	

/**
 * This plugin provides methodes to layout the choreography diagrams of BPMN 2.0.
 * 
 * @class ORYX.Plugins.Bpmn2_0Choreography
 * @extends ORYX.Plugins.AbstractPlugin
 * @param {Object} facade
 * 		The facade of the Editor
 */
ORYX.Plugins.Bpmn2_0Choreography = {
	
	/**
	 *	Constructor
	 *	@param {Object} Facade: The Facade of the Editor
	 */
	construct: function(facade) {
		this.facade = facade;
		
		/* Register on event ORYX.CONFIG.EVENT_STENCIL_SET_LOADED and ensure that
		 * the stencil set extension is loaded.
		 */
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_STENCIL_SET_LOADED, 
										this.handleStencilSetLoaded.bind(this));
		
		this.participantSize = 20;
		this.choreographyTasks = new Hash();
		this.numOfParticipantsOnTop = 0;
		this.numOfParticipantsOnBottom = 0;
	},
	
	
	/**
	 * Check if the 'http://oryx-editor.org/stencilsets/extensions/bpmn2.0choreography#'
	 * stencil set extension is loaded and thus register or unregisters on the 
	 * appropriated events.
	 */
	handleStencilSetLoaded : function() {
		if(this.isStencilSetExtensionLoaded('http://oryx-editor.org/stencilsets/extensions/bpmn2.0choreography#')) {
			this.registerPluginOnEvents();
		} else {
			this.unregisterPluginOnEvents();
		}
	},
	
	/**
	 * Register this plugin on the events.
	 */
	registerPluginOnEvents: function() {
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_PROPWINDOW_PROP_CHANGED, this.handlePropertyChanged.bind(this));
		this.facade.registerOnEvent('layout.bpmn2_0.choreography.task', this.handleLayoutChoreographyTask.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_PROPERTY_CHANGED, this.handlePropertyChanged.bind(this));
	},
	
	/**
	 * Unregisters this plugin from the events.
	 */
	unregisterPluginOnEvents: function() {
		this.facade.unregisterOnEvent(ORYX.CONFIG.EVENT_PROPWINDOW_PROP_CHANGED, this.handlePropertyChanged.bind(this));
		this.facade.unregisterOnEvent(ORYX.CONFIG.EVENT_PROPERTY_CHANGED, this.handlePropertyChanged.bind(this));
		this.facade.unregisterOnEvent('layout.bpmn2_0.choreography.task', this.handleLayoutChoreographyTask.bind(this));
	},
	
	/**
	 * Handler for layouting event 'layout.bpmn2_0.choreography.task'
	 * 
	 * @param {Object} event
	 * 		The layout event
	 */
	handleLayoutChoreographyTask: function(event) {
		var choreographyTask = event.shape;
		
		/* Handle participants on top side */
		var participants = this.getParticipants(choreographyTask,true,false);
		
		if(!participants) {return;}
		this.ensureParticipantsParent(choreographyTask, participants);
		
		/* Put participants into the right position */
		participants.each(function(participant, i) {
			participant.setProperty('oryx-corners', "None");
			participant.bounds.set(0, i * this.participantSize, 
								choreographyTask.bounds.width(), 
								this.participantSize +  i * this.participantSize);
			
			/* The first participants gets rounded corners */
			if(i == 0) {
				participant.setProperty('oryx-corners', "Top");
			}
		}.bind(this));
		
		/* Resize choreography task to top side */
		
		resizeFactor = participants.length - this.numOfParticipantsOnTop;
		
		var bounds = choreographyTask.bounds;
		var ul = bounds.upperLeft();
		var lr = bounds.lowerRight();
		bounds.set(ul.x, ul.y - resizeFactor * this.participantSize, lr.x, lr.y);
		
		
		this.numOfParticipantsOnTop = participants.length;
		
		/* Handle participants on bottom side */
		
//		var participants = this.getParticipants(choreographyTask,false,true);
//		
//		if(!participants) {return;}
//		this.ensureParticipantsParent(choreographyTask, participants);
//		
//		
//		
//		/* Put participants into the right position */
//		participants.each(function(participant, i) {
//			participant.setProperty('oryx-corners', "None");
//			participant.bounds.set(0, i * this.participantSize, 
//								choreographyTask.bounds.width(), 
//								this.participantSize +  i * this.participantSize);
//			
//			/* The first participants gets rounded corners */
//			if(i == 0) {
//				participant.setProperty('oryx-corners', "Top");
//			}
//		}.bind(this));
//		
//		/* Resize choreography task to top bottom side */
//		
//		resizeFactor = participants.length - this.numOfParticipantsOnBottom;
//		
//		var bounds = choreographyTask.bounds;
//		var ul = bounds.upperLeft();
//		var lr = bounds.lowerRight();
//		bounds.set(ul.x, ul.y, lr.x, lr.y + resizeFactor * this.participantSize);
//		
//		
//		this.numOfParticipantsOnTop = participants.length;
		
	},
	
	ensureParticipantsParent: function(shape, participants) {
		if(!shape || !participants) {return;}
		
		participants.each(function(participant) {
			if(participant.parent.getId() == shape.getId()) {return;}
			
			/* Set ChoreographyTask as Parent */
			participant.parent.remove(participant);
			shape.add(participant);
		});
	},
	
	/**
	 * Returns the participants of a choreography task ordered by theire position.
	 * 
	 * @param {Object} shape
	 * 		The choreography task
	 * @param {Object} onTop
	 * 		Flag to get the participants from the top side of the task.
	 * @param {Object} onBottom
	 * 		Flag to get the participants from the bottom side of the task.
	 * @return {Array} participants;
	 * 		The child participants
	 */
	getParticipants: function(shape, onTop, onBottom) {
		if(shape.getStencil().id() !== "http://b3mn.org/stencilset/bpmn2.0#ChoreographyTask") {
			return null;
		}
		
		var centerPoint = shape.absoluteBounds().center();
		
		/* Get participants of top side */
		var participantsTop = shape.getChildNodes(true).findAll(function(node) { 
			return (onTop && 
					node.getStencil().id() === "http://b3mn.org/stencilset/bpmn2.0#ChoreographyParticipant" &&
					node.absoluteBounds().center().y <= centerPoint.y); 
		});
		
		/* Get participants of bottom side */
		var participantsBottom = shape.getChildNodes(true).findAll(function(node) { 
			return (onBottom && 
					node.getStencil().id() === "http://b3mn.org/stencilset/bpmn2.0#ChoreographyParticipant" &&
					node.absoluteBounds().center().y > centerPoint.y); 
		});
		
		var participants = participantsTop.concat(participantsBottom);
		
		participants.sort(function(a,b) {return (a.absoluteBounds().upperLeft().y >
													b.absoluteBounds().upperLeft().y);
		});
		
		return participants;
	},
	
	/**
	 * PropertyWindow.PropertyChanged Handler
	 * 
	 * It sets the correct color of the elements of a participant depending on
	 * either initiating or returning nature.
	 * 
	 * @param {Object} event
	 * 		The property changed event
	 */
	handlePropertyChanged: function(event) {
		var shapes = event.elements;
		var propertyKey = event.key || event.name;
		console.log(propertyKey)
		var propertyValue = event.value;
		
		var changed = false;
		shapes.each(function(shape) {
			if( shape.getStencil().id() === 
					"http://b3mn.org/stencilset/bpmn2.0#ChoreographyParticipant" &&
				propertyKey === "oryx-behavior") {
				
				/* Set appropriate color */
				if(propertyValue === "Returning") {
					shape.setProperty("oryx-color", "#c6c6c6");
				} else if(propertyValue === "Initiating") {
					shape.setProperty("oryx-color", "#ffffff");
				}	
				
				changed = true;	
			}
		});
		
		/* Update visualisation if necessary */
		if(changed) {
			this.facade.getCanvas().update();
		}
	}
	
};

ORYX.Plugins.Bpmn2_0Choreography = ORYX.Plugins.AbstractPlugin.extend(ORYX.Plugins.Bpmn2_0Choreography);
