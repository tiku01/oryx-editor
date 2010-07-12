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
	
	 // Opens a upload dialog
	_showImportDialog: function( successCallback )
	{
		// Define the form panel
	    var form = new Ext.form.FormPanel({
			baseCls: 		'x-plain',
	        labelWidth: 	50,
	        defaultType: 	'textfield',
	        items: 
	        [
	         {
	            text : 		ORYX.I18N.VisioImport.selectFile, 
				style : 	'font-size:12px;margin-bottom:10px;display:block;',
	            anchor:		'100%',
				xtype : 	'label' 
	         },
	         {
	            fieldLabel: ORYX.I18N.VisioImport.file,
	            name: 		'subject',
				inputType : 'file',
				style : 	'margin-bottom:10px;display:block;',
				itemCls :	'ext_specific_window_overflow'
	         }, 
	         {
	            xtype: 'textarea',
	            hideLabel: true,
	            name: 'msg',
	            anchor: '100% -63'  
	         }
	        ]
	    });

		// Create the panel
		var dialog = new Ext.Window({ 
			autoCreate: true, 
			layout: 	'fit',
			plain:		true,
			bodyStyle: 	'padding:5px;',
			title: 		ORYX.I18N.VisioImport.name, 
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
					text: ORYX.I18N.VisioImport.btnImp,
					handler:function(){
						
						var loadMask = new Ext.LoadMask(Ext.getBody(), {msg:ORYX.I18N.VisioImport.progress});
						loadMask.show();
						
						window.setTimeout(function()
						{					
							// Get the text which is in the text field
							var vdx =  form.items.items[2].getValue();							
							this._getVDX(vdx, loadMask);

						}.bind(this), 100);

						dialog.hide();
						
					}.bind(this)
					
				},{
					text: ORYX.I18N.VisioImport.btnClose,
					handler:function()
					{						
						dialog.hide();					
					}.bind(this)
				}
			]
		});
		
		// Destroy the panel when hiding
		dialog.on('hide', function()
		{
			dialog.destroy(true);
			delete dialog;
		});

		// Show the panel
		dialog.show();
		
				
		// Adds the change event handler to 
		form.items.items[1].getEl().dom.addEventListener('change',function(evt)
			{
				var text = evt.target.files[0].getAsText('UTF-8');
				form.items.items[2].setValue( text );
			}, true)

	},
	
	_getVDX: function(vdx, loadMask)
	{
		var url = ORYX.CONFIG.VISIOIMPORT;
		
		Ext.Ajax.request({
			url: url,
			method: 'POST',
			success: function(request){this.facade.importJSON(request.responseText); loadMask.hide()}.bind(this),
			failure: function() {loadMask.hide(); Ext.Msg.alert(ORYX.I18N.VisioImport.connectionError);},
			params: {data: vdx,
					 action: "EPCImport"}
		});
				
		loadMask.hide();
	}
				
});
