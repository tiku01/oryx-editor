/**
 * Copyright (c) 2008
 * Willi Tscheschner
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

// define plugin namespace

if(!Repository.Plugins) Repository.Plugins = {};

Repository.Plugins.LinkView = Repository.Core.ViewPlugin.extend({
	
	
	construct: function(facade) {
		this.name = "Link View"
		
		this.icon = '/backend/images/silk/application_view_icons.png';
		this.numOfDisplayedModels = 100;
		
		// define required data uris
		this.dataUris = ["/meta"];
		
		arguments.callee.$.construct.apply(this, arguments); // call superclass constructor
		
	},
	
	
	render : function(modelData) {
		
		// Remove current panel		
		if( this.myPanel ){
			this.panel.remove( this.myPanel )
		}
		
		var data = [];
		modelData.each(function( pair ){			
			data.push( [ pair.value.title, pair.key] )
		}.bind(this));
		
		var store = new Ext.data.SimpleStore({
	        fields	: ['name', 'link'],
	        data	: data
	    });
	
	    this.myPanel = new Ext.DataView({
		        store			: store,
				itemSelector 	: 'span',
				style			: 'padding:20px;',
		        tpl				: new Ext.XTemplate(
						       	 	'<div>', 
										'<tpl for=".">',
											'<span>{name} <a href="' + Repository.Config.BACKEND_PATH + '{link}/self" target="_blank">{link}/self</a></span>, ',
            							'</tpl>',
									'</div>')
			})


		// Add new Panel
		this.panel.add( this.myPanel );
		this.panel.doLayout(); 

	}
		
});
