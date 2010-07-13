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
	},
	
	
	/**
	 * Resizes the qualifier box of a qualified association according to its content.
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
		}
	},
};

ORYX.Plugins.UMLUseCase = Clazz.extend(ORYX.Plugins.UMLUseCase);
