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
	
	
	/* importXML: function( successCallback ){
		var url =  ORYX.CONFIG.ADONISSUPPORT;
		
		var fileupload = new Ext.form.TextField({
		    fieldLabel: ORYX.I18N.AdonisSupport.file,
			name: 		'subject',
			inputType : 'file',
			style : 	'margin-bottom:10px;display:block;',
			itemCls :	'ext_specific_window_overflow'
		});
		var filecontent = new Ext.form.TextArea({
			hideLabel: true,
			name: 'msg',
			anchor: '100% -100'
		});
		var modelchoser = new Ext.form.ComboBox({
			id: 'modelchoser',
			fieldLabel: 'Select a model to display',
			hiddenName: 'hiddenSelect',
			emptyText: 'Select a model',
			store: new Ext.data.SimpleStore({
				fields: ['model'],
				data:	['test']
			}),
			displayField: 'model',
			valueField: 'model',
			selectOnFocus: true,
			mode: 'local',
			typeAhead: true,
			editable: false,
			triggerAction: 'all'
			
		});
		
		var fill = function(){
			alert('TEST');
			modelchoser.	
		}.bind(this);
		
	    var form = new Ext.form.FormPanel({
			baseCls: 		'x-plain',
	        labelWidth: 	150,
	        defaultType: 	'textfield',
	        items: [{
					text : 		ORYX.I18N.AdonisSupport.selectFile, 
					style : 	'font-size:12px;margin-bottom:10px;display:block;',
					anchor:		'100%',
					xtype : 	'label' 
				},
				fileupload, 
				filecontent,
				modelchoser
			]
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
					text:		ORYX.I18N.AdonisSupport.impBtn,
					handler:	function(){
						
						var loadMask = new Ext.LoadMask(Ext.getBody(), {msg:ORYX.I18N.AdonisSupport.impProgress});
						loadMask.show();
						
						window.setTimeout(function(){
					
							var xmlString =  form.items.items[2].getValue();

							Ext.Ajax.request({
								url: url,
								method: 'POST',
								success: function(request){
									this.facade.importJSON(request.responseText); 
									loadMask.hide();
									dialog.hide();
								}.bind(this),
								failure: function() {
									loadMask.hide(); 
									Ext.Msg.alert("Import failed");
								},
								params: {data: xmlString,
										 action: "Import"}
							});										
							
						}.bind(this), 100);
			
					}.bind(this)
				},{
					text:		ORYX.I18N.AdonisSupport.close,
					handler:	function(){
						
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
		fileupload.getEl().dom.addEventListener('change',function(evt){
				var text = evt.target.files[0].getAsText('UTF-8');
				filecontent.setValue( text );
				fill();
			}, true);
		filecontent.getEl().dom.addEventListener('change',function(evt){
			fill();
		},true);
		
	},
	
	extractDiagrams: function(xmlString){
		if (!xmlString.startsWith('<?xml')){
			xmlString = '<?xml version="1.0" encoding="utf-8"?>'+xmlString;
		}
		var parser = new DOMParser();
		var document = parser.parseFromString(xmlString,"text/xml"); 
		var model = document.getElementsByTagName("MODEL");
		var modelNames = [];
		for (var i = 0; i < model.length; i++){
			alert(model[i].getAttribute("name"));
		}
	} */
	
	
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
								success: function(request){
									loadMask.hide();
									dialog.hide();
									var jsonModels = Ext.decode(request.responseText);
									this.selectDisplayedModels(jsonModels);
								}.bind(this),
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

	},
	
	loadModels: function(importModels){
		
		var loadMask;
		var requestSuccessful = true;
		var loadedModels = [];
		loadMask = new Ext.LoadMask(Ext.getBody(), {msg:"Loading diagrams"});
		loadMask.show();
		importModels.each(function(item){
			
			requestSuccessful = true;
			
			//generate dummy data for each model
			var stencilset = item.model.stencilset.url.replace('/oryx/','');
			var size = item.model.bounds.lowerRight;
			var url = '/backend/poem' + ORYX.CONFIG.ORYX_NEW_URL + "?stencilset="+stencilset;
			var dummyData = '<div class="processdata"><div class="-oryx-canvas" id="'+item.model.resourceId+'" style="display: none; width:'+size.x+'px; height:'+size.y+'px;"><a href="'+stencilset+'" rel="oryx-stencilset"></a><span class="oryx-mode">writeable</span><span class="oryx-mode">fullscreen</span></div></div>';
			var dummySVG = '<svg/>';
			var parameter = {
				data: dummyData,
				svg: dummySVG,
				title: item.title,
				summary: "",
				type: item.model.stencilset.namespace
				
			}
			
			requestSuccessful = this.sendRequest(url,parameter, function(transport){
				var location = transport.getResponseHeader('location');
				var id = this.getNodesByClassName(item.data, "div", "-oryx-canvas")[0].getAttribute("id");
				
				loadedModels.push({
					title: item.title,
					model: item.model,
					url: location,
					id: id
				});
			
			}.bind(this));
			
			if (!requestSuccessful){
				Ext.Msg.alert('Processing of model '+item.title+' not possible\nAn error occured');
			}
			//this.facade.importJSON(importModels[i]); 
		}.bind(this));
		
		loadedModels.each(function(item){
			
			var dummySVG = '<svg/>';
			var data = DataManager.serialize(item.model);
			data = "<div "+data.slice(data.search("class"));
			var parameter = {
				data: data,
				svg: dummySVG
			};
			if (!this.sendRequest(url,parameter)){
				Ext.Msg.alert('Could not send model '+item.title+' to server\nAn error occured');
			}
			
		}.bind(this));
		
		loadMask.hide();
	},
	
	/**
     *
     *
     * @param {Object} url
     * @param {Object} params
     * @param {Object} successcallback
     */
    sendRequest: function(url, params, successcallback){
    
        var successState = false;
        
        new Ajax.Request(url, {
            method: 'POST',
            asynchronous: false,
            parameters: params,
            onSuccess: function(transport){
            
                successState = true;
                
                if (successcallback) {
                    successcallback(transport)
                }
                
            }.bind(this)            ,
            
            onFailure: function(transport){
            
                Ext.Msg.alert(ORYX.I18N.Oryx.title,"FAILURE");
                ORYX.Log.warn("Import adonis XML failed: " + transport.responseText);
                
            }.bind(this)            ,
            
            on403: function(transport){
            
                Ext.Msg.alert(ORYX.I18N.Oryx.title, "ERROR 403 no rights");
                ORYX.Log.warn("Import adonis XML failed: " + transport.responseText);
                
            }.bind(this)
        });
		 
        return successState;
        
    },
	
	selectDisplayedModels: function(jsonModels){
		var data = [];
		
		if (jsonModels.length == 1){
			this.loadModels(
			[
				{
					title: jsonModels[0].properties.name,
					model: jsonModels[0]
				}
			]);
			return;
		}
		
		for (var i = 0; i < jsonModels.length; i++){
			data.push(
				[
					jsonModels[i].properties.name,
					jsonModels[i]
				]);
		}
		
		var models = new Ext.data.SimpleStore({
			data: data,
			fields: ['model','model']
		});
		
		var selectionmodel = new Ext.grid.CheckboxSelectionModel({
            singleSelect	:false,
			width: 20
        });
		
        // Create a new Grid with a selection box
        var grid = new Ext.grid.GridPanel({
            store: new Ext.data.SimpleStore({
                data: data,
                fields: ['title','model']
            }),
            cm: new Ext.grid.ColumnModel(
				[	selectionmodel, 
					{
						header: "Models to store",
						sortable: true,
						width: '100% -40',
						dataIndex: 'title'
					}, 
				]),
            sm: selectionmodel,
            frame: true,
			width: '100%',
			hight: '100%'
            //	iconCls: 'icon-grid'
        });
		
		var form = new Ext.form.FormPanel({
			baseCls:		'x-plain',
			labelWidth:		100,
			defaultType:	'textfield',
			items: [
				/* {
					xtype: 'combo',
					id: 'selectModel',
					fieldLabel: "Select a model",
					hiddenName: 'hiddenSelect',
					emptyText: "",
					store: new Ext.data.SimpleStore({
						fields: ['model','index'],
						data:	data
					}),
					displayField: 'model',
					valueField: 'index',
					selectOnFocus: true,
					mode: 'local',
					typeAhead: true,
					editable: false,
					triggerAction: 'all',
				}, */
				grid
			]
		});
		var dialog = new Ext.Window({ 
			autoCreate: true, 
			layout: 	'fit',
			plain:		true,
			bodyStyle: 	'padding:5px;',
			title: 		"Select a model", 
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
					text:	"Display",
					handler:function(){
						
						
						var selectionModel = grid.getSelectionModel();
						var selectedModels = selectionModel.selections.items.collect(function(row){
							return {
								title: row.json[0],
								model: row.json[1]
							}
						});
						this.loadModels(selectedModels);
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
		
	}
});
