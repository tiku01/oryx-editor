/**
 * Copyright (c) 2010
 * Uwe Hartmann, Helen Kaltegaertner
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
 * Handles the displaying of connections in the viewers
 * @class Discovery
 * @constructor
 */

Discovery = function(gadget){
	this.gadget 	= gadget;
	this.currentViewer = null;
	this.currentShape = null;
	this.init();
}

Discovery.prototype = {
		
	init : function(){
	
		this.gadget.registerSelectionChanged("all");
		this.gadget.registerRPC("handleSelection", "", "", this.showConnections, this);		

		
	},

	/**
	 * deselect previously selected shape and remove all markers
	 * and show connections associated with the recently chosen one
	 * @param {{index[Node]}} reply [Node] are the selected Nodes, Index the Viewer index
	 */
	showConnections: function(reply){
		
		var selectedShapes = reply.selected;
		var viewer = reply.index;
		if (!viewer) {
			return;
		}
		this.currentViewer = viewer;
		// those events caused by reseting the selection of the previously selected shape must be filtered out
		
		for (var s in reply.selected) {
			this.currentShape = s;
		}
		if (!this.currentShape) {
			return;
		}
		
		
		// remove all markers (associations belonging to previously selected shape)
		// mark all shapes associated to the recently selected
		var showAssociations = function(viewers) {
			/*
			for (var i = 0; i < viewers.length; i++){
				this.gadget.removeMarker(viewers[i], "all");
			}
			*/
			this.gadget.clearViewers(viewers);
			
			var connections = this.gadget.connectionCollection.connections;
			var firstFound = true;
			/*
			for ( var i = 0; i < connections.length; i++){
				if (connections[i]) {
					connections[i].clearModels();
					connections[i].deselect();
				}
			}
			*/
			for ( var i = 0; i < connections.length; i++){
				if (connections[i] && connections[i].includesShape(this.currentViewer, this.currentShape)) {
					connections[i].markShapes(firstFound);
					connections[i].select();
					firstFound = false;
				}
			}
		};
		this.gadget.sendViewers( showAssociations, this);		
		
	},
	
	
	/**
	 * remove selection and markers associated shapes
	 */
	stopDiscoveryMode : function(){
		var setMultiSelect = function(viewers) {
			for (var i = 0; i < viewers.length; i++){
				var index = viewers[i];
				this.gadget.setSelectionMode(index, "multi");
			}
		};
		//this.gadget.unregisterSelectionChanged();
		this.gadget.sendViewers( setMultiSelect, this);
		
		this.gadget.resetModels();
		
	}
		
}

