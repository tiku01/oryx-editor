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
		this.mouseCoordinates = {x:0, y:0};
		this.rotPointParent = undefined;	// set the Parent for the RotaionPoint	
		this.rotate=false; //true ->Rotation of label is active; False -> Rotation of Label is not active
		this.State=0; 		//current States for Rotation
		this.prevState=0;	//previous State for Rotation
		this.canvasLabel = undefined; //Reference to Canvas
		this.canvas=false; //true if Reference to Canvas was saved
		this.edgeSelected = false;
		
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEDOWN, this.handleMouseDown.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEOVER, this.handleMouseOver.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEOUT, this.handleMouseOut.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEMOVE, this.handleMouseMove.bind(this));
		
		//Visual representaion of the default RotationPoint
		/*this.rotationPoint = ORYX.Editor.graft("http://www.w3.org/2000/svg", null ,
				['g', {"pointer-events":"none"},
					['circle', {cx: "8", cy: "8", r: "5", fill:"green"}]]); */
		
		this.rotationPoint = ORYX.Editor.graft("http://www.w3.org/2000/svg", null ,
				['path', {
					"stroke-width": 2.0, "stroke":"black", "d":  "M4,4 L0,6 M0,6 L-4,4 M-4,4 L-6,0 M-6,0 L-4,-4 M-4,-4 L0,-6 M0,-6 L4,-4 M4,-4 L6,2 M6,2 L2,0 M6,2 L8,0", "line-captions": "round"
					}]);
		
		
		//Visual representation of the Rotationpoint if rotation is active
		this.rotationPointActive = ORYX.Editor.graft("http://www.w3.org/2000/svg", null ,
				['g', {"pointer-events":"none"},
					['circle', {cx: "8", cy: "8", r: "5", fill:"yellow"}]]);
		
		this.line = ORYX.Editor.graft("http://www.w3.org/2000/svg", null,
				['path', {
					'stroke-width': 1, stroke: 'silver', fill: 'none',
					'pointer-events': 'none'}]);
		
		this.startMovingCross = ORYX.Editor.graft("http://www.w3.org/2000/svg", null ,
				['path', {
					"stroke-width": 1.0, "stroke":"black", "d":  "M0,0 L-10,0 M-10,0 L-6,-4 M-10,0 L-6,4 M0,0 L10,0 M10,0 L6,4 M10,0 L6,-4 M0,0 L0,10 M0,10 L4,6 M0,10 L-4,6 M0,0 L0,-10 M0,-10 L4,-6 M0,-10 L-4,-6", "line-captions": "round"
					}]);
		
		this.endMovingCross = ORYX.Editor.graft("http://www.w3.org/2000/svg", null ,
				['path', {
					"stroke-width": 1.0, "stroke":"black", "d":  "M-2,0 L-10,0 M2,0 L10,0 M0,2 L0,10 M0,-2 L0,-10 M-2,0 L-6,4 M-2,0 L-6,-4 M2,0 L6,4 M2,0 L6,-4 M0,-2 L4,-6 M0,-2 L-4,-6 M0,2 L4,6 M0,2 L-4,6", "line-captions": "round"
					}]);
		
		this.moveLeftRight = ORYX.Editor.graft("http://www.w3.org/2000/svg", null ,
				['path', {
					"stroke-width": 2.0, "stroke":"black", "d":  "M0,0 L-15,0 M-15,0 L-11,-4 M-15,0 L-11,4 M0,0 L15,0 M15,0 L11,4 M15,0 L11,-4", "line-captions": "round"
					}]);
		
		 
		 this.facade.offer({
	            'name': "Label rotate left",
	            'functionality': this.rotate_left.bind(this),
	            'group': "Overlay",
	            'icon': ORYX.PATH + "images/rotate_left.png",
	            'description': "rotate a label left",
	            'index': 1,
	            'minShape': 0,
	            'maxShape': 0
	        });
		 
		 this.facade.offer({
	            'name': "Label rotate right",
	            'functionality': this.rotate_right.bind(this),
	            'group': "Overlay",
	            'icon': ORYX.PATH + "images/rotate_right.png",
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
/*
	if( uiObj instanceof ORYX.Core.Edge && this.edgeSelected==false ){
		if(this.myLabel){
			this.hideRotationPointOverlay();
			this.hideLine();
		}
	 }*/
},

handleMouseOver: function(event, uiObj) {

	//Save Canvas for Reference(used for showing Line between edge and label)
	if(this.canvas==false){
		if( uiObj instanceof ORYX.Core.Canvas){
			this.canvasLabel=uiObj;
			canvas=true;
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
			if(this.myLabel._rotate==360){
				this.myLabel.x=MouseX+5;
				this.myLabel.y=MouseY-5;
			}
			else if(this.myLabel._rotate==90) {
				this.myLabel.x=MouseX+10;
				this.myLabel.y=MouseY-10;
			}
			else if(this.myLabel._rotate==180) {
				this.myLabel.x=MouseX-10;
				this.myLabel.y=MouseY-10;
			}
			else if(this.myLabel._rotate==270) {
				this.myLabel.x=MouseX-10;
				this.myLabel.y=MouseY-10;
			}
			else {
				//Default
				this.myLabel.x=MouseX+5;
				this.myLabel.y=MouseY-5;
			}
			
			//refresh real Rotationpoint
			if(this.myLabel._rotationPoint){
				this.myLabel._rotationPoint.x=MouseX;
				this.myLabel._rotationPoint.y=MouseY;
			}
			
			// save the current position of the label in edge.js, mark the label as free moved
			this.myLabel.edgePosition="freeMoved";
			this.myLabel.update();
			
		
			//Refresh Coordinates
			this.calculateLabelCoordinates();
			this.calculateRotationPointCoordinates();		
			
			//Show visaul RotationPoint
			this.showRotationPointOverlay( this.rotPointParent, this.rotationPointCoordinates );
			
			this.hideSettingArrows();
			this.mouseCoordinates = {x:this.LabelX-5, y:this.LabelY+5};			
			this.showMovingArrows(this.rotPointParent, this.mouseCoordinates );
			
			//show the Association line
			//this.showLine();
		}
		else {
			//hide RotationPoint
			this.hideRotationPointOverlay();
			//hide Association Line
			this.hideLine();
			this.hideMovingArrows();
			this.hideSettingArrows();
		}
	
		//Check if Mouse is in the ClickArea of the Label (for different degrees)
		if(	this.labelSelected==false && 
			MouseX >= this.LabelX-20 && 
			MouseX <= this.LabelXArea && 
			MouseY <= this.LabelY+20 && 
			MouseY >= this.LabelYArea &&
			this.myLabel._rotate == 360 ||
			
			this.labelSelected==false &&
			MouseX >= this.LabelX-5 && 
			MouseX <= this.LabelXArea  &&
			MouseY >= this.LabelY-20 &&
			MouseY <= this.LabelYArea &&
			this.myLabel._rotate ==90 ||
			
			this.labelSelected==false &&
			MouseX >= this.LabelXArea && 
			MouseX <= this.LabelX+5  &&
			MouseY >= this.LabelY-20 &&
			MouseY <= this.LabelYArea &&
			this.myLabel._rotate ==180 ||
			
			this.labelSelected==false &&
			MouseX >= this.LabelXArea && 
			MouseX <= this.LabelX+5  &&
			MouseY >= this.LabelYArea &&
			MouseY <= this.LabelY &&
			this.myLabel._rotate==270 ||
			
			this.labelSelected==false && 
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
			MouseX <= this.rotationPointCoordinates.x+15 && 
			MouseY >= this.rotationPointCoordinates.y && 
			MouseY <= this.rotationPointCoordinates.y+15){
		
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
	
	//clicking on an Edge saves the label and show the line
	if( uiObj instanceof ORYX.Core.Edge){  		
		
		//Identify and set the label of the current Edge	
		this.myLabel=uiObj._labels[uiObj.id+"condition"];
	 
		//save the edge for adding rotationpoint	
		this.rotPointParent=uiObj;
		
		//Show the RotationPoint and line of the label of the current Edge
		if(this.myLabel && this.myLabel._text!=""){
			this.calculateLabelCoordinates();
			this.calculateRotationPointCoordinates();
			//this.showRotationPointOverlay( uiObj, this.rotationPointCoordinates );	
			this.showLine();
			
			this.calculateLabelCoordinates();
			this.mouseCoordinates = {x:this.LabelX-5, y:this.LabelY+5};			
			this.showMovingArrows(this.rotPointParent, this.mouseCoordinates );
			this.showRotationPointOverlay(this.rotPointParent, this.rotationPointCoordinates);
		}
	}		
	else {		
		if(this.myLabel){
			this.hideRotationPointOverlay();
			this.hideLine();
			this.hideRotationPointOverlay();
		}		 
	}
	
	
},

handleMouseMove: function(event, uiObj) {

	//if label is selected Posision is set to MousePosition
	if(this.labelSelected==true){
		if(this.myLabel){
			
			//Set Label positon for different Rotations
			
			if(this.myLabel._rotate==360){
				this.myLabel.x=this.facade.eventCoordinates(event).x+5;
				this.myLabel.y=this.facade.eventCoordinates(event).y-5;
				this.myLabel._rotationPoint.x=this.facade.eventCoordinates(event).x+5;
				this.myLabel._rotationPoint.y=this.facade.eventCoordinates(event).y-5;
			}
			else if(this.myLabel._rotate==90) {
				this.myLabel.x=this.facade.eventCoordinates(event).x+10;
				this.myLabel.y=this.facade.eventCoordinates(event).y-10;
				this.myLabel._rotationPoint.x=this.facade.eventCoordinates(event).x+10;
				this.myLabel._rotationPoint.y=this.facade.eventCoordinates(event).y-10;
			}
			else if(this.myLabel._rotate==180) {
				this.myLabel.x=this.facade.eventCoordinates(event).x-10;
				this.myLabel.y=this.facade.eventCoordinates(event).y-10;
				this.myLabel._rotationPoint.x=this.facade.eventCoordinates(event).x-10;
				this.myLabel._rotationPoint.y=this.facade.eventCoordinates(event).y-10;
			}
			else if(this.myLabel._rotate==270) {
				this.myLabel.x=this.facade.eventCoordinates(event).x-10;
				this.myLabel.y=this.facade.eventCoordinates(event).y-10;
				this.myLabel._rotationPoint.x=this.facade.eventCoordinates(event).x-10;
				this.myLabel._rotationPoint.y=this.facade.eventCoordinates(event).y-10;
			}
			else {
				//Default
				this.myLabel.x=this.facade.eventCoordinates(event).x+5;
				this.myLabel.y=this.facade.eventCoordinates(event).y-5;
				this.myLabel._rotationPoint.x=this.facade.eventCoordinates(event).x+5;
				this.myLabel._rotationPoint.y=this.facade.eventCoordinates(event).y-5;
			}

			this.myLabel.update();
		
			//refresh Coordinates
			this.calculateLabelCoordinates();
			this.calculateRotationPointCoordinates();
		
			//show RotationPoint
			//this.showRotationPointOverlay( this.rotPointParent, this.rotationPointCoordinates );	
			this.hideRotationPointOverlay();
		
			
			this.mouseCoordinates = {x:this.LabelX-5, y:this.LabelY+5};	
			this.hideMovingArrows();
			this.hideSettingArrows();
			this.showSettingArrows(this.rotPointParent, this.mouseCoordinates );
			
			
			//Refresh the Line
			this.hideLine();
			this.showLine();
		}
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
		if(MouseX < this.rotationPointCoordinates.x-150 ){
			this.State=-8;
		}
		else if (MouseX < this.rotationPointCoordinates.x-130 && MouseX >= this.rotationPointCoordinates.x-150){
			this.State=-7;	
		}
		else if (MouseX < this.rotationPointCoordinates.x-110 && MouseX >= this.rotationPointCoordinates.x-130){
			this.State=-6;	
		}
		else if (MouseX < this.rotationPointCoordinates.x-90 && MouseX >= this.rotationPointCoordinates.x-110){
			this.State=-5;	
		}
		else if (MouseX < this.rotationPointCoordinates.x-70 && MouseX >= this.rotationPointCoordinates.x-90){
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
		else if ( MouseX < this.rotationPointCoordinates.x+90 && MouseX >= this.rotationPointCoordinates.x+70){
			this.State=4;	
		}
		else if ( MouseX < this.rotationPointCoordinates.x+110 && MouseX >= this.rotationPointCoordinates.x+90){
			this.State=5;	
		}
		else if ( MouseX < this.rotationPointCoordinates.x+130 && MouseX >= this.rotationPointCoordinates.x+110){
			this.State=6;	
		}
		else if ( MouseX < this.rotationPointCoordinates.x+150 && MouseX >= this.rotationPointCoordinates.x+130){
			this.State7;	
		}
		else if(MouseX >= this.rotationPointCoordinates.x+150){
			this.State=8;
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
		//this.myLabel._rotationPoint.x=this.LabelX;
		//this.myLabel._rotationPoint.y=this.LabelY;
		//console.log(this.myLabel._rotationPoint);
	}
},


//rotate the label to the right with 45° (clockwise)
rotate_right:function() {

	var myRotPoint= {x:this.LabelX, y:this.LabelY};
	var myRotation= this.myLabel._rotate;
	
	if(myRotation==0 || myRotation < 45 && myRotation > 0 || myRotation == 360){
		this.myLabel.rotate(45, myRotPoint);
	}
	else if(myRotation==45 || myRotation <90 && myRotation > 45){
		this.myLabel.rotate(90, myRotPoint);
	}
	else if(myRotation == 315||myRotation >315 && myRotation <360){
		this.myLabel.rotate(360,myRotPoint);
	}
	else if(myRotation == 270||myRotation >270 && myRotation <315){
		this.myLabel.rotate(315,myRotPoint);
	}
	else if(myRotation == 90||myRotation <135 && myRotation >90){
		this.myLabel.rotate(135,myRotPoint);
	}
	else if(myRotation == 135||myRotation <180 && myRotation >135){
		this.myLabel.rotate(180,myRotPoint);
	}
	else if(myRotation == 180||myRotation <225 && myRotation >180){
		this.myLabel.rotate(225,myRotPoint);		
	}
	else if(myRotation == 225||myRotation <270 && myRotation >225){
		this.myLabel.rotate(270,myRotPoint);
	}
	this.myLabel.update();
},

//rotate the label to the left with 45° (anticlockwise)
rotate_left:function() {

	var myRotPoint= {x:this.LabelX, y:this.LabelY};
	var myRotation= this.myLabel._rotate;
	
	if(myRotation==0 || myRotation < 360 && myRotation > 315 || myRotation == 360){
		this.myLabel.rotate(315,myRotPoint);
	}
	else if(myRotation==315 || myRotation <315 && myRotation > 270){
		this.myLabel.rotate(270,myRotPoint);
	}
	else if(myRotation == 45||myRotation <45 && myRotation >0){
		this.myLabel.rotate(360,myRotPoint);
	}
	else if(myRotation == 90||myRotation <90 && myRotation >45){
		this.myLabel.rotate(45,myRotPoint);
	}
	else if(myRotation == 135||myRotation <135 && myRotation >90){
		this.myLabel.rotate(90,myRotPoint);
	}
	else if(myRotation == 180||myRotation <180 && myRotation >135){
		this.myLabel.rotate(135,myRotPoint);
	}
	else if(myRotation == 225||myRotation <225 && myRotation >180){
		this.myLabel.rotate(180,myRotPoint);
	}
	else if(myRotation == 270||myRotation <270 && myRotation >225){
		this.myLabel.rotate(225,myRotPoint);
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
			node:			this.moveLeftRight,
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

showMovingArrows: function(edge, point) {
	
	this.facade.raiseEvent({
		type: 			ORYX.CONFIG.EVENT_OVERLAY_SHOW,
		id: 			"MovingArrows",
		shapes: 		[edge],
		node:			this.startMovingCross,
		rotationPoint:	point,
		dontCloneNode:	true
	});	
},

hideMovingArrows: function() {
	this.facade.raiseEvent({
		type: ORYX.CONFIG.EVENT_OVERLAY_HIDE,
		id: "MovingArrows"
	});	
},

showSettingArrows: function(edge, point) {
	
	this.facade.raiseEvent({
		type: 			ORYX.CONFIG.EVENT_OVERLAY_SHOW,
		id: 			"SettingArrows",
		shapes: 		[edge],
		node:			this.endMovingCross,
		rotationPoint:	point,
		dontCloneNode:	true
	});	
},

hideSettingArrows: function() {
	this.facade.raiseEvent({
		type: ORYX.CONFIG.EVENT_OVERLAY_HIDE,
		id: "SettingArrows"
	});	
},


//set the Coordinates of the RotationPoint for different degree values
calculateRotationPointCoordinates: function(){
	
	if(this.rotate==false) {
		this.labelLength=this.myLabel._estimateTextWidth(this.myLabel._text,14);
	
		if(this.myLabel._rotate==360){
			this.rotationPointCoordinates.x=this.LabelX-8+this.labelLength/3;
			this.rotationPointCoordinates.y=this.LabelY-35;	
		}
		else if(this.myLabel._rotate==90) {
			this.rotationPointCoordinates.x=this.LabelX+35;
			this.rotationPointCoordinates.y=this.LabelY-8+this.labelLength/3;	
		
		}
		else if(this.myLabel._rotate==180) {
			this.rotationPointCoordinates.x=this.LabelX-this.labelLength/2;
			this.rotationPointCoordinates.y=this.LabelY+35;
		}
		else if(this.myLabel._rotate==270) {
			this.rotationPointCoordinates.x=this.LabelX-35;
			this.rotationPointCoordinates.y=this.LabelY+8-this.labelLength/2;	
		}
		else {
			this.rotationPointCoordinates.x=this.LabelX-8+this.labelLength/3;
			this.rotationPointCoordinates.y=this.LabelY-35;	
		}
	}
},

//set the Coordinates of the Label
calculateLabelCoordinates: function(){		
		
		this.LabelX=this.myLabel.x;
		this.LabelY=this.myLabel.y;		
	
		if(this.myLabel._rotate==360){		
			this.LabelXArea=this.LabelX+this.labelLength+10;
			this.LabelYArea=this.LabelY-20;	
		}
		else if(this.myLabel._rotate==90) {
			this.LabelXArea=this.LabelX+20;
			this.LabelYArea=this.LabelY+this.labelLength+10;
		}
		else if(this.myLabel._rotate==180) {
			this.LabelXArea=this.LabelX-this.labelLength-10;
			this.LabelYArea=this.LabelY+20;
		}
		else if(this.myLabel._rotate==270) {
			this.LabelXArea=this.LabelX-20;
			this.LabelYArea=this.LabelY-this.labelLength-10;
		}
		else {
			//Default
			this.LabelXArea=this.LabelX+this.labelLength+10;
			this.LabelYArea=this.LabelY-20;
		}	
},

//show the Association Line between Edge and Label
showLine: function() {

	var x= this.rotPointParent.dockers[0].bounds.b.x-8;
	var y= this.rotPointParent.dockers[0].bounds.b.y-8;
	
	//Set the Position of the Line
	this.line.setAttributeNS(null, 'd', 'M'+x+' '+y+' L '+this.LabelX+' '+this.LabelY);


	this.facade.raiseEvent({
		type: 			ORYX.CONFIG.EVENT_OVERLAY_SHOW,
		id: 			"line",
		shapes: 		[this.canvasLabel],
		node:			this.line,
		position:		"northeast",
		dontCloneNode:	true
	});
	
},

hideLine: function() {
	this.facade.raiseEvent({
		type: ORYX.CONFIG.EVENT_OVERLAY_HIDE,
		id: "line"
	});
}

});