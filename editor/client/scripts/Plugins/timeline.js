/**
 * Copyright (c) 2010, Matthias Kunze
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
if (!ORYX.Plugins) 
    ORYX.Plugins = {};

if (!ORYX.Config)
	ORYX.Config = {};

ORYX.Config.Timeline = {
	LAYOUT_ERROR_NO_DATE: "ORYX.Config.Timeline.LAYOUT_ERROR_NO_DATE"
}

ORYX.Plugins.Timeline = ORYX.Plugins.AbstractPlugin.extend({
	
	construct: function(facade, data) {
		ORYX_LOGLEVEL = 4;
		ORYX.Log.warn("Set ORYX_LOGLEVEL to " + ORYX_LOGLEVEL);
		
		if ("undefined" != typeof(data) && "undefined" != typeof(data.properties)) {
			// access configuration from plugin.xml, profile.xml
			// data.properties.<name> = <value>

			// register layout button
			this.facade = facade;
			this.bibliography = [];
			
			this.facade.offer({
				name:"Layout",
				functionality: function(){
				
/* read only mode -- does not seem to work
 * 					var events = [
						ORYX.CONFIG.EVENT_MOUSEDOWN,
						ORYX.CONFIG.EVENT_MOUSEUP,
						ORYX.CONFIG.EVENT_MOUSEOVER,
						ORYX.CONFIG.EVENT_MOUSEOUT,
						ORYX.CONFIG.EVENT_MOUSEMOVE,
						ORYX.CONFIG.EVENT_DBLCLICK,
						ORYX.CONFIG.EVENT_KEYDOWN,
						ORYX.CONFIG.EVENT_KEYUP
					];

					events.each(function(event){
						this.facade.disableEvent(event);
					}.bind(this))
*/
					
					try {
						this.layout();
					}
					catch(e) {
						if (ORYX.Config.Timeline.LAYOUT_ERROR_NO_DATE == e) {
							Ext.Msg.alert('Layout', 'Layouting aborted, due to invalid dates in the diagram'); 
						}
						else {
							Ext.Msg.alert('Layout', 'Layouting aborted, due to internal error'); 
							ORYX.Log.error(e);
						}
					}
					
/* read only mode -- does not seem to work
					events.each(function(event){
						this.facade.enableEvent(event);
					}.bind(this))
*/			
				}.bind(this),
				group: "Alignment",
				icon: ORYX.PATH + "images/auto_layout.png",
				description: "Automagic layouting. Orders events from left to right, according to their date, to form a tree, based on causal relations.",
				index: 0,
				minShape: 0,
				maxShape: 0});
		}
	},
	
	isEventNode: function(shape) {
		return "http://matthias-kunze.info/oryx/stencilsets/timeline#event" == shape.getStencil().id().toLowerCase();
	},
	
	isCauseEdge: function(shape) {
		return "http://matthias-kunze.info/oryx/stencilsets/timeline#cause" == shape.getStencil().id().toLowerCase();
	},
	
	getParentEvent: function(shape) {
		var parent = null;
		shape.getIncomingShapes(function(edge) {
			if (this.isCauseEdge(edge)) {
				edge.getIncomingShapes(function(node) {
					if (this.isEventNode(node)) {
	 					parent = node;
					}
				}.bind(this));
			}
		}.bind(this));
		return parent;
	},
	
	getChildEvents: function(shape) {
		var children = []
		shape.getOutgoingShapes(function(edge) {
			if (this.isCauseEdge(edge)) {
				edge.getOutgoingShapes(function(node) {
					if (this.isEventNode(node)) {
	 					children.push(node);
					}
				}.bind(this));
			}
		}.bind(this));
		return children;
	},
	
	dateFromString: function(str) {
		if (str.length == 0 || isNaN(Date.parse(str))) {
			throw ORYX.Config.Timeline.LAYOUT_ERROR_NO_DATE;	
		} 
		return Date.parse(str);
	},
	
	layout: function() {
		
		// dir contains a directory of layoutinformation for shapes
		var dir = {};
		
		// inspect the tree
		this.facade.getCanvas().getChildShapes().each(function(shape){
			if (this.isEventNode(shape)) {
				// if this is a leaf node, move up to the root and add
				// weights
				if (this.getChildEvents(shape).length == 0) {
					dir[shape.id] = {
						w: 1, 
						leaf: true,
						date: this.dateFromString(shape.properties["oryx-date"]),
						center: shape.bounds.center(),
						shape: shape,
						id: shape.id
					}
					
					var root = shape;
					var parent = this.getParentEvent(shape); 					
					while (parent) {
						dir[parent.id] = dir[parent.id] || {
							w:0,
							date: this.dateFromString(parent.properties["oryx-date"]),
							center: parent.bounds.center(),
							shape: parent,
							id: parent.id
						};
						dir[parent.id].w ++;
						root = parent
						parent = this.getParentEvent(parent);
					}
					dir[root.id].root = true;
				}
			}
		}.bind(this));
		
		var min_date = Infinity;
		var max_date = -Infinity;
		var min_x = Infinity;
		var max_x = -Infinity;
		var min_y = Infinity;
		
		for (i in dir) {
			min_date = Math.min(min_date, parseInt(dir[i].date));
			max_date = Math.max(max_date, parseInt(dir[i].date));
			min_x = Math.min(min_x, dir[i].center.x);
			max_x = Math.max(max_x, dir[i].center.x);
			min_y = Math.min(min_y, dir[i].center.y);
		};
		
		// calculates position of event within given space, relative to its date
		function getXCoordinate(id) {
			if (0 == max_date - min_date) {
				return min_x;
			}
			
			var d = parseInt(dir[id].date - min_date) * (max_x - min_x) / (max_date - min_date) + min_x;
			return d;
		}
		
		// preserve vertical ordering of events (within a subtree)
		function compare_fn(a,b) {
			if (a.center.y < b.center.y) return -1;
			if (a.center.y > b.center.y) return 1;
			return 0;
		}
		
		// storage for move commands
		var commands = [];
		
		// find root nodes
		var roots = [];	
		for (var i in dir) {
			if ("undefined" != dir[i].root && dir[i].root) {
				roots.push(dir[i]);
			}
		}
	
		// layout recursively, starting from root nodes
		(function _layout(nodes, y_offset) {

			nodes.sort(compare_fn);
			nodes.each(function(node) {
				
				var c = [];
				this.getChildEvents(node.shape).each(function(shape){
					c.push(dir[shape.id]);
				});
				if (c.length > 0) {
					_layout.apply(this, [c, y_offset]);
				}

				var coords = { //offset
					x: getXCoordinate(node.id),
					y: y_offset + 70 * (node.w - 1) / 2
				};

				y_offset = y_offset + 70 * (node.w)
				
				//collect commands
				commands.push(new ORYX.Core.Command.Move(
					[node.shape], 
					{
						x: coords.x - dir[node.id].center.x,
						y: coords.y - dir[node.id].center.y
					}, 
					null, //this.containmentParentNode, 
					null, //this.currentShapes, 
					this
				));
				
			}.bind(this))
		}.bind(this))(roots, min_y);
		
		// execute all moves as one command, 
		// allows for rollback of layouting with a single undo
		this.facade.executeCommands(commands);
		
	}
});