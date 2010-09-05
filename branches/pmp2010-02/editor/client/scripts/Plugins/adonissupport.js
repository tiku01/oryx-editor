/**
 * Copyright (c) 2010
 * Christian Kieschnick
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
	ORYX.Plugins = new Object();

ORYX.Plugins.AdonisSupport = ORYX.Plugins.AbstractPlugin.extend({
	
	construct: function() {
		arguments.callee.$.construct.apply(this, arguments);
		
		this.facade.offer({
			'name'				: ORYX.I18N.AdonisSupport.xmlExport,
			'functionality'		: this.exportXML.bind(this),
			'group'				: ORYX.I18N.AdonisSupport.group,
            dropDownGroupIcon   : ORYX.PATH + "images/export2.png",
			'icon'				: ORYX.PATH + "images/page_white_gear.png",
			'description'		: ORYX.I18N.AdonisSupport.xmlExport,
			'index'				: 1,
			'minShape'			: 0,
			'maxShape'			: 0});
		
		this.facade.offer({
			'name'				: ORYX.I18N.AdonisSupport.xmlImport,
			'functionality'		: this.importXML.bind(this),
			'group'				: ORYX.I18N.AdonisSupport.group,
            dropDownGroupIcon   : ORYX.PATH + "images/import.png",
			'icon'				: ORYX.PATH + "images/page_white_gear.png",
			'description'		: ORYX.I18N.AdonisSupport.xmlImport,
			'index'				: 1,
			'minShape'			: 0,
			'maxShape'			: 0});	
	},

	exportXML: function( successCallback ){
		var url = ORYX.CONFIG.ADONISSUPPORT;
		var json = this.facade.getSerializedJSON();
		var form = new Ext.form.FormPanel({
			baseCls:		'x-plain',
			labelWidth:		100,
			defaultType:	'textfield',
			items: [
				{
					xtype: 'combo',
					id: 'selectLanguage',
					fieldLabel: ORYX.I18N.AdonisSupport.languageSelect,
					hiddenName: 'hiddenSelect',
					emptyText: ORYX.I18N.AdonisSupport.languageSelectEmpty,
					store: new Ext.data.SimpleStore({
						fields: ['languageCode','language'],
						data:	ORYX.I18N.AdonisSupport.exportLanguages
					}),
					displayField: 'language',
					valueField: 'languageCode',
					selectOnFocus: true,
					mode: 'local',
					typeAhead: true,
					editable: false,
					triggerAction: 'all',
					value: 'en'
				}
			]
		});
		var dialog = new Ext.Window({ 
			autoCreate: true, 
			layout: 	'fit',
			plain:		true,
			bodyStyle: 	'padding:5px;',
			title: 		ORYX.I18N.AdonisSupport.expXml, 
			height: 	200, 
			width:		350,
			modal:		true,
			fixedcenter:true, 
			shadow:		true, 
			proxyDrag: 	true,
			resizable:	true,
			items: 		[form],
			buttons:[
				{
					text:ORYX.I18N.AdonisSupport.expBtn,
					handler:function(){

						var loadMask = new Ext.LoadMask(Ext.getBody(), {msg:ORYX.I18N.AdonisSupport.expProgress});
						loadMask.show();
						
						window.setTimeout(function(){
					
							var exportLanguage =  form.items.items[0].getValue();
							Ext.Ajax.request({
								url: url,
								method: 'POST',
								success: function(request) {
									loadMask.hide();
									this.openDownloadWindow(window.document.title + ".xml", request.responseText);
									dialog.hide();
								}.bind(this),
								failure: function() {
									Ext.Msg.alert("Export failed");
									loadMask.hide();
								}.bind(this),
								params: {
									data: json,
									action: "Export",
									language: exportLanguage
								}
							});
						}.bind(this), 100);
					}.bind(this)
				},{
					text:ORYX.I18N.AdonisSupport.close,
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
	},
	
	importXML: function( successCallback ){
		var url =  ORYX.CONFIG.ADONISSUPPORT;
	    var form = new Ext.form.FormPanel({
			baseCls: 		'x-plain',
	        labelWidth: 	50,
	        defaultType: 	'textfield',
	        items: [{
	            text : 		ORYX.I18N.AdonisSupport.selectFile, 
				style : 	'font-size:12px;margin-bottom:10px;display:block;',
	            anchor:		'100%',
				xtype : 	'label' 
	        },{
	            fieldLabel: ORYX.I18N.AdonisSupport.file,
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
			title: 		ORYX.I18N.AdonisSupport.impXml, 
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
					text:ORYX.I18N.AdonisSupport.impBtn,
					handler:function(){
						
						var loadMask = new Ext.LoadMask(Ext.getBody(), {msg:ORYX.I18N.AdonisSupport.impProgress});
						loadMask.show();
						
						window.setTimeout(function(){
					
							
							var xmlString =  form.items.items[2].getValue();
							Ext.Ajax.request({
								url: url,
								method: 'POST',
								success: function(request){this.facade.importJSON(request.responseText); loadMask.hide();dialog.hide()}.bind(this),
								failure: function() {loadMask.hide(); Ext.Msg.alert("Import failed");},
								params: {data: xmlString,
										 action: "Import"}
							});										
							
						}.bind(this), 100);
			
					}.bind(this)
				},{
					text:ORYX.I18N.AdonisSupport.close,
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

	}
});



