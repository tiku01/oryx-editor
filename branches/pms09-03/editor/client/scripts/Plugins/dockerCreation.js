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
		/*
		this.shapeSelection = undefined;
		this.docker 		= undefined;
		this.dockerParent   = undefined;
		this.dockerSource 	= undefined;
		this.dockerTarget 	= undefined;
		this.lastUIObj 		= undefined;
		this.isStartDocker 	= undefined;
		this.isEndDocker 	= undefined;
		this.undockTreshold	= 10;
		this.initialDockerPosition = undefined;
		this.outerDockerNotMoved = undefined;
		this.isValid 		= false; */
		this.counter=0;
		this.ghost=ORYX.Editor.graft("http://www.w3.org/2000/svg",null,['g']);
		
		this.ghostPoint = ORYX.Editor.graft("http://www.w3.org/2000/svg",
				this.ghost,
				['g', {"pointer-events":"all"},
						['circle', {cx:"8", cy:"8", r:"8", stroke:"none", fill:"none"}],
						['circle', {cx:"8", cy:"8", r:"3", stroke:"black", fill:"yellow", "stroke-width":"1"}]
					]);
		
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEDOWN, this.handleMouseDown.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEOVER, this.handleMouseOver.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEOUT, this.handleMouseOut.bind(this));
		
		this.addEventHandlers(this.ghostPoint);
},

/**
 * MouseOut Handler
 *
 */

handleMouseOut: function(event, uiObj) {
	
	//hideDocker 
	
/*	if (uiObj instanceof ORYX.Core.Docker){
		
		this.deleteDockerCommand({ edge: uiObj, position: this.facade.eventCoordinates(event)});
	}*/
},

handleMouseOver: function(event, uiObj) {
	
	//show Point at MousePosition
	
/*	
	if (uiObj instanceof ORYX.Core.Edge){
		
		this.counter+=1;
		
		
	this.addDockerCommand({ edge: uiObj, position: this.facade.eventCoordinates(event)});
		
			
		
	}*/		
},

handleMouseDown: function(event, uiObj) {
	
	//createDocker
	
	if (uiObj instanceof ORYX.Core.Edge){
		this.addDockerCommand({
            edge: uiObj,
            position: this.facade.eventCoordinates(event)
        });
		
	}
		
},




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

deleteDockerCommand: function(options){
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
           
        	this.index = this.edge.dockers.indexOf(this.docker);
            this.pos = this.docker.bounds.center();
            this.edge.removeDocker(this.docker);
                                    
            this.facade.getCanvas().update();
            this.facade.updateSelection();
        },
        rollback: function(){
          
        	//this.edge.add(this.docker, this.index); 
            
            this.facade.getCanvas().update();
            this.facade.updateSelection(); 
        }
    })
    
    var command = new commandClass(options.edge, options.docker, options.position, this.facade);
    
    this.facade.executeCommands([command]);
}






});

