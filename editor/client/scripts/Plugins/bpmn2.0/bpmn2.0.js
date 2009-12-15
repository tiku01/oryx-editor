/**
 * Copyright (c) 2009
 * Sven Wagner-Boysen, Willi Tscheschner
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

 if(!ORYX.Plugins)
	ORYX.Plugins = new Object();

ORYX.Plugins.BPMN2_0 = {

	/**
	 *	Constructor
	 *	@param {Object} Facade: The Facade of the Editor
	 */
	construct: function(facade){
		this.facade = facade;
		
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_DRAGDOCKER_DOCKED, this.handleDockerDocked.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_PROPWINDOW_PROP_CHANGED, this.handlePropertyChanged.bind(this));
		this.facade.registerOnEvent('layout.bpmn2_0.pool', this.handleLayoutPool.bind(this));
		this.facade.registerOnEvent('layout.bpmn2_0.subprocess', this.handleSubProcess.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_SHAPEREMOVED, this.handleShapeRemove.bind(this));
		
		
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_LOADED, this.afterLoad.bind(this));
		
		//this.facade.registerOnEvent('layout.bpmn11.lane', this.handleLayoutLane.bind(this));
	},
	
	/**
	 * Force to update every pool
	 */
	afterLoad: function(){
		this.facade.getCanvas().getChildNodes().each(function(shape){
			if (shape.getStencil().id().endsWith("Pool")) {
				this.handleLayoutPool({
					shape: shape
				});
			}
		}.bind(this))
	},
	
	/**
	 * If a pool is selected and contains no lane,
	 * a lane is created automagically
	 */
	onSelectionChanged: function(event) {
		if(event.elements && event.elements.length === 1) {
			var shape = event.elements[0];
			if(shape.getStencil().idWithoutNs() === "Pool") {
				if(shape.getChildNodes().length === 0) {
					// create a lane inside the selected pool
					var option = {
							type:"http://b3mn.org/stencilset/bpmn2.0#Lane",
							position:{x:0,y:0},
							namespace:shape.getStencil().namespace(),
							parent:shape
					};
					this.facade.createShape(option);
					this.facade.getCanvas().update();
					this.facade.setSelection([shape]);
				}
			}
		}
	},
	
	handleShapeRemove: function(option) {
		
		var sh 				= option.shape;
		var parent 			= option.parent;
					
		if (sh.getStencil().idWithoutNs() === "Lane") {
		
			var command = new ORYX.Core.ResizeLanesCommand(sh, parent, this);
			this.facade.executeCommands([command]);
			
	
			/*
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 */
			
		}
		
	},
	
	hashedSubProcesses: {},
	
	hashChildShapes: function(shape){
		var children = shape.getChildNodes();
		children.each(function(child){
			if (this.hashedSubProcesses[child.id]){
				this.hashedSubProcesses[child.id] = child.absoluteXY();
				this.hashedSubProcesses[child.id].width 	= child.bounds.width();
				this.hashedSubProcesses[child.id].height 	= child.bounds.height();
				this.hashChildShapes(child);
			}
		}.bind(this));
	},

	/**
	 * Handle the layouting of a sub process.
	 * Mainly to adjust the child dockers of a sub process. 
	 *
	 */
	handleSubProcess : function(option) {
		
		var sh = option.shape;
		
		if (!this.hashedSubProcesses[sh.id]) {
			this.hashedSubProcesses[sh.id] = sh.absoluteXY();
			this.hashedSubProcesses[sh.id].width 	= sh.bounds.width();
			this.hashedSubProcesses[sh.id].height 	= sh.bounds.height();
			return;
		}
		
		var offset = sh.absoluteXY();
		offset.x -= this.hashedSubProcesses[sh.id].x;
		offset.y -= this.hashedSubProcesses[sh.id].y;
		
		var resized = this.hashedSubProcesses[sh.id].width !== sh.bounds.width() || this.hashedSubProcesses[sh.id].height !== sh.bounds.height();
		
		this.hashedSubProcesses[sh.id] = sh.absoluteXY();
		this.hashedSubProcesses[sh.id].width 	= sh.bounds.width();
		this.hashedSubProcesses[sh.id].height 	= sh.bounds.height();
		this.hashChildShapes(sh);
		
		
		// Move dockers only if currently is not resizing
		if (this.facade.isExecutingCommands()&&!resized) {
			this.moveChildDockers(sh, offset);
		}
	},
	
	moveChildDockers: function(shape, offset){
		
		if (!offset.x && !offset.y) {
			return;
		} 
		
		var children = shape.getChildNodes(true);
		
		// Get all nodes
		var dockers = children
			// Get all incoming and outgoing edges
			.map(function(node){
				return [].concat(node.getIncomingShapes())
						.concat(node.getOutgoingShapes())
			})
			// Flatten all including arrays into one
			.flatten()
			// Get every edge only once
			.uniq()
			// Get all dockers
			.map(function(edge){
				return edge.dockers.length > 2 ? 
						edge.dockers.slice(1, edge.dockers.length-1) : 
						[];
			})
			// Flatten the dockers lists
			.flatten();

		var abs = shape.absoluteBounds();
		abs.moveBy(-offset.x, -offset.y)
		var obj = {};
		dockers.each(function(docker){
			
			if (docker.isChanged){
				return;
			}
			
			var off = Object.clone(offset);
			
			if (!abs.isIncluded(docker.bounds.center())){
				var index 	= docker.parent.dockers.indexOf(docker);
				var size	= docker.parent.dockers.length;
				var from 	= docker.parent.getSource();
				var to 		= docker.parent.getTarget();
				
				var bothAreIncluded = children.include(from) && children.include(to);
				
				if (!bothAreIncluded){
					var previousIsOver = index !== 0 ? abs.isIncluded(docker.parent.dockers[index-1].bounds.center()) : false;
					var nextIsOver = index !== size-1 ? abs.isIncluded(docker.parent.dockers[index+1].bounds.center()) : false;
					
					if (!previousIsOver && !nextIsOver){ return; }
					
					var ref = docker.parent.dockers[previousIsOver ? index-1 : index+1];
					if (Math.abs(-Math.abs(ref.bounds.center().x-docker.bounds.center().x)) < 2){
						off.y = 0;
					} else if(Math.abs(-Math.abs(ref.bounds.center().y-docker.bounds.center().y)) < 2){
						off.x = 0;
					} else {
						return;
					}
				}
				
			}
			
			obj[docker.getId()] = {
				docker:docker,
				offset:off
			}
		})
		
		// Set dockers
		this.facade.executeCommands([new ORYX.Core.MoveDockersCommand(obj)]);
			
	},
	
	/**
	 * DragDocker.Docked Handler
	 *
	 */	
	handleDockerDocked: function(options) {
		var edge = options.parent;
		var edgeSource = options.target;
		
		if(edge.getStencil().id() === "http://b3mn.org/stencilset/bpmn2.0#SequenceFlow") {
			var isGateway = edgeSource.getStencil().groups().find(function(group) {
					if(group == "Gateways") 
						return group;
				});
			if(!isGateway && (edge.properties["oryx-conditiontype"] == "Expression"))
				// show diamond on edge source
				edge.setProperty("oryx-showdiamondmarker", true);
			else 
				// do not show diamond on edge source
				edge.setProperty("oryx-showdiamondmarker", false);
			
			// update edge rendering
			//edge.update();
			
			this.facade.getCanvas().update();
		}
	},
	
	/**
	 * PropertyWindow.PropertyChanged Handler
	 */
	handlePropertyChanged: function(option) {
		
		var shapes = option.elements;
		var propertyKey = option.key;
		var propertyValue = option.value;
		
		var changed = false;
		shapes.each(function(shape){
			if((shape.getStencil().id() === "http://b3mn.org/stencilset/bpmn2.0#SequenceFlow") &&
				(propertyKey === "oryx-conditiontype")) {
				
				if(propertyValue != "Expression")
					// Do not show the Diamond
					shape.setProperty("oryx-showdiamondmarker", false);
				else {
					var incomingShapes = shape.getIncomingShapes();
					
					if(!incomingShapes) {
						shape.setProperty("oryx-showdiamondmarker", true);
					}
					
					var incomingGateway = incomingShapes.find(function(aShape) {
						var foundGateway = aShape.getStencil().groups().find(function(group) {
							if(group == "Gateways") 
								return group;
						});
						if(foundGateway)
							return foundGateway;
					});
					
					if(!incomingGateway) 
						// show diamond on edge source
						shape.setProperty("oryx-showdiamondmarker", true);
					else
						// do not show diamond
						shape.setProperty("oryx-showdiamondmarker", false);
				}
				
				changed = true;
			}
		});
		
		if(changed) {this.facade.getCanvas().update();}
		
	},
	
	hashedPoolPositions : {},
	hashedLaneDepth : {},
	hashedBounds : {},
	
	/**
	 * Handler for layouting event 'layout.bpmn2_0.pool'
	 * @param {Object} event
	 */
	handleLayoutPool: function(event){
		
		
		var pool = event.shape;
		var selection = this.facade.getSelection(); 
		var currentShape = selection.include(pool) ? pool : selection.first();
		
		currentShape = currentShape || pool;
		
		this.currentPool = pool;
		
		// Check if it is a pool or a lane
		if (!(currentShape.getStencil().id().endsWith("Pool") || currentShape.getStencil().id().endsWith("Lane"))) {
			return;
		}
		
		if (!this.hashedBounds[pool.id]) {
			this.hashedBounds[pool.id] = {};
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
		var considerForDockers = allLanes.clone();
		
		var deletedLanes = [];
		var addedLanes = [];
		
		// Get all new lanes
		var i=-1;
		while (++i<allLanes.length) {
			if (!this.hashedBounds[pool.id][allLanes[i].id]){
				addedLanes.push(allLanes[i])
			}
		}
		
		if (addedLanes.length > 0){
			currentShape = addedLanes.first();
		}
		
		
		// Get all deleted lanes
		var resourceIds = $H(this.hashedBounds[pool.id]).keys();
		var i=-1;
		while (++i<resourceIds.length) {
			if (!allLanes.any(function(lane){ return lane.id == resourceIds[i]})){
				deletedLanes.push(this.hashedBounds[pool.id][resourceIds[i]]);
				selection = selection.without(function(r){ return r.id == resourceIds[i] });
			}
		}		
		
		var height, width, x, y;
		
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
			
			if (selection.length === 1 && this.isResized(pool, this.hashedPoolPositions[pool.id])) {
				var oldXY = this.hashedPoolPositions[pool.id].upperLeft();
				var xy = pool.bounds.upperLeft();
				var scale = 0;
				if (this.shouldScale(pool)){
					var old = this.hashedPoolPositions[pool.id];
					scale = old.height()/pool.bounds.height();
				}
			
				this.adjustLanes(pool, allLanes, oldXY.x - xy.x, oldXY.y - xy.y, scale);
			}
			
			// Set height from the pool
			height = this.adjustHeight(lanes, undefined, pool.bounds.height());
			// Set width from the pool
			width = this.adjustWidth(lanes, pool.bounds.width());		
		}
		
		/**‚
		 * Set width/height depending on containing lanes
		 */		
		else {
			
			// Reposition the pool if one shape is selected and the upperleft has changed
			if (selection.length === 1 && this.isResized(currentShape, this.hashedBounds[pool.id][currentShape.id])){
				var oldXY = this.hashedBounds[pool.id][currentShape.id].upperLeft();
				var xy = currentShape.absoluteXY();
				x = oldXY.x - xy.x;
				y = oldXY.y - xy.y;
				
				// Adjust all other lanes beneath this lane
				if (x||y){
					considerForDockers = considerForDockers.without(currentShape);
					this.adjustLanes(pool, this.getAllExcludedLanes(pool, currentShape), x, 0);
				}
				
				// Adjust all child lanes
				var childLanes = this.getLanes(currentShape, true);
				if (childLanes.length > 0 && this.shouldScale(currentShape)){
					var old = this.hashedBounds[pool.id][currentShape.id];
					var scale = old.height()/currentShape.bounds.height();
					this.adjustLanes(pool, childLanes, x, y, scale);
				}
			}
			
			// Get height and adjust child heights
			height = this.adjustHeight(lanes, currentShape);
			// Set width from the current shape
			width = this.adjustWidth(lanes, currentShape.bounds.width()+(this.getDepth(currentShape,pool)*30));
		}
		
		this.setDimensions(pool, width, height, x, y);
		
		
		if (this.facade.isExecutingCommands()){ 
			// Update all dockers
			this.updateDockers(considerForDockers, pool);
		}
		
		this.hashedBounds[pool.id] = {};
		
		var i=-1;
		while (++i < allLanes.length) {
			// Cache positions
			this.hashedBounds[pool.id][allLanes[i].id] = allLanes[i].absoluteBounds();
			
			this.hashedLaneDepth[allLanes[i].id] = this.getDepth(allLanes[i], pool);
			
			this.forceToUpdateLane(allLanes[i]);
		}
		
		this.hashedPoolPositions[pool.id] = pool.bounds.clone();
		
		
		// Update selection
		//this.facade.setSelection(selection);		
	},
	
	shouldScale: function(element){
		var childLanes = element.getChildNodes().findAll(function(shape){ return shape.getStencil().id().endsWith("Lane") })
		return childLanes.length > 1 || childLanes.any(function(lane){ return this.shouldScale(lane) }.bind(this)) 
	},
	
	isResized: function(shape, bounds){
		
		var oldB = bounds;
		//var oldXY = oldB.upperLeft();
		//var xy = shape.absoluteXY();
		
		return Math.round(oldB.width() - shape.bounds.width()) !== 0 || Math.round(oldB.height() - shape.bounds.height()) !== 0
		
	},
	
	adjustLanes: function(pool, lanes, x, y, scale){
		
		scale = scale || 0;

		// For every lane, adjust the child nodes with the offset
		lanes.each(function(l){
			
			l.getChildNodes().each(function(child){
				if (!child.getStencil().id().endsWith("Lane")){
					var cy = scale ? child.bounds.center().y - (child.bounds.center().y/scale) : -y;
					child.bounds.moveBy((x||0), -cy);
				}
			});
			
			this.hashedBounds[pool.id][l.id].moveBy(-(x||0), 0);
			if (scale) {
				l.isScaled = true;
			}
		}.bind(this))
		
	},
	
	getAllExcludedLanes: function(parent, lane){
		var lanes = [];
		parent.getChildNodes().each(function(shape){
			if ((!lane || shape !== lane) && shape.getStencil().id().endsWith("Lane")){
				lanes.push(shape);
				lanes = lanes.concat(this.getAllExcludedLanes(shape, lane));
			}
		}.bind(this));
		return lanes;
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
	
	setDimensions: function(shape, width, height, x, y){
		var isLane = shape.getStencil().id().endsWith("Lane");
		// Set the bounds
		shape.bounds.set(
				isLane 	? 30 : (shape.bounds.a.x - (x || 0)), 
				isLane 	? shape.bounds.a.y : (shape.bounds.a.y - (y || 0)), 
				width	? shape.bounds.a.x + width - (isLane?30:(x||0)) : shape.bounds.b.x, 
				height 	? shape.bounds.a.y + height - (isLane?0:(y||0)) : shape.bounds.b.y
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
		 				
			
			var offParent = this.hashedBounds[pool.id][parent.id] ||(includePool === true ? this.hashedPoolPositions[parent.id] : undefined);
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
		
		var hashed = this.hashedBounds[pool.id][lane.id] ||(includePool === true ? this.hashedPoolPositions[lane.id] : undefined);
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
	
	updateDockers: function(lanes, pool){
		
		var absPool = pool.absoluteBounds();
		var oldPool = (this.hashedPoolPositions[pool.id]||absPool).clone();
		
		var i=-1, j=-1, k=-1, l=-1, docker;
		var dockers = {};
		
		while (++i < lanes.length) {
			
			if (!this.hashedBounds[pool.id][lanes[i].id]) {
				continue;
			}
			
			var isScaled = lanes[i].isScaled;
			delete lanes[i].isScaled;
			var children = lanes[i].getChildNodes();
			var absBounds = lanes[i].absoluteBounds();
			var oldBounds = (this.hashedBounds[pool.id][lanes[i].id]||absBounds);
			//oldBounds.moveBy((absBounds.upperLeft().x-lanes[i].bounds.upperLeft().x), (absBounds.upperLeft().y-lanes[i].bounds.upperLeft().y));
			var offset = this.getOffset(lanes[i], true, pool);
			var xOffsetDepth = 0;

			var depth = this.getDepth(lanes[i], pool);
			if ( this.hashedLaneDepth[lanes[i].id] !== undefined &&  this.hashedLaneDepth[lanes[i].id] !== depth) {
				xOffsetDepth = (this.hashedLaneDepth[lanes[i].id] - depth) * 30;
				offset.x += xOffsetDepth;
			}
			
			j=-1;
			
			while (++j < children.length) {
				
				if (xOffsetDepth && !children[j].getStencil().id().endsWith("Lane")) {
					children[j].bounds.moveBy(xOffsetDepth, 0);
				}
				
				if (children[j].getStencil().id().endsWith("Subprocess")) {
					this.moveChildDockers(children[j], offset);
				}
				
				var edges = [].concat(children[j].getIncomingShapes())
					.concat(children[j].getOutgoingShapes())
					// Remove all edges which are included in the selection from the list
					.findAll(function(r){ return r instanceof ORYX.Core.Edge })

				k=-1;
				while (++k < edges.length) {			
					
					if (edges[k].getStencil().id().endsWith("MessageFlow")) {
						this.layoutEdges(children[j], [edges[k]], offset);
						continue;
					}
					
					l=-1;
					while (++l < edges[k].dockers.length) {
						
						docker = edges[k].dockers[l];
						
						if (docker.getDockedShape()||docker.isChanged){
							continue;
						}
					
					
						pos = docker.bounds.center();
						
						// Check if the modified center included the new position
						var isOverLane = oldBounds.isIncluded(pos);
						// Check if the original center is over the pool
						var isOutSidePool = !oldPool.isIncluded(pos);
						var previousIsOverLane = l == 0 ? isOverLane : oldBounds.isIncluded(edges[k].dockers[l-1].bounds.center());
						var nextIsOverLane = l == edges[k].dockers.length-1 ? isOverLane : oldBounds.isIncluded(edges[k].dockers[l+1].bounds.center());
						var off = Object.clone(offset);
						
						// If the 
						if (isScaled && isOverLane && this.isResized(lanes[i], this.hashedBounds[pool.id][lanes[i].id])){
							var relY = (pos.y - absBounds.upperLeft().y + off.y);
							off.y -= (relY - (relY * (absBounds.height()/oldBounds.height()))); 
						}
						
						// Check if the previous dockers docked shape is from this lane
						// Otherwise, check if the docker is over the lane OR is outside the lane 
						// but the previous/next was over this lane
						if (isOverLane){
							dockers[docker.id] = {docker: docker, offset:off};
						} 
						/*else if (l == 1 && edges[k].dockers.length>2 && edges[k].dockers[l-1].isDocked()){
							var dockedLane = this.getNextLane(edges[k].dockers[l-1].getDockedShape());
							if (dockedLane != lanes[i])
								continue;
							dockers[docker.id] = {docker: docker, offset:offset};
						}
						// Check if the next dockers docked shape is from this lane
						else if (l == edges[k].dockers.length-2 && edges[k].dockers.length>2 && edges[k].dockers[l+1].isDocked()){
							var dockedLane = this.getNextLane(edges[k].dockers[l+1].getDockedShape());
							if (dockedLane != lanes[i])
								continue;
							dockers[docker.id] = {docker: docker, offset:offset};
						}
												
						else if (isOutSidePool) {
							dockers[docker.id] = {docker: docker, offset:this.getOffset(lanes[i], true, pool)};
						}*/
						
					
					}
				}
						
			}
		}
		
		// Set dockers
		this.facade.executeCommands([new ORYX.Core.MoveDockersCommand(dockers)]);

	},
	
	moveBy: function(pos, offset){
		pos.x += offset.x;
		pos.y += offset.y;
		return pos;
	},
	
	getHashedBounds: function(shape){
		return this.currentPool && this.hashedBounds[this.currentPool.id][shape.id] ? this.hashedBounds[this.currentPool.id][shape.id] : shape.bounds.clone();
	},
	
	/**
	 * Returns a set on all child lanes for the given Shape. If recursive is TRUE, also indirect children will be returned (default is FALSE)
	 * The set is sorted with first child the lowest y-coordinate and the last one the highest.
	 * @param {ORYX.Core.Shape} shape
	 * @param {boolean} recursive
	 */
	getLanes: function(shape, recursive){
		
		// Get all the child lanes
		var lanes = shape.getChildNodes(recursive||false).findAll(function(node) { return (node.getStencil().id() === "http://b3mn.org/stencilset/bpmn2.0#Lane"); });
		
		// Sort all lanes by there y coordinate
		lanes = lanes.sort(function(a, b){
			
					// Get y coordinates for upper left and lower right
					var auy = Math.round(a.bounds.upperLeft().y);
					var buy = Math.round(b.bounds.upperLeft().y);
					var aly = Math.round(a.bounds.lowerRight().y);
					var bly = Math.round(b.bounds.lowerRight().y);
					
					// Get the old y coordinates
					var oauy = Math.round(this.getHashedBounds(a).upperLeft().y);
					var obuy = Math.round(this.getHashedBounds(b).upperLeft().y);
					var oaly = Math.round(this.getHashedBounds(a).lowerRight().y);
					var obly = Math.round(this.getHashedBounds(b).lowerRight().y);
					
					// If equal, than use the old one
					if (auy == buy && aly == bly) {
						auy = oauy; buy = obuy; aly = oaly; bly = obly;
					}
					
					// Check if upper left and lower right is completely above/below
					var above = auy < buy && aly < bly;
					var below = auy > buy && aly > bly;
					// Check if a is above b including the old values
					var slightlyAboveBottom = auy < buy && aly >= bly && oaly < obly;
					var slightlyAboveTop = auy >= buy && aly < bly && oauy < obuy;
					// Check if a is below b including the old values
					var slightlyBelowBottom = auy > buy && aly <= bly && oaly > obly;
					var slightlyBelowTop = auy <= buy && aly > bly && oauy > obuy;
					
					// Return -1 if a is above b, 1 if b is above a, or 0 otherwise
					return  (above || slightlyAboveBottom || slightlyAboveTop ? -1 : (below || slightlyBelowBottom || slightlyBelowTop ? 1 : 0))
				}.bind(this));
				
		// Return lanes
		return lanes;
	}
	
};

ORYX.Core.ResizeLanesCommand = ORYX.Core.Command.extend({

	construct: function(shape, parent, plugin) {
	
		this.facade  = plugin.facade;
		this.plugin  = plugin;
		this.shape	 = shape;
		this.changes;
		
		this.parent	= parent;
		this.lanes 	= plugin.getLanes(parent);
		this.lane 	= this.lanes.find(function(l,i){ return l.bounds.upperLeft().y >= this.shape.bounds.upperLeft().y || i == this.lanes.length-1 }.bind(this));

		this.shapeChildren = [];
		
		/*
		 * The Bounds have to be stored 
		 * separate because they would
		 * otherwise also be influenced 
		 */
		this.shape.getChildShapes().each(function(childShape) {
			this.shapeChildren.push({
				shape: childShape,
				bounds: {
				a: {
				x: childShape.bounds.a.x,
				y: childShape.bounds.a.y
			},
			b: {
				x: childShape.bounds.b.x,
				y: childShape.bounds.b.y
			}
			}
			});
		}.bind(this));

		if(this.lane) {
		
			this.shapeUpperLeft = this.shape.bounds.upperLeft().y;
			this.laneUppperLeft = this.lane.bounds.upperLeft().y;	
			this.parentHeight 	= this.parent.bounds.height(); 
		
		}
	},
	
	execute: function() {
		
		if(this.changes) {
			this.executeAgain();
			return;
		}

		/* 
		 * Rescue all ChildShapes of the deleted
		 * Shape into the lane that takes its 
		 * place 
		 */
		
		if(this.lane) {			
			
			var laUpL = this.laneUppperLeft;
			var shUpL = this.shapeUpperLeft;
						
			this.changes = $H({});
			
			if(laUpL > shUpL) {				
				this.lane.getChildShapes().each(function(childShape) {
					
					/*
					 * Cache the changes for rollback
					 */
					if(!this.changes[childShape.getId()]) {
						this.changes[childShape.getId()] = this.computeChanges(childShape, this.lane, this.lane, this.shape.bounds.height());
					}
					
					childShape.bounds.moveBy(0, this.shape.bounds.height());
					
				}.bind(this));
				
				this.shapeChildren.each(function(shapeChild) {
					shapeChild.shape.bounds.set(shapeChild.bounds);

					/*
					 * Cache the changes for rollback
					 */
					if(!this.changes[shapeChild.shape.getId()]) {
						this.changes[shapeChild.shape.getId()] = this.computeChanges(shapeChild.shape, this.shape, this.lane, 0);
					}
					
					this.lane.add(shapeChild.shape);
					
				}.bind(this));
				
			} else if(shUpL > laUpL){
				
				this.shapeChildren.each(function(shapeChild) {
					shapeChild.shape.bounds.set(shapeChild.bounds);					
					
					/*
					 * Cache the changes for rollback
					 */
					if(!this.changes[shapeChild.shape.getId()]) {
						this.changes[shapeChild.shape.getId()] = this.computeChanges(shapeChild.shape, this.shape, this.lane, this.lane.bounds.height());
					}
					
					shapeChild.shape.bounds.moveBy(0, this.lane.bounds.height());
					this.lane.add(shapeChild.shape);
					
				}.bind(this));
			}
		}
				
		if(this.lanes.length === 1) {
			/*
			 * There were only two lanes, so the 
			 * remaining lane will be resized and 
			 * the job is done
			 */
			var oldHeight 	= this.lane.bounds.height(); 	
			var newHeight 	= this.plugin.adjustHeight([this.lane], undefined, this.parentHeight);
			
			this.changes[this.lane.getId()] = this.computeChanges(this.lane, this.parent, this.parent, 0, oldHeight, newHeight);
						
			this.plugin.setDimensions(this.parent, this.parent.bounds.width(), newHeight);
							
		} else if(this.lanes.length > 1) {
			/*
			 * There was more than one lane
			 */
			var oldHeight	   = this.lane.bounds.height();				
			var adjustedHeight = this.lane.bounds.height() + this.shape.bounds.height();				
			var height 		   = this.plugin.adjustHeight([this.lane], undefined, adjustedHeight);
			
			this.changes[this.lane.getId()] = this.computeChanges(this.lane, this.parent, this.parent, 0, oldHeight, height);
			
			this.plugin.setDimensions(this.lane, this.lane.bounds.width(), height);
		}
		
		this.facade.getCanvas().update();
	},
	
	rollback: function() {
		
		this.changes.each(function(pair) {
			
			var parent 	  		= pair.value.oldParent;
			var shape  	  		= pair.value.shape;
			var parentHeight 	= pair.value.parentHeight;
			var oldHeight 		= pair.value.oldHeight;
			var newHeight 		= pair.value.newHeight;
			
			if(oldHeight) {
				var height = this.plugin.adjustHeight([shape], undefined, oldHeight);
				this.plugin.setDimensions(shape, shape.bounds.width(), height);
				this.plugin.setDimensions(parent, parent.bounds.width(), newHeight - oldHeight);
			}
			
			parent.add(shape);
			shape.bounds.moveTo(pair.value.oldPosition);	
			
		}.bind(this));
		
		this.facade.getCanvas().update();
		
	},
	
	executeAgain: function() {
		
		this.changes.each(function(pair) {
			var parent 	  = pair.value.newParent;
			var shape  	  = pair.value.shape;
			var newHeight = pair.value.newHeight;
			
			parent.add(shape);
			shape.bounds.moveTo(pair.value.newPosition);
			
			if(newHeight) {
				this.plugin.setDimensions(shape, shape.bounds.width(), newHeight);
			}
			
		}.bind(this));
		
		this.facade.getCanvas().update();
		
	},
	
	computeChanges: function(shape, oldParent, parent, yOffset, oldHeight, newHeight) {
		
		oldParent = this.changes[shape.getId()] ? this.changes[shape.getId()].oldParent : oldParent;
		var oldPosition = this.changes[shape.getId()] ? this.changes[shape.getId()].oldPosition : shape.bounds.upperLeft();
		
		var sUl = shape.bounds.upperLeft();
		
		var pos = {x: sUl.x, y: sUl.y + yOffset};
		
		var changes = {
			shape		: shape,
			parentHeight: oldParent.bounds.height(),
			oldParent	: oldParent,
			oldPosition	: oldPosition,
			oldHeight	: oldHeight,
			newParent	: parent,
			newPosition : pos,
			newHeight	: newHeight
		};
			
		return changes;
	}
	
});

	
ORYX.Plugins.BPMN2_0 = ORYX.Plugins.AbstractPlugin.extend(ORYX.Plugins.BPMN2_0);