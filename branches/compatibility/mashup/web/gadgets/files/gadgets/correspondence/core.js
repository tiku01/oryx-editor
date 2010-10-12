
/**
 * Copyright (c) 2010
 * Uwe Hartmann
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

GADGET_DIR = "/mashup/gadgets/files/gadgets/core/"
CORRESPONDENCE_DIR = "/mashup/gadgets/files/gadgets/correspondence/"
REPOSITORY_DIR = "/mashup/gadgets/files/gadgets/repository/"
MODELMATCHING_DIR = "/mashup/gadgets/files/gadgets/correspondence/modelMatching/"
MULTIMODEL_DIR = "/mashup/gadgets/files/gadgets/multimodel/"
	
var correspondence = null;
	
//load MOVI api
MOVI.init(
		
		function(){
			
		    new YAHOO.util.YUILoader({ 
				base: "http://yui.yahooapis.com/2.7.0/build/", 
		        require: ["yahoo", "fonts","grids","layout","reset","resize"],
		        loadOptional: false, 
		        combine: true, 
		        filter: "MIN", 
		        allowRollup: true, 
		        onSuccess: function(){ getScripts();}
		    }).insert(); 

		},
		"http://oryx-editor.googlecode.com/svn/trunk/poem-jvm/src/javascript/movi/src",
		undefined,
		["resize"]
	);

// load required YUI modules	
new YAHOO.util.YUILoader({ 
    base: "http://yui.yahooapis.com/2.7.0/build/", 
    require: ["yahoo", "datatable","animation","paginator",
        		"fonts","grids","layout","reset","resize", 
        		"button", "connection", "container", "dragdrop"], 
    loadOptional: false, 
    filter: "RAW", 
    onSuccess: function() {
			getScripts();
    }, 
    onFailure: function() {
    	alert("Failed loading required YUI components");
    }
}).insert(); 




// load gadget specific scripts and create an instance of the current gadget type if successful
getScripts = function(){
	


	YAHOO.util.Get.script([  GADGET_DIR + "abstractGadget.js",	 
	                         MULTIMODEL_DIR + "multimodel.js",
	                         MULTIMODEL_DIR + "connection.js",
	                         CORRESPONDENCE_DIR + "correspondence.js", 
	                         CORRESPONDENCE_DIR + "connector.js",
	                         CORRESPONDENCE_DIR + "connection.js",	
	                         CORRESPONDENCE_DIR + "discovery.js",	                        
	                         MODELMATCHING_DIR + "autoConnector.js",	                         	                    
	                         MODELMATCHING_DIR + "openPair.js",
	                         CORRESPONDENCE_DIR + "connectionCollection.js",
	                         CORRESPONDENCE_DIR + "modelChooserDialog.js",
	                         CORRESPONDENCE_DIR + "modelLoaderDialog.js",
	                         CORRESPONDENCE_DIR + "connectionModel.js",
	                         CORRESPONDENCE_DIR + "correspondenceLoader.js",
	                         CORRESPONDENCE_DIR + "loadingScreen.js"  
	                         

	                         ],
			{
				onSuccess : function(){ 
						correspondence = new Correspondence(); 
				} ,
				onFailure : function(){ alert("Error loading scripts!"); }
			}
	);
	
} 