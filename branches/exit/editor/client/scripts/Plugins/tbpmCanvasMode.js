/**
 * Copyright (c) 2009
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
if (!ORYX.Plugins) 
    ORYX.Plugins = new Object();

/**
 * Enables exporting and importing current model in JSON.
 */
ORYX.Plugins.TBPMCanvasMode = ORYX.Plugins.AbstractPlugin.extend({

	facade: undefined,	
	canvasId: "ext-gen56",
	fill: "#ffffcc",
	edited: "RGBA(255,255,255,0.8)",
	shapeTransform: false,
	
    construct: function(){
        // Call super class constructor
        arguments.callee.$.construct.apply(this, arguments);
        /*
        if ( $(this.canvasId).getStyle("backgroundImage") ){
        	this.background = $(this.canvasId).getStyle("background");
        }
        else
        	this.background = null;
        
        this.background = $(this.canvasId).style.background;
        
        */
        this.facade.offer({
			name:			ORYX.I18N.TBPMCanvasMode.name,
			functionality: 	this.enableToggle.bind(this),
			group: 			ORYX.I18N.TBPMCanvasMode.group,
			//dropDownGroupIcon: ORYX.PATH + "images/tbpm.png",
			icon: 			ORYX.PATH + "images/shape_move_backwards.png",
			description: 	ORYX.I18N.TBPMCanvasMode.desc,
			index: 			1,
            toggle: 		true,
			minShape: 		0,
			maxShape: 		0});
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_TBPM_BACKGROUND_UPDATE, this.enableShapeTransform.bind(this));
    },
    
    enableShapeTransform: function(){
    	if (!this.shapeTransform){
    		this.shapeTransform = true;
    		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_PROPERTY_CHANGED, this.updateShapeSVG.bind(this));
		}
    },
    
    updateShapeSVG: function(event){
    	if (this.shapeTransform){
			var shape = event.elements[0];
			if (shape && event.name == "oryx-name" && !shape.isEdited ){
				shape._svgShapes.each(function(svgShape){
					if (svgShape.element.id && svgShape.element.id.endsWith("bg_frame")){
						svgShape.element.isEdited = true;	// for check before mode change
						shape.isEdited = true;		// for check after PROPERTY_CHANGED						
						svgShape.element.setAttributeNS(null,"fill",this.edited);
					}
				}.bind(this));
			}
			shape.refresh();
			this.facade.getCanvas().update();
		}
    },
    
    enableToggle: function(button, pressed) {

    	if (pressed && $(this.canvasId).getStyle("backgroundImage")){
    		
    		$(this.canvasId).style.background = "";
			$A($$('rect')).each( 
				function(rect) {
    				if (rect.id.endsWith("bg_frame")){
    					rect.setAttributeNS(null,"fill",this.fill);
    					rect.setAttributeNS(null,"stroke-width","1");
    				}
				}.bind(this)
    		)
			
    		this.shapeTransform = false;
    		
    	}
		else if (!pressed && this.facade.getCanvas().properties["oryx-photo"]){
			$(this.canvasId).style.background = 
				"url(" + this.facade.getCanvas().properties["oryx-photo"] + ") no-repeat scroll 0% 0%";;
			$A($$('rect')).each( 
				function(rect) {
					if (rect.id.endsWith("bg_frame")){
						if (rect.isEdited)
							rect.setAttributeNS(null,"fill",this.edited);
						else
							rect.setAttributeNS(null,"fill","None");
						rect.setAttributeNS(null,"stroke-width","4");
					}
				}.bind(this)
			);
			this.shapeTransform = true;				
		}
	}
   
});