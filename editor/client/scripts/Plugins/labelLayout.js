/**
 * Copyright (c) 2009
 * Robert Böhme
 * 
 **/

if(!ORYX.Plugins)
	ORYX.Plugins = new Object();

ORYX.Plugins.LabelLayout = Clazz.extend({
	
	//facade: null,
	
	construct: function( facade ){
		this.facade = facade;	
		this.myLabel= undefined;
		
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEDOWN, this.handleMouseDown.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEOVER, this.handleMouseOver.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEOUT, this.handleMouseOut.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEMOVE, this.handleMouseMove.bind(this));
		
		 this.facade.offer({
	            'name': "Label move",
	            'functionality': this.labeling.bind(this),
	            'group': "Overlay",
	            'icon': ORYX.PATH + "images/edges.png",
	            'description': "Move a label to bottom right",
	            'index': 1,
	            'minShape': 0,
	            'maxShape': 0
	        });
		 
		 this.facade.offer({
	            'name': "Label rotate left",
	            'functionality': this.rotate_left.bind(this),
	            'group': "Overlay",
	            'icon': ORYX.PATH + "images/arrow_redo.png",
	            'description': "rotate a label left",
	            'index': 1,
	            'minShape': 0,
	            'maxShape': 0
	        });
		 
		 this.facade.offer({
	            'name': "Label rotate right",
	            'functionality': this.rotate_right.bind(this),
	            'group': "Overlay",
	            'icon': ORYX.PATH + "images/arrow_undo.png",
	            'description': "rotate a label right",
	            'index': 1,
	            'minShape': 0,
	            'maxShape': 0
	        });
		 

},

/**
 * MouseOut Handler
 *
 */


handleMouseOut: function(event, uiObj) {

},

handleMouseOver: function(event, uiObj) {
	
	if(uiObj instanceof ORYX.Core.SVG.Label){			
			console.log("label");
			console.log(uiObj);
		}
	else if( uiObj instanceof ORYX.Core.Edge){
		var myId= uiObj.id+"condition";
		this.myLabel=uiObj._labels[myId];
		console.log(uiObj);
		console.log(uiObj._labels[myId].x);
		console.log(uiObj._labels[myId].y);
		console.log(uiObj._labels[myId]._rotationPoint);
		console.log(uiObj._labels[myId].rotate);
		//console.log(uiObj._labels.y);
	}

},

handleMouseDown: function(event, uiObj) {
	//this.myLabel.x=event.pageX-190;
	//this.myLabel.y=event.pageY-40;
	
	this.myLabel.x=this.facade.eventCoordinates(event).x;
	this.myLabel.y=this.facade.eventCoordinates(event).y;
	this.myLabel.update();
		
},

handleMouseMove: function(event, uiObj) {
	
	var xPos= event.pageX;
	var yPos= event.pageY;
	var yLayer=event.y;
	var xLayer= event.x;
	var xScreen= event.screenX;
	var yScreen= event.screenY;
	//console.log("Page: X:"+xPos+ " Y: "+yPos);
	//console.log("*****");
		
},

labeling: function () {
	this.myLabel.x+=10;
	this.myLabel.y+=10;
	this.myLabel.update();
},

rotate_right:function() {
	console.log("rotate right");
	var myRotation= this.myLabel._rotate;
	console.log("mycurrentRotation: "+myRotation);
	
	if(myRotation==0 || myRotation < 45 && myRotation > 0 || myRotation == 360)
	{
		this.myLabel.rotate(45);
	}
	else if(myRotation==45 || myRotation <90 && myRotation > 45)
	{
		this.myLabel.rotate(90);
	}
	else if(myRotation == 315||myRotation >315 && myRotation <360)
	{
		this.myLabel.rotate(360);
	}
	else if(myRotation == 270||myRotation >270 && myRotation <315)
	{
		this.myLabel.rotate(315);
	}
	
	this.myLabel.update();
},

rotate_left:function() {
	console.log("rotate left");
	var myRotation= this.myLabel._rotate;
	
	if(myRotation==0 || myRotation < 360 && myRotation > 315 || myRotation == 360)
	{
		this.myLabel.rotate(315);
	}
	else if(myRotation==315 || myRotation <315 && myRotation > 270)
	{
		this.myLabel.rotate(270);
	}
	else if(myRotation == 45||myRotation <45 && myRotation >0)
	{
		this.myLabel.rotate(360);
	}
	else if(myRotation == 90||myRotation <90 && myRotation >45)
	{
		this.myLabel.rotate(45);
	}
	
	this.myLabel.update();
	
}


});

