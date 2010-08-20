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
/**
 * Plugin to realize constraints of Adonis BPMS
 */
ORYX.Plugins.AdonisLayout= {

	/**
	 * Constructor
	 * @param {Object} Facade : the facade of the editor
	 */
	construct: function(facade) {
		this.facade = facade;

		this.facade.registerOnEvent('layout.adonis.swimlane', this.handleLayout.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_PROPERTY_CHANGED, this.handleRename.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_SHAPEADDED, this.handleCreate.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_LOADED, this.handleLoaded.bind(this));
		//TODO react on canvase resize and expand lanes accordingly
	},
	
	/**
	 * after load, the store is updated - assuming a loaded model is valid
	 * @param {Object} event: the loaded event
	 */
	handleLoaded: function(event){
		this.updateLaneStore();
	},
	
	/**
	 * looks for an sprecific property and returns true only if no other
	 * stencil in the array has a property with the same value
	 * @param {String} property : the property identifier
	 * @param {String} value : the value which should be tested
	 * @param {Array} sameClassStencils : the array of stencils
	 */
	existsStencilProperty: function(property, value, sameClassStencils){
		for (var i = 0; i < sameClassStencils.length; i++){
			var childName = sameClassStencils[i].properties[property];
			if (childName == value){
				return false;
			}
		}
		return true;
	},
	
	/**
	 * generate a unique value of a given property (currently using the
	 * standardValue + "(" + number + ")")
	 * @param {Object} shape : the given shape
	 * @param {String} property : the property identifier
	 * @param {String} standardValue : the standardValue
	 */
	unique: function(shape,property,standardValue){
		var sameClassStencils = this.facade.getCanvas().getChildNodes().findAll(
			function(childShape){
				return (childShape.resourceId != shape.resourceId
						&& childShape.getStencil().id() == shape.getStencil().id());
			});
		var uniqueName = standardValue;
		var counter = 1;
		while (!this.existsStencilProperty(property,uniqueName,sameClassStencils)){
			uniqueName = standardValue +" ("+ counter+")";
			counter++;
		}
		shape.setProperty(property,uniqueName);
	},
	
	/**
	 * Adonis needs unique names as object ids 
	 * - the handleCreate ensure this during stencil creation
	 * @param {Object} event: the triggered ShapeAdded event
	 */
	handleCreate: function (event){
		if (event.shape instanceof ORYX.Core.Node)
			if (event.shape.properties["oryx-name"] != null 
				&& event.shape.properties["oryx-name"] != undefined){
				this.unique(event.shape,"oryx-name",event.shape.properties["oryx-name"]);
			}
	},
	
	/**
	 * Adonis needs unique names as object ids 
	 * - the handleCreate ensure this during stencil rename
	 * @param {Object} event: the triggered property changed event
	 */
	handleRename: function(event){
		var elements = event.elements;
		var propId = event.name;
		var name = event.value;
		elements.each(function(shape){
			if (propId == "oryx-name" 
				&& shape.properties[propId] != null 
				&& shape.properties[propId] != undefined){
				this.unique(shape,propId,name);
			}
		}.bind(this));
		
	},
		
	//the following methods are responsible for holding a copy of bounds of the lanes in the model
	
	/**
	 * get the position of a lane in the bound store
	 * @param {Object} lane : the lane shape
	 * @return {Integer} -1 if not found, the index else
	 */
	indexInLanePosition: function(lane){
		for (var i = 0; i < this.lanePositions.length; i++){
			if (this.lanePositions[i].id == lane.resourceId){
				return i;
			}
		}
		return -1;
	},
	
	/**
	 * adds a lane to the bound store
	 * every lane occures only once
	 * @param {Shape} lane : the lane to be stored
	 */
	addLanePosition: function(lane){
		this.removeLanePosition(lane);
		this.lanePositions.push({
			id: lane.resourceId,
			bounds: lane.bounds.clone()
		});
	},	
	
	/**
	 * removes a lane from the bound store
	 */
	removeLanePosition: function(lane){
		var index = this.indexInLanePosition(lane);
		if ( index > -1){
			this.lanePositions.splice(index,1);
		}	
	},
	
	/**
	 * get the (old) bounds of a lane stored in the bound store
	 * @param {Shape} lane : the lane
	 * @return {Object} a bounds object or undefined if the lane was not stored
	 */
	getLanePosition: function(lane){
		return this.lanePositions[this.indexInLanePosition(lane)];
	},
	
	/**
	 * discards the old bound store and stores the current lane positions
	 */
	updateLaneStore: function(){
	
		this.lanePositions = [];
		var lanes = this.getLanesSorted();
		for (var i = 0; i < lanes.length; i++){
			this.addLanePosition(lanes[i]);
		}
		
		var debugString = "";
		for (var i = 0; i < this.getLanes().length; i++){
			debugString = debugString + " - " + this.getLanes()[i];
		}
		console.log("Adonis "+debugString);
	},
	
	/**
	 * get the lanes from the current canvas
	 * @return {Array} the lanes unordered
	 */
	getLanes: function(){
		var lanes = [];
		this.facade.getCanvas().getChildNodes().each(
			function(shape){
				if (shape.getStencil().id().indexOf('#swimlane') >= 0){
					lanes.push(shape);
				}
			}.bind(this));
		return lanes;
	},
	
	/**
	 * get the lanes from the canvas sorted
	 * @return {Array} the lanes sorted from left to right or top to down (depending on the existing lanes)
	 */
	getLanesSorted: function(){
		var lanes = this.getLanes();
		lanes = lanes.sort(function(first, second){
			// Get y coordinate
			var a;
			var b;
			if (this.isVertical(first)){
				a = Math.round(first.bounds.upperLeft().x);
				b = Math.round(second.bounds.upperLeft().x);
			}  else {
				a = Math.round(first.bounds.upperLeft().y);
				b = Math.round(second.bounds.upperLeft().y);
			}
			return  a < b ? -1 : (a > b ? 1 : 0);
		}.bind(this));
		return lanes;
	},
	
	/**
	 * extends a lane to the canvas bounds
	 * (vertical/horizontal lanes are extended to bottom/right)
	 * @param {Shape} lane : the lane to be extended
	 */
	extendLane: function(lane){
		var orientation = {
			x: this.isVertical(lane) ? 0 : 1,
			y: this.isVertical(lane) ? 1 : 0
		};
		//move the lane to the top
		lane.bounds.moveBy({
			x: (0 - lane.bounds.upperLeft().x) * orientation.x,
			y: (0 - lane.bounds.upperLeft().y) * orientation.y
		});
		//extend to the canvas bottom
		console.log("Adonis      Canvas "+this.facade.getCanvas().bounds);
		console.log("Adonis      Orientation "+orientation.x + " | "+ orientation.y);
		lane.bounds.extend({
			x: (this.facade.getCanvas().bounds.lowerRight().x - lane.bounds.lowerRight().x) * orientation.x,
			y: (this.facade.getCanvas().bounds.lowerRight().y - lane.bounds.lowerRight().y) * orientation.y
		});
	},
	
	/**
	 * shifts all lanes inclusive the extended lane to right
	 * @param {Shape} lane : the extended lane
	 */
	resizedLeft: function(eventLane){
		var lanes = this.getLanesSorted();
		var oldBounds = this.getLanePosition(eventLane).bounds;
		var orientation = {
			x: this.isVertical(eventLane) ? 1 : 0,
			y: this.isVertical(eventLane) ? 0 : 1
		};
		var offset = {
			x: (oldBounds.upperLeft().x - eventLane.bounds.upperLeft().x) * orientation.x, 
			y: (oldBounds.upperLeft().y - eventLane.bounds.upperLeft().y) * orientation.y
		};
		//move all lanes right of the extended lane to the right,
		for (var i = 0; i < lanes.length; i++){
			var oldLane = this.getLanePosition(lanes[i]);
			if ((this.isVertical(lanes[i])  && oldLane.bounds.center().x >= oldBounds.center().x)
				||(!this.isVertical(lanes[i]) && oldLane.bounds.center().y >= oldBounds.center().y)){
				lanes[i].bounds.moveBy(offset);
				this.extendLane(lanes[i]);
				this.liftUnderlyingStencils(lanes[i]);
			}
		}
	},
	
	/**
	 * shifts all lanes exclusive the extended lane to right
	 * @param {Shape} lane : the extended lane
	 */
	resizedRight: function(eventLane){
		var lanes = this.getLanesSorted();
		var oldBounds = this.getLanePosition(eventLane).bounds;
		var orientation = {
			x: this.isVertical(eventLane) ? 1 : 0,
			y: this.isVertical(eventLane) ? 0 : 1
		};
		var offset = {
			x: (eventLane.bounds.lowerRight().x - oldBounds.lowerRight().x) * orientation.x, 
			y: (eventLane.bounds.lowerRight().x - oldBounds.lowerRight().x) * orientation.y
		};
		//move all lanes right of the extended lane to the right,
		for (var i = 0; i < lanes.length; i++){
			var oldLane = this.getLanePosition(lanes[i]);
			if ((this.isVertical(lanes[i])  && oldLane.bounds.center().x > oldBounds.center().x)
				||(!this.isVertical(lanes[i]) && oldLane.bounds.center().y > oldBounds.center().y)){
				lanes[i].bounds.moveBy(offset);
				this.extendLane(lanes[i]);
				this.liftUnderlyingStencils(lanes[i]);
			}
		}
	},
	
	/**
	 * in case a lane moved its position, all other lanes have to
	 * adjust their positions
	 * @param {Shape} selectedLane : the moved lane
	 */ 
	moveForDragDrop: function(selectedLane){
		var lanes = this.getLanesSorted();
		var orientation = {
			x: this.isVertical(selectedLane) ? 1 : 0,
			y: this.isVertical(selectedLane) ? 0 : 1
		};
		
		var lastBounds = {
			x: 0,
			y: 0
		};
		var center = selectedLane.bounds.center();
		var eventIntegrated = false;
		
		//the lanes are sorted, so we can move one by one
		for (var i = 0; i < lanes.length; i++){
			var lane = lanes[i];
			lane.bounds.moveTo(lastBounds);
			this.extendLane(lanes[i]);
			lastBounds = {
					x: lane.bounds.lowerRight().x * orientation.x, 
					y: lane.bounds.lowerRight().y * orientation.y
			};
			
		}
	},
	
	/**
	 * adds shapes which are covered by the lane as the lane's children
	 * @param {Shape} lane : the covering lane
	 */ 
	liftUnderlyingStencils: function(lane){
		this.facade.getCanvas().getChildNodes().each(
			function(shape){
				if (shape !== lane
					&& lane.bounds.isIncluded(shape.bounds.upperLeft())
					&& lane.bounds.isIncluded(shape.bounds.lowerRight())
					&& shape.getParentShape() === this.facade.getCanvas()){
					lane.add(shape);
				}
			}.bind(this)
		 );
	},
	
	/**
	 * indicates if a lane is vertical or horizontal
	 * @param {Shape} lane : the lane
	 * @return true if it is vertical and false if it is horizontal
	 */
	isVertical: function(lane){
		return lane.getStencil().id().indexOf("vertical") >= 0;
	},
	
	/**
	 * indicates in which direction the lane moved
	 * @param {Shape} lane : the lane
	 * @return 
	 *		-1 => the lane moved to top left	
	 *       0 => the lane didn't move
	 *		 1 => the lane moved to bottom right
	 */
	resizeDirection: function(lane){
		if (this.indexInLanePosition(lane) < 0){
			return false;
		}
		var old = this.getLanePosition(lane).bounds;
		if (this.isVertical(lane)){
			if (lane.bounds.upperLeft().x == old.upperLeft().x 
				&& lane.bounds.lowerRight().x == old.lowerRight().x)
				return 0;
			if (lane.bounds.upperLeft().x == old.upperLeft().x 
				&& lane.bounds.lowerRight().x != old.lowerRight().x)
				return 1;
			if (lane.bounds.upperLeft().x != old.upperLeft().x 
				&& lane.bounds.lowerRight().x == old.lowerRight().x)
				return -1;
		} else {
			if (lane.bounds.upperLeft().y == old.upperLeft().y 
				&& lane.bounds.lowerRight().y == old.lowerRight().y)
				return 0;
			if (lane.bounds.upperLeft().y == old.upperLeft().y 
				&& lane.bounds.lowerRight().y != old.lowerRight().y)
				return 1;
			if (lane.bounds.upperLeft().x != old.upperLeft().x 
				&& lane.bounds.lowerRight().y == old.lowerRight().y)
				return -1;
		}
	},
	
	/**
	 * ensures the Adonis BPMS constraint, that all lanes are
	 * layouted side by side and only vertical or horizontal lanes are existing
	 * @param {Object} event : the event
	 */
	handleLayout: function(event){
		var canvas = this.facade.getCanvas();
		var eventLane = event.shape;
		var lanes = this.getLanesSorted();
		
		if (lanes.length > 0){
			if (eventLane.getStencil().id() != lanes[0].getStencil().id()){
				canvas.remove(eventLane);
				//a lane with wrong orientation was inserted - remove it
				return;
			}
		} 
		
		this.extendLane(eventLane);
		
		//handle resize events - none or resize to left or right
		if (this.indexInLanePosition(eventLane) >= 0){
			var direction = this.resizeDirection(eventLane);
			if (direction == 0){
				//lane did't move
				return;
			} else if (direction > 0){
				console.log("Adonis resize to right")
				//the lane was resized to bottom right
				this.resizedRight(eventLane);
				//it is possible that stencils are under the lane 
				//-> lift them als childs of the lane
				//this.liftUnderlyingStencils(eventLane);
				this.updateLaneStore();
				return;
			} else if (direction < 0){
				console.log("Adonis resize to left")
				//the lane was resized to top left
				this.resizedLeft(eventLane);
				this.updateLaneStore();
				return;
			 } 
		}
		console.log("Adonis drag drop")
		this.moveForDragDrop(eventLane);
		this.liftUnderlyingStencils(eventLane);
		this.updateLaneStore();
	}
};



ORYX.Plugins.AdonisLayout = ORYX.Plugins.AbstractPlugin.extend(ORYX.Plugins.AdonisLayout);
