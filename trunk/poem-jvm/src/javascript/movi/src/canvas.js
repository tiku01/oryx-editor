/**
 * Copyright (c) 2009
 * Jan-Felix Schwarz
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


MOVI.namespace("model");

(function() {
	
	var _CLASS_NAME = "movi-canvas";
	
	/**
     * Canvas provides a wrapper object for the model's root shape.
     * @namespace MOVI.model
     * @class MOVI.model.Canvas
     * @extends MOVI.model.Shape
     * @constructor
	 * @param {Modelviewer} modelviewer The Modelviewer object that owns
	 * the canvas
     * @param {Object} jsonObj The JSON object from which the new canvas
     * is created.
	 * @param {String} prefix The element's ID prefix (unique per modelviewer)
     */
    MOVI.model.Canvas = function(modelviewer, jsonObj, prefix) {
	
		this._modelviewer = modelviewer;

		// element's attributes
		var attr = {};
		
		MOVI.model.Canvas.superclass.constructor.call(this, jsonObj, jsonObj.stencilset, null, prefix, attr); 
		
		this.set("className", _CLASS_NAME);
		
		this.shapes = {};
		this._resourceIds = [];
		this._indexShapes();
		
		this._modelviewer.onZoomLevelChangeStart.subscribe(function() {
			this.setStyle("display", "none");
		}, this, true);
		this._modelviewer.onZoomLevelChangeEnd.subscribe(function() {
			this._update();
			this.setStyle("display", "block");
		}, this, true);
		
		this._update();
		
	}
	
	MOVI.extend(MOVI.model.Canvas, MOVI.model.Shape, {
		
		/**
		 * The owning modelviewer
		 * @property modelviewer
		 * @type Modelviewer
		 * @private
		 */
		_modelviewer: null,
		
		/**
		 * A key map containing all shapes of the model with their
		 * resource ids as keys.
		 * @property shapes
		 * @type Object
		 */
		shapes: null,
		
		/**
	     * Index all shapes owned by the model. Deep traverse canvas'
	     * child shapes and add all objects to the 'shapes' property.
	     * @method _indexShapes
		 * @param {Shape} recShape (optional) Temporarily considered shape
		 * for recursion
	     * @private
	     */
		_indexShapes: function(recShape) {
			recShape = recShape || this;
			for(var key in recShape.childShapes) {
				if(!YAHOO.lang.hasOwnProperty(recShape.childShapes, key)) continue;
				
				var child = recShape.childShapes[key];
				this.shapes[child.resourceId] = child;
				this._resourceIds.push(child.resourceId);
				this._indexShapes(child);
			}
		},
		
		/**
	     * Update style properties of the element
	     * @method _update
	     * @private
	     */
		_update: function() {
			var zoomFactor = this._modelviewer.getZoomLevel() / 100;
			var minX, minY, maxX, maxY, bounds;
			for(var i in this.childShapes) {
				if(!YAHOO.lang.hasOwnProperty(this.childShapes, i)) continue;
				
				bounds = this.childShapes[i].getAbsBounds();
				if(minX==undefined) minX = bounds.upperLeft.x;
				else minX = Math.min(minX, bounds.upperLeft.x);
				if(minY==undefined) minY = bounds.upperLeft.y;
				else minY = Math.min(minY, bounds.upperLeft.y);
				
				if(maxX==undefined) maxX = bounds.upperLeft.x;
				else maxX = Math.max(maxX, bounds.upperLeft.x);
				if(maxY==undefined) maxY = bounds.upperLeft.y;
				else maxY = Math.max(maxY, bounds.upperLeft.y);
			}
			for(var i in this.childShapes) {
				if(!YAHOO.lang.hasOwnProperty(this.childShapes, i)) continue;
				
				bounds = this.childShapes[i].bounds;
				bounds.upperLeft.x -= minX;
				bounds.upperLeft.y -= minY;
				bounds.lowerRight.x -= minX;
				bounds.lowerRight.y -= minY;
				this.childShapes[i].update();
				
				// for edges also adjust the absolute coordinates of the dockers
				if(this.childShapes[i].isEdge() && this.childShapes[i].dockers && this.childShapes[i].dockers.length>2) {
					for(var j=1; j<this.childShapes[i].dockers.length-1; j++) {
						this.childShapes[i].dockers[j].x -= minX;
						this.childShapes[i].dockers[j].y -= minY;
					}
				}
			}
			if(!this.bounds || !this.bounds.upperLeft || !this.bounds.lowerRight)
			    this.bounds = { upperLeft: {x: 0, y: 0}, lowerRight: {x: maxX-minX, y: maxY-minY}};
			    
			var left = (MOVI.config.MODEL_MARGIN / 2)*zoomFactor + "px";
			var top = (MOVI.config.MODEL_MARGIN / 2)*zoomFactor + "px";
			var width = (this._modelviewer.getImgWidth() - MOVI.config.MODEL_MARGIN)*zoomFactor + "px";
			var height = (this._modelviewer.getImgHeight() - MOVI.config.MODEL_MARGIN)*zoomFactor + "px";
			this.setStyle("left", left);
			this.setStyle("top", top);
			this.setStyle("width", width);
			this.setStyle("height", height); 
		},
		
		
		/**
	     * Returns the shape with the specified resource id. 
		 * If no matching stencil is found null is returned.
	     * @method getShape
	     * @param resourceId The shape's resource id
	     */
		getShape: function(resourceId) {
			return this.shapes[resourceId] || null;
		},
		
		/**
		 * Returns the owning modelviewer
		 * @method getModelViewer
		 * @return {ModelViewer} The owning model viewer
		 */
		getModelViewer: function() {
			return this._modelviewer;
		},
		
		/**
		 * Returns all nodes of the model
		 * @method getNodes
		 * @return {Object} A key map (String -> Node) of all nodes with their resource ids as keys
		 */
		getNodes: function() {
			var nodes = {};
			for(var key in this.shapes) {
				if(!YAHOO.lang.hasOwnProperty(this.shapes, key)) continue;
				
				if(this.shapes[key].stencil.type=="node")
					nodes[key] = this.shapes[key];
			}
			return nodes;
		},
		
		/**
		 * Returns all edges of the model
		 * @method getEdges
		 * @return {Object} A key map (String -> Node) of all edges with their resource ids as keys
		 */
		getEdges: function() {
			var edges = {};
			for(var key in this.shapes) {
				if(!YAHOO.lang.hasOwnProperty(this.shapes, key)) continue;
				
				if(this.shapes[key].stencil.type=="edge")
					edges[key] = this.shapes[key];
			}
			return edges;
		},
		
		/**
		 * Returns an Array containing all resource IDs of all shapes
		 * @method getResourceIds
		 * @return {Array} An Array of resource IDs as Strings of all shapes of the model
		 */
		getResourceIds: function() {
			return this._resourceIds;
		}
		
	});
	
})();