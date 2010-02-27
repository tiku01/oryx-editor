/**
* Copyright (c) 2010
* Martin Krüger
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

// define namespace
if(!Repository) var Repository = {};
if(!Repository.Plugins) Repository.Plugins = {};

Repository.Plugins.ViewGenerator = {

	modelUris:null,
	button:null,

	construct: function( facade ) {
		// set the name
		this.name = "ViewGenerator";

		// call plugin super class
		arguments.callee.$.construct.apply(this, arguments);
		this._generateGUI();
	},

	render: function( modelData ){
		this.modelUris=new Array();
		modelData.keys().each(function(uri) {
			this.modelUris.push("http://" + location.host+"/backend/poem"+uri+"/xml")
		}.bind(this));

		if(!this.button) {
			return;
		};
	},

	_sendRequest: function(){
		// Send the request to the server.
		//console.log(this.modelUris);

		new Ajax.Request("/oryx/viewgenerator", {
			method: 'post',
			asynchronous: true,
			parameters: {
			modeluris : this.modelUris,
			},

			onSuccess: function(response){
				//success handling
				var url = response.responseText;
				//console.log(url);
				window.open(url, "_blank").focus();
				
				//window.location.href=url;
			}.bind(this),

			onFailure: function(response){
				//error handling
			}.bind(this)
		});
	},

	_generateGUI: function(){
		var totalLabel = {text: "Generate Views", xtype:'label', style:"display:block;font-weight:bold;margin-bottom:5px;"};
		this.button = new Ext.LinkButton({
			image: '../images/silk/user_go.png',
			imageStyle: 'width:16px;',
			text: "Try",
		
			click: function(){
				this._sendRequest();
			}.bind(this) ,

			border: '4px'
		});
	
		items = [totalLabel, this.button]
		this.myPanel = new Ext.Panel({
			style : 'padding:10px;',
			border : false,
			items : items
		})

		// ... before the new child gets added
		this.panel.add( this.myPanel );

		// Update layouting
		this.panel.doLayout();
	},
};

Repository.Plugins.ViewGenerator = Repository.Core.ContextPlugin.extend(Repository.Plugins.ViewGenerator);