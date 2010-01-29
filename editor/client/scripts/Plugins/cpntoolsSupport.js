if(!ORYX.Plugins)
	ORYX.Plugins = new Object();

ORYX.Plugins.CPNToolsSupport = ORYX.Plugins.AbstractPlugin.extend({

	facade: undefined,
	
	jPDLImporterUrl: 'g',
	jPDLExporterUrlSuffix: 'g',
	
	stencilSetExtensionNamespace: 'g',
	stencilSetExtensionDefinition: 'g',
	
	stencilSetNamespace: 'http://b3mn.org/stencilset/coloredpetrinet#',
	stencilSetUrlSuffix: 'g',

	/**
	 * constructor method
	 * 
	 */
	construct: function(facade) {
		
		this.facade = facade;
			
		this.facade.offer({
			'name':				"Export to CPN Tools",
			'functionality': 	this.exportCPN.bind(this),
			'group': 			"CPNTools",
			'icon': 			ORYX.PATH + "images/cpn/cpn_export.png",
			'description': 		"CPNTools",
			'index': 			1,
			'minShape': 		0,
			'maxShape': 		0,
			'maxShape': 		0
//			'isEnabled': 		this._isJpdlStencilSetExtensionLoaded.bind(this)
		});
					
		this.facade.offer({
			'name':				"Import from CPN Tools",
			'functionality': 	this.importCPN.bind(this),
			'group': 			"CPNTools",
			'icon': 			ORYX.PATH + "images/cpn/cpn_import.png",
			'description': 		"CPNTools",
			'index': 			1,
			'minShape': 		0,
			'maxShape': 		0
		});
		
		this.facade.offer({
			'name':				"Pages",
			'functionality': 	this.testWindowOpen.bind(this),
			'group': 			"CPNTools",
			'icon': 			ORYX.PATH + "images/cpn/cpn_button.png",
			'description': 		"CPNTools",
			'index': 			1,
			'minShape': 		0,
			'maxShape': 		0
		});

	},
	
//	/**
//	 * Checks if the jPDL stencil set is loaded right now.
//	 */
//	_isJpdlStencilSetExtensionLoaded: function() {
//		return this.isStencilSetExtensionLoaded(this.stencilSetExtensionNamespace);
//	},

	
	// Test
	
	testWindowOpen: function()
	{
		var allJSON = "aaaaaaa;;;bbbbbbb;;;ccccccc";
		
		var allJSONParts = allJSON.split(";;;");
		
		allJSON = "aaaaaaa";
		
		allJSONParts = allJSON.split(";;;");
		
		var win = window.open();
		
		var i = 9;
	},
	
	// Imports CPN - File
	//	 
	importCPN: function(){
		this._showImportDialog();
	},		

	
	// Exports CPN - File
	//
	exportCPN: function(){
				
		// Get the JSON representation which is needed for the mapping 
		var cpnJson = this.facade.getSerializedJSON();
		
		// aufpassen Unterstrich bedeutet private Methoden 
		this._doExportToCPNTools( cpnJson );
	},
	
// ---------------------------------------- Ajax Request --------------------------------
	

	_sendRequest: function( url, method, params, successcallback, failedcallback ){

		var suc = false;

		new Ajax.Request(url, {
           method			: method,
           asynchronous	: false,
           parameters		: params,
		   onSuccess		: function(transport) {
				
				suc = true;
				
				if(successcallback){
					successcallback( transport.responseText )	
				}
				
			}.bind(this),
			
			onFailure		: function(transport) {

				if(failedcallback){
					
					failedcallback();
					
				} else {
					this._showErrorMessageBox(ORYX.I18N.Oryx.title, ORYX.I18N.jPDLSupport.impFailedReq);
					ORYX.log.warn("Import jPDL failed: " + transport.responseText);	
				}
				
			}.bind(this)		
		});
		
		return suc;		
	},
	
	
	// Loads JSON into the editor
	//
	_loadJSON: function( jsonString )
	{		
		if (jsonString)
		{
			var jsonObj = jsonString.evalJSON();
			if( jsonObj && this._hasStencilset(jsonObj) ) 
			{
				if ( this._isJpdlStencilSetExtensionLoaded() ) 
				{
					this.facade.importJSON(jsonString);
				}
				else
				{
					Ext.MessageBox.confirm(
						ORYX.I18N.jPDLSupport.loadSseQuestionTitle,
						ORYX.I18N.jPDLSupport.loadSseQuestionBody,
						function(btn){
							if (btn == 'yes') {
								
								if (this.loadStencilSetExtension(this.stencilSetNamespace, this.stencilSetExtensionDefinition)){
									this.facade.importJSON(jsonString);
								} else {
									this._showErrorMessageBox(ORYX.I18N.Oryx.title, ORYX.I18N.jPDLSupport.impFailedJson);
								}
								
							} else {
								this._showErrorMessageBox(ORYX.I18N.Oryx.title, ORYX.I18N.jPDLSupport.impFailedJsonAbort);
							}
						},
						this
					);
				}				
			}
			else 
			{
				this._showErrorMessageBox(ORYX.I18N.Oryx.title, ORYX.I18N.jPDLSupport.impFailedJson);
			}
		}
		else 
		{
			this._showErrorMessageBox(ORYX.I18N.Oryx.title, ORYX.I18N.jPDLSupport.impFailedJson);
		}
	},
	
//	loadStencilSetExtension: function(stencilSetNamespace, stencilSetExtensionDefinition) {
//		var stencilset = this.facade.getStencilSets()[stencilSetNamespace];
//		if (stencilset) {
//			stencilset.addExtension(ORYX.CONFIG.SS_EXTENSIONS_FOLDER + stencilSetExtensionDefinition);
//			this.facade.getRules().initializeRules(stencilset);
//			this.facade.raiseEvent({type: ORYX.CONFIG.EVENT_STENCIL_SET_LOADED});
//			return true;
//		} 
//		return false;
//	},
	
	
	// Checks if a json object references the CNP stencil set.
	//
//	_hasStencilset: function( jsonObj ){
//		return jsonObj.properties.ssextension == this.stencilSetExtensionNamespace && jsonObj.stencilset.url.endsWith(this.stencilSetUrlSuffix);
//	},
	

// -------------------------------------------- Export Functions ----------------------------
	
	_doExportToCPNTools: function( cpnJSON ){
		
//		this.openDownloadWindow( window.document.title + ".txt", text3 ); // das muss in die onSuccessFunction des Requestes
		
		this._sendRequest(
			ORYX.CONFIG.CPNTOOLSEXPORTER,
			'POST',
			{ 
				data: cpnJSON
			},
			function( result )
			{ 			
				this.openDownloadWindow( window.document.title + ".cpn", result );
			}.bind(this),
			function() { 
				this._showErrorMessageBox(ORYX.I18N.Oryx.title, ORYX.I18N.jPDLSupport.expFailedReq);
		 	}.bind(this)
		)
	}, 

// -------------------------------------------- Import Functions ------------------------
	
	 // Opens a upload dialog.
	 //
	_showImportDialog: function( successCallback ){
	
	    var form = new Ext.form.FormPanel({
			baseCls: 		'x-plain',
	        labelWidth: 	50,
	        defaultType: 	'textfield',
	        items: [{
	            text : 		"Select an CNP (.cpn) file or type in the CPN XML structure to import it!", 
				style : 	'font-size:12px;margin-bottom:10px;display:block;',
	            anchor:		'100%',
				xtype : 	'label' 
	        },{
	            fieldLabel: "File:",
	            name: 		'subject',
				inputType : 'file',
				style : 	'margin-bottom:10px;display:block;',
				itemCls :	'ext_specific_window_overflow'
	        }, {
	            xtype: 'textarea',
	            hideLabel: true,
	            name: 'msg',
	            anchor: '100% -63'  
	        }]
	    });

		// Create the panel
		var dialog = new Ext.Window({ 
			autoCreate: true, 
			layout: 	'fit',
			plain:		true,
			bodyStyle: 	'padding:5px;',
			title: 		"CPN", 
			height: 	350, 
			width:		500,
			modal:		true,
			fixedcenter:true, 
			shadow:		true, 
			proxyDrag: 	true,
			resizable:	true,
			items: 		[form],
			buttons:[
				{
					text: "Import",
					handler:function(){
						
						var loadMask = new Ext.LoadMask(Ext.getBody(), {msg:ORYX.I18N.jPDLSupport.impProgress});
						loadMask.show();
						
						window.setTimeout(function(){
					
							var cpnToImport =  form.items.items[2].getValue();
							
							this._getAllPages(cpnToImport, loadMask);

						}.bind(this), 100);

						dialog.hide();
						
					}.bind(this)
					
				},{
					text: "Close",
					handler:function(){
						
						dialog.hide();
					
					}.bind(this)
				}
			]
		});
		
		// Destroy the panel when hiding
		dialog.on('hide', function(){
			dialog.destroy(true);
			delete dialog;
		});


		// Show the panel
		dialog.show();
		
				
		// Adds the change event handler to 
		form.items.items[1].getEl().dom.addEventListener('change',function(evt){
				var text = evt.target.files[0].getAsText('UTF-8');
				form.items.items[2].setValue( text );
			}, true)

	},
	
	_getAllPages: function(cpnXML, loadMask)
	{
		var parser = new DOMParser();
		var xmlDoc = parser.parseFromString(cpnXML,"text/xml");
		var allPages = xmlDoc.getElementsByTagName("page");
		
		if (allPages.length == 0) // so ist es wahrscheinlich dass es sich nicht um ein CPN netz handelt
		{
			loadMask.hide();
			this._showErrorMessageBox("CPN Oryx","No correct CPN!");
			
			return;
		}
		
		
		if (allPages.length == 1)
		{
			// mache dann einen Call zum Server, der dir dann das JSON für Oryx erstellt
			pageAttr = allPages[0].children[0];
			pageName = pageAttr.attributes[0].nodeValue;
			alert("Eine Seite nur im Netz. Jetzt wird es an den Server geschickt.");
			this._sendRequest(
					ORYX.CONFIG.CPNTOOLSIMPORTER,
					'POST',
					{ 'pagesToImport': pageName,
						'data' : cpnXML },
					function( arg ) { this.facade.importJSON(arg);  loadMask.hide(); }.bind(this),
					function() { loadMask.hide();  }.bind(this)
				);
			
//			loadMask.hide();
			return;
		}
		
		var i, pageName, data = [];
		for (i = 0; i < allPages.length; i++)
		{
			pageAttr = allPages[i].children[0];
			pageName = pageAttr.attributes[0].nodeValue;
			data.push([pageName, true]);
		}
		
		loadMask.hide();
		this.showPageDialog(data, cpnXML);		
	},
	
	_showErrorMessageBox: function(title, msg)
	{
        Ext.MessageBox.show({
           title: title,
           msg: msg,
           buttons: Ext.MessageBox.OK,
           icon: Ext.MessageBox.ERROR
       });
	},
	
	
	showPageDialog: function(data, cpnXML)
	{
		var reader = new Ext.data.ArrayReader(
				{}, 
				[ {name: 'name'}, {name: 'engaged'} ]);
		
		var sm = new Ext.grid.CheckboxSelectionModel();
		
	    var grid2 = new Ext.grid.GridPanel({
	    		store: new Ext.data.Store({
		            reader: reader,
		            data: data
		        	}),
		        cm: new Ext.grid.ColumnModel([
		            
		            {id:'name',width:200, sortable: true, dataIndex: 'name'},
					sm]),
			sm: sm,
	        frame:true,
			hideHeaders:true,
	        iconCls:'icon-grid',
			listeners : {
				render: function() {
					var recs=[];
					this.grid.getStore().each(function(rec)
					{
						if(rec.data.engaged){
							recs.push(rec);
						}
					}.bind(this));
					this.suspendEvents();
					this.selectRecords(recs);
					this.resumeEvents();
				}.bind(sm)
			}
	    });
	    
	 // Create a new Panel
        var panel = new Ext.Panel({
            items: [{
                xtype: 'label',
                text: 'CPNTools Page',
                style: 'margin:10px;display:block'
            }, grid2],
            frame: true
        })
        
        // Create a new Window
        var window = new Ext.Window({
            id: 'oryx_new_stencilset_extention_window',
            autoWidth: true,
            title: 'CPN Page Oryx',
            floating: true,
            shim: true,
            modal: true,
            resizable: true,
            autoHeight: true,
            items: [panel],
            buttons: [{
                text: 'Import',
                handler: function(){
            		var chosenRecs = "";

            		sm.getSelections().each(function(rec){
						chosenRecs += rec.data.name + ";;";						
					}.bind(this));
            		
            		var strLen = chosenRecs.length; 
            		
            		if (chosenRecs.length == 0)
            		{
            			alert("Es wurden keine Netze ausgewählt!!");
            			return;
            		}
            		
            		chosenRecs = chosenRecs.substring(0, strLen - 2);
				
            		alert(chosenRecs);
                }.bind(this)
            }, {
                text: 'Close',
                handler: function(){
                    window.hide();
                }.bind(this)
            }]
        })
        
        // Show the window
        window.show();
	}		
	
});