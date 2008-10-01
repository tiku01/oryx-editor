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

// define namespace
if(!Repository) var Repository = {};
if(!Repository.Plugins) Repository.Plugins = {};

/**
 * Supplies filtering by model type (stencil set)
 * Note: Only stencil sets defined in the stencilsets.json can be selected as filter
 */

Repository.Plugins.TagInfo = {
	construct: function( facade ) {
		// Set the name
		this.name = Repository.I18N.TagInfo.name;

		this.dataUris = ["/tags"];
				
		// call Plugin super class
		arguments.callee.$.construct.apply(this, arguments); 

	},
	
	render: function( modelData ){
		
		var div = "<div>";
		
		var modelTags = $H(modelData).values().map(function( tags ){ return tags.userTags }).flatten().compact().uniq();
		modelTags.each(function(tag){
			div += '<a href="Delete" onclick="' + this._onTagClick.bind(this, tag) + '">' + tag + '</a><br/>';
		}.bind(this))
		
		div += "</div>";
		
		this.panel.add( new Ext.Panel({html:div}) );
		this.panel.doLayout();

	},
	
	_onTagClick: function( tag ){
		
		return false;
	}	
};

Repository.Plugins.TagInfo = Repository.Core.ContextPlugin.extend(Repository.Plugins.TagInfo);
