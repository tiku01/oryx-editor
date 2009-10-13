/**
 * Copyright (c) 2009
 * Philipp Giese, Sven Wagner-Boysen
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/**
   @namespace Oryx name space for plugins
   @name ORYX.Plugins
*/
if(!ORYX.Plugins)
	ORYX.Plugins = new Object();
	
/**
 * This plugin provides methodes to layout the choreography diagrams of BPMN 2.0.
 * 
 * @class ORYX.Plugins.Bpmn2_0Choreography
 * @extends ORYX.Plugins.AbstractPlugin
 * @param {Object} facade
 * 		The facade of the Editor
 */
ORYX.Plugins.BPMN2_0Serialization = {
	bpmnSerializationHandlerUrl: "/oryx/bpmn2_0serialization",
	
	construct: function(facade) {
	
		this.facade = facade;
	
		this.facade.offer({
			'name'				: ORYX.I18N.Bpmn2_0Serialization.show,
			'functionality'		: this.showBpmnXml.bind(this),
			'group'				: 'Export',
            dropDownGroupIcon : ORYX.PATH + "images/export2.png",
			'icon' 				: ORYX.PATH + "images/source.png",
			'description'		: ORYX.I18N.Bpmn2_0Serialization.showDesc,
			'index'				: 0,
			'minShape'			: 0,
			'maxShape'			: 0
		});
		
		this.facade.offer({
			'name'				: ORYX.I18N.Bpmn2_0Serialization.download,
			'functionality'		: this.downloadBpmnXml.bind(this),
			'group'				: 'Export',
            dropDownGroupIcon : ORYX.PATH + "images/export2.png",
			'icon' 				: ORYX.PATH + "images/source.png",
			'description'		: ORYX.I18N.Bpmn2_0Serialization.downloadDesc,
			'index'				: 0,
			'minShape'			: 0,
			'maxShape'			: 0
		});
	},
	
	showBpmnXml: function() {	
		//var options = JSON.stringify({action : 'transform'});
		
		this.generateBpmnXml( function( xml ) {
			this.openXMLWindow(xml);
		}.bind(this));
	},
	
	downloadBpmnXml: function() {	
		//var options = JSON.stringify({action : 'transform'});
		this.generateBpmnXml(
			function ( xml ) {
				this.openDownloadWindow("Oryx-BPMN 2.0", xml);
			}.bind(this));
	},
	
	generateBpmnXml: function( bpmnHandleFunction ) {
		var loadMask = new Ext.LoadMask(Ext.getBody(), {msg:"Serialization of BPMN 2.0 model"});
		loadMask.show();
		
		var jsonString = this.facade.getSerializedJSON();
		this._sendRequest(
				this.bpmnSerializationHandlerUrl,
				'POST',
				{ 'data' : jsonString },
				function( bpmnXml ) { 
					bpmnHandleFunction( bpmnXml );  
					loadMask.hide();
				}.bind(this),
				function(transport) { 
					loadMask.hide();
					this._showErrorMessageBox(ORYX.I18N.Oryx.title, ORYX.I18N.Bpmn2_0Serialization.serialFailed + transport.responseText);
					ORYX.log.warn("Serialization of BPMN 2.0 model failed: " + transport.responseText);
				}.bind(this)
			);
	},
	
	_sendRequest: function( url, method, params, successcallback, failedcallback ){

		var suc = false;

		new Ajax.Request(url, {
           method			: method,
           asynchronous	: false,
           parameters		: params,
		   onSuccess		: function(transport) {
				
				suc = true;
				
				if(successcallback){
					successcallback( transport.responseText );
				}
				
			}.bind(this),
			
			onFailure : function(transport) {

				if(failedcallback){
					failedcallback(transport);
					
				} else {
					Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Bpmn2Bpel.transfFailed);
					ORYX.log.warn("Serialization of BPMN 2.0 model failed: " + transport.responseText);	
				}
				
			}.bind(this)		
		});
		
		return suc;		
	},
	
	_showErrorMessageBox: function(title, msg){
        Ext.MessageBox.show({
           title: title,
           msg: msg,
           buttons: Ext.MessageBox.OK,
           icon: Ext.MessageBox.ERROR
       });
	}
};

ORYX.Plugins.BPMN2_0Serialization = ORYX.Plugins.AbstractPlugin.extend(ORYX.Plugins.BPMN2_0Serialization);
