/**
 * Copyright (c) 2010
 * Kai Höwelmeyer
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
	ORYX.Plugins = {};
	
ORYX.Plugins.Patterns = ORYX.Plugins.AbstractPlugin.extend({
	
	facade : undefined,
	buttonVisible : false,
	button : undefined,
	
	construct: function(facade) {
		
		//call superclass constructor
		arguments.callee.$.construct.apply(this, arguments);
		
		//todo remove the explicit this.facade.getCanvas(). is handled in beforeDragOver
		this._currentParent /*= this.facade.getCanvas()*/;
		this._canContain = undefined;
		this._canAttach = undefined;
		
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_SELECTION_CHANGED, this.togglePatternButton.bind(this));
		
		//adding of "capture as pattern"-func   // TODO I18N
		this.facade.offer({
			name: "Selection as pattern",
			functionality: this.selAsPattern.bind(this),
			group: "Patterns", 
			description: "Captures the current selection as a pattern for reuse",
			minShape: 2,
			icon: ORYX.PATH + "images/pattern_add.png"
		});
		
		//create rootNode for patternrepository   // TODO I18N
		this.patternRoot = new Ext.tree.TreeNode({
			cls: 'headerShapeRep',
			text: "Patterns",
			iconCls: 'headerShapeRepImg',
			expandable: true,
			allowDrag: false,
			allowDrop: false
		});
				
		//create Patternpanel as ext-tree-panel
		this.patternPanel = new Ext.tree.TreePanel({
			iconCls: 'headerShapeRepImg',
			cls:'shaperespository',
			root: this.patternRoot,			
			lines: false,
			rootVisible: true
		});
		
		//add pattern panel
		var region = this.facade.addToRegion("West", this.patternPanel, null);
			
		//creating a dragzone
		//analogous to shaperepository
		var dragZone = new Ext.dd.DragZone(this.patternRoot.getUI().getEl(), {shadow: !Ext.isMac});
		
		//register drag and drop function, curry in the dragzone
		dragZone.afterDragDrop = this.afterDragDrop.bind(this, dragZone);
		dragZone.beforeDragOver = this.beforeDragOver.bind(this, dragZone);
		dragZone.beforeDragEnter = function(){
			this._lastOverElement = false;
			return true;
		}.bind(this);
		
		this.createPatternButton();
		
		//TODO register on reload event for stencil sets! don't forget that!
		this.loadAllPattern();
		
	},
	
	createPatternButton: function() {
		// graft the button.
		this.button = ORYX.Editor.graft("http://www.w3.org/1999/xhtml", $(null),
			['div', {'class': 'Oryx_button'}]);
		
		var imgOptions = {src: ORYX.PATH + "images/pattern_add.png"};
		/*if(this.option.msg){
			imgOptions.title = this.option.msg;
		}*/

		// graft and update icon (not in grafting for ns reasons).
		ORYX.Editor.graft("http://www.w3.org/1999/xhtml", this.button,
				['img', imgOptions]);
				
		//maybe not the right container...
		this.facade.getCanvas().getHTMLContainer().appendChild(this.button);
		
		this.hidePatternButton();
		
		this.button.addEventListener(ORYX.CONFIG.EVENT_MOUSEOVER, this.buttonHover.bind(this), false);
		this.button.addEventListener(ORYX.CONFIG.EVENT_MOUSEOUT, this.buttonUnHover.bind(this), false);
		this.button.addEventListener(ORYX.CONFIG.EVENT_MOUSEDOWN, this.buttonActivate.bind(this), false);
		this.button.addEventListener(ORYX.CONFIG.EVENT_MOUSEUP, this.buttonHover.bind(this), false);
		this.button.addEventListener('click', this.buttonTrigger.bind(this), false);
	},
	
	togglePatternButton: function(options) {
		var selection = this.facade.getSelection();
		
		if (!this.buttonVisible) {
			//remove magic number!
			if(selection.size() >= 2) this.showPatternButton();
		} else {
			if(selection.size() >= 2) {
				this.relocatePatternButton();
			} else {
				this.hidePatternButton();
			}
		}
	},
	
	showPatternButton: function() {
		this.relocatePatternButton();
		this.button.style.display = "";
		this.buttonVisible = true;
		this.facade.getCanvas().update();
	},
	
	hidePatternButton: function() {
		this.button.style.display = "none";
		this.buttonVisible = false;
		this.facade.getCanvas().update();
	},
		
	relocatePatternButton: function() {
		var selection = this.facade.getSelection();
		
		//get bounds of selection
		var bounds = null;
		selection.each(function(shape) {
			if(!bounds) {
				bounds = shape.absoluteBounds();
			} else {
				bounds.include(shape.absoluteBounds());
			}
		});
		
		//position for button
		var buttonPos = {
			x: bounds.upperLeft().x + bounds.width() + ORYX.CONFIG.SELECTED_AREA_PADDING,
			y: bounds.upperLeft().y
		};
		
		this.button.style.left = buttonPos.x + "px";
		this.button.style.top = buttonPos.y + "px";
		
	},
	
	showButtonOpaque: function() {
		this.button.style.opacity = 1.0;
	},
	
	showButtonTransparent: function() {
		this.button.style.opacity = 0.5;
	},
	
	buttonActivate: function() {
		this.button.addClassName('Oryx_down');
	},
	
	buttonHover: function() {
		this.button.addClassName('Oryx_hover');
	},
	
	buttonUnHover: function() {
		if(this.button.hasClassName('Oryx_down'))
			this.button.removeClassName('Oryx_down');

		if(this.button.hasClassName('Oryx_hover'))
			this.button.removeClassName('Oryx_hover');
	},
	
	buttonTrigger: function(evt) {
		this.selAsPattern();
	},
			
	selAsPattern: function() {
		var selection = this.facade.getSelection();
		
		//json everything
		var jsonSel = selection.collect(function(element) {
			return element.toJSON();
		});
		
		//clean it up
		jsonSel = this.removeDanglingEdges(jsonSel);
		jsonSel = this.removeObsoleteReferences(jsonSel);
		
		/*//delete all patterns
		selection.each(function(element) {
			this.facade.deleteShape(element);
		}.bind(this));
		*/
		
		this.addNewPattern(jsonSel);	
		
	},
	
	loadAllPattern: function() {
		var ssNameSpace = $A(this.facade.getStencilSets()).flatten().flatten()[0];
		var repos = new ORYX.Plugins.Patterns.PatternRepository(ssNameSpace, this.addPatternNodes.bind(this)); //TODO initialize only one of the repos and make actually update possible!
		
	},
	
	/**
	* Adds a new pattern to the server for current stencilset and adds pattern node in pattern repository
	*/
	addNewPattern: function(serPattern) {
		var ssNameSpace = $A(this.facade.getStencilSets()).flatten().flatten()[0];
		var opt = {
			serPattern: serPattern,
			description: "New Pattern",
			imageUrl: undefined,
			id: undefined,
			ssNameSpace: ssNameSpace
		};
		var pattern = new ORYX.Plugins.Patterns.Pattern(opt);
		
		var repos = new ORYX.Plugins.Patterns.PatternRepository(ssNameSpace, this.addPatternNodes);
		
		repos.saveNewPattern(pattern);
	},
	
	addPatternNodes: function(patternArray) {
		patternArray.each(function(pattern){
			this.addPatternNode(pattern);
		}.bind(this));
	},
	
	/**
	* Add the nodes for the supplied pattern to pattern repository
	*/
	addPatternNode: function(pattern) {
		//add the pattern subnode
		var newNode = new Ext.tree.TreeNode({
			leaf: true,
			text: pattern.description,  
			iconCls: 'ShapeRepEntreeImg',
			cls: 'ShapeRepEntree',
			icon:  ORYX.PATH + "images/pattern_add.png",
			allowDrag: false,
			allowDrop: false,
			attributes: pattern
		});
		
		this.patternRoot.appendChild(newNode);
		newNode.render();	
		
		var ui = newNode.getUI();
		
		/*//Set the tooltip
		//Warum NS nutzen, wenn dann kein NS übergeben wird?!?!
		ui.elNode.setAttributeNS(null, "title", "Testdescription");*/
		
		//register the pattern on drag and drop
		Ext.dd.Registry.register(ui.elNode, {
			node: ui.node,
			handles: [ui.elNode, ui.textNode].concat($A(ui.elNode.childNodes)), //has one undefined element! fix that!
			isHandle: false,
			type: "and-split" //this does not make sense!
		});
		
		//make node editable
		var treeEditor = new Ext.tree.TreeEditor(this.patternPanel, {
			constrain: true, //constrains editor to the viewport
			completeOnEnter: true,
			editDelay: 500, //number of max ms between to clicks of double click
			ignoreNoChange: true
		});
		
		treeEditor.on("beforecomplete", this.beforeComplete.bind(this));

		this.patternPanel.on({
			scope : this,
			//beforeclick : this.beforeNodeClick.curry(treeEditor),
			dblclick : this.onNodeDblClick.bind(this, treeEditor) //TODO fix after editing description has to be changed as well!
		});
		
		this.patternRoot.expand();	
	},
	
	onNodeDblClick : function(treeEditor, node, e) {
		e.stopEvent(); //why?
		treeEditor.triggerEdit(node);
	},
	
	beforeComplete: function(editor, value, startValue) {
		var pattern = editor.editNode.attributes.attributes;
		return pattern.setDescription(value);
	},
	
	afterDragDrop: function(dragZone, target, event, id) {
		
		this._lastOverElement = undefined;
		
		//Hide the highlighting
		//do i really need this???????
		this.facade.raiseEvent({type: ORYX.CONFIG.EVENT_HIGHLIGHT_HIDE, highlightId:'patternRepo.added'});
		this.facade.raiseEvent({type: ORYX.CONFIG.EVENT_HIGHLIGHT_HIDE, highlightId:'patternRepo.attached'});
		
		//Check if drop is allowed
		var proxy = dragZone.getProxy();
		if(proxy.dropStatus == proxy.dropNotAllowed) {return;}
		
		//check if there is a current Parent
		//what do these lines do?
		//if(!this._currentParent) {return;}
				
		//save it elsewhere!
		var templatePatternShapes = Ext.dd.Registry.getHandle(target.DDM.currentTarget).node.attributes.attributes.serPattern;
		
		//renew resourceIds
		var patternShapes = this.renewResourceIds(templatePatternShapes);
		
		//copies positionmanagement from shape repository
		var xy = event.getXY();
		var pos = {x: xy[0], y: xy[1]};
		
		var a = this.facade.getCanvas().node.getScreenCTM();

		// Correcting the UpperLeft-Offset
		pos.x -= a.e; pos.y -= a.f;
		// Correcting the Zoom-Faktor
		pos.x /= a.a; pos.y /= a.d;
		// Correcting the ScrollOffset
		pos.x -= document.documentElement.scrollLeft;
		pos.y -= document.documentElement.scrollTop;
		// Correct position of parent  
		// brauch ich das???
		/*var parentAbs = this._currentParent.absoluteXY();
		pos.x -= parentAbs.x;
		pos.y -= parentAbs.y;*/
		
		
		var centralPoint = this.findCentralPoint(patternShapes);
		
		patternShapes = this.transformPattern(patternShapes, centralPoint, pos);
		
		var commandClass = ORYX.Core.Command.extend({
			construct : function(patternShapes, facade, centralPoint, pos, plugin){
				this.patternShapes = patternShapes;
				this.facade = facade;
				this.centralPoint = centralPoint;
				this.pos = pos;
				this.shapes;
				this.plugin = plugin;
			},
			
			execute : function() {
				
				//add the shapes
				this.shapes = this.facade.getCanvas().addShapeObjects(this.patternShapes, this.facade.raiseEvent);

				/*//calc difference in positions
				var transVector = {
					x : this.pos.x - this.centralPoint.x,
					y : this.pos.y - this.centralPoint.y
				};

				//recursively change the position		
				var posChange = function(transVector, shapes) {
					shapes.each(function(transVector, shape) {
						shape.bounds.moveBy(transVector);
						posChange(transVector, shape.getChildren());
					}.bind(this, transVector));
				};

				posChange(transVector, this.shapes);*/
				
				this.plugin.doLayout(this.shapes);
				
				this.facade.setSelection(this.shapes);
				this.facade.getCanvas().update();
				this.facade.updateSelection();
			},
			
			rollback: function() {
				var selection = this.facade.getSelection();
				
				//delete all shapes
				this.shapes.each(function(shape, index){
					this.facade.deleteShape(shape);
					this.facade.setSelection(selection.without(shape));
				}.bind(this));
				
				this.facade.getCanvas().update();
				this.facade.updateSelection();
				
			}
		});
		
	//	var position = this.facade.eventCoordinates(event.browserEvent);
		
		var command = new commandClass(patternShapes, this.facade, centralPoint, pos, this);
		
		this.facade.executeCommands([command]);
	},
	
	transformPattern: function(patternShapes, oldPos, newPos) {
		
		var transVector = {
			x : newPos.x - oldPos.x,
			y : newPos.y - oldPos.y
		};
		
		
		//recursively change the position		
		var posChange = function(transVector, shapes) {
			shapes.each(function(transVector, shape) {
				shape.bounds.lowerRight.x += transVector.x;
				shape.bounds.lowerRight.y += transVector.y;
				shape.bounds.upperLeft.x += transVector.x;
				shape.bounds.upperLeft.y += transVector.y;
				
				//except last and first docker all have relative positions.
				var counter = 0;
				var max = shape.dockers.size();
				
				for(var i=1; i<shape.dockers.size()-1; i++) {
					shape.dockers[i].x += transVector.x;
					shape.dockers[i].y += transVector.y;
				}
				
/*				shape.dockers.each(function(transVector, counter, max, docker) {
					counter++;
					if (counter == 1 || counter == max) return;
					docker.x += transVector.x;
					docker.y += transVector.y;
				}.bind(this, transVector, counter, max));
*/				
				posChange(transVector, shape.childShapes);
			}.bind(this, transVector));
		};
		
		posChange(transVector, patternShapes);
		
		return patternShapes;
	},
	
	findCentralPoint: function(shapeArray) {
		
		if(shapeArray.size() === 0) return;
		
		var initBounds = new ORYX.Core.Bounds(shapeArray[0].bounds.upperLeft, shapeArray[0].bounds.lowerRight);
		
		var shapeBounds = shapeArray.inject(initBounds, function(bounds, shape) {
			var add = new ORYX.Core.Bounds(shape.bounds.upperLeft, shape.bounds.lowerRight);
			bounds.include(add);
			return bounds;
		});
		
		return shapeBounds.center();
		
		/*//hier sollte vllt. noch der mittelpunkt vom shape berechnet werden?
		var sumX = shapeArray.inject(0, function(acc, shape) {
			return acc + shape.bounds.upperLeft.x;
		});
		
		var sumY = shapeArray.inject(0, function(acc, shape) {
			return acc + shape.bounds.upperLeft.y;
		});
		
		var meanX = sumX / shapeArray.size();
		var meanY = sumY / shapeArray.size();
		
		return {
			x: meanX,
			y: meanY
		};*/
		
	},
	
	beforeDragOver: function(dragZone, target, event) {
		/*
		var coord = this.facade.eventCoordinates(event.browserEvent);
		var aShapes = this.facade.getCanvas().getAbstractShapesAtPosition( coord );
		
		if(aShapes.length <= 0) {
			var pr = dragZone.getProxy();
			pr.setStatus(pr.dropNotAllowed);
			pr.sync();
			
			return false;
		}
		
		//get the topmost shape
		var el = aShapes.last();
		
		//muss das hier length oder lenght heißen?
		if(aShapes.length == 1 && aShapes[0] instanceof ORYX.Core.Canvas) {
			return false;
		} else {
			//check containment rules for each shape of pattern
			var option = Ext.dd.Registry.getHandle(target.DDM.currentTarget);
			var pattern = this.retrievePattern(option.id);			
			var stencilSet = this.facade.getStencilSets()[option.namespace];
			
			pattern.shapes.each(function(shape, index, stencilSet, coord){
				var stencil = stencilSet.stencil(shape.type);
				
				if(stencil.type() === "node") {
					
					var parentCandidate = aShapes.reverse().find(function(candidate){
						return (candidate instanceof ORYX.Core.Canvas
							|| candidate instanceof ORYX.Core.Node
							|| candidate instanceof ORYX.Core.Edge);
					}); //gibt der nicht einfach any aus? das sind doch alle drei typen oder?
					
					if (parentCandidate !== this._lastOverElement){
						
						this._canAttach = undefined;
						this._canContain = undefined;
						
					}
					
					if (parentCandidate) {
						
						//check containment rule
						
						if(!(parentCandidate instanceof ORYX.Core.Canvas) && parentCandidate.isPointOverOffset(coord.x, coord.y) && this._canAttach == undefined) {
							
							this._canAttach = this.facade.getRules().canConnect({
								sourceShape: parentCandidate,
								edgeStencil: stencil,
								targetStencil: stencil
							});
							
							if( this._canAttach ) {
								//Show Highlight
								this.facade.raiseEvent({
									type: ORYX.CONFIG.EVENT_HIGHLIGHT_SHOW,
									highlightId: "patternRepo.attached",
									elements: [parentCandidate],
									style: ORYX.CONFIG.SELECTION_HIGHLIGHT_STYLE_RECTANGLE,
									color: ORYX.CONFIG.SELECTION_VALID_COLOR
								});
								
								this.facade.raiseEvent({
									type: ORYX.CONFIG.EVENT_HIGHLIGHT_HIDE,
									highlightId: "patternRepo.added"
								});
								
								this._canContain = undefined;
							}
						}
						
						if(!(parentCandidate instanceof ORYX.Core.Canvas) && !(parentCandidate.isPointOverOffset(coord.x, coord.y))) {
							this._canAttach = this._canAttach == false ? this._canAttach : undefined;
						}
						
						if (!this._canContain == undefined && !this._canAttach) {
							this._canContain = this.facade.getRules;
						}
					}
				}
				
			}.bind(this, stencilSet, coord)); //curry in stencilset, coord
		}*/
	},
	//copied from main
	
	//OBSOLETE COMMENT
	
	
	/**
     * This method renew all resource Ids and according references.
     * Warning: The implementation performs a substitution on the serialized object for
     * easier implementation. This results in a low performance which is acceptable if this
     * is only used when importing models.
     * @param {Object|String} jsonObject
     * @throws {SyntaxError} If the serialized json object contains syntax errors.
     * @return {Object} The jsonObject with renewed ids.
     * @private
     */
    renewResourceIds: function(jsonObjectArray){
        // For renewing resource ids, a serialized and object version is needed
        /*
		if(Ext.type(jsonObjectCollection) === "string"){
            try {
                var serJsonObject = jsonObject;
                jsonObject = Ext.decode(jsonObject);
            } catch(error){
                throw new SyntaxError(error.message);
            }
        } else {
            var serJsonObject = Ext.encode(jsonObject);
        } */

       var serJsonObjectArray = Ext.encode(jsonObjectArray);

		// collect all resourceIds recursively
        var collectResourceIds = function(shapes){
            if(!shapes) return [];

            return shapes.collect(function(shape){
                return collectResourceIds(shape.childShapes).concat(shape.resourceId);
            }).flatten();
        };
        var resourceIds = collectResourceIds(jsonObjectArray);

        // Replace each resource id by a new one
        resourceIds.each(function(oldResourceId){
            var newResourceId = ORYX.Editor.provideId();
            serJsonObjectArray = serJsonObjectArray.gsub('"'+oldResourceId+'"', '"'+newResourceId+'"');
        });

        return Ext.decode(serJsonObjectArray);
    },

	/*
	* removes all edges that reference non-existing (in the pattern) shapes.
	*/
	removeDanglingEdges: function(jsonObjectArray) {
		//recursion deep check???
		var result = jsonObjectArray.select(function(jsonObjectArray, serShape) {
			if(!serShape.target) { //is node?
				return true;
			} else { //is edge
				return this.isTargetOfShapeInCollection(serShape, jsonObjectArray);			}
		}.bind(this, jsonObjectArray));
		
		return result;
	},
	
	isTargetOfShapeInCollection: function(serShape, collection) {
		return collection.any(function(serShape, possibleTarget) {
			return serShape.target.resourceId == possibleTarget.resourceId;
		}.bind(this, serShape));
	},
	
	removeObsoleteReferences: function(jsonObjectArray) {
		var result = jsonObjectArray;
		
		result.each(function(serShape) {
			var newOutgoingEdges = serShape.outgoing.select(function(out) {
				return result.any(function(out, possibleMatch) {
					return possibleMatch.resourceId == out.resourceId;
				}.bind(this, out));
			});
			
			serShape.outgoing = newOutgoingEdges;
		});
		
		return result;
	}

});

