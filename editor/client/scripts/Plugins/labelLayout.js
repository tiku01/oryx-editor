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
		this.point = {x:0, y:0};
		this.labelSelected = false;
		this.labelMovable=false;
		this.down=false;
		this.LabelX=undefined;
		this.LabelY=undefined;
		this.LabelXArea=undefined;
		this.LabelYArea=undefined;
		this.labelLength=undefined;
		this.rotationPointCoordinates = {x:0, y:0};
		this.rotPointParent = undefined;
		this.rotate=false;
		this.firstRotate=false;
		this.State=0;
		this.prevState=0;
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEDOWN, this.handleMouseDown.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEOVER, this.handleMouseOver.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEOUT, this.handleMouseOut.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEMOVE, this.handleMouseMove.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEUP, this.handleMouseUp.bind(this));
		
		this.rotationPoint = ORYX.Editor.graft("http://www.w3.org/2000/svg", null ,
				['g', {"pointer-events":"none"},
					['circle', {cx: "8", cy: "8", r: "5", fill:"green"}]]);
		
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
/*	
	if(uiObj instanceof ORYX.Core.SVG.Label){			
			console.log("label");
			console.log(uiObj);
		}*/
 if( uiObj instanceof ORYX.Core.Edge){
		var myId= uiObj.id+"condition";
		this.myLabel=uiObj._labels[myId];
		this.rotPointParent=uiObj;
		console.log("label registriert");
		console.log(uiObj);
		//console.log(uiObj);
		//console.log(uiObj._labels[myId].x);
		//console.log(uiObj._labels[myId].y);
		//console.log(uiObj._labels[myId]._rotationPoint);
		//console.log(uiObj._labels[myId].rotate);
		//console.log(uiObj._labels.y);
		//this.point.x = uiObj._labels[myId].x;
		//this.point.y = uiObj._labels[myId].y;	
	/*	
		if(this.myLabel)
		{
			this.labelLength=this.myLabel._estimateTextWidth(this.myLabel._text,14);
			this.rotationPointCoordinates.x=this.LabelX+this.labelLength/2;
			this.rotationPointCoordinates.y=this.LabelY-20;	
			console.log("gemalt");
		this.showOverlay( uiObj, this.rotationPointCoordinates );
		
		}*/

	}
 
 
},

handleMouseDown: function(event, uiObj) {


	if(this.myLabel)
	{
	
	this.LabelX=this.myLabel.x;
	this.LabelY=this.myLabel.y;
	this.LabelXArea=this.LabelX+this.labelLength+10;
	this.LabelYArea=this.LabelY-20;	
	this.labelLength=this.myLabel._estimateTextWidth(this.myLabel._text,14);
	this.rotationPointCoordinates.x=this.LabelX-8+this.labelLength/2;
	this.rotationPointCoordinates.y=this.LabelY-30;	

	var MouseX=this.facade.eventCoordinates(event).x;
	var MouseY=this.facade.eventCoordinates(event).y;


/*	
	console.log("LabelX"+this.LabelX);
	console.log("LabelY"+this.LabelY);
	console.log("LabelXArea"+this.LabelXArea);
	console.log("LabelYArea"+this.LabelYArea);
	console.log("MouseX"+MouseX);
	console.log("MouseY"+MouseY);
	console.log("Labellänge:"+this.labelLength);
*/	
	
	if(this.labelSelected==true)
	{
		console.log("label bewegt");
		//LabelPosition setzen
		this.myLabel.x=MouseX;
		this.myLabel.y=MouseY;
		this.myLabel.update();
		
		this.LabelX=this.myLabel.x;
		this.LabelY=this.myLabel.y;
		this.LabelXArea=this.labelLength+10;
		this.LabelYArea=this.LabelY-20;
		this.rotationPointCoordinates.x=this.LabelX-8+this.labelLength/2;
		this.rotationPointCoordinates.y=this.LabelY-30;
		
		this.showOverlay( this.rotPointParent, this.rotationPointCoordinates );		
		
	}
	else {
		this.hideOverlay();
	}
	
	
	if(this.labelSelected==false && MouseX>=this.LabelX-20  && MouseX<=this.LabelXArea && MouseY<=this.LabelY+20 && MouseY >=this.LabelYArea)
	{
		console.log("Label selected");
		
		this.labelSelected=true;		
		this.rotationPointCoordinates.x=this.LabelX-8+this.labelLength/2;
		this.rotationPointCoordinates.y=this.LabelY-30;
		this.showOverlay( this.rotPointParent, this.rotationPointCoordinates );
	}
	else {
		this.labelSelected=false;	
		console.log("label unselected");
	}
	
	//Rotation
	var rotationAreaX=this.rotationPointCoordinates.x+10;
	var rotationAreaY=this.rotationPointCoordinates.y+10;
	console.log("MouseX:"+MouseX);
	console.log("MouseY:"+MouseY);
	console.log("RPX:"+this.rotationPointCoordinates.x);
	console.log("RPY:"+this.rotationPointCoordinates.y);
	console.log("RPXArea:"+rotationAreaX);
	console.log("RPYArea:"+rotationAreaY);
	console.log(MouseX >= this.rotationPointCoordinates.x);
	
	if(this.rotate==false && MouseX >= this.rotationPointCoordinates.x && MouseX <= rotationAreaX && MouseY >= this.rotationPointCoordinates.y && MouseY <= rotationAreaY)
	{	
		//Rotation
		this.rotate=true;
		console.log("Rotation active");
		this.State=0;

	}
	else
	{
		this.rotate=false;
		console.log("Rotation not active");
	}
	
	}
},

