/**
 * Copyright (c) 2006
 * Helen Kaltegaertner
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

if(!ORYX.Plugins)
	ORYX.Plugins = new Object();

ORYX.Plugins.TBPMShapeConnector = Clazz.extend({

	/**
	 *	Constructor
	 *	@param {Object} Facade: The Facade of the Editor
	 */
	construct: function(facade) {
		this.facade = facade;
		this.active = false;
		this.sourceNode = null;

		this.facade.offer({
			name:			ORYX.I18N.TBPMShapeconnctor.name,
			functionality: 	this.enableConnector.bind(this),
			group: 			ORYX.I18N.TBPMShapeconnctor.group,
			//dropDownGroupIcon: ORYX.PATH + "images/tbpm.png",
			icon: 			ORYX.PATH + "images/pencil_go.png",
			description: 	ORYX.I18N.TBPMShapeconnctor.desc,
			index: 			1,
            toggle: 		true,
			minShape: 		0,
			maxShape: 		0});
		
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEDOWN, this.handleMouseDown.bind(this));
	},
	
	enableConnector: function(button, pressed) {
		this.connectButton = button;
		if (!pressed){
        	this.active = false;
        	this.sourceNode = null;
        }
		else {
			this.active = true;
			this.facade.raiseEvent({
				type:ORYX.CONFIG.EVENT_LOADING_STATUS,
				text:ORYX.I18N.TBPMShapeconnctor.usage
			});
		}
	},	
	/**
	 * MouseDown Handler
	 *
	 */	
	handleMouseDown: function(event, uiObj) {
		if (this.active && uiObj instanceof ORYX.Core.Node) {
            if (this.sourceNode){	
	            if (! this.createEdge( this.sourceNode, uiObj))
	            	return;
	            this.facade.raiseEvent({
                    type: ORYX.CONFIG.EVENT_LOADING_DISABLE
                });
            }
        	this.sourceNode = uiObj
		}
		else if (this.active) {
			if (this.connectButton){
				this.connectButton.toggle();
			}
		}
	},
    
	createEdge: function(source, target){

		// Create a new Stencil		
		var ssn 	= this.facade.getStencilSets().keys()[0];						
		var stencil = ORYX.Core.StencilSet.stencil(ssn + "SequenceFlow");
		var isValid = this.facade.getRules().canConnect({
			sourceShape: source,
			edgeStencil: stencil,
			targetShape: target
		});	
		//if(!isValid)
		//	return null;
		
		var command = new ORYX.Plugins.TBPMShapeConnector.CreateEdge(source, target, stencil, this.facade);
        
		this.facade.executeCommands([command]);		
		return command.edge;
					
	},
});
ORYX.Plugins.TBPMShapeConnector.CreateEdge = ORYX.Core.Command.extend({
    construct: function(source, target, stencil, facade){
        this.source          	= source;
        this.target       		= target;
        this.stencil      		= stencil;
        this.facade             = facade;
                
    },          
    execute: function(){
		console.log("execute");
    	var edge = new ORYX.Core.Edge({'eventHandlerCallback':this.facade.raiseEvent}, this.stencil);
		edge.dockers.first().setDockedShape( this.source );
		edge.dockers.first().setReferencePoint({x: this.source.bounds.width() / 2.0, y: this.source.bounds.height() / 2.0});
		//shape.dockers.first().update()

		edge.dockers.last().setDockedShape( this.target );
		edge.dockers.last().setReferencePoint({x: this.target.bounds.width() / 2.0, y: this.target.bounds.height() / 2.0});
		
		// Add the shape to the canvas
		this.facade.getCanvas().add(edge);
        this.facade.getCanvas().update();		
        this.edge = edge;
    },
    rollback: function(){
        this.facade.deleteShape(this.edge);
        this.facade.getCanvas().update();	
		this.facade.updateSelection();
        
    }
});