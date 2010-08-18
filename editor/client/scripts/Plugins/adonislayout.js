/**
 * Copyright (c) 2010
 * Christian Kieschnick
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

ORYX.Plugins.AdonisLayout = {


	/**
	 *	Constructor
	 *	@param {Object} Facade: The Facade of the Editor
	 */
	construct: function(facade) {
		this.facade = facade;
		this.lanePositions = [];
		this.orientation = "vertical";
		console.log("Adonis facade constructed");

		this.facade.registerOnEvent('layout.adonis.swimlane.horizontal', this.handleVertical.bind(this));
		this.facade.registerOnEvent('layout.adonis.swimlane.vertical', this.handleVertical.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_SHAPEADDED,this.stub.bind(this));
	},
	
	stub: function(event){
		var debugString = "";
		for (var a in event){
			debugString += " " + a;
		}
		console.log(debugString);
	},
		
	indexInLanePosition: function(lane){
		for (var i = 0; i < this.lanePositions.length; i++){
			if (this.lanePositions[i].id == lane.resourceId){
				return i;
			}
		}
		return -1;
	},
	
	addLanePosition: function(lane){
		this.removeLanePosition(lane);
		this.lanePositions.push({
			id: lane.resourceId,
			bounds: lane.bounds.clone()
		});
	},	
	
	removeLanePosition: function(lane){
		var index = this.indexInLanePosition(lane);
		if ( index > -1){
			this.lanePositions.splice(index,1);
		}	
	},
	
	getLanePosition: function(lane){
		return this.lanePositions[this.indexInLanePosition(lane)];
	},
	
	getLaneById: function(id){
		var lane;
		this.facade.getCanvas().getChildNodes().each(
			function(shape){
				if (shape.resourceId == id){
					lane = shape;
				}
			}.bind(this)
		);
		return lane	;
	},
	
	getLanes: function(){
		var lanes = [];
		this.facade.getCanvas().getChildNodes().each(
			function(shape){
				if (shape.id.indexOf('http://b3mn.org/stencilset/adonis#swimlane' >= 0)){
					lanes.push(shape);
				}
			}.bind(this)
		);
		return lanes;
	},
	
	stub: function(event){
		console.warn("horizontal handling not implemented");
	},

	
	handleVertical: function(event){
		console.log("Adonis event triggered");
		var canvas = this.facade.getCanvas();
		var eventLane = event.shape;
		var lanes = this.getLanes();
		
		console.log("Adonis "+eventLane+" "+eventLane.bounds);
		
		if (this.indexInLanePosition(eventLane) >= 0){
			var oldBounds = this.getLanePosition(eventLane).bounds;
			console.log("Adonis "+eventLane+" "+oldBounds+" old");
		
			if (eventLane.bounds.a.x == oldBounds.a.x 
				&& eventLane.bounds.a.y == oldBounds.a.y
				&& eventLane.bounds.b.x == oldBounds.b.x
				&& eventLane.bounds.b.y == oldBounds.b.y ){
				console.log("Adonis - lane did't move");
				return;
			}
			var lastBounds = {x:0,y:0};
			//move lanes to fill spaces
			for (var i = 0; i < lanes.length; i++){
			
				if (lanes[i].resourceId !== eventLane.resourceId){
					console.log("Adonis     move "+lanes[i]+" "+lanes[i].bounds);
					var xOffset = oldBounds.b.x - oldBounds.a.x;
					//case the lane is in front off all actions
					if (lanes[i].bounds.b.x <= oldBounds.a.x
						&& lanes[i].bounds.b.x < eventLane.bounds.a.x){
						//do nothing - your are not affected
						console.log("Adonis     move "+lanes[i]+" "+lanes[i].bounds);
						if (lastBounds.x < lanes[i].bounds.b.x){
							lastBounds = lanes[i].bounds.b;
						}
					} else if (lanes[i].bounds.a.x >= oldBounds.b.x
						&& lanes[i].bounds.a.x < eventLane.bounds.a.x
						&& lanes[i].bounds.b.x < eventLane.bounds.b.x){
						console.log("Adonis     move "+lanes[i]+" "+lanes[i].bounds);
						//move the lane to the left
						console.log("Adonis     move left "+lanes[i]+" "+lanes[i].bounds);
						lanes[i].bounds.moveBy({	x: - xOffset, y: 0	});
						console.log("Adonis     move "+lanes[i]+" "+lanes[i].bounds+" finished");
						if (lastBounds.x < lanes[i].bounds.b.x){
							lastBounds = lanes[i].bounds.b;
						}
					}
				}
			}	
			console.log("lastBounds ( "+lastBounds.x+" | "+lastBounds.y+" )");
			eventLane.bounds.moveBy({
				x: lastBounds.x - eventLane.bounds.a.x,
				y: 0
			});
			eventLane.bounds.extend({
				x: 0,
				y: canvas.bounds.b.y - eventLane.bounds.b.y
			});
			console.log("Adonis bounds new "+eventLane.bounds);
			for (var i = 0; i < lanes.length; i++){
			
				if (lanes[i].resourceId !== eventLane.resourceId){
				
					var xOffset = oldBounds.b.x - oldBounds.a.x;
					//case the lane is in front off all actions
					if (lanes[i].bounds.b.x < oldBounds.a.x
						&& lanes[i].bounds.a.x >= eventLane.bounds.a.x
						&& lanes[i].bounds.b.x >= eventLane.bounds.b.x){
						//move the lane to the right
						console.log("Adonis     move "+lanes[i]+" "+lanes[i].bounds);
						console.log("Adonis     move right "+lanes[i]+" "+lanes[i].bounds);
						lanes[i].bounds.moveBy({	x: xOffset, y: 0	});
						console.log("Adonis     move "+lanes[i]+" "+lanes[i].bounds+" finished");
						if (lastBounds.x < lanes[i].bounds.b.x){
							lastBounds = lanes[i].bounds.b;
						}
					} else {
						//there is nothing to do ... the action is on the front side
						console.log("Adonis     move "+lanes[i]+" "+lanes[i].bounds);
						if (lastBounds.x < lanes[i].bounds.b.x){
							lastBounds = lanes[i].bounds.b;
						}
					}
				}
			}
			
			this.lanePositions = [];
			for (var i = 0; i < lanes.length; i++){
				this.addLanePosition(lanes[i]);
			}
			
			return;
		}

		
		
		//get the right end of the line of lanes
		var xOffset = 0;
		for (var i = 0; i < lanes.length; i++){
			if (lanes[i].resourceId != eventLane.resourceId){
				console.log("Adonis    calc offset "+lanes[i]+" "+lanes[i].bounds);
				if (lanes[i].bounds.b.x > xOffset && lanes[i].bounds.b.x < eventLane.bounds.b.x){
					xOffset = lanes[i].bounds.b.x;
				}
			}
		}
		console.log("Adonis offset "+xOffset);
		
		//move the lane to the right position and extend it to the canvas bounds
		eventLane.bounds.moveBy({	
			x: xOffset - eventLane.bounds.a.x,
			y: 0 - eventLane.bounds.a.y
		});
		eventLane.bounds.extend({
			x: 0,
			y: canvas.bounds.b.y - eventLane.bounds.b.y
		});
		
		this.lanePositions = [];
		for (var i = 0; i < lanes.length; i++){
			this.addLanePosition(lanes[i]);
		}
		
		
		console.log("Adonis bounds new "+eventLane.bounds);
				
		var debugString = "";
		for (var i = 0; i < this.getLanes().length; i++){
			debugString = debugString + " - " 
					+ this.getLanes()[i];
		}
		console.log("Adonis "+debugString);

		
	}
};


ORYX.Plugins.AdonisLayout = ORYX.Plugins.AbstractPlugin.extend(ORYX.Plugins.AdonisLayout);
