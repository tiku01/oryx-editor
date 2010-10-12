/**
 * Copyright (c) 2010
 * Uwe Hartmann, Helen Kaltegaertner
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
 * This is the gadget in the mashup responsible for creating Correspondence between process models.
 * @class Correspondence
 * @constructor
 */
Correspondence = function(){
	
	Correspondence.superclass.constructor.call(this, "Correspondence");
	this.table = null;
	this.connectionCollection = new ConnectionCollection();
	this.selectedViewers = [];	
	this.availableModelViewers = [];	
	this.discoveryMode = false;
	this.connector = null;
	this.icon = this.GADGET_BASE + "icons/chart_line.png";
	this.init();
};



YAHOO.lang.extend( Correspondence, AbstractGadget, {	
	
	/**
	 * Draws and initializes the UI
	 */
	init: function() {
	
		var layout = new YAHOO.widget.Layout({ 
			units: [ 
	            { position: 'top', header: 'Correspondence - Menu', height: '70px', resize: false, body: 'top', gutter: '5px', collapse: true}, 
	            { position: 'center', body: 'center', header: 'Connections', gutter: '5px'} 
	            ] 
	     }); 
		
		layout.render();
		
		// enter and leave selection mode
		var connectButton = new YAHOO.widget.Button({
			id :		"connectButton", 
			container :	"button_group", 
			title : 	"create a new association between the currently selected shapes" 
		});
		connectButton.on("click", this.createConnection.bind(this));
		connectButton.setStyle("background", "url('" + this.GADGET_BASE + "correspondence/icons/cart_add.png') no-repeat center");
		connectButton.className ="button";
		
		var discoverButton = new YAHOO.widget.Button({
			id :		"discoverButton", 
			container :	"button_group", 
			title : 	"discover connections associated with a specific shape" 
		});
		discoverButton.on("click", this.enterDiscoveryMode.bind(this));
		discoverButton.setStyle("background", "url('" + this.GADGET_BASE + "correspondence/icons/magnifier.png') no-repeat center");
		discoverButton.className ="button";
		
		//reset connections
		var deleteButton = new YAHOO.widget.Button({
			id :		"deleteButton", 
			container :	"button_group",
			title : 	"delete all connections"
		});
		deleteButton.on("click", this.resetConnections.bind(this));
		deleteButton.setStyle("background", "url('" + this.GADGET_BASE + "correspondence/icons/cancel.png') no-repeat center");
		deleteButton.className ="button";
		
		//save connections permanently
		var saveButton = new YAHOO.widget.Button({ 
			id :		"saveButton", 
			container :	"button_group" ,
			title : 	"save connections"
		});
		saveButton.on("click", this.saveConnections.bind(this));
		saveButton.setStyle("background", "url('" + this.GADGET_BASE + "correspondence/icons/disk.png') no-repeat center");
		saveButton.className ="button";
		
		//opens a dialog to import a bunch of connections between models
		var loadButton = new YAHOO.widget.Button({ 
			id :		"loadConnectButton", 
			container :	"button_group" ,
			title : 	"load connections"
		});		
		loadButton.on("click", this.loadConnections.bind(this));
		loadButton.setStyle("background", "url('" + this.GADGET_BASE + "correspondence/icons/drive_add.png') no-repeat center");
		loadButton.className ="button";
		
		//determine correspondences automatically 
		var autoConnectButton = new YAHOO.widget.Button({ 
			id :		"autoConnectButton", 
			container :	"button_group" ,
			title : 	"create correspondences automatically"
		});
		autoConnectButton.on("click", this.autoConnect.bind(this));
		autoConnectButton.setStyle("background", "url('" + this.GADGET_BASE + "correspondence/icons/wand.png') no-repeat center");
		autoConnectButton.className ="button";
		
		//POST to Oryx-Backend for compatibility check 
		var compatibilityButton = new YAHOO.widget.Button({ 
			id :		"compatibilityButton", 
			container :	"button_group" ,
			title : 	"check compatibility"
		});
		compatibilityButton.on("click", this.checkCompatibility.bind(this));
		compatibilityButton.setStyle("background", "url('" + this.GADGET_BASE + "correspondence/icons/cog_go.png') no-repeat center");
		compatibilityButton.className ="button";

		var el = document.createElement("li");
		el.className = "connection";
		this.enterDiscoveryMode();
	},
	

	
	
	/**
	 * show dialog to configure new connection
	 * 
	 */
	createConnection: function(){
		
		// leave discovery mode if currently active
		this.stopDiscoveryMode();		
		
		for (var i = 0; i < this.connectionCollection.connections.length; i++){
			if (this.connectionCollection.connections[i] && this.connectionCollection.connections[i].isActive ) {
				this.connectionCollection.connections[i].deselect();
			}
		}
		var stopSelection = function() {
			this.connector.stopSelectionMode();
			this.enterDiscoveryMode();
		};
		//this.connectionMode = true;			
		this.disable("Add Connection", "Please select the nodes in the viewers to associate them with each other.", stopSelection.bind(this));
		this.connector = new Connector(this);
		

	},
	
	/**
	 * Adds a connection between models to represent a correspondence 
	 * @param {Connection} connection
	 */
	addConnection : function(connection){
		this.connectionCollection.addConnection(connection);
	},
	
	/**
	 * store connections permanently
	 * 
	 */
	saveConnections: function(){		
		
		this.connectionCollection.save();	
		
	},
	
	/**
	 * loads Connections from a file
	 */	
	loadConnections: function() {
		var loadJSON = function(jsonText){
			//first remove all current connection
			loadingScreen.show();			
			this.connectionCollection.clear();
			this.connectionCollection = new ConnectionCollection();
			this.connectionCollection.load(jsonText,this,this.onConnectionCollectionLoaded.bind(this));
		};
		
		var dialog = new ModelLoaderDialog(loadJSON.bind(this));
		dialog.show();		
	},
	
	/**
	 * Function executed after having loaded a file, hides the loading screen
	 */
	onConnectionCollectionLoaded : function() {
		loadingScreen.hide();
		this.enterDiscoveryMode();
	},
	

	
	/**
	 * remove all connections
	 */
	resetConnections: function(){
		this.connectionCollection.connections.clear();
		this.connectionCollection.connections = [];
		$("connections").innerHTML = "";
	
		this.resetModels();
		
	},
	
	/**
	 * reset markers and selections to enter the discovery Mode
	 */	
	enterDiscoveryMode : function(){		
		this.resetModels();
		this.discoveryMode = true;
		this.discovery = new Discovery(this);
		
	},
	
	stopDiscoveryMode : function() {		
			this.discoveryMode = false;
			this.discovery.stopDiscoveryMode();
			this.discovery = null;
		
	},
	/**
	 * Clears all selections ,markers etc. from the given viewers
	 * @param {Array of Viewer} viewers
	 */
	clearViewers : function(viewers){			
		for (var i = 0; i < viewers.length; i++){
			this.removeMarker(viewers[i], "all");
			this.resetSelection(viewers[i]);
			this.undoGrey(viewers[i], "all");
		}
	},
	
	/**
	 * remove shadows, markers and selections from all viewers
	 */
	resetModels : function(){
		

	
		this.sendViewers(this.clearViewers, this);
		
	},
	
	openAutoConnectDialog : function(){
		if (this.availableModelViewers.length>=2) {
			var dialog = new ModelChooserDialog(this.availableModelViewers, this);
			dialog.show();
		} else {
			alert("You need to open at least two viewers to connect them automatically.");
		}
	},	
	
	/**
	 * Executes the auto connector when the viewers were selected
	 * @param {Array of {Integer, ModelViewer}} selectedViewers
	 */
	onModelChooserDialogClose : function(selectedViewers) {	
		loadingScreen.show();	
		this.connectionCollection.autoConnect(selectedViewers, this);
		loadingScreen.hide();
		this.enterDiscoveryMode();
		
	},
	
	/**
	 * creates connections between all viewers on the screen automatically 
	 */
	autoConnect : function() {
		this.executeWithAllViewers(this.openAutoConnectDialog.bind(this));
	},
	
	
	/**
	 * Executes aFunction with arguments in the context of all viewers currently displayed on the screen
	 */
	executeWithAllViewers : function (aFunction, argums) {	
		
		this.availableModelViewers = [];
		var getViewers = function(viewers){
			var args = [];
			if (viewers.length!==0) {
				for (var i = 0; i < viewers.length; i++){
					args = [];
					args.push(viewers[i]);  
					args.push(viewers.length);
					args.push(aFunction);
					args.push(argums);
					this.getViewer(viewers[i],this.addViewer,this,args);
				}	
			} else {
				args = [];
				args.push(null);  
				args.push(viewers.length);
				args.push(aFunction);
				args.push(argums);
				this.addViewer(null,args)
			}
		};	
		this.sendViewers(getViewers, this);		

	},
	
	addViewer : function(reply,args) {		
		var numberViewers = args[1];
		if (args[0]!=null && numberViewers>0) {
			this.availableModelViewers.push({
				index : 	args[0],
				viewer : 		reply			
			});
		}
		if (this.availableModelViewers.length==numberViewers) {
			args[2](args[3]);
		}
	},
	
	markActiveConnection : function(para) {
		var rowIndex = para.target.rowIndex-1;
		for (var i = 0; i < this.connectionCollection.connections.length; i++){
			if (this.connectionCollection.connections[i].associatedRow == rowIndex) {
				this.connectionCollection.connections[i].highlightInViewer();
			}		
		}
		
	},
	
	checkCompatibility : function() {
		new Ajax.Request("/oryx/compatibility", 
				 {
					method			: "post",
					asynchronous 	: false,
					onSuccess		: function(response){
						alert(response.responseText);
					},
					onFailure		: function(){
						alert('Server communication failed!');
					},
					parameters 		: { data : this.connectionCollection.connections.toJSON() }
				});
	},
	
	/**
	 * 	Creates a grey overlay over the gadget, so that it cannot be edited anymore. 
	 * 	Shows a message window with the title modename and shows message.
	 *  If the user clicks done, the function onClose is executed.
	 *  @param {String} modename displayed in the popup header
	 *  @param {String} message 
	 *  @param {Function} onClose will be executed when closing this window
	 */
	disable : function(modename, message, onClose) {
		
		var handleSubmit = function() {
			onClose();
			this.cancel();
		};
		
		
		var disableScreen =  new YAHOO.widget.Dialog("disableScreen",   
	            { width:"240px",  
	              fixedcenter:true,  
	              close:false,  
	              draggable:false,  
	              zindex:4, 
	              modal:true, 
	              center: true,
	              visible:false, 	              
				  buttons : [ { text:"Done", handler:handleSubmit, isDefault:true } ]
	            }  
	        ); 

		
		disableScreen.setHeader(modename); 
		disableScreen.setBody(message); 
		disableScreen.render(document.body);
		disableScreen.show();
	}
});