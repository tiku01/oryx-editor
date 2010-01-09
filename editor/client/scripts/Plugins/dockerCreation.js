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
	var x= evPos.x;
	var y= evPos.y;
	var myXPosition =x.toString();
	var myYPosition =y.toString();
	//console.log(x);
	//console.log(y);
	//console.log("x als String "+ myXPosition);
	//console.log("y als String "+ myYPosition);
	var positionStr = "p_"+myXPosition+"_"+myYPosition;
	console.log(positionStr);
	var positionPart = positionStr.split("_")
	//console.log(positionPart[0]);
	//console.log(positionPart[1]);
	//console.log(positionPart[2]);
	console.log(evPos);
	
	if(positionPart[0]=="p"){
		console.log(":)");}
	else {
		console.log(":(");}
	
	if( uiObj instanceof ORYX.Core.Edge){
		
		this.showOverlay( uiObj, positionStr )
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

//refresh the ghostpoint when moving the mouse over an edge
handleMouseMove: function(event, uiObj) {		
	
	if (uiObj instanceof ORYX.Core.Edge){
		var evPos = this.facade.eventCoordinates(event);
		var x= evPos.x;
		var y= evPos.y;
		var myXPosition =x.toString();
		var myYPosition =y.toString();
		var positionStr = "p_"+myXPosition+"_"+myYPosition;
		
		console.log("MouseMove");
		if (this.active) {
			
			this.facade.raiseEvent({
				type: ORYX.CONFIG.EVENT_OVERLAY_HIDE,
				id: "ghostpoint"
			});			
			this.showOverlay( uiObj, positionStr );
		}
		else{
			this.showOverlay( uiObj, positionStr );	
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
			node:			circle,
			nodePosition:	position,
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

