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
	 */
	construct : function(facade) {

		this.facade = facade;
		this.facade.registerOnEvent('layout.uml.sequence.combinedFragment',
				this.sendToBack.bind(this));

	},

	/**
	 * Sends the element on which this function is called to the back, so it
	 * doesn't block the interaction with other objects-
	 * 
	 * This is called whenever a combined Fragment is moved, as they are
	 * supposed to be somehwat behind the interaction as you can't draw arrows
	 * while they are in front.
	 * 
	 * If you want to know what combined Fragments are, please refer to the
	 * umlsequence.json in the stencilsets folder.
	 */
	sendToBack : function sendToBack(event) {
		var shape = event.shape;
		// basically taken from arrangement.js of the stToBack function
		shape.node.parentNode.insertBefore(shape.node,
				shape.node.parentNode.firstChild);
	}

});