handleMouseMove: function(event, uiObj) {
//console.log("mouseMove");
	/*
	var MouseX=this.facade.eventCoordinates(event).x;
	var MouseY=this.facade.eventCoordinates(event).y;
	
	
	if(this.labelSelected==false && MouseX>=this.LabelX && MouseX<=this.LabelXArea && MouseY>=this.LabelY && MouseY<this.LabelYArea){
		console.log("Label selected");
		this.labelSelected=true;
	}
		
	if(this.labelMovable==true && this.down==true)
		
	{
		this.myLabel.x=MouseX;
		this.myLabel.y=MouseY;
		this.myLabel.update();
	}
		//console.log("MouseX"+MouseX);
		//console.log("MouseY"+MouseY);
	*/
	
	if(this.labelSelected==true)
	{
		//console.log("label bewegt");
		//LabelPosition setzen
		
		var MouseX=this.facade.eventCoordinates(event).x;
		var MouseY=this.facade.eventCoordinates(event).y;
		
		this.myLabel.x=MouseX+5;
		this.myLabel.y=MouseY-5;
		this.myLabel.update();
		
		this.LabelX=this.myLabel.x;
		this.LabelY=this.myLabel.y;
		this.LabelXArea=this.labelLength+10;
		this.LabelYArea=this.LabelY-20;
		this.rotationPointCoordinates.x=this.LabelX-8+this.labelLength/2;
		this.rotationPointCoordinates.y=this.LabelY-30;
		
		this.showOverlay( this.rotPointParent, this.rotationPointCoordinates );		
		
	}
	
	if(this.rotate==true) {
		var MouseX=this.facade.eventCoordinates(event).x;
		var MouseY=this.facade.eventCoordinates(event).y;
		this.LabelX=this.myLabel.x;
		this.LabelY=this.myLabel.y;
		this.rotationPointCoordinates.x=this.LabelX-8+this.labelLength/2;
		this.rotationPointCoordinates.y=this.LabelY-30;
		

		//console.log("rotate in move active");
		/*
		if(this.firstRotate==false && MouseX <= this.rotationPointCoordinates.x){
			this.firstRotate=true;
			this.rotate_left();
			console.log("rotateleft");
			
		}
		
		if(this.firstRotate==false && MouseX >= this.rotationPointCoordinates.x ){
			this.rotate_right();
			console.log("rotateright");
			this.firstRotate=true;
		}
		
		if(this.firstRotate==true && MouseX < this.rotationPointCoordinates.x-20 && MouseX > this.rotationPointCoordinates.x-40)
		{
			this.firstRotate=false;
		}
		
		if(this.firstRotate==true && MouseX < this.rotationPointCoordinates.x-40 && MouseX > this.rotationPointCoordinates.x-60 )
		{
			this.firstRotate=false;
		}
		
		if(this.firstRotate==true && MouseX > this.rotationPointCoordinates.x+20 && MouseX < this.rotationPointCoordinates.x+40 )
		{
			this.firstRotate=false;
		}
		
		if(this.firstRotate==true && MouseX > this.rotationPointCoordinates.x+40 && MouseX < this.rotationPointCoordinates.x+60 )
		{
			this.firstRotate=false;
		}*/
		if(MouseX<this.rotationPointCoordinates.x-70)
		{
			this.State=-4;
		}
		else if (MouseX < this.rotationPointCoordinates.x-50 && MouseX >= this.rotationPointCoordinates.x-70)
		{
			this.State=-3;	
		}
		else if (MouseX < this.rotationPointCoordinates.x-30 && MouseX >= this.rotationPointCoordinates.x-50)
		{
			this.State=-2;	
		}
		else if (MouseX < this.rotationPointCoordinates.x-10 && MouseX >= this.rotationPointCoordinates.x-30)
		{
			this.State=-1;
		}
		else if (MouseX < this.rotationPointCoordinates.x+10 && MouseX >= this.rotationPointCoordinates.x-10)
		{
			this.State=0;	
		}
		else if (MouseX < this.rotationPointCoordinates.x+30 && MouseX >= this.rotationPointCoordinates.x+10)
		{
			this.State=1;	
		}
		else if (MouseX < this.rotationPointCoordinates.x+50 && MouseX >= this.rotationPointCoordinates.x+30)
		{
			this.State=2;
		}
		else if (MouseX < this.rotationPointCoordinates.x+70 && MouseX >= this.rotationPointCoordinates.x+50)
		{
			this.State=3;
		}
		else if ( MouseX >= this.rotationPointCoordinates.x+70)
		{
			this.State=4;	
		}
		
		if(this.State>this.prevState)
		{
			this.rotate_right();
			this.prevState=this.State;
		}
		else if(this.State<this.prevState)
		{
			this.rotate_left();
			this.prevState=this.State;
		}
		
	}
	
},

handleMouseUp: function(event, uiObj) {

},

labeling: function () {
	this.myLabel.x+=10;
	this.myLabel.y+=10;
	this.myLabel.update();
},

rotate_right:function() {
	//console.log("rotate right");
	var myRotation= this.myLabel._rotate;
//	console.log("mycurrentRotation: "+myRotation);
	
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
	//console.log("rotate left");
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
	
},

showOverlay: function(edge, point){

	this.facade.raiseEvent({
			type: 			ORYX.CONFIG.EVENT_OVERLAY_SHOW,
			id: 			"rotationPoint",
			shapes: 		[edge],
			node:			this.rotationPoint,
			rotationPoint:	point,
			dontCloneNode:	true
		});			
},

hideOverlay: function() {
	
	this.facade.raiseEvent({
		type: ORYX.CONFIG.EVENT_OVERLAY_HIDE,
		id: "rotationPoint"
	});	
}


});

