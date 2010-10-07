/**
 * Copyright (c) 2010
 * Alexander Koglin
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

if (!ORYX) 
	ORYX = new Object();
if (!ORYX.Plugins) 
	ORYX.Plugins = new Object();

ORYX.Plugins.EAMCartesianLayouter = ORYX.Plugins.AbstractPlugin.extend({
	facade: undefined,
	construct: function(facade){
		this.facade = facade;
		this.matrix = [];
		
		this.currentShapes = [];
		this.lastPersistedParentShapes = [];
		this.currentPersistedParentShapes = [];
		this.lastPersistedParentShapes = [];
		this.toMoveShapes = [];
		this.dragBounds = undefined;
		
		this._ignoreLayoutHandling = false;
		
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_LAYOUT_TABLE,
				this.handleLayout.bind(this));
		// TODO: HOW TO REGISTER ON CHANGE EVENT
		// this.facade.registerOnEvent(ORYX.CONFIG.EVENT_WILLCHANGE,
		//		this.handleConsistency.bind(this));

		
		this.facade.offer({
			'name' : "Layout-EAM-Cartesian",
			'description' : "Layout EAM Cartesian Model",
			'functionality' : this.layout.bind(this),
			'group' : "Layout",
			'icon' : ORYX.PATH + "images/auto_layout.png",
			'index' : 1,
			'minShape' : 0,
			'maxShape' : 0
		});
	},
	layout: function(){
		
		this.facade.raiseEvent({
            type: ORYX.CONFIG.EVENT_LOADING_ENABLE,
			text: ORYX.I18N.Layouting.doing
        });
		
		new Ajax.Request(ORYX.CONFIG.EAM_CARTESIAN_LAYOUTER, {
			method : 'POST',
			asynchronous : false,
			parameters : {
				data: this.facade.getSerializedJSON(),
				output: "coordinatesonly"
			},
			onFailure: function(request){
				Ext.Msg.alert("Layouting Error", "Error while layouting:!\n" + request.responseText);
				
				this.plugin._ignoreLayoutHandling = true;
				this.plugin.facade.updateSelection();					
				this.plugin._ignoreLayoutHandling = false;
				
            	this.facade.raiseEvent({type:ORYX.CONFIG.EVENT_LOADING_DISABLE});
			},
			onSuccess: function(request){

				//Ext.Msg.alert("Oryx", "New Layout arrived:!\n" + request.responseText);
				//ORYX.Log.debug("New Layout arrived:!\n" + request.responseText);
				
				var setLayoutCommandClass = ORYX.Core.Command.extend({
					construct: function(layoutArray, plugin){
						this.layoutArray = layoutArray;
						this.plugin = plugin;
						this.oldLayoutArray = [];
					},
					execute: function(){
						this.plugin._ignoreLayoutHandling = true;
						this.layoutArray.each(function(elem){
							/* get shape */
							var shape = this.plugin.facade.getCanvas().getChildShapeByResourceId(elem.id);
											
							/* save old layout for undo*/
							var oldLayout = {
								id : elem.id,
								bounds : shape.bounds.clone()
							};
							this.oldLayoutArray.push(oldLayout);
							
							/* set new bounds */
							var bound = elem.bounds.split(" ");
							
							shape.bounds.set(bound[0],bound[1],bound[2],bound[3]);

							if (shape.getParentShape() == this.plugin.facade.getCanvas()){
								shape.bounds.moveTo(oldLayout.bounds.upperLeft());
							}
							
							/* set new dockers */
							if(elem.dockers != null){
								this.plugin.setDockersBad(shape,elem.dockers);
							}
							
							
							shape.update();
						}.bind(this));
						
						this.plugin.createNewMatrixWithSlots();
						this.plugin.orderSlotsSideBySide();						
						this.plugin.facade.getCanvas().update();
						this.plugin.facade.updateSelection();					
						this.plugin._ignoreLayoutHandling = false;
					},
					rollback: function(){
						this.plugin._ignoreLayoutHandling = true;
						this.oldLayoutArray.each(function(elem){
							var shape = this.plugin.facade.getCanvas().getChildShapeByResourceId(elem.id);
							shape.bounds.set(elem.bounds);
							shape.update();
						}.bind(this));
						
						this.plugin.facade.getCanvas().update();
						this.plugin.facade.updateSelection();	
						this.plugin._ignoreLayoutHandling = false;
					}
				});
				
				var resp = request.responseText.evalJSON();
				
				if (resp instanceof Array && resp.size() > 0) {
					/* create command */
					var command = new setLayoutCommandClass(resp, this);
					/* execute command */
					this.facade.executeCommands([command]);
					
				}
							
            	this.facade.raiseEvent({type:ORYX.CONFIG.EVENT_LOADING_DISABLE});
			}.bind(this)
		})
	},
	setDockersBad: function(shape, dockers){
		var dockersString = "";
		dockers.each(function(p){
			dockersString += p.x + " " + p.y + " ";
		});
		dockersString += " # ";
		shape.deserialize([{
								prefix: 'oryx',
								name: 'dockers',
								value: dockersString
							}]);
	},
	setDockersGood: function(shape, dockers){
		if(elem.dockers.length == 1){
			/* docked event */
			
		}else{
			
			/* clear all except of the first and last dockers */
			var dockers = shape.getDockers().slice(1,-1);
			dockers.each(function(docker){
				shape.removeDocker(docker);
			});
			
			/* set first and last docker */
			var firstDocker = shape.getDockers()[0];
			if (firstDocker.getDockedShape()) {
				firstDocker.setReferencePoint(elem.dockers[0]);
			}
			else {
				firstDocker.bounds.moveTo(elem.dockers[0].x,elem.dockers[0].y);
			}
			firstDocker.refresh();
			
			var lastDocker = shape.getDockers()[1];
			if (lastDocker.getDockedShape()) {
				lastDocker.setReferencePoint(elem.dockers[elem.dockers.length - 1]);
			}
			else {
				lastDocker.bounds.moveTo(elem.dockers[elem.dockers.length - 1].x, elem.dockers[elem.dockers.length - 1].y);
			}
			lastDocker.refresh();
			
			/* add new dockers except of the first and last */
			var dockersToAdd = elem.dockers.slice(1,-1);
			dockersToAdd.each(function(dockerPoint){
				var newDocker = shape.createDocker();
				newDocker.parent = shape;
				newDocker.bounds.centerMoveTo(dockerPoint.x, dockerPoint.y);
				/*newDocker.setReferencePoint(dockerPoint);*/
				newDocker.update();
			});
		}		
	},
	createNewMatrixWithSlots: function()
	{		
		// PART1: INITIALIZING SLOT TYPES
		/* optimize the ordering of items by placing them side by side */
		var childNodes = this.facade.getCanvas().getChildNodes(false);
		
		/* set all the variables */
		var xslots=[]; var yslots=[]; var cellslots=[];
		
		//ORYX.Log.debug("length: "+childNodes.length);
		
		/* filter out all the non-slots and empty slots */
		childNodes = childNodes.filter(function(element){
			var isSlot = 
				(element.getStencil().id() === "http://b3mn.org/stencilset/eamcartesian#XAxisSlot") ||
				(element.getStencil().id() === "http://b3mn.org/stencilset/eamcartesian#YAxisSlot") ||
				(element.getStencil().id() === "http://b3mn.org/stencilset/eamcartesian#CellSlot");
			
			/* all the empty slots will be removed */
			//if (isSlot && (element.getChildNodes(true).length===0)) {
			//	this.facade.getCanvas().remove(element); return false; }
			
			return isSlot;
		});
		
		//ORYX.Log.debug("length: "+childNodes.length);
		
		// PART2: SORTING SLOTS BY PROPERTIES AND COORDINATES
		/* sort all slots by their Y and X values */
		childNodes = childNodes.sortBy(function(element){
			return element.properties["oryx-ycoord"] * 100000 +
				element.properties["oryx-xcoord"] * 1000 +
				element.bounds.upperLeft().y * 1 + element.bounds.upperLeft().y * 0.0001;
		});
		
		//ORYX.Log.debug("length: "+childNodes.length);
		
		/* sort them into seperate arrays for axis and cells */
		childNodes.each(function(element){
			if (element.getStencil().id() === "http://b3mn.org/stencilset/eamcartesian#XAxisSlot") { xslots.push(element); }
			else if (element.getStencil().id() === "http://b3mn.org/stencilset/eamcartesian#YAxisSlot") { yslots.push(element); }
			else if (element.getStencil().id() === "http://b3mn.org/stencilset/eamcartesian#CellSlot") { cellslots.push(element); }
		});
		
		//ORYX.Log.debug("lengths: "+xslots.length+" "+yslots.length+" "+cellslots.length);
		
		// PART3: INITIALIZING NEW TWO-AXIS-MATRIX WITH SLOTS
		/* write into matrix and rewrite their coordinates */
		this.matrix = []; this.matrix.push([]); this.matrix[0].push(null);
		
		for (var i=0;i<xslots.length;i++) {
			this.matrix[0].push(xslots[i]);
			xslots[i].properties["oryx-xcoord"] = i+1;
			xslots[i].properties["oryx-ycoord"] = 0;
		}
		
		for (var i=0;i<yslots.length;i++) {
			this.matrix.push([]); this.matrix[i+1].push(yslots[i]);
			yslots[i].properties["oryx-ycoord"] = i+1;
			yslots[i].properties["oryx-xcoord"] = 0;
		}
		
		for (var y=1;y<=yslots.length;y++) {
			for (var x=1;x<=xslots.length;x++) {
				var cellno = (y-1)*(xslots.length)+(x-1);
				
				if (cellno>=cellslots.length || cellslots[cellno]==undefined) {
					// create empty cell
					this.matrix[y].push(undefined);
				} else {
					// add the existing cell
					this.matrix[y].push(cellslots[cellno]);
					this.matrix[y][x].properties["oryx-xcoord"] = x;
					this.matrix[y][x].properties["oryx-ycoord"] = y;
				} 
				
			}
		}

	},
	orderSlotsSideBySide: function()
	{			

		var minX = 10, minY = 10, currentX = minX, currentY = minY;
		var marginX = 5; var marginY = 5; var usedWidth = 0; var usedHeight = 0;
		var rowsizes=[]; var colsizes=[];
		
		// PART4: MEASURING THE ROW AND COL SIZES
		/* measure the maximum row and column sizes */
		for (var i=0;i<this.matrix[0].length;i++) { colsizes.push(0); }
		for (var i=0;i<this.matrix.length;i++) { rowsizes.push(0); }
		
		//ORYX.Log.debug("lengths: "+colsizes.length+" "+rowsizes.length);
		
		for (var i=1;i<this.matrix[0].length;i++) {
			colsizes[i]=this.matrix[0][i].bounds.width();
			rowsizes[0]=Math.max(rowsizes[0],this.matrix[0][i].bounds.height());
		}
		
		for (var i=1;i<this.matrix.length;i++) {
			rowsizes[i]=this.matrix[i][0].bounds.height();
			colsizes[0]=Math.max(colsizes[0],this.matrix[i][0].bounds.width());
		}
		
		//ORYX.Log.debug("lengths: "+colsizes.length+" "+rowsizes.length);
		
		for (var y=1;y<this.matrix.length;y++) {
			for (var x=1;x<this.matrix[0].length;x++) {
				if (this.matrix[y][x] === undefined) continue;
				colsizes[x]= Math.max(colsizes[x],
						this.matrix[y][x].bounds.width());
				rowsizes[y]= Math.max(rowsizes[y],
						this.matrix[y][x].bounds.height());
			}
		}
		
		//ORYX.Log.debug("lengths: "+colsizes.length+" "+rowsizes.length);
						
		// PART5: SET THE NEW COORDINATES OF MATRIX SLOTS (WITH SIZES)
		/* place all the nodes line by line so that they not overlap */
		currentY=minY;
		for (var y=0;y<this.matrix.length;y++) {
			ORYX.Log.debug("Inner y: "+y);
			currentX=minX;
			for (var x=0;x<this.matrix[0].length;x++) {
				ORYX.Log.debug("Inner x: "+x);

				if (x===0 && y===0) {}
				else if (x===0 || y===0) {
					this.matrix[y][x].bounds.set(
							currentX, currentY,
							currentX+colsizes[x],
							currentY+rowsizes[y]);
					this.matrix[y][x].update();
					usedWidth = Math.max(usedWidth, currentX+colsizes[x]);
					usedHeight = Math.max(usedWidth, currentY+rowsizes[y]);
				}
				else {
					if (this.matrix[y][x] != undefined){
						this.matrix[y][x].bounds.set(
								currentX, currentY,
								currentX+colsizes[x],
								currentY+rowsizes[y]);
						this.matrix[y][x].update();
					//} else if (y<matrix.length-1 && 
					//			x<matrix[0].length-1) {
						/* TODO: fill them with empty cellslot placeholders with the right bounds */
						// matrix[y][x] = ...
					}
				}
				currentX += colsizes[x]+marginX;
			}
			currentY += rowsizes[y]+marginY;
		}
		
		/* TODO: add empty placeholders with bounds of [0,0]
		 * to the most right xslot and most down yslot */
		//this.matrix.push([undefined]); this.matrix[0].push(undefined);
		
		//ORYX.Log.debug("Matrix size: " + this.matrix.length + " x " + this.matrix[0].length);
		
		this.extendCanvasTo(usedWidth, usedHeight, marginX, marginY);
	},
	/**
	 * On the Selection-Changed
	 *
	 */
	onSelectionChanged: function(event) {
		if (event == null) return;
		
		var elements = event.elements;
		ORYX.Log.debug("Selection changed");
		
		// If there are no elements
		if(!elements || elements.length === 0) {
			// reset all variables
			this.currentShapes = [];
			this.lastPersistedParentShapes = this.currentPersistedParentShapes;
			this.currentPersistedParentShapes = [];
			this.toMoveShapes = [];
			this.dragBounds = undefined;
		} else {

			var oldElementCount = elements.length;
			
			// exclude specified elements with
			// stencil-group "slots" from selection
			elements = elements.filter(function(element){
				//alert(element.getStencil().roles());
				return !ORYX.Utils.contains(element.getStencil().roles(),
						"http://b3mn.org/stencilset/eamcartesian#slots");
			});

			if (oldElementCount > elements.length) {
				this.facade.setSelection(elements);
			}

			// Set the current Shapes
			this.currentShapes = elements;
			this.lastPersistedParentShapes = this.currentPersistedParentShapes;
			this.currentPersistedParentShapes = [];
			this.currentShapes.each(function(element){
				if (!ORYX.Utils.contains(this.currentPersistedParentShapes,element.parent)) {
					this.currentPersistedParentShapes.push(element.parent);
				}
			}.bind(this));

			// Get all shapes with the highest parent in object hierarchy (canvas is the top most parent)
			this.toMoveShapes = this.facade.getCanvas().getShapesWithSharedParent(elements);

			this.toMoveShapes = this.toMoveShapes.findAll( function(shape) { return shape instanceof ORYX.Core.Node && 
																			(shape.dockers.length === 0 || !elements.member(shape.dockers.first().getDockedShape()))});		
		
			// Calculate the area-bounds of the selection
			var newBounds;
			elements.each(function(value) {
				if(!newBounds) {
					newBounds = value.absoluteBounds();
				} else {
					newBounds.include(value.absoluteBounds());
                                }
			});

			// Set the new bounds
			this.dragBounds = newBounds;
		}
		
		return;
	},
	// this is called if something will be changed
	handleConsistency: function(event) {
		// item(s) will be inserted/moved/removed 
		var sourceSlots = this.lastPersistedParentShapes;
		var targetSlot = event.shape;
		
		// TODO: checks that operations are valid
		
		// TODO: checks that no duplicates are created in same slot
	},
	// this is called if something has changed
	handleLayout: function(event) {
		// if no event or no matrix or layouthandling is disabled, don't do this
		if (event==null || this._ignoreLayoutHandling === true || this.matrix.length == 0)  { return; }
				
		// item(s) were inserted/moved/removed 
		var sourceSlots = this.currentPersistedParentShapes;
		var targetSlot = (this.currentShapes.length==0) ? null : this.currentShapes[0].getParentShape();
		
		targetslotIsInMatrix = this.isItemInMatrix(targetSlot);
		targetslotIsEmpty = (targetSlot == null || targetSlot.getChildNodes().length===0);
		
		// TODO: check if slot is in matrix and must be deleted??
		if (targetslotIsEmpty) {
			ORYX.Log.debug("Slot is empty!");
		}
		else if (!targetslotIsEmpty) {
			ORYX.Log.debug("Item must be changed!");

			// new slot must be entered into matrix
			if (!targetslotIsInMatrix)
			{
				ORYX.Log.debug("Slot must be put into Matrix!");
				// Look for the next free xslot/yslot
				if (event.changeid === "XAxisSlot") { this.matrix[0].push(targetSlot); }
				if (event.changeid === "YAxisSlot") { this.matrix.push([targetSlot]); }
				// Look for the first undefined cellslot
				if (event.changeid === "CellSlot") {
					var stop = false;
					for (var y=1;y<this.matrix.length;y++){
						for (var x=1;x<this.matrix[0].length;x++){
							if (this.matrix[y][x]==undefined) { 
								this.matrix[y][x]=targetSlot;
								this.matrix[y][x].properties["oryx-xcoord"]=x;
								this.matrix[y][x].properties["oryx-ycoord"]=y;
								stop=true; break;
							}
						}
						if (stop === true) { break; }
					}
				}
			}

			/* depending on action rebuilt the matrix */
			if (event.changeid === "XAxisSlot" || event.changeid === "YAxisSlot"){
				// are children in the last cols/rows now? then fill col/row with items!
				if (this.matrix[0][this.matrix[0].length-1] !== undefined)
				{
					// TODO: create empty items in last col
					for (var y=1; y<this.matrix.length; y++){
						if (this.matrix[y].length<this.matrix[0].length){
							this.matrix[y].push(undefined);
						}
					}
				}
				if (this.matrix[this.matrix.length-1][0] !== undefined)
				{
					// TODO: create empty items in last row
					for (var x=this.matrix[this.matrix.length-1].length; x<this.matrix[0].length; x++){
						this.matrix[this.matrix.length-1].push(undefined);
					}
				}
				
				// need cols/rows be exchanged with each other? then exchange!
				if (sourceSlots.length===1 &&
						!ORYX.Utils.contains(sourceSlots,targetSlot)) {
					var exchangeCell;
	
					// exchange cols
					if (event.changeid === "YAxisSlot") {
						targetSlot.remove(this.currentShapes[0]);
						sourceSlots[0].add(this.currentShapes[0]);
						var newy = targetSlot.properties["oryx-ycoord"];
						var oldy = sourceSlots[0].properties["oryx-ycoord"];
						targetSlot.properties["oryx-ycoord"] = oldy;
						sourceSlots[0].properties["oryx-ycoord"] = newy;
						for (var x = 0; x<this.matrix[0].length; x++){
							exchangeCell = this.matrix[newy][x];
							this.matrix[newy][x] = this.matrix[oldy][x];
							this.matrix[oldy][x] = exchangeCell;
						}
					}
					// exchange rows
					if (event.changeid === "XAxisSlot") {
						targetSlot.remove(this.currentShapes[0]);
						sourceSlots[0].add(this.currentShapes[0]);
						var newx = targetSlot.properties["oryx-xcoord"];
						var oldx = sourceSlots[0].properties["oryx-xcoord"];
						targetSlot.properties["oryx-xcoord"] = oldx;
						sourceSlots[0].properties["oryx-xcoord"] = newx;
						for (var y = 0; y<this.matrix.length; y++){
							exchangeCell = this.matrix[y][newx];
							this.matrix[y][newx] = this.matrix[y][oldx];
							this.matrix[y][oldx] = exchangeCell;
						}
					}
				}
				
			}
			
			var colsToRemove = [];
			var rowsToRemove = [];
			var removeCurrent;
						
			// are there empty cols/rows now? then remove
			for (var x = this.matrix[0].length-1; x>0; x--){
				removeCurrent = true;
				for (var y = 0; y<this.matrix.length; y++){
					if (this.matrix[y][x] !== undefined &&
							this.matrix[y][x].getChildNodes(false).length>0) {
						removeCurrent = false; break;
					}
				}
				if (removeCurrent) { colsToRemove.push(x); }
			}
			for (var y = this.matrix.length-1; y>0; y--){
				removeCurrent = true;
				for (var x = 0; x<this.matrix[0].length; x++){
					if (this.matrix[y][x] !== undefined &&
							this.matrix[y][x].getChildNodes(false).length>0) {
						removeCurrent = false; break;
					}
				}
				if (removeCurrent) { rowsToRemove.push(y); }
			}
			for (var i = 0; i<colsToRemove.length; i++){
	            removeCurrent = colsToRemove[i];
				for (var y = this.matrix.length-1; y>=0; y--){
					this.facade.getCanvas().remove(this.matrix[y][removeCurrent]);
					this.matrix[y].remove(this.matrix[y][removeCurrent]);
				}
			}
			for (var i = 0; i<rowsToRemove.length; i++){
	            removeCurrent = rowsToRemove[i];
				for (var x = this.matrix[0].length-1; x>=0; x--){
					this.facade.getCanvas().remove(this.matrix[removeCurrent][x]);
				}
				this.matrix.remove(this.matrix[removeCurrent]);
			}
			
			// TODO: add cols/rows to the ends
		}
		
		// handle the layout of elements
		this.facade.raiseEvent({
            type: ORYX.CONFIG.EVENT_LOADING_ENABLE,
			text: ORYX.I18N.Layouting.doing
        });
		
		new Ajax.Request(ORYX.CONFIG.EAM_CARTESIAN_LAYOUTER, {
			method : 'POST',
			asynchronous : false,
			parameters : {
				data: this.facade.getSerializedJSON(),
				output: "coordinatesonly"
			},
			onFailure: function(request){
				Ext.Msg.alert("Layouting Error", "Error while layouting:!\n" + request.responseText);
				this.plugin._ignoreLayoutHandling = true;
				this.plugin.facade.updateSelection();					
				this.plugin._ignoreLayoutHandling = false;
            	this.facade.raiseEvent({type:ORYX.CONFIG.EVENT_LOADING_DISABLE});
			},
			onSuccess: function(request){
				var setLayoutCommandClass = ORYX.Core.Command.extend({
					construct: function(layoutArray, plugin){
						this.layoutArray = layoutArray;
						this.plugin = plugin;
						this.oldLayoutArray = [];
					},
					execute: function(){
						ORYX.Log.debug("DISABLE LAYOUT HANDLING");
						this.plugin._ignoreLayoutHandling = true;
						this.layoutArray.each(function(elem){
							/* get shape */
							var shape = this.plugin.facade.getCanvas().getChildShapeByResourceId(elem.id);
											
							/* save old layout for undo*/
							var oldLayout = {
								id : elem.id,
								bounds : shape.bounds.clone()
							};
							this.oldLayoutArray.push(oldLayout);
							
							/* set new bounds */
							var bound = elem.bounds.split(" ");
							
							shape.bounds.set(bound[0],bound[1],bound[2],bound[3]);

							if (shape.getParentShape() == this.plugin.facade.getCanvas())
								shape.bounds.moveTo(oldLayout.bounds.upperLeft());
							
							/* set new dockers */
							if(elem.dockers != null){
								this.plugin.setDockersBad(shape,elem.dockers);
							}
							
							
							shape.update();
						}.bind(this));
						
						this.plugin.orderSlotsSideBySide();						
						this.plugin.facade.getCanvas().update();
						this.plugin.facade.updateSelection();					
						ORYX.Log.debug("ENABLE LAYOUT HANDLING");
						this.plugin._ignoreLayoutHandling = false;
					},
					rollback: function(){
						this.plugin._ignoreLayoutHandling = true;
						this.oldLayoutArray.each(function(elem){
							var shape = this.plugin.facade.getCanvas().getChildShapeByResourceId(elem.id);
							shape.bounds.set(elem.bounds);
							shape.update();
						}.bind(this));
						
						this.plugin.facade.getCanvas().update();
						this.plugin.facade.updateSelection();	
						this.plugin._ignoreLayoutHandling = false;
					}
				});
				
				var resp = request.responseText.evalJSON();
				
				if (resp instanceof Array && resp.size() > 0) {
					/* create command */
					var command = new setLayoutCommandClass(resp, this);
					/* execute command */
					this.facade.executeCommands([command]);
					
				}
							
            	this.facade.raiseEvent({type:ORYX.CONFIG.EVENT_LOADING_DISABLE});
			}.bind(this)
		});
		
		this.onSelectionChanged();
	},
	extendCanvasTo: function(width, height, marginX, marginY) {
		// extend canvas size if necessary
		if (width + marginX > this.facade.getCanvas().bounds.width()) {
			this.facade.getCanvas().setSize({
				width: (width + marginX),
				height: this.facade.getCanvas().bounds.height()
			});
		}
		if (height + marginY > this.facade.getCanvas().bounds.height()) {
			this.facade.getCanvas().setSize({
				width: this.facade.getCanvas().bounds.width(),
				height: (height + marginY)
			});
		}
	},
	isItemInMatrix: function(item) {
		for (var y = 0; y<this.matrix.length; y++){
			for (var x = 0; x<this.matrix[0].length; x++){
				if (this.matrix[y][x]==item) return true;
			}
		}
		return false;
	}
});
