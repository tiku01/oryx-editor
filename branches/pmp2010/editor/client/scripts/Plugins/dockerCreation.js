/**
 * Copyright (c) 2010
 * Robert Böhme, Philipp Berger
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

if(!ORYX.Plugins)
	ORYX.Plugins = new Object();

ORYX.Plugins.DockerCreation = Clazz.extend({
	
	construct: function( facade ){
		this.facade = facade;		
		this.active = false; //true-> a ghostdocker is shown; false->ghostdocker is hidden
		this.point = {x:0, y:0}; //Ghostdocker
		
		//visual representation of the Ghostdocker
		this.circle = ORYX.Editor.graft("http://www.w3.org/2000/svg", null ,
				['g', {"pointer-events":"none"},
					['circle', {cx: "8", cy: "8", r: "3", fill:"yellow"}]]); 	
		
		//Event registrations
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEDOWN, this.handleMouseDown.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEOVER, this.handleMouseOver.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEOUT, this.handleMouseOut.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEMOVE, this.handleMouseMove.bind(this));
		/*
		 * Double click is reserved for label access, so abort action
		 */
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_DBLCLICK,function(){window.clearTimeout(this.timer)}.bind(this));
		/*
		 * click is reserved for selecting, so abort action when mouse goes up
		 */
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEUP,function(){window.clearTimeout(this.timer)}.bind(this));

},

/**
 * MouseOut Handler
 * 
 *hide the Ghostpoint when Leaving the mouse from an edge
 */
handleMouseOut: function(event, uiObj) {
	
	if (this.active) {		
		this.hideOverlay();
		this.active = false;
	}	
},

/**
 * MouseOver Handler
 * 
 *show the Ghostpoint if the edge is selected
 */
handleMouseOver: function(event, uiObj) {
	
	this.point.x = this.facade.eventCoordinates(event).x;
	this.point.y = this.facade.eventCoordinates(event).y;

	//show the Ghostdocker on the edge
	if( uiObj instanceof ORYX.Core.Edge){		
		this.showOverlay( uiObj, this.point )
	}
	//ghostdocker is active
	this.active = true;
	
},

/**
 * MouseDown Handler
 * 
 *create a Docker when clicking on a selected edge
 */
handleMouseDown: function(event, uiObj) {	
	if (event.which==1 && uiObj instanceof ORYX.Core.Edge){
		//Timer for Doubleclick to be able to create a label
		window.clearTimeout(this.timer);
		
		this.timer = window.setTimeout(function () {
			/*
			 * Give the event to enable one click creation and drag
			 */
			this.addDockerCommand({
	            edge: uiObj,
				event: event,
	            position: this.facade.eventCoordinates(event)
	        });

		}.bind(this),200);
		this.hideOverlay();

	}
},

//
/**
 * MouseMove Handler
 * 
 *refresh the ghostpoint when moving the mouse over an edge
 */
handleMouseMove: function(event, uiObj) {		
		if (uiObj instanceof ORYX.Core.Edge){
			this.point.x = this.facade.eventCoordinates(event).x;
			this.point.y = this.facade.eventCoordinates(event).y;

			if (this.active) {	
				//refresh Ghostpoint
				this.hideOverlay();			
				this.showOverlay( uiObj, this.point);
			}else{
				this.showOverlay( uiObj, this.point);	
			}		
		}	
},


/**
 * Command for creating a new Docker
 * 
 * @param {Object} options
 */
addDockerCommand: function(options){
    if(!options.edge)
        return;
    
    var commandClass = ORYX.Core.Command.extend({
        construct: function(edge, docker, pos, facade, options){            
            this.edge = edge;
            this.docker = docker;
            this.pos = pos;
            this.facade = facade;
			this.options= options;
        },
        execute: function(){
            this.docker = this.edge.addDocker(this.pos, this.docker);
			this.index = this.edge.dockers.indexOf(this.docker);                                    
            this.facade.getCanvas().update();
            this.facade.updateSelection();
            this.options.docker=this.docker;

        },
        rollback: function(){
          
             if (this.docker instanceof ORYX.Core.Controls.Docker) {
                    this.edge.removeDocker(this.docker);
             }             
            this.facade.getCanvas().update();
            this.facade.updateSelection(); 
        }
    });
    var command = new commandClass(options.edge, options.docker, options.position, this.facade, options);    
    this.facade.executeCommands([command]);

    
	this.facade.raiseEvent({
		uiEvent:	options.event,
		type:		ORYX.CONFIG.EVENT_DOCKERDRAG}, options.docker );
    
},

/**
 *show the ghostpoint overlay
 *
 *@param {Shape} edge
 *@param {Point} point
 */
showOverlay: function(edge, point){

	this.facade.raiseEvent({
			type: 			ORYX.CONFIG.EVENT_OVERLAY_SHOW,
			id: 			"ghostpoint",
			shapes: 		[edge],
			node:			this.circle,
			ghostPoint:		point,
			dontCloneNode:	true
		});			
},

/**
 *hide the ghostpoint overlay
 */
hideOverlay: function() {
	
	this.facade.raiseEvent({
		type: ORYX.CONFIG.EVENT_OVERLAY_HIDE,
		id: "ghostpoint"
	});	
}

});

