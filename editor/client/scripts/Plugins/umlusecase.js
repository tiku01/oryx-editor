/**
 * Copyright (c) 2010
 * Ralf Diestelkaemper
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

/**
 * @namespace Oryx name space for plugins
 * @name ORYX.Plugins
*/
 if(!ORYX.Plugins)
	ORYX.Plugins = new Object();

/**
 * The UML plugin provides layout methods referring to the UML stencilset. 
 * 
 * @class ORYX.Plugins.UML
 * @extends Clazz
 * @param {Object} facade The facade of the editor
 */
ORYX.Plugins.UMLUseCase = 
/** @lends ORYX.Plugins.UML.prototype */
{
	/**
	 * Creates a new instance of the UML plugin and registers it on the
	 * layout events listed in the UML UseCase stencil set.
	 * 
	 * @constructor
	 * @param {Object} facade The facade of the editor
	 */
	construct: function(facade) {
		this.facade = facade;
		this.facade.registerOnEvent('layout.uml.system', this.handleLayoutSystem.bind(this));
		this.facade.registerOnEvent('layout.uml.useCaseExtended', this.handleUseCaseExtended.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_LOADED, this.addStereotypeOnLoad.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_PROPWINDOW_PROP_CHANGED, this.handlePropertyChanged.bind(this));
	},
	
	handleUseCaseExtended : function(event) {
		var shape= event.shape;

		var separator = shape._svgShapes.find(
					function(element) { return element.element.id == (event.shape.id + "separator") }
				).element;
		var extensionPointTextFrame = shape._svgShapes.find(
					function(element) { return element.element.id == (event.shape.id + "extension_point_text_frame") }
				).element;
		var extensionPointText = shape.getLabels().find(
					function(label) { return label.id == (shape.id + "extensions") }
				);
		var extensionText = shape.getLabels().find(
					function(label) { return label.id == (shape.id + "extensionpoint") }
				);
		//var extensionPointTextElement = shape._svgShapes.find(
		//			function(element) { return element.element.id == (event.shape.id + "extensions") }
		//		).element;
		
		window.setTimeout(function(){
			alert(""+extensionPointTextFrame.y.baseVal.value+"\n"
					+extensionPointTextFrame.y.baseVal.value+"\n"
					+separator.y1.baseVal.value+"\n"
					+separator.y1.baseVal.value+"\n"
					+extensionPointText.y);
			//extensionPointTextFrame.setAttribute("y", separator.y1.baseVal.value+18);
			extensionPointTextFrame.y.baseVal.value=separator.y1.baseVal.value+18;
			extensionPointText.y= extensionText.y+16;
			//extensionPointText.y= extensionText.y+
			//extensionPointText.setAttribute("y", seperator.y1.baseVal.value+18);
			alert(""+extensionPointTextFrame.y.baseVal.value+"\n"
					+extensionPointTextFrame.y.baseVal.value+"\n"
					+separator.y1.baseVal.value+"\n"
					+separator.y1.baseVal.value+"\n"
					+extensionPointText.y);
			shape.update();
			
		},200)
		
	},
	
	
	
	/**
	 * Anpassen
	 */
	addStereotypeOnLoad : function(event) {
		this.facade.getCanvas().nodes.each(function(shape){
		//shape.getStencil().id().toLowerCase()==
			if (shape._stencil.id().endsWith("umlUcSystem")) {
				this.addStereotype(shape);
			}
		}.bind(this));
	},
	
	handlePropertyChanged : function(event) {
		var shape = event.elements.first();
		alert(shape.id);
		alert(shape._stencil.id());
		if (shape._stencil.id().endsWith("umlUcSystem"))
			alert("treffer");
		if (event["key"] == "oryx-stereotype") {
			this.addStereotype(shape);
		}
	},
	/**
	 * Anpassen
	 */
	handleLayoutSystem : function(event) {
		var shape = event.shape;
		var stereotype = shape.getLabels().find(
					function(label) { return label.id == (event.shape.id + "stereotype") }
				);
		var title = shape.getLabels().find(
				function(label) { return label.id == (event.shape.id + "text") }
			);
		if (stereotype.text().empty()){
			title.y=stereotype.y;
			stereotype.hide();
		}else{
			title.y = stereotype.y+14;
			stereotype.show();
			this.addStereotype(event.shape);
		}
	},
	/**
	 * Anpassen
	 */
	addStereotype : function(shape) {
		var stereotype = shape.getLabels().find(
					function(label) { return label.id == (shape.id + "stereotype") }
				);
		stereotype.text("≪" + shape.properties["oryx-stereotype"] + "≫");
		stereotype.update();
	}
};

ORYX.Plugins.UMLUseCase = Clazz.extend(ORYX.Plugins.UMLUseCase);
