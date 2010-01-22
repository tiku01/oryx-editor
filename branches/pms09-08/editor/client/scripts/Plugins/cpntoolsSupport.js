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

	},
	
//	/**
//	 * Checks if the jPDL stencil set is loaded right now.
//	 */
//	_isJpdlStencilSetExtensionLoaded: function() {
//		return this.isStencilSetExtensionLoaded(this.stencilSetExtensionNamespace);
//	},

	
	// Imports CPN - File
	//	 
	importCPN: function(){
		this._showImportDialog();
	},		

	
	// Exports CPN - File
	//
	exportCPN: function(){
		// TODO: save?
		var loc = location.href;
		var jpdlLoc ;
		if ( loc.length > 4 && loc.substring(loc.length - 5) == "/self" ) {
			jpdlLoc = loc.substring(0, loc.length - 5) + this.jPDLExporterUrlSuffix;
		} else {
			alert("TODO: Integrate existing export with new models.. ");
			return ;
		}
//		this._doExport( jpdlLoc );
		
	},
	
	
	// Sends request to a given URL.
	//
//	_sendRequest: function( url, method, params, successcallback, failedcallback ){

//		var suc = false;
//
//		new Ajax.Request(url, {
//           method			: method,
//           asynchronous	: false,
//           parameters		: params,
//		   onSuccess		: function(transport) {
//				
//				suc = true;
//				
//				if(successcallback){
//					successcallback( transport.responseText )	
//				}
//				
//			}.bind(this),
//			
//			onFailure		: function(transport) {
//
//				if(failedcallback){
//					
//					failedcallback();
//					
//				} else {
//					this._showErrorMessageBox(ORYX.I18N.Oryx.title, ORYX.I18N.jPDLSupport.impFailedReq);
//					ORYX.log.warn("Import jPDL failed: " + transport.responseText);	
//				}
//				
//			}.bind(this)		
//		});
//		
//		return suc;		
//	},
	
	
	// Loads JSON into the editor
	//
//	_loadJSON: function( jsonString ){
		
//		if (jsonString) {
//			var jsonObj = jsonString.evalJSON();
//			if( jsonObj && this._hasStencilset(jsonObj) ) {
//				if ( this._isJpdlStencilSetExtensionLoaded() ) {
//					this.facade.importJSON(jsonString);
//				} else {
//					Ext.MessageBox.confirm(
//						ORYX.I18N.jPDLSupport.loadSseQuestionTitle,
//						ORYX.I18N.jPDLSupport.loadSseQuestionBody,
//						function(btn){
//							if (btn == 'yes') {
//								
//								if (this.loadStencilSetExtension(this.stencilSetNamespace, this.stencilSetExtensionDefinition)){
//									this.facade.importJSON(jsonString);
//								} else {
//									this._showErrorMessageBox(ORYX.I18N.Oryx.title, ORYX.I18N.jPDLSupport.impFailedJson);
//								}
//								
//							} else {
//								this._showErrorMessageBox(ORYX.I18N.Oryx.title, ORYX.I18N.jPDLSupport.impFailedJsonAbort);
//							}
//						},
//						this
//					);
//				}
//				
//			} else {
//				this._showErrorMessageBox(ORYX.I18N.Oryx.title, ORYX.I18N.jPDLSupport.impFailedJson);
//			}
//		} else {
//			this._showErrorMessageBox(ORYX.I18N.Oryx.title, ORYX.I18N.jPDLSupport.impFailedJson);
//		}
//	},
	
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
	

	
	// Opens an export window / tab.
	//
//	_doExport: function( url ){
		
//		this._sendRequest(
//			url,
//			'GET',
//			{ },
//			function( result ) { 
//				var parser = new DOMParser();
//				var parsedResult = parser.parseFromString(result, "text/xml");
//				if (parsedResult.firstChild.localName == "error") {
//					this._showErrorMessageBox(ORYX.I18N.Oryx.title, ORYX.I18N.jPDLSupport.expFailedXml + parsedResult.firstChild.firstChild.data);
//				} else {
//					this.openXMLWindow(result);
//				}
//			}.bind(this),
//			function() { 
//				this._showErrorMessageBox(ORYX.I18N.Oryx.title, ORYX.I18N.jPDLSupport.expFailedReq);
//		 	}.bind(this)
//		)
//	}, 
	
	
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
						
//						var loadMask = new Ext.LoadMask(Ext.getBody(), {msg:ORYX.I18N.jPDLSupport.impProgress});
//						loadMask.show();
//						
//						window.setTimeout(function(){
//					
//							var jpdlString =  form.items.items[2].getValue();
//							
//							this._sendRequest(
//									this.jPDLImporterUrl,
//									'POST',
//									{ 'data' : jpdlString },
//									function( arg ) { this._loadJSON( arg );  loadMask.hide();  dialog.hide(); }.bind(this),
//									function() { loadMask.hide();  dialog.hide(); }.bind(this)
//								);
//
//						}.bind(this), 100);
					alert("Hallo");
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
	
	_showErrorMessageBox: function(title, msg){
        Ext.MessageBox.show({
           title: title,
           msg: msg,
           buttons: Ext.MessageBox.OK,
           icon: Ext.MessageBox.ERROR
       });
	}
	
});