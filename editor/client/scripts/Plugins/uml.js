/**
 * Copyright (c) 2010
 * Christian Ress <bart@oryx-uml.the-bart.org>
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
ORYX.Plugins.UML = 
/** @lends ORYX.Plugins.UML.prototype */
{
	/**
	 * Creates a new instance of the UML plugin and registers it on the
	 * layout events listed in the UML stencil set.
	 * 
	 * @constructor
	 * @param {Object} facade The facade of the editor
	 */
	construct: function(facade) {
		this.facade = facade;
		this.facade.registerOnEvent('layout.uml.class', this.handleLayoutClass.bind(this));
		this.facade.registerOnEvent('layout.uml.interface', this.handleLayoutInterface.bind(this));
		this.facade.registerOnEvent('layout.uml.association', this.handleLayoutAssociation.bind(this));
		this.facade.registerOnEvent('layout.uml.qualified_association', this.handleLayoutQualifiedAssociation.bind(this));

	},
	
	/**
	 * 
	 * @param {Object} event
	 */
	
	calculateLabelHeight : function (labelElement, labelValue) {
		var fontSize = labelElement.getFontSize();
		var newlineOccurences = 1;
		
		labelValue.scan('\n', function() { newlineOccurences += 1; });
		
		return newlineOccurences * fontSize + 0.75;
	},
	
	handleLayoutClass : function(event) {
		var shape = event.shape;
		
		if (shape.propertiesChanged["oryx-abstract"] == true) {
			var className = event.shape.getLabels().find(
					function(label) { return label.id == (event.shape.id + "className") }
				);
			
			if (shape.properties["oryx-abstract"] == true) {
				className.node.setAttribute("font-style", "italic");
			} else {
				className.node.setAttribute("font-style", "none");
			}
		}
		
		if (shape.propertiesChanged["oryx-attributes"] == true || shape.propertiesChanged["oryx-methods"]) {
			var attributesValue = event.shape.properties["oryx-attributes"];
			var methodsValue = event.shape.properties["oryx-methods"];
			var attributes = event.shape.getLabels().find(
					function(label) { return label.id == (event.shape.id + "attributes") }
				);
			var methods = event.shape.getLabels().find(
					function(label) { return label.id == (event.shape.id + "methods") }
				);
			var separator = event.shape._svgShapes.find(
					function(element) { return element.element.id == (event.shape.id + "separator") }
				).element;
					
					
			var attributesHeight = this.calculateLabelHeight(attributes, attributesValue);
			var methodsHeight = this.calculateLabelHeight(methods, methodsValue);
		
			var distanceTilSeparator = 24 + attributesHeight + 2;
			var distanceTilBottom = distanceTilSeparator + methodsHeight + 2;
			
			separator.setAttribute("y1", distanceTilSeparator);
			separator.setAttribute("y2", distanceTilSeparator);
			
			// realign methods label (so that oryx' internal references are correct)
			methods.y = distanceTilSeparator + 2;
			methods.node.setAttribute("y", distanceTilSeparator + 2);
			// realign the methods line by line (required for a visual change)
			for (var i = 0; i < methods.node.childElementCount; i++) {
				methods.node.childNodes[i].setAttribute("y", distanceTilSeparator + 2);
			}
			
			// resize shape
			shape.bounds.set(
				shape.bounds.a.x, 
				shape.bounds.a.y, 
				shape.bounds.b.x, 
				shape.bounds.a.y + distanceTilBottom + 5
			);
		}
	},
	
	handleLayoutInterface : function(event) {
		var shape = event.shape;
		
	//	if (shape.propertiesChanged["oryx-attributes"] == true) {
			var methodsValue = shape.properties["oryx-methods"];
			var methods = shape.getLabels().find(
					function(label) { return label.id == (event.shape.id + "methods") }
				);
			
			var methodsHeight = this.calculateLabelHeight(methods, methodsValue);
		
			var distanceTilBottom = 32 + methodsHeight + 2;
			
			// resize shape
			shape.bounds.set(
				shape.bounds.a.x, 
				shape.bounds.a.y, 
				shape.bounds.b.x, 
				shape.bounds.a.y + distanceTilBottom + 5
			);
	},
	
	handleLayoutAssociation : function(event) {
		var shape = event.shape;
		var name = shape.getLabels().find(
					function(label) { return label.id == (event.shape.id + "name") }
				);
		
		if (shape.properties["oryx-direction"] == "left") {
			name.text("◀ " + shape.properties["oryx-name"]);
		}
		else if (shape.properties["oryx-direction"] == "right") {
			name.text(shape.properties["oryx-name"] + " ▶");
		}
		else {
			name.text(shape.properties["oryx-name"]);
		}
	},
	
	handleLayoutQualifiedAssociation : function(event) {
		var shape = event.shape;
		var qualifier = shape.getLabels().find(
					function(label) { return label.id == (event.shape.id + "qualifier") }
				);
		
		var size = qualifier._estimateTextWidth(shape.properties["oryx-qualifier"], 12);
		if (size < 40) size = 40;
		shape._markers.values()[0].element.lastElementChild.setAttribute("width", size+5);
		shape._markers.values()[0].element.setAttributeNS(null, "markerWidth", size+5)
//		console.log(tspans);
	},
};

ORYX.Plugins.UML = Clazz.extend(ORYX.Plugins.UML);