ORYX.Plugins.Patterns.Pattern = Clazz.extend({
	serPattern : undefined,
	id : undefined,
	imageUrl : undefined,
	description : undefined,
	ssNameSpace: undefined,
	
	construct: function(opt) {
		if(opt.serPattern != null) this.serPattern = opt.serPattern;
		if(opt.id != null) this.id = opt.id;
		if(opt.imageUrl != null) this.imageUrl = opt.imageUrl;
		if(opt.description != null) this.description = opt.description;
		if(ssNameSpace != null) this.ssNameSpace = opt.ssNameSpace;
	},
	
	setDescription: function(desc) {
		var params = {
			modify: true,
			ssNameSpace: this.ssNameSpace,
			id: this.id,
			description: desc
		};
		return this._sendRequest("POST", params);
	},
	
	//Dopplung!
	_sendRequest: function( method, params, successcallback, failedcallback ){

		var suc = false;

		new Ajax.Request(
		ORYX.CONFIG.ROOT_PATH + "/pattern", //url is fixed
		{
           method			: method,
           asynchronous		: false, 
           parameters		: params,
		   onSuccess		: function(transport) 
		   {
				suc = true;
		
				if(successcallback)
				{
					successcallback( transport.responseText.evalJSON(true) );	
				}
		
		   }.bind(this),
		   onFailure		: function(transport) 
		   {
				if(failedcallback)
				{							
					failedcallback();							
				} 
				else 
				{
					//this._showErrorMessageBox(ORYX.I18N.Oryx.title, ORYX.I18N.cpntoolsSupport.serverConnectionFailed);
					ORYX.log.warn("Communication failed: " + transport.responseText);	
				}					
		   }.bind(this)		
		});
		
		return suc;		
	}
	
});

