/**
 * Copyright (c) 2010
 * Thomas Milde
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

ORYX.Plugins.ProcessLogGenerator = ORYX.Plugins.AbstractPlugin.extend({
	
	processLogGeneratorHandleURL: ORYX.PATH + "processloggenerator",
	
	construct: function(facade) {
	
		// Call super class constructor
    	arguments.callee.$.construct.apply(this, arguments);
		//this.facade = facade;
	
		this.facade.offer({
			'name'				: ORYX.I18N.ProcessLogGenerator.generate,
			'functionality'		: this.showOptionsDialog.bind(this),
			'description'		: ORYX.I18N.ProcessLogGenerator.generateDescription,
			'icon'				: ORYX.PATH + "images/processLogGeneratorIcon.png",
			'index'				: 0,
			'minShape'			: 0,
			'maxShape'			: 0
		});	
	},
	
	perform: function() {
		var options = JSON.stringify(this.showOptionsDialog());
		//var log = this.generateLog(options);
		//this.openDownloadWindow("generated_log.mxml", log);
		this.generateLogAsynchronously(options);
	},
	
	showOptionsDialog: function() {			
		/*var dialog = new Ext.XTemplate(		
			'<form class="oryx_repository_edit_model" action="#" id="LogGenerationPreferences" onsubmit="return false;">',					
				'<fieldset>',
					'<p class="description">' + ORYX.I18N.ProcessLogGenerator.dialogDescription + '</p>',
					'<input type="hidden" name="namespace" value="{namespace}" />',
					//'<p><label for="requireNoCompleteness">' + ORYX.I18N.ProcessLogGenerator.requireNoCompleteness + '</label><input type="radio" class="radio" name="completeness" value="none" id="requireNoCompleteness" checked="true"/></p>',
					//'<p><label for="requireTraceCompleteness">' + ORYX.I18N.ProcessLogGenerator.requireTraceCompleteness + '</label><input type="radio" class="radio" name="completeness" value="trace" id="requireTraceCompleteness"/></p>',
					//'<p><label for="requireOrderingCompleteness">' + ORYX.I18N.ProcessLogGenerator.requireOrderingCompleteness + '</label><input type="radio" class="radio" name="completeness" value="ordering" id="requireOrderingCompleteness"/></p>',
					'<p><label for="completeness">' + ORYX.I18N.ProcessLogGenerator.completenessSelect + '</label><select class="select" name="completeness" id="completeness"><option value="none">'+ ORYX.I18N.ProcessLogGenerator.requireNoCompleteness +'</option><option value="trace">'+ ORYX.I18N.ProcessLogGenerator.requireTraceCompleteness +'</option><option value="ordering">'+ ORYX.I18N.ProcessLogGenerator.requireOrderingCompleteness +'</option></select>',
					'<p><label for="degreeOfNoise">' + ORYX.I18N.ProcessLogGenerator.degreeOfNoise + '</label><input type="text" class="text num-field invalid" name="noise" value="0" id="degreeOfNoise"/></p>',
					'<p><label for="respectPropabilities">' + ORYX.I18N.ProcessLogGenerator.respectPropabilities + '</label><input type="checkbox" name="respectPropabilities" class="checkbox" checked="true" id="respectPropabilities" /></p>',
				'</fieldset>',
			'</form>')
		
		// Create the callback for the template
		callback = function(form){
					
			var completeness			= form.elements["completeness"].value.strip();
			var noise 					= form.elements["degreeOfNoise"].value.strip();
			if(!(0 <= noise && noise <= 100)) return;
			var respectPropabilities 	= form.elements["respectPropabilities"].value.strip();
			
			win.destroy();
			
			// Send the request out
			this.generateLogAsynchronously({'completeness': completeness,
				'noise': noise,
				'respectPropabilities': respectPropabilities});
			
		}.bind(this);
		
		// Create a new window				
		win = new Ext.Window({
			id:		'GeneratePreferencesWindow',
	        width:	'auto',
	        height:	'auto',
	        title:	ORYX.I18N.ProcessLogGenerator.preferencesWindowTitle,
	        modal:	true,
			bodyStyle: 'background:#FFFFFF',
	        html: 	dialog,
			buttons:[{
				text: ORYX.I18N.ProcessLogGenerator.generateButton,
				handler: function(){
					callback( $('LogGenerationPreferences') )					
				}
			},{
            	text: ORYX.I18N.ProcessLogGenerator.cancelButton,
            	handler: function(){
	               this.facade.raiseEvent({
	                    type: ORYX.CONFIG.EVENT_LOADING_DISABLE
	                });						
                	win.destroy();
            	}.bind(this)
			}]
	    });
				      
		win.show();*/
		
		var completenessOptions = [['None'], ['Trace'], ['Ordering']];
		
		var dataStore = new Ext.data.SimpleStore({
			fields	: ['value'],
		    data 	: completenessOptions
		});
		
		var completenessSelector = new Ext.form.ComboBox({
			fieldLabel: ORYX.I18N.ProcessLogGenerator.completenessSelect,
	        store: dataStore,
	        displayField:'value',
			valueField: 'value',
	        typeAhead: true,
	        mode: 'local',
	        triggerAction: 'all',
	        selectOnFocus:true,
	        forceSelection: true,
	        emptyText: ORYX.I18N.ProcessLogGenerator.pleaseSelect
		});

		/*var completenessOptions = [ORYX.I18N.ProcessLogGenerator.completeness.None, 
		           				ORYX.I18N.ProcessLogGenerator.completeness.Trace, 
		        				ORYX.I18N.ProcessLogGenerator.completeness.Ordering];*/
			
		/*var completenessSelector = new Ext.form.ComboBox({
			//tpl: '<tpl for="."><div class="x-combo-list-item">{[(values.icon) ? "<img src=\'" + values.icon + "\' />" : ""]} {title}</div></tpl>',
	        store: store,
	        displayField:'value',
			valueField: 'value',
	        typeAhead: true,
	        mode: 'local',
	        triggerAction: 'all',
	        selectOnFocus:true
	    });*/
		
		var noiseField = new Ext.form.NumberField({
			fieldLabel		: ORYX.I18N.ProcessLogGenerator.degreeOfNoise,
			allowBlank		: false,
			allowDecimals	: false,
			minValue		: 0,
			maxValue		: 100
		});
		
		var propabilityCheckbox = new Ext.form.Checkbox({
			fieldLabel		: ORYX.I18N.ProcessLogGenerator.respectPropabilities
		});
		
		var form = new Ext.form.FormPanel({
			frame : false,
			defaultType : 'textfield',
		 	waitMsgTarget : true,
		  	labelAlign : 'left',
		  	buttonAlign: 'right',
		  	enctype : 'multipart/form-data',
		  	style: 'font-size:12px;',
		  	items : [
		  	         completenessSelector,
		  	         noiseField,
		  	         propabilityCheckbox]
		});
		
		var errorMsg = new Ext.Panel({style: 'font-size:12px;', autoScroll: true});
		
		var dialog = new Ext.Window({ 
			autoCreate: true, 
			title: ORYX.I18N.ProcessLogGenerator.preferencesWindowTitle, 
			height: 240, 
			width: 400, 
			modal:true,
			collapsible:false,
			fixedcenter: true, 
			shadow:true, 
		  	style: 'font-size:12px;',
			proxyDrag: true,
			resizable:true,
			items: [
			        new Ext.form.Label({
			        	text: ORYX.I18N.ProcessLogGenerator.dialogDescription, 
			        	style: 'font-size:12px;'}),
			        form,
			        errorMsg
			        ],
			buttons:[{
				text:"Submit",
				handler: function() {
					this.generateLogAsynchronously({completeness: completenessSelector.value,
						noise: noiseField.value,
						respectPropabilities: propabilityCheckbox.checked});
					dialog.hide();
				}.bind(this)
			}]
		});
		dialog.on('hide', function(){
			dialog.destroy(true);
			delete dialog;
		});
		dialog.show();
	},
	
	generateLog: function(options) {
		var loadMask = new Ext.LoadMask(Ext.getBody(), {msg: ORYX.I18N.ProcessLogGenerator.waitText});
		loadMask.show();
		
		var erdf = this.getRDFFromDOM();
		var success = false;
		var response = null;

		new Ajax.Request(this.processLogGeneratorHandleURL, {
           method			: 'POST',
           asynchronous		: false,
           parameters		: { 'model'		: erdf,
				  				'options' 	: options },
		   onSuccess		: function(ajaxResponse) {
				success = true;
				response = ajaxResponse.responseText;
			}.bind(this),
			
			onFailure 		: function(ajaxResponse) {
				Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.ProcessLogGenerator.failed + ajaxResponse.responseText);
				ORYX.log.warn("Generating a Process Log failed: " + ajaxResponse.responseText);					
			}.bind(this)		
		});
		loadMask.hide();
		
		return response;
	},
	
	generateLogAsynchronously: function(options) {
		this.facade.raiseEvent({type:ORYX.CONFIG.EVENT_LOADING_ENABLE, text: ORYX.I18N.ProcessLogGenerator.shortWaitText});
		var erdf = this.getRDFFromDOM();
		var optionsString = JSON.stringify(options);
		new Ajax.Request(this.processLogGeneratorHandleURL, {
            method: 'POST',
            parameters: {
				'options'	: optionsString,
				'model'		: erdf
            },
            onSuccess: (function(request){
            	this.facade.raiseEvent({type:ORYX.CONFIG.EVENT_LOADING_DISABLE});
            	this.openDownloadWindow("generated_log.mxml", request.responseText);
            }).bind(this),
            
			onFailure: (function(){
				this.facade.raiseEvent({type:ORYX.CONFIG.EVENT_LOADING_DISABLE});
				Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.ProcessLogGenerator.failed);
			}).bind(this)
        });
	}
	
});