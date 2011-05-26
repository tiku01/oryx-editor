/**
 * Copyright (c) 2010
 * Ralf Diestelkaemper
 *
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
 * The UMLUseCase plugin provides layout methods referring to the UMLUseCase stencilset. 
 * 
 * @class ORYX.Plugins.UMLUseCase
 * @extends Clazz
 * @param {Object} facade The facade of the editor
 */
ORYX.Plugins.UMLUseCase = 
/** @lends ORYX.Plugins.UMLUseCase.prototype */
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
		this.facade.registerOnEvent('layout.uml.useCaseExtended', this.handleUseCaseExtendedLayout.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_LOADED, this.handleDiagramOnLoad.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_PROPWINDOW_PROP_CHANGED, this.handlePropertyChanged.bind(this));
	},
	
	/**
	 * Handles the layouting after loading a diagram
	 * This Function starts a recursion 
	 * which layouts the stereotypes of all systems and
	 * the UseCaseExtended.
	 *
	 * @param {Object} event The event, that is fired after a Use Case diagram is loaded
	 */
	handleDiagramOnLoad : function(event) {
		this.layoutAllSystems(this.facade.getCanvas());
	},
	
	/**
	* Recursivly layouts the system, 
	* all child systems and the UseCasesExtended the system includes
	*
	* @param {ORYX.Core.Node} system A system shape
	*/
	layoutAllSystems : function(system){
		
		this.layoutAllUseCaseExtended(system);
		
		var systems = system.getChildNodes().findAll( function(child) {
			if (this.isSystemNode(child)) {
				return child;
			}
		}.bind(this));
		
		systems.each(function(systemNode){ 
			this.layoutStereotype(systemNode);
			this.layoutAllSystems(systemNode); //recursion
		}.bind(this));
	
	},
	
	/**
	* Layouts all UseCasesExtended contained in the system
	*
	* @param {ORYX.Core.Node} system A system shape
	*/
	layoutAllUseCaseExtended : function(system){
		
		var useCasesExtended = system.getChildNodes().findAll(function(child) {
			if (this.isUseCaseExtendedNode(child)) {
				return child;
			}
		}.bind(this));
		
		useCasesExtended.each(function(useCaseExtendedNode){ 
			this.layoutUseCaseExtended(useCaseExtendedNode);
		}.bind(this));
	
	},
	
	/**
	* This method is called when a UseCaseExtended shape is modified.
	* It registers another handling method to the selection changed event, 
	* since that event is fired late enough to do the needed layouting.
	*
	* The chain of called methods, to do the layouting:
	*		handleUseCaseExtendedLayout(...) | layouting event fired
	*	->	handleUseCaseExtendedSelection(...) | selection event fired
	*	->	layoutUseCaseExtended(...) | simple method call
	*
	* @param {Object} event The event which is fired after the UseCaseExtended shape is modified
	*/
	handleUseCaseExtendedLayout : function(event) {
		
		var shape = event.shape;
		if (shape.isResized){
			this.SelectionEventFunction = this.handleUseCaseExtendedSelection.bind(this);
			this.facade.registerOnEvent(ORYX.CONFIG.EVENT_SELECTION_CHANGED, this.SelectionEventFunction);
		}		
	},
	
	/**
	* This method is called after a UseCaseExtended shape is modified.
	* It deregisters itself on the selection changed event, 
	* since this event is fired late enough to do the needed layouting.
	*
	* First the method checks whether the event has a reference to a UseCaseExtended shape, 
	* since there is no guarantee.
	* 
	* @param {Object} event The event which is fired after the UseCaseExtended is resized
	*/
	handleUseCaseExtendedSelection : function(event) {
		
		var shape = event.elements.first();
		if (!this.isUseCaseExtendedNode(shape)) {return;}
		
		this.facade.unregisterOnEvent(ORYX.CONFIG.EVENT_SELECTION_CHANGED, this.SelectionEventFunction);
		this.layoutUseCaseExtended(shape);
	},
	
	/**
	* This method layouts the UseCaseExtended shape
	* It places the textframe and text which describe the extensions directly underneath 
	* the text "Extension Points"
	* Neither the resize attribute nor the anchors attribute had the desired effect.
	* 
	* @param {ORYX.Core.Node} useCaseExtended A UseCaseExtended shape
	*/
	layoutUseCaseExtended : function(useCaseExtended){
	
		var extensionPointTextFrame = useCaseExtended._svgShapes.find(
				function(element) { return element.element.id == (useCaseExtended.id + "extension_point_text_frame"); 
			}).element;
		var extensionPointText = useCaseExtended.getLabels().find(
				function(label) { return label.id == (useCaseExtended.id + "extensions"); 
			});
		var extensionText = useCaseExtended.getLabels().find(
				function(label) { return label.id == (useCaseExtended.id + "extensionpoint"); 
			});
		var verticalDifference = extensionPointTextFrame.y.baseVal.value - extensionText.y-16;
		extensionPointTextFrame.y.baseVal.value= extensionText.y+16;
		extensionPointText.y= extensionText.y+16;
		extensionPointTextFrame.height.baseVal.value += verticalDifference;	
	
	},	

	
	/**
	* Initiates the Stereotype layout
	* This method is called whenever a property of a shape is changed. 
	* It filters the events, which belong to a System shape and are fired,
	* when the stereotype or name is changed.
	*
	*@param {Object} event The event that is fired after changing a property of a shape.
	*/
	handlePropertyChanged : function(event) {
		var shape = event.elements.first();
		if (!this.isSystemNode(shape)) {return;}
		if (!(this.isStereotypeKeyEvent(event) || this.isNameKeyEvent(event))) {return;}
		this.layoutStereotype(shape);			
	},
	
	/**
	* Layouts the stereotype and name properties of a System shape
	* It places the stereotype and property of a System properly and hides the stereotype when necessary
	*
	*@param {ORYX.Core.Node} system The System shape the user modified the stereotype/name from.
	*/
	layoutStereotype: function(system) {
	
		var stereotype = system.getLabels().find(
				function(label) { return label.id == (system.id + "stereotype"); 
			});
		var name = system.getLabels().find(
				function(label) { return label.id == (system.id + "text"); 
			});
		
		if (stereotype.text().empty()){
					name.y=stereotype.y;
					stereotype.hide();
				}else{
					name.y = stereotype.y+14;
					stereotype.show();
					stereotype.text("≪" + system.properties["oryx-stereotype"] + "≫");
				}
		name.update();
		stereotype.update();
	},
	
	/**
	* Helper method, which returns true, if the received shape is an extended Use Case. 
	*@private
	*@param {Object} shape The shape that is checked for beeing an extended Use Case.
	*@return {boolean} The result is true, if the shape is an extended Use Case.
	*/
	isUseCaseExtendedNode : function(shape) {
		return "http://b3mn.org/stencilset/umlusecase#usecaseextended" == shape.getStencil().id().toLowerCase();
	},
	
	/**
	* Helper method, which returns true, if the received shape is an  Use Case. 
	*@private
	*@param {Object} shape The shape that is checked for beeing an  Use Case.
	*@return {boolean} The result is true, if the shape is an  Use Case.
	*/
	isUseCaseNode : function(shape) {
		return "http://b3mn.org/stencilset/umlusecase#usecase" == shape.getStencil().id().toLowerCase();
	},

	
	/**
	* Helper method, which returns true, if the received shape is a system. 
	*@private
	*@param {Object} shape The shape that is checked for beeing a system.
	*@return {boolean} The result is true, if the shape is a system
	*/
	isSystemNode : function(shape) {
		return "http://b3mn.org/stencilset/umlusecase#system" == shape.getStencil().id().toLowerCase();
	},
	
	/**
	* Helper method, which returns true, if the received shape is an include edge. 
	*@private
	*@param {Object} shape The shape that is checked for beeing an include edge.
	*@return {boolean} The result is true, if the shape is an include edge
	*/
	isIncludeEdge : function(shape) {
		return "http://b3mn.org/stencilset/umlusecase#include" == shape.getStencil().id().toLowerCase();
	},
	
	/**
	* Helper method, which returns true, if the received shape is an extend edge. 
	*@private
	*@param {Object} shape The shape that is checked for beeing an extend edge.
	*@return {boolean} The result is true, if the shape is an extend edge
	*/
	isExtendEdge : function(shape) {
		return "http://b3mn.org/stencilset/umlusecase#extend" == shape.getStencil().id().toLowerCase();
	},
	
	/**
	* Helper method, which returns true, if the received event handles a stereotype.
	*@private 
	*@param {Object} event The event that is checked for handling an oryx-stereotype.
	*@return {boolean} The result is true, if event handles a stereotype
	*/
	isStereotypeKeyEvent : function(event) {
		return event["key"] == "oryx-stereotype";
	},
	
	/**
	* Helper method, which returns true, if the received event handles a name.
	*@private 
	*@param {Object} event The event that is checked for handling an oryx-name.
	*@return {boolean} The result is true, if event handles a name
	*/
	isNameKeyEvent : function(event) {
		return event["key"] == "oryx-name";
	}
};

ORYX.Plugins.UMLUseCase = Clazz.extend(ORYX.Plugins.UMLUseCase);

