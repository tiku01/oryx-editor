/** Import of Visio EPC diagrams, that have been build with the visio EPC stencil sets.
 *	Created in the PMP seminar 2010. 
 *  Author: Lauritz Thamsen
 **/
if(!ORYX.Plugins)
	ORYX.Plugins = new Object();

ORYX.Plugins.VisioEPCImport = ORYX.Plugins.AbstractPlugin.extend({

	facade: undefined,
	
	construct: function(facade) 
	{
		// Call super class constructor
		arguments.callee.$.construct.apply(this, arguments);
		
		this.facade = facade;
					
		this.facade.offer({
			'name':				ORYX.I18N.VisioImport.EPCName,
			'functionality': 	this.importVDX.bind(this),
			'group': 			'Export',
			'dropDownGroupIcon':ORYX.PATH + "images/import.png",
			'icon': 			ORYX.PATH + "images/visio_icon.png",
			'description': 		ORYX.I18N.VisioImport.desc,
			'index': 			2,
			'minShape': 		0,
			'maxShape': 		0
		});
		
	},

	// Imports VDX - file
	importVDX: function()
	{
		this._showImportDialog();
	},		

	// Request
	
	_sendRequest: function( url, method, params, successcallback, failedcallback ){

		var suc = false;

		new Ajax.Request(
		url, 
		{
           method			: method,
           asynchronous		: false,
           parameters		: params,
		   onSuccess		: function(transport) 
		   {
				suc = true;
		
				if(successcallback)
				{
					successcallback( transport.responseText )	
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
					this._showErrorMessageBox(ORYX.I18N.Oryx.title, ORYX.I18N.VisioImport.connectionError);
					ORYX.log.warn("Communication failed: " + transport.responseText);	
				}					
		   }.bind(this)		
		});
		
		return suc;		
	},	


// -------------------------------------------- Import Functions ------------------------
	
	// Import through form.getForm().submit because .vdx can be up to 10 mb and
	// that's too much for the other text-area using ajax request, which is used
	// in most other imports into oryx-editor.
	
	 // Opens a upload dialog
	_showImportDialog: function( successCallback ) {
		
		// Define the form panel
		var form = new Ext.form.FormPanel({
			defaultType 	: 'textfield',
			layout     		: "anchor",
			border     		: false,
			fileUpload 		: true,   
			// replace regex: MIME type of the response is plain text, because app/json is interpreted as download
			// to import the text as json to a oryx model, <pre> and </pre> must be escaped
			reader			: {read:function(r){ return r.responseText.replace(/\s*<(pre|\/pre)>\s*/gi, ""); }},
			items: [{
				text    	: ORYX.I18N.VisioImport.PleaseSelect,
	            style   	: 'font-size:12px;margin-bottom:10px;display:block;',
	            xtype   	: 'label'
	        }, {
	            name      	: 'vdxFile',
	            inputType  	: 'file',
	            style      	: 'margin-bottom:10px;display:block;',
	            itemCls    	: 'ext_specific_window_overflow'
	        }, {
                name       	: 'stencil',
                inputType  	: 'hidden',
                value      	: 'epc'
	        }]
	    });
	   
		// replace regex: MIME type of the response is plain text, because app/json is interpreted as download
		// to import the text as json to a oryx model, <pre> and </pre> must be escaped
		form.getForm().errorReader = {read:function(e){ return {success:e.responseText.replace(/\s*<(pre|\/pre)>\s*/gi, "").startsWith("{"),records:[]}}};
	   
	    // Create the panel
		var dialog = new Ext.Window({
			autoCreate  : true,
			cls         : "x-window-security-center",
			bodyStyle   : 'padding:5px;background:white;',
			title       : ORYX.I18N.VisioImport.Label,
		    width       : 400,
		    modal       : true,
		    resizable   : false,
		    items       : [form],
		    buttons: [{
		    	text    : ORYX.I18N.VisioImport.UploadButton,
		        handler	: function(){
		           
		    		if (form.items.items[1].el.dom.value) {
		    			form.getForm().submit({
		    				url		: ORYX.CONFIG.VISIOIMPORT,
		                    waitMsg	: ORYX.I18N.VisioImport.ImportWait,
		                    method	: "POST",
		                    success	: function(a, request) {                                                                       
		                        dialog.close();
		                        // replace regex: MIME type of the response is plain text, because app/json is interpreted as download
		                		// to import the text as json to a oryx model, <pre> and </pre> must be escaped
		                        this.facade.importJSON(request.response.responseText.replace(/\s*<(pre|\/pre)>\s*/gi, ""));
		                  	}.bind(this),
		                    failure	: function(a, request) {
		                      	dialog.close();
		                      	Ext.Msg.alert(ORYX.I18N.VisioImport.ImportFailure, ORYX.I18N.VisioImport.ImportFailureDescription)
		                      		.setIcon(Ext.MessageBox.INFO);
		                  	}
		    			});
		    		} else {
		    			dialog.close();
		          	}      
		    	}.bind(this)
		    }, {
		    	text	: Ext.MessageBox.buttonText.cancel,
		    	handler	: function(){
		        	dialog.close();
		      	}.bind(this)
		    }]	
		});
	    
	    // Show the panel
	    dialog.show();
	}			
});
