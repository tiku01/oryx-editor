/**
 * Copyright (c) 2010
 *
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

/**
 * @namespace Oryx name space for plugins
 * @name ORYX.Plugins
 */
if(!ORYX.Plugins)
	ORYX.Plugins = {};
		
/**
 * Patterns plugin adds support for a pattern repository that contains composite shapes.
 * @class ORYX.Plugins.Patterns
 * @extends ORYX.Plugins.AbstractPlugin
 * @param facade
 */
ORYX.Plugins.Patterns = ORYX.Plugins.AbstractPlugin.extend(
	/** @lends ORYX.Plugins.prototype */
	{
	
	/**
	 * facade that provides uniform access to core functions
	 */
	facade : undefined,
	
	/**
	 * indicicates if the "make as pattern" button besides a selection is visible
	 */
	buttonVisible : false,
	
	/**
	 * button that appears besides a selection of 2 or more shapes {ORYX.Plugins.Patterns.PatternButton}
	 */
	button : undefined,
	
	/**
	 * repository of patterns for the current stencilset (ORYX.Plugins.Patterns.PatternRepository)
	 */
	patternRepos : undefined,
	
	/**
	 * Ext.tree.TreeNode that represents the root of the pattern repository / panel.
	 */
	patternRoot : undefined,
	
	/**
	 * Ext.tree.TreePanel that represents the pattern panel / pattern repository in the west region.
	 */
	patternPanel : undefined,
	
	/**
	 * @constructs
	 */
	construct: function(facade) { 
		
		/** call superclass constructor */
		arguments.callee.$.construct.apply(this, arguments);
		
		//create and register pattern button
		this.button = new ORYX.Plugins.Patterns.PatternButton(this.facade, this.selAsPattern.bind(this));
		
		//adding of "capture as pattern"-func   
		this.facade.offer({
			name: ORYX.I18N.Patterns.selectionAsPattern, 
			functionality: this.selAsPattern.bind(this),
			group: ORYX.I18N.Patterns.toolbarButtonText, 
			description: ORYX.I18N.Patterns.toolbarButtonTooltip,
			minShape: 2,
			icon: ORYX.CONFIG.PATTERN_ADD_ICON
		});
		
		//create rootNode for patternrepository   
		this.patternRoot = new Ext.tree.TreeNode({ 
			cls: 'headerShapeRep',
			text: ORYX.I18N.Patterns.rootNodeText,
			iconCls: 'headerShapeRepImg',
			expandable: true,
			allowDrag: false,
			allowDrop: false,
			editable: false
		});
				
		//create Patternpanel as ext-tree-panel
		this.patternPanel = new Ext.tree.TreePanel({ 
			iconCls: 'headerShapeRepImg',
			cls:'shaperespository',
			root: this.patternRoot,			
			lines: false,
			rootVisible: true
		});
		
		//fixes double click behavior of treeEditor (cf. http://www.sencha.com/forum/archive/index.php/t-34170.html)
		Ext.override(Ext.tree.TreeEditor, {
			beforeNodeClick : function(){},
			onNodeDblClick : function(node, e){
				this.triggerEdit(node);
			}
		});
		
		//make nodes editable
		var treeEditor = new Ext.tree.TreeEditor(this.patternPanel, {
			constrain: true, //constrains editor to the viewport
			completeOnEnter: true,
			ignoreNoChange: true
		});		
		
		//register callback to handle change of label
		treeEditor.on("complete", function(editor, value, startValue) { 
			var pattern = editor.editNode.pattern;

			return pattern.setName(value);
		});
				
		//add pattern panel
		this.facade.addToRegion("West", this.patternPanel, null);
			
		//creating a dragzone
		var dragZone = new Ext.dd.DragZone(this.patternRoot.getUI().getEl(), {shadow: !Ext.isMac});
		
		//register drag and drop function, curry in the dragzone
		dragZone.afterDragDrop = this.afterDragDrop.bind(this, dragZone);
		
		//reload pattern / reinitialize plugin when stencilset changes.
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_STENCIL_SET_LOADED, this.initialize.bind(this));
		
		this.initialize();

		
	},
	
	/**
	 *	Initializes the plugin, i.e. creates new local repository, loads pattern, etc. Can safely be called when stencilset changes.
	 */
	initialize : function() {
		
		//create patternRepos
		var ssNameSpace = $A(this.facade.getStencilSets()).flatten().flatten()[0];
		
		//options for pattern repository
		var opt = {
			ssNameSpace: ssNameSpace,                              
			onPatternLoad: this.addPatternNodes.bind(this),        
			onPatternAdd: this.addPatternNode.bind(this),          
			onPatternRemove: this.deletePatternNode.bind(this)
		};    
		
		//create pattern repository and load all pattern for current stencil set
		//nodes are created through onPatternLoad callback
		this.patternRepos = new ORYX.Plugins.Patterns.PatternRepository(opt);	
		this.loadAllPattern();	
		
	},
	
			
	/**
	 * Callback for creating a new pattern from the current selection and saving it on the server.
	 */
	selAsPattern: function() {
		
		var selection = this.facade.getSelection();
		
		//json everything
		var jsonSel = selection.collect(function(element) {
			return element.toJSON();
		});
		
		//clean it up
		jsonSel = this.removeDanglingEdges(jsonSel);
		jsonSel = this.removeObsoleteReferences(jsonSel);
				
		this.addNewPattern(jsonSel);	
		
	},
	
	/**
	 * Load all pattern from the server and display them in the pattern panel.
	 */
	loadAllPattern: function() {
		this.patternRepos.loadPattern();
		
	},
	
	/**
	 * Removes all pattern nodes from the pattern panel without deleting them on the server
	 */
	resetPatternPanel: function() { 
		while(this.patternRoot.firstChild) {
		    this.patternRoot.removeChild(this.patternRoot.firstChild);
		}
	},
	
	/**
	* Adds a new pattern to the server and adds pattern node in pattern panel.
	* @param {Array} serPattern raw (directly from canvas) serialized Shapes in JSON format
	*/
	addNewPattern: function(serPattern) {
		
		var opt = {
			serPattern: serPattern,
			name: ORYX.I18N.Patterns.newPattern,
			imageUrl: undefined,
			id: undefined
		};
		
		var pattern = new ORYX.Plugins.Patterns.Pattern(opt);
		
		this.patternRepos.addPattern(pattern);
	},
	
	/**
	 * Adds the pattern from the supplied array as tree nodes in the pattern panel
	 * @param {Array} patternArray consists of ORYX.Plugins.Patterns.Pattern instances
	 */
	addPatternNodes: function(patternArray) {

		this.resetPatternPanel();
		
		patternArray.each(function(pattern){ 
			this.addPatternNode(pattern);
		}.bind(this));
	},
	
	/**
	* Add the nodes for the supplied pattern to pattern panel
	* @param {ORYX.Plugins.Patterns.Pattern} pattern to be added pattern
	*/
	addPatternNode: function(pattern) {		

		var newNode = new ORYX.Plugins.Patterns.PatternNode(pattern);
				 	
		this.patternRoot.appendChild(newNode);
		newNode.render();
		
		var ui = newNode.getUI();
		
		//register the pattern on drag and drop
		Ext.dd.Registry.register(ui.elNode, {
			node: ui.node,
			handles: [ui.elNode, ui.textNode],
			isHandle: false
		});
		
		this.patternRoot.expand();	
		
	},
	
	/**
	 * Removes the node of a pattern from the pattern panel
	 * @param {ORYX.Plugins.Patterns.Pattern} the server-side deleted pattern whose tree node representation
	 * shall be deleted.
	 */
	deletePatternNode: function(pattern) {
		pattern.treeNode.remove();
	},
	
	/**
	 * Inserts the dropped pattern from the pattern node in the canvas
	 * @param {Ext.dd.DragZone} dragZone
	 * @param {Ext.dd.DragDrop} target The drop target
	 * @param {Event e} event The event object
	 * @param {String} id The id of the dropped element
	 */
	afterDragDrop: function(dragZone, target, event, id) {
		
		//Hide the highlighting
		//do i really need this???????
		this.facade.raiseEvent({type: ORYX.CONFIG.EVENT_HIGHLIGHT_HIDE, highlightId:'patternRepo.added'});
		this.facade.raiseEvent({type: ORYX.CONFIG.EVENT_HIGHLIGHT_HIDE, highlightId:'patternRepo.attached'});
		
		//Check if drop is allowed
		var proxy = dragZone.getProxy();
		if(proxy.dropStatus == proxy.dropNotAllowed) {return;}
				
		var templatePatternShapesSer = Ext.dd.Registry.getHandle(target.DDM.currentTarget).node.pattern.serPattern;
		var templatePatternShapes = Ext.decode(templatePatternShapesSer);
		
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
		
		
		var centralPoint = this.findCentralPoint(patternShapes);
		
		var transformVector = {
			x: pos.x - centralPoint.x,
			y: pos.y - centralPoint.y
		};
		patternShapes = this.transformPattern(patternShapes, transformVector);
		
		//correct position of pattern if it leaves canvas to the left or right side of the canvas
		//transform vector is {x:0, y:0} if no correction is necessary
		transformVector = this.calculateCorrectionVector(this.facade.getCanvas().bounds, patternShapes);
		patternShapes = this.transformPattern(patternShapes, transformVector);
		
		//construct instance of command class to support undo and redo.
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
					selection = selection.without(shape);
				}.bind(this));
				
				this.facade.setSelection(selection);				
				this.facade.getCanvas().update();				
			}
		});
		
		var command = new commandClass(patternShapes, this.facade, centralPoint, pos, this);
		
		this.facade.executeCommands([command]);
	},
	
	/**
	 * Moves the pattern relatively to an old position to the new position
	 * @param {Array} patternShapes An Array of serialized oryx shapes
	 * @param {Object} transformVector Object whose x and y coordinate describe the vector by which
	 * all shapes in the pattern have to be moved.
	 */
	transformPattern: function(patternShapes, transformVector) {	
		
		//no transformation necessary? (Guarding clause)
		if (transformVector.x === 0 && transformVector.y === 0) return patternShapes;
		
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
				
				//recursion
				posChange(transVector, shape.childShapes);
			}.bind(this, transVector));
		};
		
		posChange(transformVector, patternShapes);
		
		return patternShapes;
	},
	
	/**
	* Calculates the vector by which the pattern has to be moved in order not to leave
	* the supplied bounds in the upper left corner.
	* @param {ORYX.Core.Bounds} outerBounds Bounds that shapes have to fit in
	* @param {Array} shapeArray The Shapes that have to fit into the bounds
	* @returns The correction vector
	*/
	calculateCorrectionVector: function(outerBounds, shapeArray) {
		var correctionVector = {
			x: 0,
			y: 0
		};
		
		shapeArray.each(function(shape) {
			if (shape.bounds.upperLeft.x < outerBounds.upperLeft().x) {
				correctionVector.x = Math.max(correctionVector.x, outerBounds.upperLeft().x - shape.bounds.upperLeft.x);
			}
			if (shape.bounds.upperLeft.y < outerBounds.upperLeft().y) {
				correctionVector.y = Math.max(correctionVector.y, outerBounds.upperLeft().y - shape.bounds.upperLeft.y);
			}
		});
		
		return correctionVector;
	},
	
	/**
	 * Determines the central point in an array of serialized shapes.
	 * @param {Array} shapeArray Contains serialized shapes (not JSON Strings, but JSON representations). 
	 */
	findCentralPoint: function(shapeArray) {
		
		if(shapeArray.size() === 0) return;
		
		var initBounds = new ORYX.Core.Bounds(shapeArray[0].bounds.upperLeft, shapeArray[0].bounds.lowerRight);
		
		var shapeBounds = shapeArray.inject(initBounds, function(bounds, shape) {
			var add = new ORYX.Core.Bounds(shape.bounds.upperLeft, shape.bounds.lowerRight);
			bounds.include(add);
			return bounds;
		});
		
		return shapeBounds.center();
		
	},
	
	/**
     * This method renews all resource Ids and according references.
     * @param {Object} jsonObject
     * @throws {SyntaxError} If the serialized json object contains syntax errors.
     * @return {Object} The jsonObject with renewed ids.
     * @private
     */
    renewResourceIds: function(jsonObjectArray){
       
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

	/**
	* Removes all edges that reference non-existing (in the pattern) shapes.
	* @param {Array} jsonObjectArray JSONObjects of shapes
	*/
	removeDanglingEdges: function(jsonObjectArray) {
		//recursion deep check???
		var result = jsonObjectArray.select(function(jsonObjectArray, serShape) {
			if(!serShape.target) { //is node?
				return true;
			} else { //is edge
				return this.isTargetInCollection(serShape, jsonObjectArray);			}
		}.bind(this, jsonObjectArray));
		
		return result;
	},
	
	/**
	 * Tests if the target of a shapes is itself part of the supplied shape collection.
	 * @param {Object} serShape JSONObject whose target should be tested
	 * @param {Array} collection The collection of shapes that should be considered
	 * @return {Boolean} True if the target of serShape is contained in the collection.
	 */
	isTargetInCollection: function(serShape, collection) { 
		return collection.any(function(serShape, possibleTarget) {
			return serShape.target.resourceId == possibleTarget.resourceId;
		}.bind(this, serShape));
	},
	
	/**
	 * Removes all references of outgoing edges whose edges are not contained in the shape collection
	 * from the collection of shapes.
	 * @param {Array} jsonObjectArray all shapes whose outgoing edges should be checked for inclusion.
	 * @returns Array of shapes with no dangling references to missing outgoing edges.  
	 */
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

/**
 * Represents a pattern.
 * @class ORYX.Plugins.Patterns.Pattern
 * @extends Clazz
 * @param {Object} opt Can contain the serPattern, id, imageUrl, name to be set in the new instance.
 */
ORYX.Plugins.Patterns.Pattern = Clazz.extend(
	/** @lends ORYX.Plugins.Patterns.Pattern.prototype */
	{
	/**
	 * Array of serialized pattern shapes, i.e. JSON objects.
	 */
	serPattern : undefined,
	
	/**
	 * The ID of the pattern as set by the server
	 */
	id : undefined,
	
	/**
	 * The URL of the thumbnail image of the pattern
	 */
	imageUrl : undefined,
	
	/**
	 * The name of the pattern
	 */
	name : undefined,
	
	/**
	 * The repository that saves the pattern
	 */
	repos: undefined,
	
	/**
	 * The tree node that represents the pattern.
	 */
	treeNode: undefined, //saved the "viewer" tree node
	
	/**
	 * @constructor
	 */
	construct: function(opt) {
		if(opt.serPattern !== null) this.serPattern = opt.serPattern;
		if(opt.id !== null) this.id = opt.id;
		if(opt.imageUrl !== null) this.imageUrl = opt.imageUrl;
		if(opt.name !== null) this.name = opt.name;
	},
	
	/**
	 * Sets the name of the pattern and updates the server representation of this pattern.
	 */
	setName: function(name) {
		if (this.repos == null) return;
		
		this.name = name;
		this.repos.savePattern(this);
	},
	
	/**
	 * Removes the pattern from the server.
	 */
	remove: function() {
		this.repos.removePattern(this); //toggles through removal callback of treenode!
	},
	
	/**
	 * Creates a JSON representation of the pattern containing only the id, name, serPattern, imageUrl.
	 * @returns {Object} JSON representation of this pattern
	 */
	toJSONString: function() {
		return Ext.encode({
			id: this.id,
			name: this.name,
			serPattern: this.serPattern,
			imageUrl: this.imageUrl
		});
	}
	
});

/**
 * Represents a loader for patterns for a specific stencilset.
 * @class ORYX.Plugins.Patterns.PatternRepository
 * @extends Clazz
 * @param {Object} param Parameter Object that takes:
 * ssNameSpace The namespace of the stencil for which this loader is intended,
 * onPatternLoad function(Array patterns)} Callback when a pattern are loaded from the server via loadPattern(),
 * onPatternAdd {function(pattern)} Callback when a pattern is added to the repository. Provides in pattern the pattern as received from server.
 * onPatternRemove {function()} Callback when a pattern is deleted.
 */
ORYX.Plugins.Patterns.PatternRepository = Clazz.extend(
	/** @lends ORYX.Plugins.Patterns.PatternRepository.prototype */
	{
	
	/**
	 * The name space of the stencil set for which pattern can be maintained. 
	 */
	ssNameSpace : undefined, 
	
	/**
	 * Callback when pattern are loaded.
	 */
	onPatternLoad: function(patternArray){}, 
	
	/**
	 * Callback when a single pattern is added.
	 */
	onPatternAdd: function(pattern){},
	
	/**
	 * Callback when a single pattern is deleted.
	 */
	onPatternRemove: function(){},
	
	/**
	 * @constructor
	 */
	construct: function(opt) { 
		this.ssNameSpace = opt.ssNameSpace;
		if (opt.onPatternLoad) {
			this.onPatternLoad = opt.onPatternLoad;
		}
		if (opt.onPatternAdd) {
			this.onPatternAdd = opt.onPatternAdd;
		}
		if (opt.onPatternRemove) {
			this.onPatternRemove = opt.onPatternRemove;			
		}
	},
	
	/**
	 * Loads all pattern for set stencil set from server. Fires callback onPatternLoad and provides callback with array of pattern.
	 * @returns {Array} An array containing all the loaded pattern.
	 */
	loadPattern: function() {
		var patternArray = [];
		this._sendRequest("GET", {ssNameSpace: this.ssNameSpace}, function(resp) {
			var patterns = Ext.decode(resp);
			patterns.each(function(opt) {
				var pattern = new ORYX.Plugins.Patterns.Pattern(opt);
				pattern.repos = this;
				patternArray.push(pattern);
			}.bind(this));
			this.onPatternLoad(patternArray);
		}.bind(this));
		
		return patternArray;
	},
		
	/**
	 * Adds the supplied pattern to the server.
	 * @param {ORYX.Plugins.Patterns.Pattern} pattern The pattern to be added to the server. 
	 */
	addPattern: function(pattern) {
		var params = {
			pattern: pattern.toJSONString(),
			ssNameSpace: this.ssNameSpace
		};
		
		this._sendRequest("POST", params, function(resp){
			var opt = Ext.decode(resp);
			var pattern = new ORYX.Plugins.Patterns.Pattern(opt);
			pattern.repos = this;
			this.onPatternAdd(pattern);
		}.bind(this));
	},
	
	/**
	 * Updates a pattern that is already saved on the server with the supplied values. 
	 * Not supplied values are overriden.
	 * @param {ORYX.Plugins.Patterns.Pattern} pattern The pattern to be saved.
	 */
	savePattern: function(pattern) {
		this._sendRequest("PUT", {pattern: pattern.toJSONString(), ssNameSpace: this.ssNameSpace});
	},
	
	/**
	 * Removes / Delete a pattern from the server.
	 * @param {ORYX.Plugins.Patterns.Pattern} pattern The pattern to be deleted from the server.
	 */
	removePattern: function(pattern) {
		var params = {
			pattern: pattern.toJSONString(),
			ssNameSpace: this.ssNameSpace
		};
		this._sendRequest("DELETE", params, function(resp) {  //onSuccess
			this.onPatternRemove(pattern);
		}.bind(this));
	},
	
	/**
	 * Sends an AJAX request to the server directed to the set path in ORYX.CONFIG.PATTERN_SERVER_ROOT
	 * @private
	 * @param {String} method The used method to communicate with the server. GET and POST will be 
	 * translated properly to HTTP METHODS. PUT and DELETED are sent as POSTS with the _method parameter
	 * set accordingly.
	 * @param {Object} params The parameters to be set in the request.
	 * @param {function(responseText)} successcallback Will be called on success of the server request.
	 * @param {function()} failedcallback Will be called if transport failed. 
	 */
	_sendRequest: function( method, params, successcallback, failedcallback ){

		var suc = false;

		new Ajax.Request(
		ORYX.CONFIG.PATTERNS, //url is fixed 
		{
           method			: method,
           asynchronous		: true, 
           parameters		: params,
		   onSuccess		: function(transport) 
		   {
				suc = true;
		
				if(successcallback)
				{
					successcallback( transport.responseText );	
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
					this._showErrorMessageBox(ORYX.I18N.Patterns.patternRepository, ORYX.I18N.Patterns.comFailed);
					ORYX.Log.warn("Communication failed: " + transport.responseText);
				}					
		   }.bind(this)		
		});
		
		return suc;		
	},
	
	/**
	 * Displays a simple error message box in the middle of the screen.
	 * @private
	 * @params {String} title The title of the error message dialog.
	 * @params {String} msg The message that should be displayed in the dialog.
	 */
	_showErrorMessageBox: function(title, msg)
	{
        Ext.MessageBox.show({
           title: title,
           msg: msg,
           buttons: Ext.MessageBox.OK,
           icon: Ext.MessageBox.ERROR
       });
	}
});

/**
 * Represents a tree node. Simplifies initialization of a tree node.
 * @class ORYX.Plugins.Patterns.PatterNode
 * @extends Ext.tree.TreeNode
 * @params {ORYX.Plugins.Patterns.Pattern} pattern The pattern that should be displayed by the tree node.
 */
ORYX.Plugins.Patterns.PatternNode = Ext.extend(Ext.tree.TreeNode, 
	/** @lends ORYX.Plugins.Patterns.PatternNode.prototype */
	{
	
	/**
	 * The pattern that is represented by this pattern tree node. {ORYX.Plugins.Patterns.Pattern}
	 */
	pattern: undefined, 
	
	/**
	 * @constructor
	 */
	constructor: function(pattern) {
		//normally ext uses initComponent for the following, but
		//no initComponent protocoll in TreeNode, thus using constructor!
		this.pattern = pattern;
		
		//calling superclass constructor		
		ORYX.Plugins.Patterns.PatternNode.superclass.constructor.call(this, {
			allowChildren: false,
			leaf: true,
			iconCls: 'headerShapeRepImg',
			cls: 'ShapeRepEntree PatternRepEntry',
			icon:  ORYX.CONFIG.PATTERN_ADD_ICON,
			allowDrag: false,
			allowDrop: false,
			uiProvider: ORYX.Plugins.Patterns.PatternNodeUI,
			text: this.pattern.name
		});
		
		pattern.treeNode = this;
	},
	
	/**
	 * Prevents that the delete button is shown in the Ghost Proxy of Drag and Drop of Ext.
	 */
	beforeMove: function(tree, node, newParent, oldParent, index) {
		node.getUI().deleteButton.hide();
	}
});

/**
 * Provides customized appearance of a tree node for the pattern node.
 * @class ORYX.Plugins.Patterns.PatternNodeUI
 * @extends Ext.tree.TreeNodeUI
 */
ORYX.Plugins.Patterns.PatternNodeUI = Ext.extend(Ext.tree.TreeNodeUI, 
	/** @lends ORYX.Plugins.Patterns.PatternNodeUI.prototype */
	{
		/**
		 * Renders the node and add the delete button.
		 * @param render  (cf. Ext framework)
		 */
		render: function(bulkRender) { 
			//	this.superclass.render.apply(this, arguments);
			//onRender not properly implemented in used Ext Version!
			if (this.rendered) return;
			
			ORYX.Plugins.Patterns.PatternNodeUI.superclass.render.apply(this, arguments);
						
			var span = document.createElement("span");
			span.className = "PatternDeleteButton";
			this.elNode.appendChild(span);
			
			var deleteFunction = function() {
				var pattern = this.node.pattern;
				pattern.remove();
			};
			
			this.deleteButton = new Ext.Button({
									icon: ORYX.CONFIG.PATTERN_DELETE_ICON, 
									handler: deleteFunction.bind(this),
									cls: "x-btn-icon",
									renderTo: span
								});
								
			//this.deleteButton.getEl().fadeOut();
			this.deleteButton.hide();
			
		},
		
		/**
		 * Displays the delete button when mouse is over the tree node
		 */
		onOver: function() {
			ORYX.Plugins.Patterns.PatternNodeUI.superclass.onOver.apply(this, arguments);
			
			this.deleteButton.show();
		},
		
		/**
		 * Hides the delete button when mouse is leaving the tree node.
		 */
		onOut: function() { 
 			ORYX.Plugins.Patterns.PatternNodeUI.superclass.onOut.apply(this, arguments);
			
			this.deleteButton.hide();
		}
			
});

/**
 * Represents the shape menu like button used to add a pattern.
 * The button is only shown when there are more than two elements selected.
 * @class ORYX.Plugins.Patterns.PatternButton
 * @extends Clazz
 * @param facade The standard plugin facade.
 * @param onClick Callback that is called when the button is triggered.
 */
ORYX.Plugins.Patterns.PatternButton = Clazz.extend(
	/** @lends ORYX.Plugins.Patterns.PatternButton.prototype */
	{
	
	/**
	 * The HTML node representing the button itself
	 */
	button : undefined,
	
	/**
	 * standard plugin facade
	 */
	facade : undefined,
	
	/**
	 * Callback, when the button is clicked
	 */
	onClick: function(){},
	
	
	/**
	 * @constructor
	 */
	construct: function(facade, onClick) {
		
		this.facade = facade;		
		this.onClick = onClick;
		this.initialize();
		
	},
	
	/**
	 * Adds a div-element to the canvas that represents the "mark as pattern" button which appears
	 * at the right side of the selection. 
	 * Also adds the necessary event listeners for the button.
	 */
	initialize : function() {
		// graft the button.
		this.button = ORYX.Editor.graft("http://www.w3.org/1999/xhtml", $(null),
			['div', {'class': 'Oryx_button'}]);
		
		var imgOptions = {src: ORYX.CONFIG.PATTERN_ADD_ICON}; 

		// graft and update icon (not in grafting for ns reasons).
		ORYX.Editor.graft("http://www.w3.org/1999/xhtml", this.button,
				['img', imgOptions]);
				
		this.facade.getCanvas().getHTMLContainer().appendChild(this.button);
		
		this.hidePatternButton();
		
		//handler for the button
		this.button.addEventListener(ORYX.CONFIG.EVENT_MOUSEOVER, this.buttonHover.bind(this), false);
		this.button.addEventListener(ORYX.CONFIG.EVENT_MOUSEOUT, this.buttonUnHover.bind(this), false);
		this.button.addEventListener(ORYX.CONFIG.EVENT_MOUSEDOWN, this.buttonActivate.bind(this), false);
		this.button.addEventListener(ORYX.CONFIG.EVENT_MOUSEUP, this.buttonHover.bind(this), false);
		this.button.addEventListener('click', this.buttonTrigger.bind(this), false);
		
		//add handler to trigger the visibility of the button
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_SELECTION_CHANGED, this.togglePatternButton.bind(this));
	},
	
	togglePatternButton: function() {
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
	
	/**
	 * Displays the pattern button
	 */
	showPatternButton: function() {
		this.relocatePatternButton();
		this.button.style.display = "";
		this.buttonVisible = true;
		this.facade.getCanvas().update();
	},
	
	/**
	 * Hides the pattern button
	 */
	hidePatternButton: function() {
		this.button.style.display = "none";
		this.buttonVisible = false;
		this.facade.getCanvas().update();
	},
		
	/**
	 * Moves the pattern button to the upper right side of the current selection
	 */
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
	
	/**
	 * Renders the pattern button solid instead of transparent
	 */
	showButtonOpaque: function() {
		this.button.style.opacity = 1.0;
	},
	
	/**
	 * Renders the pattern button half transparent instead of solid
	 */
	showButtonTransparent: function() {
		this.button.style.opacity = 0.5;
	},
	
	/**
	 * Changes appearance of pattern button when clicked.
	 * Add the css class Oryx_down
	 */
	buttonActivate: function() {
		this.button.addClassName('Oryx_down');
	},
	
	/**
	 * Changes appearance of pattern button when mouse is over the button.
	 * Adds the css class Oryx_hover
	 */
	buttonHover: function() {
		this.button.addClassName('Oryx_hover');
	},
	
	/**
	 * Changes the the appearance when the mouse moves out of the button.
	 * Removes the classes Oryx_down and Oryx_hover.
	 */
	buttonUnHover: function() {
		if(this.button.hasClassName('Oryx_down'))
			this.button.removeClassName('Oryx_down');

		if(this.button.hasClassName('Oryx_hover'))
			this.button.removeClassName('Oryx_hover');
	},
	
	/**
	 * Callback when clicking the button.
	 */
	buttonTrigger: function(evt) {
		this.onClick();
	}
});