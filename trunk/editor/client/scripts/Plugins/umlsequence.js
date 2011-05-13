/**
 * Copyright (c) 2011 Tobias Pfeiffer
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
if (!ORYX.Plugins) {
	ORYX.Plugins = new Object();
}
/**
 * Plugin for the UML sequence diagrams. It realizes the following things: -
 * send every combined fragment to the back so they don't interfere with the
 * interactions of the user.
 */
ORYX.Plugins.UMLSequence = Clazz.extend({

	facade : undefined,

	/**
	 * Constructor registering all the events important for us
	 * 
	 * @param facade the facade
	 */
	construct : function(facade) {

		this.facade = facade;
		this.facade.registerOnEvent('layout.uml.sequence.combinedFragment',
				this.sendToBack.bind(this));
		this.facade.registerOnEvent('layout.uml.sequence.onLifeline',
				this.centerOnLifeline.bind(this));
		this.facade.registerOnEvent('layout.uml.sequence.terminator',
				this.centerTerminatorOnLifeline.bind(this));
		

	},

	/**
	 * Sends the element on which this function is called to the back, so it
	 * doesn't block the interaction with other objects-
	 * 
	 * This is called whenever a combined Fragment is moved, as they are
	 * supposed to be somewhat behind the interaction as you can't draw arrows
	 * while they are in front.
	 * 
	 * If you want to know what combined Fragments are, please refer to the
	 * umlsequence.json in the stencilsets folder.
	 * 
	 * @param event the event of the combined fragment which was modified
	 */
	sendToBack : function sendToBack(event) {
		var shape = event.shape;
		// basically taken from arrangement.js of the setToBack function
		shape.node.parentNode.insertBefore(shape.node,
				shape.node.parentNode.firstChild);
	},

	/**
	 * This function is used for umlsequence active line elements. They work
	 * just through some more or less rough hacks, a lifeline "contains" active
	 * lines. This makes it basically work, but one can make it look bad by
	 * dropping the activeline not really in the center of a lifeline. This
	 * function is used to standardize the layout.
	 * 
	 * @param event the event of the active line which was modified
	 */
	centerOnLifeline : function centerOnLifeline(event) {
		var shape = event.shape;
		// The parent element is always a lifeline so we don't have to check for this (see the JSON)
		var parent = shape.getParentShape();

		// compute the difference of the x-coordinates of the center of the lifeline 
		// and the center of the activeline
		var difference = parent.absoluteCenterXY().x - shape.absoluteCenterXY().x;
		// move the active line by the calculated difference on the x-axis 
		shape.bounds.moveBy(difference, 0);
	},
	centerTerminatorOnLifeline: function centerTerminatorOnLifeline(event) {
		var shape = event.shape;
		// The parent element is always a lifeline so we don't have to check for this (see the JSON)
		var parent = shape.getParentShape();
		// compute the difference of the x-coordinates of the center of the lifeline 
		// and the center of the activeline
		var differenceX = parent.absoluteCenterXY().x - shape.absoluteCenterXY().x;
		var differenceY = parent.bounds.lowerRight().y - shape.absoluteCenterXY().y;

		// move the active line by the calculated difference on the x-axis 
		shape.bounds.moveBy(differenceX, differenceY);
	}

});