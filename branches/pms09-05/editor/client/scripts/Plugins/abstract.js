
/**
 * Copyright (c) 2010 Michael Wolowyk, Artem Polyvyanyy
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
if (!ORYX.Plugins)
	ORYX.Plugins = new Object();

/**
 * Supports EPC by abstraction with rpstTree method
 * 
 */
ORYX.Plugins.Abstraction = Clazz.extend({

	facade : undefined,
	jsonResponse : undefined,


	epcModel : undefined,
	active : false,
	slider : undefined,

	/**
	 * Offers the plugin functionality:
	 * 
	 */
	construct : function(facade) {
		this.facade = facade;
		this.epcModel = this.facade.getSerializedJSON(); 
		//abstraction mode
		this.facade.offer({
					'name' : "Abstraction",
					'functionality' : this.abstraction_enabled.bind(this),
					'group' : "epc",
					'icon' : ORYX.PATH + "images/wand.png",
					'description' : ORYX.I18N.Abstraction.abstractionDescription,
					'index' : 1,
					'minShape' : 0,
					'maxShape' : 0
				});
		//editing mode
		this.facade.offer({
					'name' : "Abstraction_disabled",
					'functionality' : this.abstraction_disabled.bind(this),
					'group' : "epc",
					'icon' : ORYX.PATH + "images/wand_delete.png",
					'description' : ORYX.I18N.Abstraction.editingDescription,


					'index' : 1,
					'minShape' : 0,
					'maxShape' : 0
				});
		
	},
	
	/**
	 * sends request with json to the server defines abstraction slider
	 */

	abstraction_enabled : function() {
		if (this.active) {
			Ext.Msg.alert(ORYX.I18N.Abstraction.alreadyAbstraction);


		} else {


		this.facade.raiseEvent({
											type : ORYX.CONFIG.EVENT_LOADING_ENABLE,
											text : ORYX.I18N.Abstraction.beingAbstracted


										});


			this.epcModel = this.facade.getSerializedJSON();
			this.sendRequest(this.epcModel);
			if (this.jsonResponse != undefined) {
				this.definesSlider();

				this.active = !this.active;
				this.facade.disableEvent(ORYX.CONFIG.EVENT_SELECTION_CHANGED); // ReadOnly_Mode
				this.facade.disableEvent(ORYX.CONFIG.EVENT_STENCIL_SET_LOADED);
				this.facade.disableEvent(ORYX.CONFIG.EVENT_SHAPEADDED);
				this.facade.disableEvent(ORYX.CONFIG.EVENT_MOUSEDOWN);
				this.facade.raiseEvent({
							type : ORYX.CONFIG.EVENT_LOADING_DISABLE
						});
				Ext.Msg
						.alert(ORYX.I18N.Abstraction.abstractionMode);



			} else {
				Ext.Msg.alert(ORYX.I18N.Abstraction.Error);
			}


		}
	},
	
	/**
	* back to the editing mode
	*/
	abstraction_disabled : function() {

		if (!this.active) {
			Ext.Msg.alert(ORYX.I18N.Abstraction.alreadyEditing);
		} else {
			this.active = false;
			// delete slider
			this.slider.zone.dom.parentNode.removeChild(this.slider.zone.dom);
			this.slider = undefined;
			this.cleanCanvas();
			var jsonFile = this.epcModel.evalJSON();
			this.facade.importJSONwithoutRegenerationOfResourceId(jsonFile,
					true);
			// edit enabled
			this.facade.enableEvent(ORYX.CONFIG.EVENT_STENCIL_SET_LOADED);
			this.facade.enableEvent(ORYX.CONFIG.EVENT_SELECTION_CHANGED);
			this.facade.enableEvent(ORYX.CONFIG.EVENT_SHAPEADDED);
			this.facade.enableEvent(ORYX.CONFIG.EVENT_MOUSEDOWN);
			Ext.Msg.alert(ORYX.I18N.Abstraction.editingMode);
		}
	},

	/**
	* sends epc to the server
	* @param(json) jsonRequest
	*/
	sendRequest : function(jsonRequest) {

		try {
			new Ajax.Request(ORYX.CONFIG.ABSTRACTION, {
						method : 'POST',
						asynchronous : false,
						encoding : 'UTF-8',
						parameters : {
							data : jsonRequest
						},
						onFailure : function(request) {
							this.facade.raiseEvent({
										type : ORYX.CONFIG.EVENT_LOADING_DISABLE
									});
							Ext.Msg.alert(ORYX.I18N.Abstraction.requestFailed);


						}.bind(this),
						onSuccess : function(request) {
							if (request.responseText != "") {
								// response consists of rpst tree
								this.jsonResponse = request.responseText
										.evalJSON();
							}
						}.bind(this)

					});
		} catch (error) {
			Ext.Msg.alert(error);
		}

	},

	/**
	* offers slider functionality
	*/

	definesSlider : function() {
		var treeDepth = this.jsonResponse.rpst[1].treeDepth;
		var canvas = this.facade.getCanvas().getId();
		// slider not exists
		if (!this.slider) {
			this.slider = new Ext.Slider(
			//'oryx-canvas123', {
			'oryx_editor_header', {
						width : 20, 
						height: 320,
						left : 200,
						top : 60,
						step : 300 / treeDepth,

						bgImage : ORYX.PATH + "images/slider-bg.gif",
						sliderImage : ORYX.PATH + "images/slider-thumb.gif",
						onDragEnd : function(sliderPos) {
							if (this.active) {
							// loading event
								this.facade.raiseEvent({
											type : ORYX.CONFIG.EVENT_LOADING_ENABLE,
											text : ORYX.I18N.Abstraction.beingAbstracted
										});
								this.cleanCanvas();
								var jsonFile = this.epcModel.evalJSON();
								this.facade
										.importJSONwithoutRegenerationOfResourceId(
												jsonFile, true);
								
								var abstractionLevel = treeDepth-Math.round(treeDepth
												* sliderPos);
								this.doAbstraction(abstractionLevel);
								
								// epc layouter plugin
								if (ORYX.Plugins.EPCLayouter) {
									this.facade.raiseEvent({
								type : ORYX.Plugins.EPCLayouter.LayoutEPC
							});
							}
								this.facade.raiseEvent({
											type : ORYX.CONFIG.EVENT_LOADING_DISABLE
										});
							}
						}.bind(this)
					});
		//slider exists
		} else {
			this.slider.show();
		}
	},
	
	
	
	

	/**
	 * Do Abstraction with abstractionLevel on node (rpst)
	 * @param{int} abstractionLevel
	 */
	doAbstraction : function(abstractionLevel){
		var root = this.jsonResponse.rpst[0];
		this.doNodeAbstraction(root, abstractionLevel);
	},
	/**
	 * Do Abstraction with abstractionLevel on node (rpst)
	 * @param{TreeNode} rpst
	 * @param{int} abstractionLevel
	 */
	doNodeAbstraction : function(node, abstractionLevel) {
		// not edge
		if (node.type != 'Q') {
			if (abstractionLevel == 0) {
				this.cleanCanvas();
				var subprocess = this.createSubprocess(300, 300);
				return;
			} else {
				if (node.depth == abstractionLevel) {
					var entry = this.facade.getCanvas()
							.getChildShapeByResourceId(node.nodeEntry);
					var exit = this.facade.getCanvas()
							.getChildShapeByResourceId(node.nodeExit);

					var removedEdges = [];
					// remove all edges and nodes between entry and exit
					this.removeSubTree(node, removedEdges); 
					var x = (entry.bounds.a.x + entry.bounds.b.x
							+ exit.bounds.a.x + exit.bounds.b.x)
							/ 4;
					var y = (entry.bounds.a.y + entry.bounds.b.y
							+ exit.bounds.a.y + exit.bounds.b.y)
							/ 4;
					var subprocess = this.createSubprocess(x, y);
					this.createSequenceFlow(entry, subprocess);
					this.createSequenceFlow(subprocess, exit);
					// Gateway with only 1 exit and entry
					if (node.type == 'P'
							&& entry.getOutgoingShapes().length == 1
							&& exit.getIncomingShapes().length == 1) {
						this.skipNode(node);
					}
				}

				// looks at node which depth == abstractionLevel
				else {
					for (var i = 0; i < node.children.length; i++) {
						var child = node.children[i];
						this.doNodeAbstraction(child, abstractionLevel);
					}
				}
			}
		}
	},
	
	/**********************************************
	 ****************Removing**********************
	 **********************************************/

	/**
	 * Removes all incoming and outgoing edges of the node
	 * 
	 * @param{Json} node
	 */
	removeEdges : function(node) {
		var edges = [];
		if (node != undefined) {
			if (node.outgoing != undefined) {
				for (var j = 0; j < node.outgoing.length; j++) {
					var outgoingEdge = node.outgoing[j];
					edges.push(outgoingEdge);
				}
			}
			if (node.getIncomingShapes() != undefined) {
				for (var k = 0; k < node.getIncomingShapes().length; k++) {
					var incomingEdge = node.getIncomingShapes()[k];
					edges.push(incomingEdge);
				}
			}
			for (var l = 0; l < edges.length; l++) {
				this.facade.deleteShape(edges[l]);
			}
		}
	},

	/**
	 * Removes the edge between source and target
	 * 
	 * @param{Shape} source, target shapes getting by ressourceId
	 */
	removeEdge : function(source, target) {
		if (source.getOutgoingShapes() != undefined
				&& target.getIncomingShapes() != undefined) {
			for (var j = 0; j < source.getOutgoingShapes().length; j++) {
				if (source.getOutgoingShapes()[j].getOutgoingShapes()[0] == target) {
					var edge = source.getOutgoingShapes()[j];
					this.facade.deleteShape(edge);
					return;
				}
			}
		}
	},
	
	/**
	* deletes all shapes from the canvas
	*/
	cleanCanvas : function() {
		var json = this.facade.getJSON();
		for (var i = 0; i < json.childShapes.length; i++) {
			var shape = this.facade.getCanvas()
					.getChildShapeByResourceId(json.childShapes[i].resourceId);
			this.facade.deleteShape(shape);
		}
		this.facade.getCanvas().update();
	},

	/**
	*Removes the subtree between node.entry and node.exit
	*@param{rpstNode}  node
	*@param{Array} removedChildren 
	*/

	removeSubTree : function(node, removedChildren) {
		if (!removedChildren) {
			removedChildren = [];
		}
		// is edge
		if (node.type == 'Q')
			return;
		// has subtree
		for (var i = 0; i < node.children.length; i++) {
			var child = node.children[i];
			if (child.type == 'Q') {
				var entry = this.facade.getCanvas()
						.getChildShapeByResourceId(child.nodeEntry);
				var exit = this.facade.getCanvas()
						.getChildShapeByResourceId(child.nodeExit);
		// nodeEntry and nodeExit shalln't be removed
				if (child.nodeEntry != node.nodeEntry
						&& child.nodeEntry != node.nodeExit) {
					removedChildren.push(entry);
					this.removeEdges(entry);
				}
				if (child.nodeExit != node.nodeEntry
						&& child.nodeExit != node.nodeExit) {
					removedChildren.push(exit);
					this.removeEdges(exit);
				}
			}
			// go deeper
			else {
				this.removeSubTree(child, removedChildren);
			}
		}
		for (var i = 0; i < removedChildren.length; i++) {
			this.facade.deleteShape(removedChildren[i]);
		}
	},

	/***************************************
	 ***************Creating**************** 
	 ***************************************/
 
	 /**
	 *createsSubprocess on x and y
	 *@param{int} x,y
	 *@return{Shape} newShape
	 */
	createSubprocess : function(x, y) {
		var type = "ProcessInterface";

		// create a new Stencil
		var ssn = this.facade.getStencilSets().keys()[0];
		var stencil = ORYX.Core.StencilSet.stencil(ssn + type);

		if (!stencil)
			return null;
			
		// create a new Shape

		var newShape = new ORYX.Core.Node({
					'eventHandlerCallback' : this.facade.raiseEvent
				}, stencil);

		// add the shape to the canvas
		this.facade.getCanvas().add(newShape);

		// set properities
		newShape.bounds.centerMoveTo(x, y);
		// newShape.setProperty("oryx-name", title);
		this.facade.getCanvas().update();

		return newShape;
	},
	/**
	*skips the Node
	*@param{rpstNode} node
	*/

	skipNode : function(node) {
		if (node) {
			var entry = this.facade.getCanvas().getChildShapeByResourceId(node.nodeEntry);
			var exit = this.facade.getCanvas().getChildShapeByResourceId(node.nodeExit);
			var parentEntry = entry.incoming[0].incoming[0];
			var parentExit = exit.outgoing[0].outgoing[0];
			var subprocess = entry.outgoing[0].outgoing[0];
			this.removeEdges(entry);
			this.removeEdges(exit);
			this.facade.deleteShape(entry);
			this.facade.deleteShape(exit);
			var x = (parentEntry.bounds.a.x + parentEntry.bounds.b.x
							+ parentExit.bounds.a.x + parentExit.bounds.b.x)
							/ 4;
			var y = (parentEntry.bounds.a.y + parentEntry.bounds.b.y
							+ parentExit.bounds.a.y + parentExit.bounds.b.y)
							/ 4;

			this.createSequenceFlow(parentEntry, subprocess);
			this.createSequenceFlow(subprocess, parentExit);
		}
	},

	/**
	*creates sequence flow between from and to
	*@param{Shape} from, to
	*@return{Shape} newEdge
	*/
	createSequenceFlow : function(from, to) {
		newEdge = this.facade.createShape({
					type : "http://b3mn.org/stencilset/epc#ControlFlow",
					namespace : "http://b3mn.org/stencilset/epc#"
				});
		// add the edge to the canvas
		this.facade.getCanvas().add(newEdge);
		// set dockers
		newEdge.dockers.first().setDockedShape(from);
		newEdge.dockers.first().setReferencePoint({
					x : from.bounds.width() / 2.0,
					y : from.bounds.height() / 2.0
				});
		newEdge.dockers.last().setDockedShape(to);
		newEdge.dockers.last().setReferencePoint({
					x : to.bounds.width() / 2.0,
					y : to.bounds.height() / 2.0
				});
		// update canvas
		this.facade.getCanvas().update();
		return newEdge;
	}
});

