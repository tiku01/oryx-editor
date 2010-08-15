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
 ORYX.Plugins.UMLActivity = 
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
		this.facade.registerOnEvent('layout.uml.activityPartition', this.handleLayoutActivityRegion.bind(this));
	},
	
	hashedBounds : {}, 
	
	/**
	* Helper method, which returns true, if the received shape is an Activiy Partition. 
	*@param {Object} shape The shape that is checked for beeing an Activiy Partition.
	*@return {boolean} The result is true, if the shape is an Activiy Partition.
	*/
	isActivityPartitionNode : function (shape) {
		return "http://b3mn.org/stencilset/umlactivity#activitypartition" == shape.getStencil().id().toLowerCase();
	},
	
	/**
	 * Handler for layouting event 'layout.bpmn2_0.pool'
	 * @param {Object} event
	 */
	handleLayoutActivityRegion: function(event){
		
		var rootPartition = event.shape;
		var directChildPartitions = this.getChildActivityPartitions(rootPartition, false);
		
		var currentShape = this.facade.getSelection().first(); 
		currentShape = currentShape || rootPartition;
		
		//Guards
		if (this.isActivityPartitionNode(rootPartition.parent)) return;
		if(!this.isActivityPartitionNode(rootPartition)) return;
		if (!this.isActivityPartitionNode(currentShape)) return;
		if (directChildPartitions.length <= 0) return;
		
		this.currentRootPartition = rootPartition;
		
		if (!this.hashedBounds[rootPartition.resourceId]) {
			this.hashedBounds[rootPartition.resourceId] = {};
		}
				
		var allChildPartitions = this.getChildActivityPartitions(rootPartition, true);
		var addedPartitions = this.getAllAddedPartitions(allChildPartitions);
		var deletedPartitions = this.getAllDeletedPartitions(allChildPartitions);
				
		if (addedPartitions.length > 0){
			currentShape = addedPartitions.first();
		}
	
		var height, width; //future height and width of the rootPartition
		if (deletedPartitions.length > 0 || addedPartitions.length > 0) {
			this.resizeAfterAddorDeleteOfPartition(rootPartition, directChildPartitions);
		}
		else if (rootPartition == currentShape) {
			this.resizeAfterRootPartitionChangend ( rootPartition, directChildPartitions);
		}	
		else {
			this.resizeAfterChildPartitionChanged ( rootPartition, directChildPartitions, currentShape);
		}
				
		this.cachePositions(rootPartition, allChildPartitions);
	},
	
	cachePositions : function(rootPartition, allChildPartitions) {
		this.hashedBounds[rootPartition.resourceId] = {};
		allChildPartitions.each(function(partition){
				this.hashedBounds[rootPartition.resourceId][partition.resourceId] = partition.absoluteBounds();
				this.forceToUpdateActivityPartition(partition);
			}.bind(this));
	},
	
	resizeAfterAddorDeleteOfPartition : function (rootPartition, directChildPartitions){
		width = this.updateActivityPartitionWidth(rootPartition);
		height = this.adjustActivityPartitionHeight(directChildPartitions, rootPartition.bounds.height());	
		rootPartition.update();
		this.setActivityPartitionDimensions(rootPartition, width, height);
	},
	
	resizeAfterRootPartitionChangend : function (rootPartition, directChildPartitions){
		width = this.adjustActivityPartitionWidth(directChildPartitions, undefined, rootPartition.bounds.width());
		height = this.adjustActivityPartitionHeight(directChildPartitions, rootPartition.bounds.height());
		this.setActivityPartitionDimensions(rootPartition, width, height);
	},
	
	resizeAfterChildPartitionChanged : function ( rootPartition, directChildPartitions, currentShape){
			width = this.adjustActivityPartitionWidth(directChildPartitions, currentShape);
			height = this.adjustActivityPartitionHeight(directChildPartitions, currentShape.bounds.height()+(this.getDepth(currentShape,rootPartition)*30));
			this.setActivityPartitionDimensions(rootPartition, width, height);
	},
	
	getAllAddedPartitions : function (allChildPartitions){
			return allChildPartitions.findAll(function(shape) {
					if (!this.hashedBounds[this.currentRootPartition.resourceId][shape.resourceId]) return shape
					}.bind(this));
	},
	
	getAllDeletedPartitions : function (allChildPartitions){
	
		var deletedPartitions = [];
		var resourceIds = $H(this.hashedBounds[this.currentRootPartition.resourceId]).keys();
		var i=-1;
		while (++i<resourceIds.length) {
			if (!allChildPartitions.any(function(partition){ return partition.resourceId == resourceIds[i]})){
				deletedPartitions.push(this.hashedBounds[this.currentRootPartition.resourceId][resourceIds[i]]);
			}
		}
		return deletedPartitions;
	},
	
	forceToUpdateActivityPartition: function(partition){
		
		if (partition.bounds.width() !== partition._svgShapes[0].width) {	
			partition.isChanged = true;
			partition.isResized = true;
			partition._update();
		}
	},
	
	getDepth: function(child, parent){
		
		var i=0;
		while(child && child.parent && child !== parent){
			child = child.parent;
			++i
		}
		return i;
	},
	
	setActivityPartitionDimensions: function (shape, width, height){
		var isChildActivityPartition = this.isActivityPartitionNode(shape);
		isChildActivityPartition = (isChildActivityPartition && this.isActivityPartitionNode(shape.parent))
		shape.bounds.set(
			shape.bounds.a.x,
			isChildActivityPartition ? 30 : shape.bounds.a.y,
			width ? shape.bounds.a.x + width : shape.bounds.b.x,
			height ? shape.bounds.a.y + height - (isChildActivityPartition ? 30:0) : shape.bounds.b.y
		);
	},
	
	setActivityPartitionPosition: function(shape, x){
		shape.bounds.moveTo(x, 30);
	},
		
	adjustActivityPartitionHeight: function(partitions, height) {
		(partitions||[]).each(function(partition){
			this.setActivityPartitionDimensions(partition, null, height);
			this.adjustActivityPartitionHeight(this.getChildActivityPartitions(partition), height-30);
		}.bind(this));
		return height;
	},
	
	adjustActivityPartitionWidth: function(activityPartitions, changedActivityPartition, propagateWidth){
		
		var oldWidth = 0;
		if (!changedActivityPartition && propagateWidth){
			var i=-1;
			while (++i<activityPartitions.length){	
				oldWidth += activityPartitions[i].bounds.width();		
			}
		}
		
		var i=-1;
		var width = 0;
		
		// Iterate trough every lane
		while (++i<activityPartitions.length){
			
			if (activityPartitions[i] === changedActivityPartition) {
				// Propagate new height down to the children
				
				this.adjustActivityPartitionWidth(this.getChildActivityPartitions(activityPartitions[i]), undefined, activityPartitions[i].bounds.width());
				activityPartitions[i].bounds.set({y:30, x:width}, {y:activityPartitions[i].bounds.height()+30, x:activityPartitions[i].bounds.width()+width})
								
			} else if (!changedActivityPartition && propagateWidth) {
				
				var tempWidth = (activityPartitions[i].bounds.width() * propagateWidth) / oldWidth;
				// Propagate height
				this.adjustActivityPartitionWidth(this.getChildActivityPartitions(activityPartitions[i]), undefined, tempWidth);
				// Set height propotional to the propagated and old height
				this.setActivityPartitionDimensions(activityPartitions[i],tempWidth, null);
				this.setActivityPartitionPosition(activityPartitions[i], width);
			} else {
				// Get height from children
				var tempWidth = this.adjustActivityPartitionWidth(this.getChildActivityPartitions(activityPartitions[i]), changedActivityPartition, propagateWidth);
				if (!tempWidth) {
					tempWidth = activityPartitions[i].bounds.width();
				}
				this.setActivityPartitionDimensions(activityPartitions[i], tempWidth, null);
				this.setActivityPartitionPosition(activityPartitions[i], width);
			}
			
			width += activityPartitions[i].bounds.width();
		}
		
		return width;
		
	},
	
	updateActivityPartitionWidth: function(root){
		var activityPartitions = this.getChildActivityPartitions(root);
		if (activityPartitions.length == 0){
			return root.bounds.width();
		}
		
		var width = 0;
		var i=-1;
		while (++i < activityPartitions.length) {
			this.setActivityPartitionPosition(activityPartitions[i], width);
			width += this.updateActivityPartitionWidth(activityPartitions[i]);
		}
		
		this.setActivityPartitionDimensions(root, width, null);
		
		return width;
	},

	moveBy: function(pos, offset){
		pos.x += offset.x;
		pos.y += offset.y;
		return pos;
	},
	
	getHashedBounds: function(shape){
		return this.currentRootPartition && this.hashedBounds[this.currentRootPartition.resourceId][shape.resourceId] ? this.hashedBounds[this.currentRootPartition.resourceId][shape.resourceId] : shape.bounds.clone();
	},
	
	/**
	 * Returns a set on all child lanes for the given Shape. If recursive is TRUE, also indirect children will be returned (default is FALSE)
	 * The set is sorted with first child the lowest y-coordinate and the last one the highest.
	 * @param {ORYX.Core.Shape} shape
	 * @param {boolean} recursive
	 */
		
	getChildActivityPartitions: function(shape, recursive){
		var activityPartitions = shape.getChildNodes(recursive||false).findAll(function(node) { 
			if (this.isActivityPartitionNode(node)) return node;}.bind(this));
		activityPartitions = activityPartitions.sort(function(a, b){
					// Get x coordinate
					var ax = Math.round(a.bounds.upperLeft().x);
					var bx = Math.round(b.bounds.upperLeft().x);
					
					// If equal, than use the old one
					if (ax == bx) {
						ax = Math.round(this.getHashedBounds(a).upperLeft().x);
						bx = Math.round(this.getHashedBounds(b).upperLeft().x);
					}
					return  ax < bx ? -1 : (ax > bx ? 1 : 0)
				}.bind(this))
		return activityPartitions;
	}
};

ORYX.Plugins.UMLActivity = Clazz.extend(ORYX.Plugins.UMLActivity);
