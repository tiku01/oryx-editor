/**
 * Copyright (c) 2009
 * Robert Böhme
 * 
 **/

if(!ORYX.Plugins)
	ORYX.Plugins = new Object();

ORYX.Plugins.DockerCreation = Clazz.extend({
	
	//facade: null,
	
	construct: function( facade ){
		this.facade = facade;		
		this.active = false;
		
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEDOWN, this.handleMouseDown.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEOVER, this.handleMouseOver.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEOUT, this.handleMouseOut.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEMOVE, this.handleMouseMove.bind(this));
},

/**
 * MouseOut Handler
 *
 */

//hide the Ghostpoint when Leaving the mouse from an edge
handleMouseOut: function(event, uiObj) {
	
	if (this.active) {
		
		this.hideOverlay();
		this.active=false;
	}	
},

//show the ghostpoint
handleMouseOver: function(event, uiObj) {

	var evPos = this.facade.eventCoordinates(event);
	
	console.log("mouseoverstart");
	if( uiObj instanceof ORYX.Core.Edge){
		
		this.showOverlay( uiObj, evPos )
	}
	this.active=true;
},


//create a Docker
handleMouseDown: function(event, uiObj) {	
	
	if (uiObj instanceof ORYX.Core.Edge){
		this.addDockerCommand({
            edge: uiObj,
            position: this.facade.eventCoordinates(event)
        });		
		console.log("Docker added");		
	}		
},

//refresh the ghostpoint when moving the mouse ober an edge
handleMouseMove: function(event, uiObj) {		
	
	if (uiObj instanceof ORYX.Core.Edge){
		var evPos = this.facade.eventCoordinates(event);
		console.log("MouseMove");
		if (this.active) {
			
			this.facade.raiseEvent({
				type: ORYX.CONFIG.EVENT_OVERLAY_HIDE,
				id: "ghostpoint"
			});			
			this.showOverlay( uiObj, evPos );
		}
		else{
			this.showOverlay( uiObj, evPos );	
		}		
	}		
},

//Command for creating a new Docker
addDockerCommand: function(options){
    if(!options.edge)
        return;

    var commandClass = ORYX.Core.Command.extend({
        construct: function(edge, docker, pos, facade){
            
            this.edge = edge;
            this.docker = docker;
            this.pos = pos;
            this.facade = facade;
			//this.index = docker.parent.dockers.indexOf(docker);
        },
        execute: function(){
           
            this.docker = this.edge.addDocker(this.pos, this.docker);
			this.index = this.edge.dockers.indexOf(this.docker);                                    
            this.facade.getCanvas().update();
            this.facade.updateSelection();
        },
        rollback: function(){
          
             if (this.docker instanceof ORYX.Core.Controls.Docker) {
                    this.edge.removeDocker(this.docker);
             }             
            this.facade.getCanvas().update();
            this.facade.updateSelection(); 
        }
    })    
    var command = new commandClass(options.edge, options.docker, options.position, this.facade);    
    this.facade.executeCommands([command]);
},

//show the ghostpoint
showOverlay: function(edge, position){

	var circle = ORYX.Editor.graft("http://www.w3.org/2000/svg", null ,
				['g', {"pointer-events":"none"},
					['circle', {cx: "8", cy: "8", r: "3", fill:"yellow"}]]);
						
	console.log(position);
	
	this.facade.raiseEvent({
			type: 			ORYX.CONFIG.EVENT_OVERLAY_SHOW,
			id: 			"ghostpoint",
			shapes: 		[edge],
			//attributes: 	{fill: "red", stroke:"green", "stroke-width":4},
			node:			circle,
			nodePosition:	"northeast",
			dontCloneNode:	true
		});
	console.log("event fertig");				
},

//hide the ghostpoint
hideOverlay: function() {
	
	this.facade.raiseEvent({
		type: ORYX.CONFIG.EVENT_OVERLAY_HIDE,
		id: "ghostpoint"
	});	
}


});