/*******************************************************************************
 * SLIDER WIDGET
 ******************************************************************************/
Ext.Slider = function(id, config) {
	this.init(id, config || {});
}

Ext.Slider.prototype = {
	// zone



	zone : null,
	width : 20,
	height: 320,
	left : 1000,
	top : 100,
	//slider
	slider : null,
	sliderWidth: 30,
	sliderHeight: 20,
	sliderBottom: 0,
	sliderLeft: -10,
	initialPosition: 0,
	step : 10,

	ddEl : null,
	initial: 0,
	startDrag : null,
	endDrag : null,
	onDrag : null,
	
	
	bgImage : 'http://www.agentsinaction.de/ext-slider/slider_bg.gif',
	sliderImage : 'http://www.agentsinaction.de/ext-slider/slider_horiz.gif',
	
	hide : function() {
		if (this.zone != null) {
			this.zone.setStyle('visibility', 'hidden');
		}
	},
	show : function() {
		if (this.zone != null) {
			this.zone.setStyle('visibility', 'visible');
		}
	},
	




	
	init : function(id, config) {
		Ext.apply(this, config);
		// defines zone

		this.zone = Ext.DomHelper.append(Ext.get(id), {
					tag : 'div',
					id : Ext.id(),
					style : 'position:fixed; left:' + this.left
							+ 'px; top: ' + this.top 
							+ 'px; width: ' + this.width 
							+ 'px;height: ' + this.height 
							+ 'px;background-image:url(' + this.bgImage 
							+ ');z-index:1000;'
				}, true);
		// defines slider

		this.slider = Ext.DomHelper.append(this.zone, {
					tag : 'div',
					id : Ext.id(),
					style : 'position:absolute; bottom:' + this.sliderBottom
							+'px; left:' + this.sliderLeft
							+'px; width:'+ this.sliderWidth
							+'px; height:' + this.sliderHeight
							+'px;background-image:url(' + this.sliderImage

							+ '); overflow: hidden;z-index:2000;'
				}, true);
		
		var instance = this;
		this.initial = instance.zone.getBottom()-instance.zone.getTop()-instance.slider.getHeight();		
		this.ddEl = new Ext.dd.DD(this.slider);
		this.ddEl.setXConstraint(0, 0);
		
		this.ddEl.setYConstraint(instance.slider.getTop() - instance.zone.getTop(),
				instance.zone.getHeight() - instance.slider.getHeight() + instance.zone.getTop() - instance.slider.getTop());
		

		this.slider.on('mouseover', function() {
					if (!instance.ddEl.isDragged)
						this.setStyle('background-position', '0px -20px');
						this.setStyle('cursor', 'h-resize');
				});
		this.slider.on('mouseout', function() {
					if (!instance.ddEl.isDragged)
						this.setStyle('background-position', '0px 0px');
						this.setStyle('cursor', 'default');
				});
		this.ddEl.onMouseDown = function(x, y) {
			instance.ddEl.isDragged = true;
			if (typeof instance.onDragStart == 'function')
				instance.onDragStart((instance.slider.getY() - instance.zone.getY())

						/ instance.width);
			instance.slider.setStyle('background-position', '0px -40px');
		};
		this.ddEl.onMouseUp = function(x, y) {
			instance.ddEl.isDragged = false;
			if (typeof instance.onDragEnd == 'function')
				var value  = (instance.zone.getBottom()-instance.slider.getBottom())/(instance.zone.getHeight()-instance.slider.getHeight());
				instance.onDragEnd(value);

			instance.slider.setStyle('background-position', '0px 0px');
		};
		this.ddEl.onDrag = function(e) {
		var position = Math.round(((instance.slider.getBottom()-instance.zone.getBottom())/instance.step))*instance.step+instance.zone.getBottom()-instance.slider.getHeight();
					instance.slider.setY(position);
		};

	}
};
