// most badass copy&paste action evveeeeerrrr! I am really sorry for it but right now I see no other way...
/**
 * Copyright (c) 2008
 * Tobias Pfeiffer
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
if (!ORYX.Plugins) 
    ORYX.Plugins = new Object();


ORYX.Plugins.UMLState = Clazz.extend({

    facade: undefined,
    
    construct: function(facade){
    
        this.facade = facade;
      	
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_DBLCLICK, this.actOnDBLClick.bind(this));
        this.facade.offer({
		 keyCodes: [{
				keyCode: 113, // F2-Key
				keyAction: ORYX.CONFIG.KEY_ACTION_DOWN 
			}
		 ],
         functionality: this.renamePerF2.bind(this)
         });
		
		
		document.documentElement.addEventListener(ORYX.CONFIG.EVENT_MOUSEDOWN, this.hide.bind(this), true );
    },
    
    /**
     * starting here come my very own functions for the templating
     * 
     */
    /**
     * templatize value expects the oldValue to be in format of the template... if not hell may break loose.
     * I'm serious. 
     * 
     * We need the propId parameter in order to differentiate between the 2 textfiels of the state with actions
     */
    templatizeValue : function templatizeValue(propId, shape){
    	var stencilID = shape._stencil.id();
    	var oldValue = shape.properties[propId];
    	// It is the edge (controlflow)
    	if (stencilID == "http://b3mn.org/stencilset/umlstate#controlFlow") {
    		return this.templatizeEdgeValue(oldValue);
    	}
    	// Make sure it's the state with actions and it is the textfield actions, not the the textfiel name
    	else if ((stencilID == "http://b3mn.org/stencilset/umlstate#stateWithActions") && (propId.indexOf("actions") != -1)) {
    		return this.templatizeStateWithActionsValue(oldValue);	
    	}
    	// Otherweise nothing special to do
    	else{	
    		return oldValue;
    	}
    },
    
    templatizeEdgeValue : function templatizeEdgeValue(oldValue){
    	// the matching is done by a couple of ifs rather than a regex since I don't know
    	// how to match on just "Event" and not "Event [Guard]"
    	// optimization: just query the strings once
    	var indexOfBracket = oldValue.indexOf("[");
    	var indexOfSlash = oldValue.indexOf("/");
    	// Case of: blank field ""
    	if (oldValue == "") {
    		return "Event [Guard] /Action";
    	}
    	// Case of: Event
    	if ((indexOfBracket == -1) && (indexOfSlash == -1)) {
    		return oldValue+" [Guard] /Action";
    	}
    	// Case of: [Guard]
    	if ((indexOfBracket == 0) && (indexOfSlash == -1)) {
    		return "Event "+oldValue+" /Action";
    	}
    	// Case of: /Action
    	if (indexOfSlash == 0) {
    		return "Event [Guard] "+oldValue;
    	}
    	// Case of: Event [Guard] 
    	if ((indexOfBracket != -1) && (indexOfSlash == -1)) {
    		return oldValue + " /Action";
    	}
    	// Case of: Event /Action
    	if ((indexOfBracket == -1) && (indexOfSlash > 0)) {
    		var splitter = oldValue.split("/");
    		return splitter[0] + "[Guard] /"+splitter[1];
    	}
    	// Case of:[Guard] /Action
    	if ((indexOfBracket == 0) && (indexOfSlash  != -1)) {
    		return "Event " + oldValue;
    	}
    	
    	// Case of: Event [Guard] /Action
    	// TODO: it may start with whitespaces? (may coutn for other too)
    	if ((indexOfBracket > 0) && (indexOfSlash  > 0)) {
    		return oldValue;
    	}
    	
    	// We didn't return so far: WTF happened?
    	return "Roflcoptaaaaaa";
    },
    /**
     * This thing templatizes the actions of the state with actions in the scheme of:
     * entry / action
     * do / action
     * exit / action
     */
    templatizeStateWithActionsValue : function templatizeStateWithActionsValue(oldValue){
    	// performance optimization, save the values of the indexes
    	var indexOfEntry = oldValue.indexOf("entry /");
    	var indexOfDo = oldValue.indexOf("do /");
    	var indexOfExit = oldValue.indexOf("exit /");
    	
    	// standardize the ending of the oldValue, with \n that is.
    	if (oldValue.charAt(oldValue.length -1) != "\n"){
    		oldValue = oldValue + "\n"
    	}
    	
    	// \n is used throughout cause this seems to be what my firefox is using.. though I'm working on Windows
    	// you see, here we match against \n because we add it above.
    	if (oldValue == "\n"){
    		return "entry / action\ndo / action\nexit / action";
    	}
    	// entry / action
    	if ((indexOfEntry == 0) && (indexOfDo == -1) && (indexOfExit == -1)){
    		return oldValue + "do / action\nexit / action";
    	}
    	// do / action
    	if ((indexOfEntry == -1) && (indexOfDo == 0) && (indexOfExit == -1)){
    		return "entry / action\n"+ oldValue + "exit / action";
    	}
    	// exit / action
    	if ((indexOfEntry == -1) && (indexOfDo == -1) && (indexOfExit == 0)){
    		return "entry / action\ndo / action\n"+ oldValue;
    	}
    	// entry / action
    	// do / action
    	if ((indexOfEntry == 0) && (indexOfDo != -1) && (indexOfExit == -1)){
    		return oldValue +"exit / action";
    	}
    	// entry / action
    	// exit / action
    	if ((indexOfEntry == 0) && (indexOfDo == -1) && (indexOfExit != -1)){
    		return oldValue.slice(0, indexOfExit) + "do / action\n" + oldValue.slice(indexOfExit);
    	}
    	// do / action
    	// exit / action
    	if ((indexOfEntry == -1) && (indexOfDo == 0) && (indexOfExit != -1)){
    		return "entry / action\n" + oldValue;
    	}
    	// the whole bunch
    	// entry / action
    	// do / action
    	// exit / action
    	if ((indexOfEntry == 0) && (indexOfDo != -1) && (indexOfExit != -1)){
    		return oldValue;
    	}
    	
    	// If we got this far something went wrong
    	return "aye what is this?\nYou seem a little bit off";
    },
    
    untemplatizeValue : function untemplatizeValue(newValue, propId, shape){
    	var stencilID = shape._stencil.id();
    	// It is the edge (controlflow)
    	if (stencilID == "http://b3mn.org/stencilset/umlstate#controlFlow") {
    		return this.untemplatizeEdgeValue(newValue);
    	}
    	// Make sure it's the state with actions and it is the textfield actions, not the the textfiel name
    	else if ((stencilID == "http://b3mn.org/stencilset/umlstate#stateWithActions") && (propId.indexOf("actions") != -1)) {
    		return this.untemplatizeStateWithActionsValue(newValue);	
    	}
    	// Otherweise nothing special to do (as we don't want to affect every stencil)
    	else{	
    		return newValue;
    	}
    },
    
    /**
     * The intent of this function is that if the user does something like this:
     * 
     * Event [x >= 5] /Action
     * 
     * what gets saved and displayed is
     * 
     * [x >= 5]
     * 
     * Therefore we'll try to delete any occurences of Event, [Guard] and /Action
     */
    untemplatizeEdgeValue: function untemplatizeEdgeValue(value){
    	// Quiet a chain, I love message chaining.
    	alert(value);
    	var newValue = value.replace("Event ", "").replace("[Guard]", "").replace(" /Action", "");
    	alert(newValue);
    	return newValue;
    },
    
    untemplatizeStateWithActionsValue: function untemplatizeStateWithActionsValue(value){
    	var newValue = value.replace("entry / action\n", "").replace("do / action\n", "").replace("exit / action", "");
    	return newValue;
    },
	
	/**
	 * This method handles the "F2" key down event. The selected shape are looked
	 * up and the editing of title/name of it gets started.
	 */
	renamePerF2 : function renamePerF2() {
		var selectedShapes = this.facade.getSelection();
		this.actOnDBLClick(undefined, selectedShapes.first());
	},
	
	getEditableProperties: function getEditableProperties(shape) {
	    // Get all properties which where at least one ref to view is set
		var props = shape.getStencil().properties().findAll(function(item){ 
			return (item.refToView() 
					&&  item.refToView().length > 0
					&&	item.directlyEditable()); 
		});
		
		// from these, get all properties where write access are and the type is String
	    return props.findAll(function(item){ return !item.readonly() &&  item.type() == ORYX.CONFIG.TYPE_STRING });
	},
	
	getPropertyForLabel: function getPropertyForLabel(properties, shape, label) {
	    return properties.find(function(item){ return item.refToView().any(function(toView){ return label.id == shape.id + toView })});
	},
	
	actOnDBLClick: function actOnDBLClick(evt, shape){
		if( !(shape instanceof ORYX.Core.Shape) ){ return }
		
		// Destroys the old input, if there is one
		this.destroy();
		var props = this.getEditableProperties(shape);
		
		// Get all ref ids
		var allRefToViews	= props.collect(function(prop){ return prop.refToView() }).flatten().compact();
		// Get all labels from the shape with the ref ids
		var labels			= shape.getLabels().findAll(function(label){ return allRefToViews.any(function(toView){ return label.id.endsWith(toView) }); })
		
		// If there are no referenced labels --> return
		if( labels.length == 0 ){ return } 
		
		// Define the nearest label
		var nearestLabel 	= labels.length == 1 ? labels[0] : null;	
		if( !nearestLabel ){
		    nearestLabel = labels.find(function(label){ return label.node == evt.target || label.node == evt.target.parentNode })
	        if( !nearestLabel ){
		        var evtCoord 	= this.facade.eventCoordinates(evt);

		        var trans		= this.facade.getCanvas().rootNode.lastChild.getScreenCTM();
		        evtCoord.x		*= trans.a;
		        evtCoord.y		*= trans.d;
			    if (!shape instanceof ORYX.Core.Node) {

			        var diff = labels.collect(function(label){

						        var center 	= this.getCenterPosition( label.node ); 
						        var len 	= Math.sqrt( Math.pow(center.x - evtCoord.x, 2) + Math.pow(center.y - evtCoord.y, 2));
						        return {diff: len, label: label} 
					        }.bind(this));
			
			        diff.sort(function(a, b){ return a.diff > b.diff })	
			
			        nearestLabel = 	diff[0].label;
                } else {

			        var diff = labels.collect(function(label){

						        var center 	= this.getDifferenceCenterForNode( label.node ); 
						        var len 	= Math.sqrt( Math.pow(center.x - evtCoord.x, 2) + Math.pow(center.y - evtCoord.y, 2));
						        return {diff: len, label: label} 
					        }.bind(this));
			
			        diff.sort(function(a, b){ return a.diff > b.diff })	
			
			        nearestLabel = 	diff[0].label;
                }
            }
		}

		// Get the particular property for the label
		var prop = this.getPropertyForLabel(props, shape, nearestLabel);

        this.showTextField(shape, prop, nearestLabel);
	},
	
	showTextField: function showTextField(shape, prop, label) {
		// Set all particular config values
		var htmlCont 	= this.facade.getCanvas().getHTMLContainer().id;
	    
	    // Get the center position from the nearest label
		var width;
		if(!(shape instanceof ORYX.Core.Node)) {
		    var bounds = label.node.getBoundingClientRect();
			width = Math.max(150, bounds.width);
		} else {
			width = shape.bounds.width();
		}
		if (!shape instanceof ORYX.Core.Node) {
		    var center 		= this.getCenterPosition( label.node );
		    center.x		-= (width/2);
        } else {
            var center = shape.absoluteBounds().center();
		    center.x		-= (width/2);
        }
		var propId		= prop.prefix() + "-" + prop.id();
		
		// Set the config values for the TextField/Area
		var config 		= 	{
								renderTo	: htmlCont,
								value		: this.templatizeValue(propId, shape), // Eingriffspunkt nummer 1 shape.properties[propId]
								x			: (center.x < 10) ? 10 : center.x,
								y			: center.y,
								width		: Math.max(100, width),
								style		: 'position:absolute', 
								allowBlank	: prop.optional(), 
								maxLength	: prop.length(),
								emptyText	: prop.title(),
								cls			: 'x_form_text_set_absolute',
                                listeners   : {specialkey: this._specialKeyPressed.bind(this)}
							};
		
		// Depending on the property, generate 
		// ether an TextArea or TextField
		if(prop.wrapLines()) {
			config.y 		-= 30;
			config['grow']	= true;
			this.shownTextField = new Ext.form.TextArea(config);
		} else {
			config.y -= 16;
			
			this.shownTextField = new Ext.form.TextField(config);
		}
		
		//focus
		this.shownTextField.focus();
		
		// Define event handler
		//	Blur 	-> Destroy
		//	Change 	-> Set new values					
		this.shownTextField.on( 'blur', 	this.destroy.bind(this) )
		this.shownTextField.on( 'change', 	function(node, value){
			var currentEl 	= shape;
			var oldValue	= currentEl.properties[propId]; 
			var newValue	= this.untemplatizeValue(value, propId, shape); // Eingriffspunkt nummer 2 value
			var facade		= this.facade;
			
			if (oldValue != newValue) {
				// Implement the specific command for property change
				var commandClass = ORYX.Core.Command.extend({
					construct: function(){
						this.el = currentEl;
						this.propId = propId;
						this.oldValue = oldValue;
						this.newValue = newValue;
						this.facade = facade;
					},
					execute: function(){
						this.el.setProperty(this.propId, this.newValue);
						//this.el.update();
						this.facade.setSelection([this.el]);
						this.facade.getCanvas().update();
						this.facade.updateSelection();
					},
					rollback: function(){
						this.el.setProperty(this.propId, this.oldValue);
						//this.el.update();
						this.facade.setSelection([this.el]);
						this.facade.getCanvas().update();
						this.facade.updateSelection();
					}
				})
				// Instanciated the class
				var command = new commandClass();
				
				// Execute the command
				this.facade.executeCommands([command]);
			}
		}.bind(this) )

		// Diable the keydown in the editor (that when hitting the delete button, the shapes not get deleted)
		this.facade.disableEvent(ORYX.CONFIG.EVENT_KEYDOWN);
	},
    
    _specialKeyPressed: function _specialKeyPressed(field, e) {
        // Enter or Ctrl+Enter pressed
        var keyCode = e.getKey();
        if (keyCode == 13  && (e.shiftKey || !field.initialConfig.grow)) {
            field.fireEvent("change", null, field.getValue());
            field.fireEvent("blur");
        } else if (keyCode == e.ESC) {
            field.fireEvent("blur");
        }
    },
	
	getCenterPosition: function(svgNode){
		
		var center 		= {x: 0, y:0 };
		// transformation to the coordinate origin of the canvas
		var trans 		= svgNode.getTransformToElement(this.facade.getCanvas().rootNode.lastChild);
		var scale 		= this.facade.getCanvas().rootNode.lastChild.getScreenCTM();
		var transLocal 	= svgNode.getTransformToElement(svgNode.parentNode);
		var bounds = undefined;
		
		center.x 	= trans.e - transLocal.e;
		center.y 	= trans.f - transLocal.f;
		
		
		try {
			bounds = svgNode.getBBox();
		} catch (e) {}

		// Firefox often fails to calculate the correct bounding box
		// in this case we fall back to the upper left corner of the shape
		if (bounds === null || typeof bounds === "undefined" || bounds.width == 0 || bounds.height == 0) {
			bounds = {
				x: Number(svgNode.getAttribute('x')),
				y: Number(svgNode.getAttribute('y')),
				width: 0,
				height: 0
			};
		}
		
		center.x += bounds.x;
		center.y += bounds.y;
		
		center.x += bounds.width/2;
		center.y += bounds.height/2;
		
		center.x *= scale.a;
		center.y *= scale.d;		
		return center;
		
	},

	getDifferenceCenterForNode: function getDifferenceCenterForNode(svgNode){
        //for shapes that do not have multiple lables on the x-line, only the vertical difference matters
        var center  = this.getCenterPosition(svgNode);
        center.x = 0;
        center.y = center.y + 10;
        return center;
    },
	
	hide: function(e){
		if (this.shownTextField && (!e || !this.shownTextField.el || e.target !== this.shownTextField.el.dom)) {
			this.shownTextField.onBlur();
		}
	},
	
	destroy: function(e){
		if( this.shownTextField ){
			this.shownTextField.destroy(); 
			delete this.shownTextField; 
			
			this.facade.enableEvent(ORYX.CONFIG.EVENT_KEYDOWN);
		}
	}
});