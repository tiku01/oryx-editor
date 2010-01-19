/**
 * Copyright (c) 2009
 * Robert Böhme
 * 
 **/

if(!ORYX.Plugins)
	ORYX.Plugins = new Object();

ORYX.Plugins.LabelLayout = Clazz.extend({
		
	construct: function( facade ){
		this.facade = facade;	
		this.myLabel= undefined; //defines the current label
		this.labelSelected = false; //true->label is selected
		this.LabelX=undefined;	//Y-Position of the Label
		this.LabelY=undefined; //X-Coordinate of the label
		this.LabelXArea=undefined; //the width of the label (X-Coordinate relative to rotaionPointCoordinates)
		this.LabelYArea=undefined; //the height of the label (Y-Coordinate relative to rotaionPointCoordinates)
		this.labelLength=undefined;
		this.rotationPointCoordinates = {x:0, y:0};
		this.rotPointParent = undefined;	// set the Parent for the RotaionPoint	
		this.rotate=false; //true ->Rotation of label is active; False -> Rotation of Label is not active
		this.State=0; 		//current States for Rotation
		this.prevState=0;	//previous State for Rotation
		
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEDOWN, this.handleMouseDown.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEOVER, this.handleMouseOver.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEOUT, this.handleMouseOut.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEMOVE, this.handleMouseMove.bind(this));
		
		//Visual representaion of the default RotationPoint
		this.rotationPoint = ORYX.Editor.graft("http://www.w3.org/2000/svg", null ,
				['g', {"pointer-events":"none"},
					['circle', {cx: "8", cy: "8", r: "5", fill:"green"}]]);
		
		//Visual representation of the Rotationpoint if rotation is active
		this.rotationPointActive = ORYX.Editor.graft("http://www.w3.org/2000/svg", null ,
				['g', {"pointer-events":"none"},
					['circle', {cx: "8", cy: "8", r: "5", fill:"yellow"}]]);		
		 
		 this.facade.offer({
	            'name': "Label rotate left",
	            'functionality': this.rotate_left.bind(this),
	            'group': "Overlay",
	            'icon': ORYX.PATH + "images/arrow_undo.png",
	            'description': "rotate a label left",
	            'index': 1,
	            'minShape': 0,
	            'maxShape': 0
	        });
		 
		 this.facade.offer({
	            'name': "Label rotate right",
	            'functionality': this.rotate_right.bind(this),
	            'group': "Overlay",
	            'icon': ORYX.PATH + "images/arrow_redo.png",
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

//Hide RotationPoint when leaving an Edge
handleMouseOut: function(event, uiObj) {

	if( uiObj instanceof ORYX.Core.Edge){
		if(this.myLabel){
			this.hideRotationPointOverlay();
		}
	 }
},

handleMouseOver: function(event, uiObj) {

	if( uiObj instanceof ORYX.Core.Edge){
		
		//Identify and set the label of the current Edge	
		this.myLabel=uiObj._labels[uiObj.id+"condition"];
	 
		//save the edge for adding rotationpoint	
		this.rotPointParent=uiObj;
		
		//Show the RotationPoint of the label of the current Edge
		if(this.myLabel && this.myLabel._text!=""){
			this.calculateRotationPointCoordinates();
			this.showRotationPointOverlay( uiObj, this.rotationPointCoordinates );		
		}
	}
},

handleMouseDown: function(event, uiObj) {

	if(this.myLabel){
	
		//save MousePosition
		var MouseX=this.facade.eventCoordinates(event).x;
		var MouseY=this.facade.eventCoordinates(event).y;
		
		//refresh Coordinates
		this.calculateLabelCoordinates();
		this.calculateRotationPointCoordinates();
	
		if(this.labelSelected==true){

			//Set LabelPosition to MousePosition
			this.myLabel.x=MouseX;
			this.myLabel.y=MouseY;
			this.myLabel.update();
		
			//Refresh Coordinates
			this.calculateLabelCoordinates();
			this.calculateRotationPointCoordinates();		
			
			//Show RotationPoint
			this.showRotationPointOverlay( this.rotPointParent, this.rotationPointCoordinates );		
		}
		else {
			//hide RotationPoint
			this.hideRotationPointOverlay();
		}
	
		//Check if Mouse is in the ClickArea of the Label
		if(	this.labelSelected==false && 
			MouseX >= this.LabelX-20 && 
			MouseX <= this.LabelXArea && 
			MouseY <= this.LabelY+20 && 
			MouseY >= this.LabelYArea){		
		
			//Set Label as Selected
			this.labelSelected=true;
		
			//refresh and show RotationPoint
			this.calculateRotationPointCoordinates();
			this.showRotationPointOverlay( this.rotPointParent, this.rotationPointCoordinates );
		}
		else {
			//Set Label as not selected
			this.labelSelected=false;	
		}
	
		//Check if MouseClick is on RotationPoint
		if(	this.rotate==false && 
			MouseX >= this.rotationPointCoordinates.x && 
			MouseX <= this.rotationPointCoordinates.x+10 && 
			MouseY >= this.rotationPointCoordinates.y && 
			MouseY <= this.rotationPointCoordinates.y+10){
		
			//acitvate Rotation
			this.rotate=true;
			
			//Set current RotationState
			this.State=0;
			
			//show yellow RotationPoint to show that rotation is active
			this.showOverlayActive( this.rotPointParent, this.rotationPointCoordinates );
		}
		else{
			//deactivate Rotation
			this.rotate=false;
			
			//Hide yellow RotationPoint
			this.hideOverlayActive();
		}	
	}
},

handleMouseMove: function(event, uiObj) {

	//if label is selected label Posision is set to MousePosition
	if(this.labelSelected==true){
		
		//Set Label positon
		this.myLabel.x=this.facade.eventCoordinates(event).x+5;
		this.myLabel.y=this.facade.eventCoordinates(event).y-5;
		this.myLabel.update();
		
		//refresh Coordinates
		this.calculateLabelCoordinates();
		this.calculateRotationPointCoordinates();
		
		//show RotationPoint
		this.showRotationPointOverlay( this.rotPointParent, this.rotationPointCoordinates );		
	}
	
	//perform the Statevalidation for Rotation
	if(this.rotate==true) {
		
		//save MouseCoordinates
		var MouseX=this.facade.eventCoordinates(event).x;
		var MouseY=this.facade.eventCoordinates(event).y;

		//refresh Coordinates
		this.calculateLabelCoordinates();
		this.calculateRotationPointCoordinates();
		
		//defines the States for Rotation(every 20px there is a new State)
		if(MouseX<this.rotationPointCoordinates.x-70){
			this.State=-4;
		}
		else if (MouseX < this.rotationPointCoordinates.x-50 && MouseX >= this.rotationPointCoordinates.x-70){
			this.State=-3;	
		}
		else if (MouseX < this.rotationPointCoordinates.x-30 && MouseX >= this.rotationPointCoordinates.x-50){
			this.State=-2;	
		}
		else if (MouseX < this.rotationPointCoordinates.x-10 && MouseX >= this.rotationPointCoordinates.x-30){
			this.State=-1;
		}
		else if (MouseX < this.rotationPointCoordinates.x+10 && MouseX >= this.rotationPointCoordinates.x-10){
			this.State=0;	
		}
		else if (MouseX < this.rotationPointCoordinates.x+30 && MouseX >= this.rotationPointCoordinates.x+10){
			this.State=1;	
		}
		else if (MouseX < this.rotationPointCoordinates.x+50 && MouseX >= this.rotationPointCoordinates.x+30){
			this.State=2;
		}
		else if (MouseX < this.rotationPointCoordinates.x+70 && MouseX >= this.rotationPointCoordinates.x+50){
			this.State=3;
		}
		else if ( MouseX >= this.rotationPointCoordinates.x+70){
			this.State=4;	
		}
		
		//checks the way of moving the Mouse through the states and rotate
		if(this.State>this.prevState){
			this.rotate_right();
			this.prevState=this.State;
		}
		else if(this.State<this.prevState){
			this.rotate_left();
			this.prevState=this.State;
		}		
	}
},


//rotate the label to the right with 45° (clockwise)
rotate_right:function() {

	var myRotation= this.myLabel._rotate;
	
	if(myRotation==0 || myRotation < 45 && myRotation > 0 || myRotation == 360){
		this.myLabel.rotate(45);
	}
	else if(myRotation==45 || myRotation <90 && myRotation > 45){
		this.myLabel.rotate(90);
	}
	else if(myRotation == 315||myRotation >315 && myRotation <360){
		this.myLabel.rotate(360);
	}
	else if(myRotation == 270||myRotation >270 && myRotation <315){
		this.myLabel.rotate(315);
	}	
	this.myLabel.update();
},

//rotate the label to the left with 45° (anticlockwise)
rotate_left:function() {

	var myRotation= this.myLabel._rotate;
	
	if(myRotation==0 || myRotation < 360 && myRotation > 315 || myRotation == 360){
		this.myLabel.rotate(315);
	}
	else if(myRotation==315 || myRotation <315 && myRotation > 270){
		this.myLabel.rotate(270);
	}
	else if(myRotation == 45||myRotation <45 && myRotation >0){
		this.myLabel.rotate(360);
	}
	else if(myRotation == 90||myRotation <90 && myRotation >45){
		this.myLabel.rotate(45);
	}	
	this.myLabel.update();	
},

showRotationPointOverlay: function(edge, point){

	this.facade.raiseEvent({
			type: 			ORYX.CONFIG.EVENT_OVERLAY_SHOW,
			id: 			"rotationPoint",
			shapes: 		[edge],
			node:			this.rotationPoint,
			rotationPoint:	point,
			dontCloneNode:	true
		});			
},

hideRotationPointOverlay: function() {
	
	this.facade.raiseEvent({
		type: ORYX.CONFIG.EVENT_OVERLAY_HIDE,
		id: "rotationPoint"
	});	
},

showOverlayActive: function(edge, point){

	this.facade.raiseEvent({
			type: 			ORYX.CONFIG.EVENT_OVERLAY_SHOW,
			id: 			"rotationPointActive",
			shapes: 		[edge],
			node:			this.rotationPointActive,
			rotationPoint:	point,
			dontCloneNode:	true
		});			
},

hideOverlayActive: function() {
	
	this.facade.raiseEvent({
		type: ORYX.CONFIG.EVENT_OVERLAY_HIDE,
		id: "rotationPointActive"
	});	
},

//set the Coordinates of the RotationPoint
calculateRotationPointCoordinates: function(){
	this.labelLength=this.myLabel._estimateTextWidth(this.myLabel._text,14);
	this.rotationPointCoordinates.x=this.LabelX-8+this.labelLength/2;
	this.rotationPointCoordinates.y=this.LabelY-30;	
},

//set the Coordinates of the Label
calculateLabelCoordinates: function(){
	this.LabelX=this.myLabel.x;
	this.LabelY=this.myLabel.y;
	this.LabelXArea=this.LabelX+this.labelLength+10;
	this.LabelYArea=this.LabelY-20;	
}

});