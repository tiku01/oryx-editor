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
	 * Handler for layouting event 'layout.uml.activityPartition'
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
		
		// layouting 
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
	
		var resourceIds = $H(this.hashedBounds[this.currentRootPartition.resourceId]).keys();
		
		var deletedResourceIds = resourceIds.reject(function (resourceId){
			return allChildPartitions.any(function(partition){ return partition.resourceId == resourceId})}); 
		
		var deletedPartitions = deletedResourceIds.findAll(function(resourceId) {
			return this.hashedBounds[this.currentRootPartition.resourceId][resourceId];
		}.bind(this));
		
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
		
		// you cannot place oldWidth in the else if part even though the condition is the same, 
		// because down there you are inside the inject part
		var oldWidth = 0;	
		if(!changedActivityPartition && propagateWidth){
			oldWidth = activityPartitions.inject(0, function(widthAcc, partition) {
				return widthAcc + partition.bounds.width();})
		} 
		
		var width = activityPartitions.inject(0, function (widthAcc, partition) {
				
			if (partition === changedActivityPartition) {
				this.propagateWidthToChildren(partition, widthAcc); //recursive
			} else if (!changedActivityPartition && propagateWidth) {
				this.setChildWidthProportionallyToTheOldWidth(partition, widthAcc, propagateWidth, oldWidth); //recursive
			} else {
				this.getWidthFromChildPartitions(partition, widthAcc, propagateWidth, changedActivityPartition); //recursive
			}				
				return widthAcc + partition.bounds.width();
			}.bind(this));
		return width;
	},
	
	propagateWidthToChildren: function(partition, currentWidth){
		this.adjustActivityPartitionWidth(this.getChildActivityPartitions(partition), undefined, partition.bounds.width()); //recursion
		partition.bounds.set({y:30, x:currentWidth}, {y:partition.bounds.height()+30, x:partition.bounds.width()+currentWidth});
	},
	
	setChildWidthProportionallyToTheOldWidth: function(partition, currentWidth, propagateWidth, oldWidth){
				
		var newWidth = (partition.bounds.width() * propagateWidth) / oldWidth;
				this.adjustActivityPartitionWidth(this.getChildActivityPartitions(partition), undefined, newWidth); //recursion
				this.setActivityPartitionDimensions(partition,newWidth, null);
				this.setActivityPartitionPosition(partition, currentWidth);
	},
	
	getWidthFromChildPartitions: function(partition, currentWidth, propagateWidth, changedActivityPartition){
				var newWidth = this.adjustActivityPartitionWidth(this.getChildActivityPartitions(partition), changedActivityPartition, propagateWidth);//recursion
				
				if (!newWidth) {
					newWidth = partition.bounds.width();
				} //end of recursion
				
				this.setActivityPartitionDimensions(partition, newWidth, null);
				this.setActivityPartitionPosition(partition, currentWidth);
	},
		
	updateActivityPartitionWidth: function(rootPartition){
		var activityPartitions = this.getChildActivityPartitions(rootPartition);
		if (activityPartitions.length == 0){
			return rootPartition.bounds.width();
		}
		
		var width = activityPartitions.inject(0,function (widthAcc, partition){
			this.setActivityPartitionPosition(partition, widthAcc);
			return widthAcc + this.updateActivityPartitionWidth(partition);
		}.bind(this));
		
		
		this.setActivityPartitionDimensions(rootPartition, width, null);
		
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
	 * Returns a set on all child activityPartitions for the given Shape. If recursive is TRUE, also indirect children will be returned (default is FALSE)
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
