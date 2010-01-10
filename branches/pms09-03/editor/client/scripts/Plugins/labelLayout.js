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
		console.log(uiObj._labels[myId].x);
		console.log(uiObj._labels[myId].y);
		//console.log(uiObj._labels.y);
	}

},

handleMouseDown: function(event, uiObj) {	
		
},

handleMouseMove: function(event, uiObj) {		
		
},

labeling: function () {
	this.myLabel.x+=10;
	this.myLabel.y+=10;
	this.myLabel.update();
}



});