ORYX.Plugins.Patterns.PatternRepository = Clazz.extend({
	patternList : [],
	ssNameSpace : undefined,
	onUpdate : function(patternArray){}, //will be called, when new pattern are available
	
	construct: function(ssNameSpace, onUpdate) {
		this.ssNameSpace = ssNameSpace;
		this.loadPattern();
		this.onUpdate = onUpdate;
	},
	
	loadPattern: function() {
		this._sendRequest("GET", {ssNameSpace: this.ssNameSpace}, function(resp) {
			resp.each(function(opt) {
				var pattern = new ORYX.Plugins.Patterns.Pattern(opt);
				pattern.ssNameSpace = this.ssNameSpace;
				this.patternList.push(pattern);
			}.bind(this));
			this.onUpdate(this.patternList);
		}.bind(this));
	},
	
	getPatterns: function() {
		return this.patternList;
	},
	
	saveNewPattern: function(pattern) {
		var params = {
			description: pattern.description,
			serPattern: pattern.serPattern,
			ssNameSpace: pattern.ssNameSpace
		};
		
		this._sendRequest("POST", params); //TODO reflect failed add with removing the node??
	},
	
	_sendRequest: function( method, params, successcallback, failedcallback ){

		var suc = false;

		new Ajax.Request(
		ORYX.CONFIG.ROOT_PATH + "/pattern", //url is fixed
		{
           method			: method,
           asynchronous		: true, 
           parameters		: params,
		   onSuccess		: function(transport) 
		   {
				suc = true;
		
				if(successcallback)
				{
					successcallback( transport.responseText.evalJSON(true) );	
				}
		
		   }.bind(this),
		   onFailure		: function(transport) 
		   {
				if(failedcallback)
				{							
					failedcallback();							
				} 
				else 
				{
					//this._showErrorMessageBox(ORYX.I18N.Oryx.title, ORYX.I18N.cpntoolsSupport.serverConnectionFailed);
					ORYX.log.warn("Communication failed: " + transport.responseText);	
				}					
		   }.bind(this)		
		});
		
		return suc;		
	}
});