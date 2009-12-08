/**
 * Copyright (c) 2006
 * Martin Czuchra, Nicolas Peters, Daniel Polak, Willi Tscheschner
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

if (!ORYX.Plugins) 
    ORYX.Plugins = new Object();

ORYX.Plugins.cpntokenpositioning = Clazz.extend({
	
	// Defines the facade
    facade		: undefined,
    
	// Constructor 
    construct: function(facade){
	
    	// define the facade
        this.facade = facade;     
		
		// Offers the functionality of tokenpositioning                
        this.facade.offer({
			'name'			: "Token Positioning",
			'description'	: "Token Positioning",
			'icon'			: ORYX.PATH + "images/arrow_undo.png",
			'functionality'	: this.tokenpositioning.bind(this, "hallo ich bin param."),
			'group'			: "CPN",
			'index' 		: 1,
			'toggle'		: true,
			'isEnabled'		: true
		}); 
		
        // Register in order to get events when a place is resize
		//this.facade.registerOnEvent(ORYX.CONFIG.EVENT_RESIZE_END, this.tokenpositioning.bind(this));
    },
    
    tokenpositioning: function(param){
    	
    	alert("hallo");
    	console.log(param);
    	var cpn = "Hallo CPN";
    	console.log(cpn);
    }
});
    	
	