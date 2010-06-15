

/**
 * Copyright (c) 2010
 * Daniel Schleicher
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


if(!ORYX.Plugins) {
	ORYX.Plugins = new Object();
}

ORYX.Plugins.FragmentRepository = Clazz.extend({

				
	facade: undefined,

	construct: function(facade) {
		this.facade = facade;
		this._currentParent;
		this._canContain = undefined;
		this._canAttach  = undefined;

		this.shapeList = new Ext.tree.TreeNode({
			
		})

		var panel = new Ext.tree.TreePanel({
            cls:'shaperepository',
			loader: new Ext.tree.TreeLoader(),
			root: this.shapeList,
			autoScroll:true,
			rootVisible: false,
			lines: false,
			autoHeight: true,
			anchors: '0, -30'
		})
		var region = this.facade.addToRegion("east", panel, ORYX.I18N.ShapeRepository.title);
	
		
		// Create a Drag-Zone for Drag'n'Drop
		var DragZone = new Ext.dd.DragZone(this.shapeList.getUI().getEl(), {shadow: !Ext.isMac});
		DragZone.afterDragDrop = this.drop.bind(this, DragZone);
		//DragZone.beforeDragOver = this.beforeDragOver.bind(this, DragZone);
		DragZone.beforeDragEnter = function(){this._lastOverElement = false; return true}.bind(this);
		
		// Load all Stencilssets
		this.setStencilSets();
		
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_STENCIL_SET_LOADED, this.setStencilSets.bind(this));

		},

		/**
		 * Load all stencilsets in the shaperepository
		 */
		setStencilSets: function() {
			// Remove all childs
			var child = this.shapeList.firstChild;
			while(child) {
				this.shapeList.removeChild(child);
				child = this.shapeList.firstChild;
			}

/*			// Go thru all Stencilsets and stencils
			this.facade.getStencilSets().values().each((function(sset) {
*/				
				// For each Stencilset create and add a new Tree-Node
				var stencilSetNode
				
				var typeTitle = "Fragment Repository";
	/*			var extensions = sset.extensions();
				if (extensions && extensions.size() > 0) {
					typeTitle += " / " + ORYX.Core.StencilSet.getTranslation(extensions.values()[0], "title");
				} 
	*/			
				this.shapeList.appendChild(stencilSetNode = new Ext.tree.TreeNode({
					text:typeTitle, 			// Stencilset Name
					allowDrag:false,
	        		allowDrop:false,           
					iconCls:'headerShapeRepImg',
		            cls:'headerShapeRep',
					singleClickExpand:true}));
				
				stencilSetNode.render();
				stencilSetNode.expand();	
				
				this.createStencilTreeNode(stencilSetNode, "description");	
				// Get Stencils from Stencilset
/*				var stencils = sset.stencils(this.facade.getCanvas().getStencil(),
											 this.facade.getRules());	
				var treeGroups = new Hash();
				
				// Sort the stencils according to their position and add them to the repository
				stencils = stencils.sortBy(function(value) { return value.position(); } );
				stencils.each((function(value) {
					
					// Show stencils in no group if there is less than 10 shapes
					if(stencils.length <= ORYX.CONFIG.MAX_NUM_SHAPES_NO_GROUP) {
						this.createStencilTreeNode(stencilSetNode, value);	
						return;					
					}
					
					// Get the groups name
					var groups = value.groups();
					
					// For each Group-Entree
					groups.each((function(group) {
						
						// If there is a new group
						if(!treeGroups[group]) {
							// Create a new group
							treeGroups[group] = new Ext.tree.TreeNode({
								text:group,					// Group-Name
								allowDrag:false,
	        					allowDrop:false,            
								iconCls:'headerShapeRepImg', // Css-Class for Icon
					            cls:'headerShapeRepChild',  // CSS-Class for Stencil-Group
								singleClickExpand:true});
							
							// Add the Group to the ShapeRepository
							stencilSetNode.appendChild(treeGroups[group]);
							treeGroups[group].render();	
						}
						
						// Create the Stencil-Tree-Node
						this.createStencilTreeNode(treeGroups[group], value);	
						
					}).bind(this));
					
					
					// If there is no group
					if(groups.length == 0) {
						// Create the Stencil-Tree-Node
						this.createStencilTreeNode(stencilSetNode, value);						
					}
		
				}).bind(this));
*/			//}).bind(this));
				
			if (this.shapeList.firstChild.firstChild) {
				this.shapeList.firstChild.firstChild.expand(false, true);
			}	
		},

		createStencilTreeNode: function(parentTreeNode, stencil) {

			// Create and add the Stencil to the Group
			var newElement = new Ext.tree.TreeNode({
					text:		"Task", 		// Text of the stencil
					icon:		ORYX.PATH + "/stencilsets/bpmn2.0/icons/activity/task.png",			// Icon of the stencil
					allowDrag:	false,					// Don't use the Drag and Drop of Ext-Tree
					allowDrop:	false,
					iconCls:	'ShapeRepEntreeImg', 	// CSS-Class for Icon
					cls:		'ShapeRepEntree'		// CSS-Class for the Tree-Entree
					});

			parentTreeNode.appendChild(newElement);		
			newElement.render();	
					
			var ui = newElement.getUI();
			
			// Set the tooltip
			ui.elNode.setAttributeNS(null, "title", stencil);
			
			// Register the Stencil on Drag and Drop
			Ext.dd.Registry.register(ui.elNode, {
					node: 		ui.node,
			        handles: 	[ui.elNode, ui.textNode].concat($A(ui.elNode.childNodes)), // Set the Handles
			        isHandle: 	false,
					type:		"http://b3mn.org/stencilset/bpmn2.0#Task",			// Set Type of stencil 
					namespace:	"http://b3mn.org/stencilset/bpmn2.0#"	// Set Namespace of stencil
					});
									
		},
		
		drop: function(dragZone, target, event) {
			
			this._lastOverElement = undefined;
			
			// Hide the highlighting
			this.facade.raiseEvent({type: ORYX.CONFIG.EVENT_HIGHLIGHT_HIDE, highlightId:'shapeRepo.added'});
			this.facade.raiseEvent({type: ORYX.CONFIG.EVENT_HIGHLIGHT_HIDE, highlightId:'shapeRepo.attached'});
			
			// Check if drop is allowed
			var proxy = dragZone.getProxy()
			if(proxy.dropStatus == proxy.dropNotAllowed) { return }
			
			// Check if there is a current Parent
/*			if(!this._currentParent) { return }
			
			var option = Ext.dd.Registry.getHandle(target.DDM.currentTarget);
			
			var xy = event.getXY();
			var pos = {x: xy[0], y: xy[1]};

			var a = this.facade.getCanvas().node.getScreenCTM();

			// Correcting the UpperLeft-Offset
			pos.x -= a.e; pos.y -= a.f;		// Correcting the Zoom-Faktor
			pos.x /= a.a; pos.y /= a.d;
			// Correting the ScrollOffset
			pos.x -= document.documentElement.scrollLeft;
			pos.y -= document.documentElement.scrollTop;
			// Correct position of parent
			var parentAbs = this._currentParent.absoluteXY();
			pos.x -= parentAbs.x;
			pos.y -= parentAbs.y;

			// Set position
			option['position'] = pos
			
			// Set parent
			if( this._canAttach &&  this._currentParent instanceof ORYX.Core.Node ){
				option['parent'] = undefined;	
			} else {
				option['parent'] = this._currentParent;
			}
			
			
			var commandClass = ORYX.Core.Command.extend({
				construct: function(option, currentParent, canAttach, position, facade){
					this.option = option;
					this.currentParent = currentParent;
					this.canAttach = canAttach;
					this.position = position;
					this.facade = facade;
					this.selection = this.facade.getSelection();
					this.shape;
					this.parent;
				},			
				execute: function(){
					if (!this.shape) {
						this.shape 	= this.facade.createShape(option);
						this.parent = this.shape.parent;
					} else {
						this.parent.add(this.shape);
					}
						
					
					
					if( this.canAttach &&  this.currentParent instanceof ORYX.Core.Node && this.shape.dockers.length > 0){
						
						var docker = this.shape.dockers[0];
			
						if( this.currentParent.parent instanceof ORYX.Core.Node ) {
							this.currentParent.parent.add( docker.parent );
						}
													
						docker.bounds.centerMoveTo( this.position );
						docker.setDockedShape( this.currentParent );
						//docker.update();	
					}
			
					//this.currentParent.update();
					//this.shape.update();

					this.facade.setSelection([this.shape]);
					this.facade.getCanvas().update();
					this.facade.updateSelection();
					
				},
				rollback: function(){
					this.facade.deleteShape(this.shape);
					
					//this.currentParent.update();

					this.facade.setSelection(this.selection.without(this.shape));
					this.facade.getCanvas().update();
					this.facade.updateSelection();
					
				}
			});
								
			var position = this.facade.eventCoordinates( event.browserEvent );	
						
			var command = new commandClass(option, this._currentParent, this._canAttach, position, this.facade);
			
			this.facade.executeCommands([command]);
			
			this._currentParent = undefined;*/
			
			var fragmentToBeImported = {
					   "resourceId":"oryx-canvas123",
					   "properties":{
					      "id":"",
					      "name":"",
					      "documentation":"",
					      "auditing":"",
					      "monitoring":"",
					      "version":"",
					      "author":"",
					      "language":"English",
					      "namespaces":"",
					      "targetnamespace":"http://www.omg.org/bpmn20",
					      "expressionlanguage":"http://www.w3.org/1999/XPath",
					      "typelanguage":"http://www.w3.org/2001/XMLSchema",
					      "creationdate":"",
					      "modificationdate":""
					   },
					   "stencil":{
					      "id":"BPMNDiagram"
					   },
					   "childShapes":[
					      {
					         "resourceId":"oryx_C2AE5B00-4718-4FD3-A60B-8B0C300223F2",
					         "properties":{
					            "id":"",
					            "name":"",
					            "documentation":"",
					            "auditing":"",
					            "monitoring":"",
					            "categories":"",
					            "startquantity":1,
					            "completionquantity":1,
					            "status":"None",
					            "performers":"",
					            "properties":"",
					            "inputsets":"",
					            "inputs":"",
					            "outputsets":"",
					            "outputs":"",
					            "iorules":"",
					            "testtime":"After",
					            "mi_condition":"",
					            "mi_flowcondition":"All",
					            "isforcompensation":"",
					            "assignments":"",
					            "pool":"",
					            "lanes":"",
					            "looptype":"None",
					            "loopcondition":"",
					            "loopcounter":1,
					            "loopmaximum":1,
					            "callacitivity":"",
					            "activitytype":"Task",
					            "tasktype":"Send",
					            "inmessage":"",
					            "outmessage":"",
					            "implementation":"webService",
					            "resources":"",
					            "complexmi_condition":"",
					            "messageref":"",
					            "operationref":"",
					            "taskref":"",
					            "instantiate":"",
					            "script":"",
					            "script_language":"",
					            "bgcolor":"#ffffcc"
					         },
					         "stencil":{
					            "id":"Task"
					         },
					         "childShapes":[

					         ],
					         "outgoing":[

					         ],
					         "bounds":{
					            "lowerRight":{
					               "x":251,
					               "y":180
					            },
					            "upperLeft":{
					               "x":151,
					               "y":100
					            }
					         },
					         "dockers":[

					         ]
					      }
					   ],
					   "bounds":{
					      "lowerRight":{
					         "x":1485,
					         "y":1050
					      },
					      "upperLeft":{
					         "x":0,
					         "y":0
					      }
					   },
					   "stencilset":{
					      "url":ORYX.PATH + "/stencilsets/bpmn2.0/bpmn2.0.json",
					      "namespace":"http://b3mn.org/stencilset/bpmn2.0#"
					   },
					   "ssextensions":[
					      "http://oryx-editor.org/stencilsets/extensions/bpmnComplianceTemplate#"
					   ]
					}
;
		
		//We have to put the fragment on the same 
		//position where the mouse-up event occured. 
		var positionOfFragment = fragmentToBeImported.childShapes[0].bounds.upperLeft.x;
		var eventCoordinates = event.getXY();	
		
		var matrix = this.facade.getCanvas().node.getScreenCTM();

		// Correcting the UpperLeft-Offset
		eventCoordinates[0] -= matrix.e; 
		eventCoordinates[1] -= matrix.f;		// Correcting the Zoom-Faktor
		
		eventCoordinates[0] /= matrix.a; 
		eventCoordinates[1] /= matrix.d; // Correting the ScrollOffset
		eventCoordinates[0] -= document.documentElement.scrollLeft;
		eventCoordinates[1] -= document.documentElement.scrollTop;
		// Correct position of parent
/*		var parentAbs = this._currentParent.absoluteXY();
		eventCoordinates[0] -= parentAbs.x;
		eventCoordinates[1] -= parentAbs.y;
*/		
		fragmentToBeImported.childShapes[0].bounds.upperLeft.x = eventCoordinates[0];
		fragmentToBeImported.childShapes[0].bounds.upperLeft.y = eventCoordinates[1];
		
		fragmentToBeImported.childShapes[0].bounds.lowerRight.x = eventCoordinates[0] + 100;
		fragmentToBeImported.childShapes[0].bounds.lowerRight.y = eventCoordinates[1] + 80;
		
		
		this.facade.importJSON(fragmentToBeImported, true);
		
		},

		beforeDragOver: function(dragZone, target, event){

			var coord = this.facade.eventCoordinates(event.browserEvent);
			var aShapes = this.facade.getCanvas().getAbstractShapesAtPosition( coord );

			if(aShapes.length <= 0) {
				
					var pr = dragZone.getProxy();
					pr.setStatus(pr.dropNotAllowed);
					pr.sync();
					
					return false;
			}	
			
			var el = aShapes.last();
		
			
			if(aShapes.lenght == 1 && aShapes[0] instanceof ORYX.Core.Canvas) {
				
				return false;
				
			} else {
				// check containment rules
				var option = Ext.dd.Registry.getHandle(target.DDM.currentTarget);

				var stencilSet = this.facade.getStencilSets()[option.namespace];

				var stencil = stencilSet.stencil(option.type);

				if(stencil.type() === "node") {
					
					var parentCandidate = aShapes.reverse().find(function(candidate) {
						return (candidate instanceof ORYX.Core.Canvas 
								|| candidate instanceof ORYX.Core.Node
								|| candidate instanceof ORYX.Core.Edge);
					});
					
					if(  parentCandidate !== this._lastOverElement){
						
						this._canAttach  = undefined;
						this._canContain = undefined;
						
					}
					
					if( parentCandidate ) {
						//check containment rule					
							
						if (!(parentCandidate instanceof ORYX.Core.Canvas) && parentCandidate.isPointOverOffset(coord.x, coord.y) && this._canAttach == undefined) {
						
							this._canAttach = this.facade.getRules().canConnect({
													sourceShape: parentCandidate,
													edgeStencil: stencil,
													targetStencil: stencil
												});
							
							if( this._canAttach ){
								// Show Highlight
								this.facade.raiseEvent({
									type: ORYX.CONFIG.EVENT_HIGHLIGHT_SHOW,
									highlightId: "shapeRepo.attached",
									elements: [parentCandidate],
									style: ORYX.CONFIG.SELECTION_HIGHLIGHT_STYLE_RECTANGLE,
									color: ORYX.CONFIG.SELECTION_VALID_COLOR
								});
								
								this.facade.raiseEvent({
									type: ORYX.CONFIG.EVENT_HIGHLIGHT_HIDE,
									highlightId: "shapeRepo.added"
								});
								
								this._canContain	= undefined;
							} 					
							
						}
						
						if(!(parentCandidate instanceof ORYX.Core.Canvas) && !parentCandidate.isPointOverOffset(coord.x, coord.y)){
							this._canAttach 	= this._canAttach == false ? this._canAttach : undefined;						
						}
						
						if( this._canContain == undefined && !this._canAttach) {
												
							this._canContain = this.facade.getRules().canContain({
																containingShape:parentCandidate, 
																containedStencil:stencil
																});
																
							// Show Highlight
							this.facade.raiseEvent({
																type:		ORYX.CONFIG.EVENT_HIGHLIGHT_SHOW, 
																highlightId:'shapeRepo.added',
																elements:	[parentCandidate],
																color:		this._canContain ? ORYX.CONFIG.SELECTION_VALID_COLOR : ORYX.CONFIG.SELECTION_INVALID_COLOR
															});	
							this.facade.raiseEvent({
																type: 		ORYX.CONFIG.EVENT_HIGHLIGHT_HIDE,
																highlightId:"shapeRepo.attached"
															});						
						}
							
					
						
						this._currentParent = this._canContain || this._canAttach ? parentCandidate : undefined;
						this._lastOverElement = parentCandidate;
						var pr = dragZone.getProxy();
						pr.setStatus(this._currentParent ? pr.dropAllowed : pr.dropNotAllowed );
						pr.sync();
		
					} 
				} else { //Edge
					this._currentParent = this.facade.getCanvas();
					var pr = dragZone.getProxy();
					pr.setStatus(pr.dropAllowed);
					pr.sync();
				}		
			}
			
			
			return false
		}	
});
