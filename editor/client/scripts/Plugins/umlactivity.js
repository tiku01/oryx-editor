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
		this.facade.registerOnEvent('layout.uml.activityRegion', this.handleLayoutActivityRegion.bind(this));
	},
	
	isActivityRegion: function(shape) {
		return "http://b3mn.org/stencilset/umlactivity#umlAcActivityRegion".toLowerCase() == shape.getStencil().id().toLowerCase();
	},

	forceToUpdateActivityRegion: function(activityRegion){
		
		if (activityRegion.bounds.height() !== activityRegion._svgShapes[0].height) {	
			activityRegion.isChanged = true;
			activityRegion.isResized = true;
			activityRegion._update();
		}
	},
	
	handleLayoutActivityRegion: function(event){
		var existingActivityRegion= event.shape;
		var selection = this.facade.getSelection();
		var currentActivityRegion = selection.first();
		
		currentActivityRegion = currentActivityRegion || existingActivityRegion;
		
		if (! this.isActivityRegion(currentActivityRegion)){
			return;
		}
		
		
		if(currentActivityRegion != existingActivityRegion){
		
			currentActivityRegion.a.x=existingActivityRegion.b.x;
			currentActivityRegion.a.y=existingActivityRegion.a.y;
			currentActivityRegion.b.x=existingActivityRegion.b.x+250;
			currentActivityRegion.b.y=existingActivityRegion.a.y;
			
			this.forceToUpdateActivityRegion(existingActivityRegion);
			this.forceToUpdateActivityRegion(currentActivityRegion);
			
		};
		
		
		
		
		if (currentActivityRegion == existingActivityRegion){};
	},
	/**
	 * Handler for layouting event 'layout.bpmn2_0.pool'
	 * @param {Object} event
	 */
	handleLayoutActivityRegion2: function(event){
		
		var pool = event.shape;
		var selection = this.facade.getSelection(); 
		var currentShape = selection.first();
		
		currentShape = currentShape || pool;
		
		this.currentPool = pool;
		
		// Check if it is a pool or a lane
		if (!(currentShape.getStencil().id().endsWith("umlAcActivityRegion") || currentShape.getStencil().id().endsWith("ActivityRegionCanvas"))) {
			return;
		}
		
		if (!this.hashedBounds[pool.resourceId]) {
			this.hashedBounds[pool.resourceId] = {};
		}
		
		// Find all child lanes
		var lanes = this.getLanes(pool);
		
		if (lanes.length <= 0) {
			return
		}
		
		// Show/hide caption regarding the number of lanes
		if (lanes.length === 1 && this.getLanes(lanes.first()).length <= 0) {
			// TRUE if there is a caption
			lanes.first().setProperty("oryx-showcaption", lanes.first().properties["oryx-name"].trim().length > 0);
			var rect = lanes.first().node.getElementsByTagName("rect");
			rect[0].setAttributeNS(null, "display", "none");
		} else {
			lanes.invoke("setProperty", "oryx-showcaption", true);
			lanes.each(function(lane){
				var rect = lane.node.getElementsByTagName("rect");
				rect[0].removeAttributeNS(null, "display");
			})
		}
		
		
		
		var allLanes = this.getLanes(pool, true);
		
		var deletedLanes = [];
		var addedLanes = [];
		
		// Get all new lanes
		var i=-1;
		while (++i<allLanes.length) {
			if (!this.hashedBounds[pool.resourceId][allLanes[i].resourceId]){
				addedLanes.push(allLanes[i])
			}
		}
		
		if (addedLanes.length > 0){
			currentShape = addedLanes.first();
		}
		
		
		// Get all deleted lanes
		var resourceIds = $H(this.hashedBounds[pool.resourceId]).keys();
		var i=-1;
		while (++i<resourceIds.length) {
			if (!allLanes.any(function(lane){ return lane.resourceId == resourceIds[i]})){
				deletedLanes.push(this.hashedBounds[pool.resourceId][resourceIds[i]]);
				selection = selection.without(function(r){ return r.resourceId == resourceIds[i] });
			}
		}		
				
		var height, width;
		
		if (deletedLanes.length > 0 || addedLanes.length > 0) {
			
			// Set height from the pool
			height = this.updateHeight(pool);
			// Set width from the pool
			width = this.adjustWidth(lanes, pool.bounds.width());	
			
			pool.update();
		}
		
		/**
		 * Set width/height depending on the pool
		 */
		else if (pool == currentShape) {
			
			// Set height from the pool
			height = this.adjustHeight(lanes, undefined, pool.bounds.height());
			// Set width from the pool
			width = this.adjustWidth(lanes, pool.bounds.width());		
		}
		
		/**‚
		 * Set width/height depending on containing lanes
		 */		
		else {
			// Get height and adjust child heights
			height = this.adjustHeight(lanes, currentShape);
			// Set width from the current shape
			width = this.adjustWidth(lanes, currentShape.bounds.width()+(this.getDepth(currentShape,pool)*30));
		}
		

		this.setDimensions(pool, width, height);
		
		//hier standen mal die Docker
		
		this.hashedBounds[pool.resourceId] = {};
		
		var i=-1;
		while (++i < allLanes.length) {
			// Cache positions
			this.hashedBounds[pool.resourceId][allLanes[i].resourceId] = allLanes[i].absoluteBounds();
			
			this.hashedLaneDepth[allLanes[i].resourceId] = this.getDepth(allLanes[i], pool);
			
			this.forceToUpdateLane(allLanes[i]);
		}
		
		this.hashedPoolPositions[pool.resourceId] = pool.bounds.clone();
		
		
		// Update selection
		//this.facade.setSelection(selection);		
	},
	forceToUpdateLane: function(lane){
		
		if (lane.bounds.height() !== lane._svgShapes[0].height) {	
			lane.isChanged = true;
			lane.isResized = true;
			lane._update();
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
	
	updateDepth: function(lane, fromDepth, toDepth){
		
		var xOffset = (fromDepth - toDepth) * 30;
		
		lane.getChildNodes().each(function(shape){
			shape.bounds.moveBy(xOffset, 0);
			
			[].concat(children[j].getIncomingShapes())
					.concat(children[j].getOutgoingShapes())
					
		})
		
	},
	
	setDimensions: function(shape, width, height){
		var isLane = shape.getStencil().id().endsWith("Lane");
		// Set the bounds
		shape.bounds.set(
				isLane ? 30 : shape.bounds.a.x, 
				shape.bounds.a.y, 
				width	? shape.bounds.a.x + width - (isLane?30:0) : shape.bounds.b.x, 
				height 	? shape.bounds.a.y + height : shape.bounds.b.y
			);
	},

	setLanePosition: function(shape, y){
		shape.bounds.moveTo(30, y);
	},
		
	adjustWidth: function(lanes, width) {
		
		// Set width to each lane
		(lanes||[]).each(function(lane){
			this.setDimensions(lane, width);
			this.adjustWidth(this.getLanes(lane), width-30);
		}.bind(this));
		
		return width;
	},
	
	
	adjustHeight: function(lanes, changedLane, propagateHeight){
		
		var oldHeight = 0;
		if (!changedLane && propagateHeight){
			var i=-1;
			while (++i<lanes.length){	
				oldHeight += lanes[i].bounds.height();		
			}
		}
		
		var i=-1;
		var height = 0;
		
		// Iterate trough every lane
		while (++i<lanes.length){
			
			if (lanes[i] === changedLane) {
				// Propagate new height down to the children
				this.adjustHeight(this.getLanes(lanes[i]), undefined, lanes[i].bounds.height());
				
				lanes[i].bounds.set({x:30, y:height}, {x:lanes[i].bounds.width()+30, y:lanes[i].bounds.height()+height})
								
			} else if (!changedLane && propagateHeight) {
				
				var tempHeight = (lanes[i].bounds.height() * propagateHeight) / oldHeight;
				// Propagate height
				this.adjustHeight(this.getLanes(lanes[i]), undefined, tempHeight);
				// Set height propotional to the propagated and old height
				this.setDimensions(lanes[i], null, tempHeight);
				this.setLanePosition(lanes[i], height);
			} else {
				// Get height from children
				var tempHeight = this.adjustHeight(this.getLanes(lanes[i]), changedLane, propagateHeight);
				if (!tempHeight) {
					tempHeight = lanes[i].bounds.height();
				}
				this.setDimensions(lanes[i], null, tempHeight);
				this.setLanePosition(lanes[i], height);
			}
			
			height += lanes[i].bounds.height();
		}
		
		return height;
		
	},
	
	
	updateHeight: function(root){
		
		var lanes = this.getLanes(root);
		
		if (lanes.length == 0){
			return root.bounds.height();
		}
		
		var height = 0;
		var i=-1;
		while (++i < lanes.length) {
			this.setLanePosition(lanes[i], height);
			height += this.updateHeight(lanes[i]);
		}
		
		this.setDimensions(root, null, height);
		
		return height;
	},
	
	getOffset: function(lane, includePool, pool){
		
		var offset = {x:0,y:0};
		
		
		/*var parent = lane; 
		 while(parent) {
		 				
			
			var offParent = this.hashedBounds[pool.resourceId][parent.resourceId] ||(includePool === true ? this.hashedPoolPositions[parent.resourceId] : undefined);
			if (offParent){
				var ul = parent.bounds.upperLeft();
				var ulo = offParent.upperLeft();
				offset.x += ul.x-ulo.x;
				offset.y += ul.y-ulo.y;
			}
			
			if (parent.getStencil().id().endsWith("Pool")) {
				break;
			}
			
			parent = parent.parent;
		}	*/
		
		var offset = lane.absoluteXY();
		
		var hashed = this.hashedBounds[pool.resourceId][lane.resourceId] ||(includePool === true ? this.hashedPoolPositions[lane.resourceId] : undefined);
		if (hashed) {
			offset.x -= hashed.upperLeft().x; 	
			offset.y -= hashed.upperLeft().y;		
		} else {
			return {x:0,y:0}
		}		
		return offset;
	},
	
	getNextLane: function(shape){
		while(shape && !shape.getStencil().id().endsWith("Lane")){
			if (shape instanceof ORYX.Core.Canvas) {
				return null;
			}
			shape = shape.parent;
		}
		return shape;
	},
	
	getParentPool: function(shape){
		while(shape && !shape.getStencil().id().endsWith("Pool")){
			if (shape instanceof ORYX.Core.Canvas) {
				return null;
			}
			shape = shape.parent;
		}
		return shape;
	},
	moveBy: function(pos, offset){
		pos.x += offset.x;
		pos.y += offset.y;
		return pos;
	},
	
	getHashedBounds: function(shape){
		return this.currentPool && this.hashedBounds[this.currentPool.resourceId][shape.resourceId] ? this.hashedBounds[this.currentPool.resourceId][shape.resourceId] : shape.bounds.clone();
	},
	
	/**
	 * Returns a set on all child lanes for the given Shape. If recursive is TRUE, also indirect children will be returned (default is FALSE)
	 * The set is sorted with first child the lowest y-coordinate and the last one the highest.
	 * @param {ORYX.Core.Shape} shape
	 * @param {boolean} recursive
	 */
	getLanes: function(shape, recursive){
		var lanes = shape.getChildNodes(recursive||false).findAll(function(node) { return (node.getStencil().id() === "http://b3mn.org/stencilset/bpmn2.0#Lane"); });
		lanes = lanes.sort(function(a, b){
					// Get y coordinate
					var ay = Math.round(a.bounds.upperLeft().y);
					var by = Math.round(b.bounds.upperLeft().y);
					
					// If equal, than use the old one
					if (ay == by) {
						ay = Math.round(this.getHashedBounds(a).upperLeft().y);
						by = Math.round(this.getHashedBounds(b).upperLeft().y);
					}
					return  ay < by ? -1 : (ay > by ? 1 : 0)
				}.bind(this))
		return lanes;
	}
};

ORYX.Plugins.UMLActivity = Clazz.extend(ORYX.Plugins.UMLActivity);
