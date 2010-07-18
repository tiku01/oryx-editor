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

Correspondence = function(){
	
	Correspondence.superclass.constructor.call(this, "Correspondence");
	this.table 				= null;
	this.connectionCollection	= new ConnectionCollection();
	this.selectedViewers	= [];	
	this.availableModelViewers	= [];
	this.connectionMode 	= false;
	this.discoveryMode		= false;
	this.connector 			= null;
	this.init();
}



YAHOO.lang.extend( Correspondence, AbstractGadget, {	
	
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


		var el = document.createElement("li");
		el.className = "connection";
		

		
	},
	
	
	/*
	 * render table in which associations will be displayed
	 * 
	 */
	initTable: function(){
		
		var sortComments = function(a, b, desc) {
            // deal with empty values
            if(!YAHOO.lang.isValue(a)) {
                return (!YAHOO.lang.isValue(b)) ? 0 : 1;
            }
            else if(!YAHOO.lang.isValue(b)) {
                return -1;
            }

            // first compare by comment
            var comp = YAHOO.util.Sort.compare;
            var compState = comp(a.getData("comment"), b.getData("comment"), desc);

            // If titles are equal, then compare by url
            return (compState !== 0) ? compState : comp(a.getData("modelA"), b.getData("modelB"), desc);
        };
        
        var expansionFormatter  = function(elCell, oRecord, oColumn, oData) { 
        	var cell_element    = elCell.parentNode; 

        	//Set trigger 
        	//if( oData ){		// Row is closed 
        		cell_element.innerHTML = '<img src= "/gadgets/files/gadgets/correspondence/icons/bullet_delete.png" onclick=""/>';
        		"<a href='#' onclick=" + 'Correspondence.resetConnections.bind("' + this + '")'
			
        		//YAHOO.util.Dom.addClass( cell_element, 
                //	"yui-dt-expandablerow-trigger" ); 
        	//}
        }.bind(this); 
        
		var formatUrl = function(elCell, oRecord, oColumn, sData) { 
			
			var args = this.SERVER_BASE + this.REPOSITORY_BASE + oRecord.getData('url') 
							+ '.' + oRecord.getData('comment').replace(/\s/g, "_");
			
        	elCell.innerHTML = "<a href='#' onclick=" + 'correspondence.saveConnections();' + ">" + '<img src="/gadgets/files/gadgets/correspondence/icons/delete.png" />' + "</a>";
        }.bind(this); 
        
        
        var columnDefs = [
            {key:"modelA", label: 'modelA', width:90, resizeable:true, sortable:true},
            {key:"modelB", label: 'modelB', width:90, resizeable:true, sortable:true},
            {key:"comment", label: "comment", width:90, resizeable: true, editor:"textbox", sortable: true, 
                    sortOptions:{sortFunction:sortComments}},
            {key:"farm", label: 'farm', width:90, resizeable:true, formatter: formatUrl}
        ];

        var data = new YAHOO.util.DataSource([])
        data.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        data.responseSchema = {
            fields: ["modelA","modelB","comment","farm"]
        };
		
        var configs = {
            sortedBy:{key:"comment",dir:"asc"},
            paginator: new YAHOO.widget.Paginator({
                rowsPerPage: 5,
                template: YAHOO.widget.Paginator.TEMPLATE_ROWS_PER_PAGE,
                rowsPerPageOptions: [5,10],
                pageLinks: 3
            }),
            draggableColumns:false
        }

        this.table = new YAHOO.widget.DataTable("table", columnDefs, data, configs);
        
        //enable row selection
        this.table.subscribe("rowClickEvent", this.table.onEventSelectRow);
        this.table.subscribe("rowClickEvent", this.markActiveConnection);
		this.table.subscribe("cellDblclickEvent",this.table.onEventShowCellEditor);
	},

	
	
	/*
	 * show dialog to configure new connection
	 * 
	 */
	createConnection: function(){
		
		// leave discovery mode if currently active
		if ( this.discoveryMode ){
			this.discoveryMode = false;
			this.discovery = null;	
		}
		
		if ( this.connectionMode ){
			this.connectionMode = false;
			this.connector.stopSelectionMode();
			this.connector = null;
			
		}
		
		else {
			for (var i = 0; i < this.connectionCollection.connections.length; i++){
				if (this.connectionCollection.connections[i] && this.connectionCollection.connections[i].isActive )
					this.connectionCollection.connections[i].deselect();
			}
			this.connectionMode = true;
			this.connector = new Connector(this);
		}

	},

	addConnection : function(connection){
		this.connectionCollection.addConnection(connection);
	},
	
	/*
	 * store connections permanently
	 * 
	 */
	saveConnections: function(){		
		
		this.connectionCollection.save();	
		
	},
	
	loadConnections: function() {
		var loadJSON = function(jsonText){
			//first remove all current connection
			this.connectionCollection.clear();
			this.connectionCollection = new ConnectionCollection();
			this.connectionCollection.load(jsonText,this);
		};
		
		var dialog = new ModelLoaderDialog(loadJSON.bind(this));
		dialog.show();		
	},
	

	
	/*
	 * remove all connections
	 */
	resetConnections: function(){
		this.connectionConnections.connections.clear();
		this.connectionConnections.connections = [];
		$("connections").innerHTML = "";
	
		this.resetModels();
		
	},
	
	/*
	 * reset markers and selections to enter the discovery Mode
	 */
	
	enterDiscoveryMode : function(){
		
		// leave connection mode if currently active
		if ( this.connectionMode ){
			this.connectionMode = false;
			this.connector = null;	
		}
		
		this.resetModels();
		
		if ( this.discoveryMode ){
			this.discoveryMode = false;
			this.discovery.stopDiscoveryMode();
			this.discovery = null;
			
		}
		
		else {
			this.discoveryMode = true;
			this.discovery = new Discovery(this);
		}
		
	},
	
	/*
	 * remove shadows, markers and selections from all viewers
	 */
	resetModels : function(){
		
		var clearViewers = function(viewers){
			
			for (var i = 0; i < viewers.length; i++){
				this.removeMarker(viewers[i], "all");
				this.resetSelection(viewers[i]);
				this.undoGrey(viewers[i], "all");
			}
		};
	
		this.sendViewers(clearViewers, this);
		
	},
	
	openAutoConnectDialog : function(){
		var dialog = new ModelChooserDialog(this.availableModelViewers, this);
		dialog.show();

	},	
	
	onModelChooserDialogClose : function(selectedViewers) {
		
		
		loadingScreen =  
	        new YAHOO.widget.Panel("wait",   
	            { width:"240px",  
	              fixedcenter:true,  
	              close:false,  
	              draggable:false,  
	              zindex:4, 
	              modal:true, 
	              visible:false 
	            }  
	        ); 
	 
		loadingScreen.setHeader("Loading, please wait..."); 
		loadingScreen.setBody('<img src="http://l.yimg.com/a/i/us/per/gr/gp/rel_interstitial_loading.gif" />'); 
		loadingScreen.render(document.body); 
		
		loadingScreen.show();
		var mapping = this.connectionCollection.autoConnect(selectedViewers[0].viewer,selectedViewers[1].viewer);
		var newConnection;
		for (var i=0;i<mapping.length;i++) {
			newConnection = new Connection(this, "generated");
			
			for (var j=0;j<2;j++) {
				var nodes = [];
				nodes[0] = mapping[i].nodes[j];		
				var index = selectedViewers[j].index;
				var url = selectedViewers[j].viewer.getModelUri();
				var title = url.substring(url.lastIndexOf("/")+1,url.length);		
				newConnection.addModel(index, title , url, nodes);
			}
			this.connectionCollection.addConnection(newConnection);
		}
		loadingScreen.hide();
		
	},
	
	/*
	 * creates connections between all viewers on the screen automatically 
	 */
	autoConnect : function() {
		this.executeWithAllViewers(this.openAutoConnectDialog.bind(this));
	},
	
	

	executeWithAllViewers : function (aFunction, arguments) {	
		
		this.availableModelViewers = [];
		var getViewers = function(viewers){
			if (viewers.length!=0) {
				for (var i = 0; i < viewers.length; i++){
					var args = [];
					args.push(viewers[i]);  
					args.push(viewers.length);
					args.push(aFunction);
					args.push(arguments);
					this.getViewer(viewers[i],this.addViewer,this,args);
				}	
			} else {
				var args = [];
				args.push(null);  
				args.push(viewers.length);
				args.push(aFunction);
				args.push(arguments);
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
		
	}
	

	

	
});