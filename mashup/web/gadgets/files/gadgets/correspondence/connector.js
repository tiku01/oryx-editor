/**
 * Copyright (c) 2010
 * Uwe Hartmann, Helen Kaltegaertner, Ole Eckermann
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
 * This class takes care of the selection process e.g. when adding a new connection 
 * @class Connector
 * @constructor
 * @param {Correspondence} gadget The associated gadget.
*/
Connector = function(gadget){
	this.gadget 	= gadget;
	this.selections = [];
	this.init();
}

Connector.prototype = {
		
	init : function(){
	
		var prepareModels = function(viewers){
			
			for (var i = 0; i < viewers.length; i++){
				this.gadget.resetSelection(viewers[i]);
				this.gadget.removeMarker(viewers[i], "all");
				this.gadget.doGrey(viewers[i], "all");
			}
		};
	
		this.gadget.sendViewers(prepareModels, this);
		this.gadget.registerSelectionChanged("all");
		this.gadget.registerRPC("handleSelection", "", "", this.updateSelection, this);
	},

	/**
	 * save currently selected shapes of the viewer that caused the event
	 * @param {Interger {Selection}} reply Index and Selection of Nodes
	 */
	updateSelection: function(reply){
		
		var index = reply.index;		
		var nodes = reply.selected;
		var nodeArray = [];
		
		var resourceIds = [];
		for (var key in nodes) {
			resourceIds.push(key);
			nodeArray.push(nodes[key]);
		}
		
		// update shadows
		this.gadget.doGrey(index, "all");
		this.gadget.undoGrey(index, resourceIds);
		
		if (!this.selections[index]){
			this.gadget.sendInfo(index, this.addSelection, this, {index: index, nodes: nodeArray} )	
		} else {
			this.selections[index].shapes = nodeArray;	
		}	
	},
	
	/**
	 * add a new viewer to the collection of viewers with selected shapes
	 * save the selected shapes and title and url of the model
	 * 
	 */
	addSelection : function(info, args){
		
		this.selections[args.index] = {
				shapes : 	args.nodes,
				model : 	info.model,
				url	: 		info.url
				//description: info.description
		};
	},
	
	/**
	 * create a new connection, mark all selected shapes and remove shadows 
	 */
	stopSelectionMode : function(){
		
		this.gadget.unregisterSelectionChanged();
		
		// remove shadows
		var removeShadows = function(viewers){
			for (var i = 0; i < viewers.length; i++){
				this.gadget.undoGrey(viewers[i], "all");
			}
		};
		this.gadget.sendViewers(removeShadows, this);
		
		var exitSelectionMode = function(viewers){
			
			// create new connection object
			var connection = new Connection( this.gadget, "no title" );
			var validViewers = 0;
			for (var i = 0; i < viewers.length; i++){
				// check if the viewer is valid, i.e. it contains selected shapes
				if (this.selections[ viewers[i] ].shapes.length == 0) {
					continue;
				}
				validViewers = validViewers + 1;
				connection.addModel(viewers[i], this.selections[ viewers[i] ].model, this.selections[ viewers[i] ].url, this.selections[ viewers[i] ].shapes);
				this.gadget.resetSelection( viewers[i] );
			}
			
			// Connection is saved, if it contains shapes from at least two models
			if(validViewers > 1) {
				connection.setComment( prompt("Description: ") );
				connection.markShapes(false);
				this.gadget.addConnection( connection );
			
			} else {
				alert("You have to select shapes from at least two models");
			}
		};
		
		// collection of indices of all models that were selected during selection mode
		var viewers = [];
		for (var i = 0; i < this.selections.length; i++){
			if (this.selections[i])
				viewers.push(i);
		};
		
		this.gadget.sendAvailableGadgets(viewers, exitSelectionMode, this, {});
		
	}

}

