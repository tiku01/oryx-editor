/**
 * Copyright (c) 2010
 * Tobias Metzke
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
 
if(!ORYX.Plugins) {
	ORYX.Plugins = {};
}

//TODO language support
ORYX.I18N.PictureSupport = {
	imp: {
		name: "Import",
		description: "Import ...",
		group: "import"
		// ...
	}
}

// just for logging
ORYX_LOGLEVEL = 3;
 
ORYX.Plugins.PictureSupport = ORYX.Plugins.AbstractPlugin.extend({
 
	 construct: function(){
        // Call super class constructor
        arguments.callee.$.construct.apply(this, arguments);
        
        // build a button in the tool bar
        this.facade.offer({
            'name': ORYX.I18N.PictureSupport.imp.name,
            'functionality': this.importPicture.bind(this),
            'group': ORYX.I18N.PictureSupport.imp.group,
            'dropDownGroupIcon': ORYX.PATH + "images/import.png",
			'icon': ORYX.PATH + "images/page_white_javascript.png",
            'description': ORYX.I18N.PictureSupport.imp.description,
            'index': 0,
            'minShape': 0,
            'maxShape': 0
        });
        
        // change the shape menu's alignment
        ORYX.CONFIG.SHAPEMENU_RIGHT = ORYX.CONFIG.SHAPEMENU_BOTTOM;
        ORYX.CONFIG.SHAPEMENU_BUTTONS_PER_LEVEL_BOTTOM = 6;
        
        // catch occurring events
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_LOADED, this.pictureInstantiation.bind(this));
		this.facade.registerOnEvent('layout.picture.node', this.handleProperties.bind(this));
		
 	},
	
	calculateLabelHeight: function (labelElement, labelValue) {
		var fontSize = labelElement.getFontSize();
		var newlineOccurences = 0;
		
		labelValue.scan('\n', function() { newlineOccurences += 1; });
		
		return newlineOccurences * fontSize;
	},
	
	findLabelValue: function(shape,string){
		var value = "";
		var properties = shape.properties.keys().findAll(function(element){return element.substr(5,string.length) == string});
		properties.each(function(element){value += shape.properties[element]});
		return value;
	},
	
	handleProperties: function(event){		

		var shape = event.shape;
		var properties = shape._svgShapes.find(function(element) { return element.element.id == (shape.id + "properties_frame") });
		var image_frames = shape._svgShapes.findAll(function(element) { return element.element.id.substr(element.element.id.length-5,5) == "image" });
		var text_frame = shape._svgShapes.find(function(element) { return element.element.id == (shape.id + "text_frame_title") });
		var propHeight = 0;
		
		// before showing the properties the correct height of the node needs to be calculated
		if(shape.properties["oryx-basic-show_properties"]==true){
			// get all chapters and their content
			var realisation = shape.getLabels().find(function(label) { return label.id == (shape.id + "realisation") });
			var realisationValue = this.findLabelValue(shape,"realisation");
			var incoming = shape.getLabels().find(function(label) { return label.id == (shape.id + "incoming") });
			var incomingValue = this.findLabelValue(shape,"incoming");
			var outgoing = shape.getLabels().find(function(label) { return label.id == (shape.id + "outgoing") });
			var outgoingValue = this.findLabelValue(shape,"outgoing");
			var communication = shape.getLabels().find(function(label) { return label.id == (shape.id + "communication") });
			var communicationValue = this.findLabelValue(shape,"communication");
			var payment = shape.getLabels().find(function(label) { return label.id == (shape.id + "payment") });
			var paymentValue = this.findLabelValue(shape,"payment");
			var resource = shape.getLabels().find(function(label) { return label.id == (shape.id + "resource") });
			var resourceValue = this.findLabelValue(shape,"resource");
			var comment = shape.getLabels().find(function(label) { return label.id == (shape.id + "comment") });
			var commentValue = this.findLabelValue(shape,"comment");
			//TODO resize properties and its HTML rectangle (@properties.element) and children according to content
			
			//TODO set the chapter labels if chapter not empty
			
			// calculate heights of all chapters
			var realisationHeight = this.calculateLabelHeight(realisation,realisationValue);
			var incomingHeight = this.calculateLabelHeight(incoming,incomingValue);
			var outgoingHeight = this.calculateLabelHeight(outgoing,outgoingValue);
			var communicationHeight = this.calculateLabelHeight(communication,communicationValue);
			var paymentHeight = this.calculateLabelHeight(payment,paymentValue);
			var resourceHeight = this.calculateLabelHeight(resource,resourceValue);
			var commentHeight = this.calculateLabelHeight(comment,commentValue);
			
			// set the properties' and the chapters' heights according to content
			
			propHeight = properties.height;
		}
		
		//bounds AND _oldBounds need to be set, otherwise bounds are reset to _oldBounds, if node is moved
		shape._oldBounds.set(
			shape.bounds.a.x, 
			shape.bounds.a.y, 
			shape.bounds.b.x, 
			shape.bounds.a.y + 60 + propHeight);
		shape.bounds.set(
			shape.bounds.a.x, 
			shape.bounds.a.y, 
			shape.bounds.b.x, 
			shape.bounds.a.y + 60 + propHeight);
		//resize the image frames on the left according to shape's bounds
		image_frames.each(function(frame){
			frame.height = shape.bounds.height();
			frame.element.setAttribute("height",shape.bounds.height());
			
		});
	},
	
	importPicture: function(){},
 	
 	onSelectionChange: function(){},
 	
 	pictureInstantiation: function(event){
 		//create a process lane if the canvas is empty
		if(this.facade.getCanvas().children.length == 0){				
			this.facade.createShape({
				type: "http://b3mn.org/stencilset/picture#process",
				namespace: "http://b3mn.org/stencilset/picture#",
				position: {x: 0, y: 0}
			}).refresh();
		}
	}
 	
 });
