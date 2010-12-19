/**
 * Copyright (c) 2010
 * Tobias Metzke
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
 
if(!ORYX.Plugins) {
	ORYX.Plugins = {};
}

//TODO language support
ORYX.I18N.PictureSupport = {
	imp: {
		name: "Import",
		description: "Import ...",
		group: "import"
		// ...
	}
}

// just for logging
ORYX_LOGLEVEL = 3;
 
ORYX.Plugins.PictureSupport = ORYX.Plugins.AbstractPlugin.extend({
 
	 construct: function(){
        // Call super class constructor
        arguments.callee.$.construct.apply(this, arguments);
        
        // build a button in the toolbar
        this.facade.offer({
            'name': ORYX.I18N.PictureSupport.imp.name,
            'functionality': this.importPicture.bind(this),
            'group': ORYX.I18N.PictureSupport.imp.group,
            dropDownGroupIcon: ORYX.PATH + "images/export2.png",
			'icon': ORYX.PATH + "images/page_white_javascript.png",
            'description': ORYX.I18N.PictureSupport.imp.description,
            'index': 0,
            'minShape': 0,
            'maxShape': 0
        });
        
        //catch occurring events
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_LOADED, this.handleShape.bind(this));
 	},
 	
 	importPicture: function(){
 		
 	},
 	
 	onSelectionChange: function(){},
 	
 	handleShape: function(event){
		//create a process lane directly after editor is loaded		
 		var mynamespace = "http://b3mn.org/stencilset/picture#";
		var mytype = "http://b3mn.org/stencilset/picture#process";
		
		this.facade.createShape({
			type: mytype,
			namespace: mynamespace,
			position: {x: 0, y: 0}
		}).refresh();
	}
 	
 });
