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
 * The UMLActivity plugin provides layout methods referring to the UMLActivity stencilset. 
 * 
 * @class ORYX.Plugins.UMLActivity
 * @extends Clazz
 * @param {Object} facade The facade of the editor
 */
 ORYX.Plugins.UMLActivity = 
/** @lends ORYX.Plugins.UMLActivity.prototype */
{
	/**
	 * Creates a new instance of the UMLActivity plugin and registers it on the
	 * layout events listed in the UMLActivity stencil set.
	 * 
	 * @constructor
	 * @param {Object} facade The facade of the editor
	 */
	construct: function(facade) {
		this.facade = facade;
		this.facade.registerOnEvent('layout.uml.activityPartition', this.handleLayoutActivityPartition.bind(this));
	},
	
	/**
	  * Global variable which contains a hashmap of the bounds of the activityPartions in the diagram
	  * @private
	  */
	hashedBounds : {}, 
	
	/**
	 * Handler for layouting event 'layout.uml.activityPartition'
	 * Initiates also the layouting and hashing of the bounds of all activityPartitions
	 * @param {Object} event This event is fired when an activityPartition Node is moved/resized/edited
	 */
	handleLayoutActivityPartition: function(event){
		
		var rootPartition = event.shape;
		var directChildPartitions = this.getChildActivityPartitions(rootPartition, false);
		
		var currentShape = this.facade.getSelection().first(); 
		currentShape = currentShape || rootPartition;
		
		//Guards
		if (this.isActivityPartitionNode(rootPartition.parent)) {return;}
		if(!this.isActivityPartitionNode(rootPartition)) {return;}
		if (!this.isActivityPartitionNode(currentShape)) {return;}
		if (directChildPartitions.length <= 0) {return;}
		
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
	/**
	 * Caches the bounds of an activityPartiton in a two dimensional array
	 * The in the first dimension the rootPartition is saved, in the second dimension allChildPartitions are saved
	 * @private
	 * @param {ORYX.Core.Node} rootPartition This parameter a an activityPartition, whose parent is not an activityPartition
	 * @param {ORYX.Core.Node[]} allChildPartitions This parameter is an array of all child partitions of the rootPartition
	 * 
	 */
	cachePositions : function(rootPartition, allChildPartitions) {
		this.hashedBounds[rootPartition.resourceId] = {};
		allChildPartitions.each(function(partition){
				this.hashedBounds[rootPartition.resourceId][partition.resourceId] = partition.absoluteBounds();
				this.forceToUpdateActivityPartition(partition);
			}.bind(this));
	},
	
	/**
	 * Resizes all activityPartitions which belong to the rootPartition
	 * after a partition is added or deleted
	 * @private
	 * @param {ORYX.Core.Node} rootPartition This parameter a an activityPartition, whose parent is not an activityPartition
	 * @param {ORYX.Core.Node[]} directChildPartitions This parameter is an array of all direct child partitions of the rootPartition
	 */
	resizeAfterAddorDeleteOfPartition : function (rootPartition, directChildPartitions){
		var width, height;
		width = this.updateActivityPartitionWidth(rootPartition);
		height = this.adjustActivityPartitionHeight(directChildPartitions, rootPartition.bounds.height());	
		rootPartition.update();
		this.setActivityPartitionDimensions(rootPartition, width, height);
	},
	
	/**
	 * Resizes all activityPartitions which belong to the rootPartition
	 * after the rootPartition is resized/moved
	 * @private
	 * @param {ORYX.Core.Node} rootPartition This parameter a an activityPartition, whose parent is not an activityPartition
	 * @param {ORYX.Core.Node[]} directChildPartitions This parameter is an array of all direct child partitions of the rootPartition
	 */
	resizeAfterRootPartitionChangend : function (rootPartition, directChildPartitions){
		var width, height;
		width = this.adjustActivityPartitionWidth(directChildPartitions, undefined, rootPartition.bounds.width());
		height = this.adjustActivityPartitionHeight(directChildPartitions, rootPartition.bounds.height());
		this.setActivityPartitionDimensions(rootPartition, width, height);
	},
	
	/**
	 * Resizes all activityPartitions which belong to the rootPartition
	 * after the rootPartition is resized/moved
	 * @private
	 * @param {ORYX.Core.Node} rootPartition This parameter a an activityPartition, whose parent is not an activityPartition
	 * @param {ORYX.Core.Node[]} directChildPartitions This parameter is an array of all direct child partitions of the rootPartition
	 * @param {ORYX.Core.Node} currentShape This parameter is the shape that caused the event
	 */
	resizeAfterChildPartitionChanged : function ( rootPartition, directChildPartitions, currentShape){
		var width, height;
		width = this.adjustActivityPartitionWidth(directChildPartitions, currentShape);
		height = this.adjustActivityPartitionHeight(directChildPartitions, currentShape.bounds.height()+(this.getDepth(currentShape,rootPartition)*30));
		this.setActivityPartitionDimensions(rootPartition, width, height);
	},
	
	/**
	 * Finds all Partitions, which are added to the currentRootPartition
	 * At the time a layouting event is fired the lanes are already children 
	 * of the currentRootPartition, but they are not added to the hashedBounds
	 * @private
	 * @param {ORYX.Core.Node[]} allChildPartitions This parameter is an array of all child partitions of the rootPartition
	 * @return {ORYX.Core.Node[]} The result is an array of all added partitions
	 */
	getAllAddedPartitions : function (allChildPartitions){
			return allChildPartitions.findAll(function(shape) {
					if (!this.hashedBounds[this.currentRootPartition.resourceId][shape.resourceId]) {return shape;}
					}.bind(this));
	},
	
	/**
	 * Finds all Partitions, which are deleted from the currentRootPartition
	 * At the time a layouting event is fired the lanes are not children of the 
	 * currentRootPartition any more, but they are still referenced in the hashedBounds 
	 * @private
	 * @param {ORYX.Core.Node[]} allChildPartitions This parameter is an array of all child partitions of the rootPartition
	 * @return {ORYX.Core.Node[]} The result is an array of all deleted partitions
	 */
	getAllDeletedPartitions : function (allChildPartitions){
	
		var resourceIds = $H(this.hashedBounds[this.currentRootPartition.resourceId]).keys();
		
		var deletedResourceIds = resourceIds.reject(function (resourceId){
			return allChildPartitions.any(function(partition){ 
				return partition.resourceId == resourceId;
		});}); 
		
		var deletedPartitions = deletedResourceIds.findAll(function(resourceId) {
			return this.hashedBounds[this.currentRootPartition.resourceId][resourceId];
		}.bind(this));
		
		return deletedPartitions;
		
	},
	
	/**
	 * Forces an activityPartition to update itself, 
	 * if the width saved in the stencil, that belongs to 
	 * the shape is different, to avoid inconsistencies
	 * @private
	 * @param {ORYX.Core.Node} partition This parameter is an Partition in an incosistent layouting state
	 */
	forceToUpdateActivityPartition: function(partition){
		
		if (partition.bounds.width() !== partition._svgShapes[0].width) {	
			partition.isChanged = true;
			partition.isResized = true;
			partition._update();
		}
	},
	
	/**
	 * Forces an activityPartition to update itself, 
	 * if the width saved in the stencil, that belongs to 
	 * the shape is different, to avoid inconsistencies
	 * @private
	 * @param {ORYX.Core.Node} partition This parameter is an Partition in an incosistent layouting state
	 */
	getDepth: function(child, parent){
		
		var i=0;
		while(child && child.parent && child !== parent){
			child = child.parent;
			++i;
		}
		return i;
	},
	
	/**
	 * Sets the bounds and therefore the size of an ActivityPartition
	 * @private
	 * @param {ORYX.Core.Node} partition This parameter is an Partition which gets new bounds
	 * @param {number} width The new width of the Partition
	 * @param {number} height The new height of the Partition
	 */
	setActivityPartitionDimensions: function (partition, width, height){
		var isChildActivityPartition = this.isActivityPartitionNode(partition);
		isChildActivityPartition = (isChildActivityPartition && this.isActivityPartitionNode(partition.parent));
		partition.bounds.set(
			partition.bounds.a.x,
			isChildActivityPartition ? 30 : partition.bounds.a.y,
			width ? partition.bounds.a.x + width : partition.bounds.b.x,
			height ? partition.bounds.a.y + height - (isChildActivityPartition ? 30:0) : partition.bounds.b.y
		);
	},
	
	/**
	 * Lowers an ActivityPartition, so that the head of the parent partition is visible
	 * @private
	 * @param {ORYX.Core.Node} shape This parameter is a shape which gets new position
	 * @param {number} x The new relative Offset to the left edge of parent partition 
	 */
	setActivityPartitionPosition: function(shape, x){
		shape.bounds.moveTo(x, 30);
	},
	
	/**
	 * Sets recursively the new height of the child partitions
	 * @private
	 * @param {ORYX.Core.Node[]} partitions Array of all direct child nodes
	 * @param {number} height The new height for the partitions 
	 */
	adjustActivityPartitionHeight: function(partitions, height) {
		(partitions||[]).each(function(partition){
			this.setActivityPartitionDimensions(partition, null, height);
			this.adjustActivityPartitionHeight(this.getChildActivityPartitions(partition), height-30);
		}.bind(this));
		return height;
	},
	
	/**
	 * Sets recursively the new width of the child partitions 
	 * regarding to the partition which was changed
	 * @private
	 * @param {ORYX.Core.Node[]} activityPartitions Array of all direct childPartitions
	 * @param {ORYX.Core.Node} activityPartition The activityPartition which is not changed
	 * @param {number} propagateWidth The width 
	 * @return {number} The new width for the parent activityPartition
	 */
	adjustActivityPartitionWidth: function(activityPartitions, changedActivityPartition, propagateWidth){
		
		// you cannot place oldWidth in the else if part even though the condition is the same, 
		// because down there you are inside the inject part at var width
		var oldWidth = 0;	
		if(!changedActivityPartition && propagateWidth){
			oldWidth = activityPartitions.inject(0, function(widthAcc, partition) {
				return widthAcc + partition.bounds.width();});
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
	
	/**
	 * Sets the size of the changed partition and informs the children of the partition
	 * that the partition was horizontally resized
	 * @private
	 * @param {ORYX.Core.Node} partition The partition which needs resizing
	 * @param {number} currentWidth The width the partition will receive
	 */
	propagateWidthToChildren: function(partition, currentWidth){
		this.adjustActivityPartitionWidth(this.getChildActivityPartitions(partition), undefined, partition.bounds.width()); //recursion
		partition.bounds.set({y:30, x:currentWidth}, {y:partition.bounds.height()+30, x:partition.bounds.width()+currentWidth});
	},
	
	/**
	 * Sets the width of the partition (which was actually not resized by the user) 
	 * proportionally to the width of its sibling and informs the children
	 * of the partition that the partition was horizontally resized
	 * @private
	 * @param {ORYX.Core.Node} partition The partition which is resized
	 * @param {number} offset The horizontal offset this partition receives, since it may have siblings to its left
	 * @param {number} propagateWidth The new width the parent partition has
	 * @param {number} oldWidth The width the parent partition had before
	 */
	setChildWidthProportionallyToTheOldWidth: function(partition, offset, propagateWidth, oldWidth){
				
		var newWidth = (partition.bounds.width() * propagateWidth) / oldWidth;
				this.adjustActivityPartitionWidth(this.getChildActivityPartitions(partition), undefined, newWidth); //recursion
				this.setActivityPartitionDimensions(partition,newWidth, null);
				this.setActivityPartitionPosition(partition, offset);
	},
	
	/**
	 * Sets the width of the partition (which was actually not resized by the user) 
	 * proportionally to the width of its sibling and informs the children
	 * of the partition that the partition was horizontally resized
	 * @private
	 * @param {ORYX.Core.Node} partition The partition which is resized
	 * @param {number} offset The horizontal offset this partition receives, since it may have siblings to its left
	 * @param {number} propagateWidth The new recommended width of the partition
	 * @param {ORYX.Core.Node} changedActivityPartition The partition the user changed
	 */
	getWidthFromChildPartitions: function(partition, offset, propagateWidth, changedActivityPartition){
				var newWidth = this.adjustActivityPartitionWidth(this.getChildActivityPartitions(partition), changedActivityPartition, propagateWidth);//recursion
				
				if (!newWidth) {
					newWidth = partition.bounds.width();
				}
				
				this.setActivityPartitionDimensions(partition, newWidth, null);
				this.setActivityPartitionPosition(partition, offset);
	},
	
	/**
	 * Updates the width of all partitions when partitions were added or deleted
	 * @private
	 * @param {ORYX.Core.Node} rootPartition The partition which is resized 
	 */
	updateActivityPartitionWidth: function(rootPartition){
		var activityPartitions = this.getChildActivityPartitions(rootPartition);
		if (activityPartitions.length === 0){
			return rootPartition.bounds.width();
		}
		
		var width = activityPartitions.inject(0,function (widthAcc, partition){
			this.setActivityPartitionPosition(partition, widthAcc);
			return widthAcc + this.updateActivityPartitionWidth(partition);
		}.bind(this));
		
		
		this.setActivityPartitionDimensions(rootPartition, width, null);
		
		return width;
	},
	
	/**
	 * Returns the hashed bounds of the partition if they are available
	 * otherwise a copy of the partition's bounds.
	 * @private
	 * @param {ORYX.Core.Node} partition The partition for which there are hashed bounds if it is an activityPartition
	 * @return {ORYX.Core.Bounds} The hashed bounds or a copy of the partition's bounds
	 */
	getHashedBounds: function(partition){
		return this.currentRootPartition && this.hashedBounds[this.currentRootPartition.resourceId][partition.resourceId] ? this.hashedBounds[this.currentRootPartition.resourceId][partition.resourceId] : partition.bounds.clone();
	},
	
	/**
	* Helper method, which returns true, if the received shape is an Activiy Partition. 
	*@private
	*@param {ORYX.Core.Node} shape The shape that is checked for being an Activiy Partition.
	*@return {boolean} The result is true, if the shape is an Activiy Partition.
	*/
	isActivityPartitionNode : function (shape) {
		return "http://b3mn.org/stencilset/umlactivity#activitypartition" == shape.getStencil().id().toLowerCase();
	},
	
	/**
	 * Returns a sorted set on all child activityPartitions for the given partition. If recursive is TRUE, also indirect children will be returned (default is FALSE)
	 * The set is sorted with the first child the lowest x-coordinate and the last one the highest.
	 * @param {ORYX.Core.Node} partition The partition the direct/all children were asked for
	 * @param {boolean} recursive Specifies, whether the you receive the direct or all children (default is FALSE = direct children)
	 * @return {ORYX.Core.Node[]} An ordered set of the child nodes with the first child the lowest x-coordinate and the last one the highest
	 */
	getChildActivityPartitions: function(partition, recursive){
		var activityPartitions = partition.getChildNodes(recursive||false).findAll(function(node) { 
			if (this.isActivityPartitionNode(node)) {
				return node;
			}}.bind(this));
		activityPartitions = activityPartitions.sort(function(a, b){
			// Get x coordinate
			var ax = Math.round(a.bounds.upperLeft().x);
			var bx = Math.round(b.bounds.upperLeft().x);
					
			// If equal, than use the old one
			if (ax == bx) {
				ax = Math.round(this.getHashedBounds(a).upperLeft().x);
				bx = Math.round(this.getHashedBounds(b).upperLeft().x);
			}
				return  ax < bx ? -1 : (ax > bx ? 1 : 0);
			}.bind(this));
		return activityPartitions;
	}
};

ORYX.Plugins.UMLActivity = Clazz.extend(ORYX.Plugins.UMLActivity);
