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


function ModelLoaderDialog(callback) {
	modelLoaderDialog.callback = callback;
}

ModelLoaderDialog.prototype = {
		show : function() {
			var isFirefox = navigator.userAgent.search(/Firefox/)!=-1;	
			var uploader = document.getElementById("modelUploader");
			if (!isFirefox) {
				uploader.disable();
				var textEle = document.getElementById("textarea");
				textEle.value =  "Your browser does not support the upload. Please enter the File Content in JSON format in this Text Field."
				
			}
			modelLoaderDialog.show();	
			var uploader = document.getElementById("modelUploader");

			uploader.addEventListener('change', function(evt){
				var textEdit = document.getElementById("textarea");
	            var text = evt.target.files[0].getAsText('UTF-8');
	            textEdit.innerText =text;
			}, true)
		},
		
		getViewer : function(url) {
			for (var i=0;i<this.availableViewers.length;i++) {
				if (this.availableViewers[i].viewer.getModelUri()==url) {
					return this.availableViewers[i];
				}
			}
		}

}





YAHOO.util.Event.onDOMReady(function () {
		
	
	// Define various event handlers for Dialog
	var handleSubmit = function() {
		var textEdit = document.getElementById("textarea");
		var text = textEdit.innerText;
		modelLoaderDialog.cancel();
		modelLoaderDialog.callback(text);
		
	};
	var handleCancel = function() {
		modelLoaderDialog.cancel();
	};
	var handleSuccess = function(o) {
		modelLoaderDialog.callback();  
	};
	var handleFailure = function(o) {	
		var textEdit = document.getElementById("textarea");
		var text = textEdit.innerText;
		modelLoaderDialog.callback(text);
	};
 
    // Remove progressively enhanced content class, just before creating the module
    YAHOO.util.Dom.removeClass("modelLoaderDialog", "yui-pe-content");
 
	// Instantiate the Dialog
	modelLoaderDialog = new YAHOO.widget.Dialog("modelLoaderDialog", 
							{ width : "30em",
							  fixedcenter : true,
							  visible : false, 
							  constraintoviewport : true,
							  buttons : [ { text:"Submit", handler:handleSubmit, isDefault:true },
								      { text:"Cancel", handler:handleCancel } ],
							  effect:[{effect:YAHOO.widget.ContainerEffect.FADE,duration:0.5}, 
								      {effect:YAHOO.widget.ContainerEffect.SLIDE,duration:0.5}] } ); 
 
	
	modelLoaderDialog.setViewers = function(viewers) {		
		modelLoaderDialog.populateSelector(viewers, "model1",0);
		modelLoaderDialog.populateSelector(viewers, "model2",1);
	};	
	
	modelLoaderDialog.populateSelector = function(viewers, id, index) {		
		var selector = document.getElementById(id);
		  //clear optionList
		  while (selector.length > 0)
		  {
			  selector.remove(selector.length - 1);
		  }
		  //add new Options
		  for (var i=0;i<viewers.length;i++) {
			  var elOptionNew = document.createElement('option');
			  elOptionNew.text = viewers[i].viewer.getModelUri();
			  elOptionNew.value = viewers[i].viewer.getModelUri();
			  selector.add(elOptionNew, null);
		  }	  
		  selector.selectedIndex = index;

	};	
 
	// Wire up the success and failure handlers
	modelLoaderDialog.callback = { success: handleSuccess,
						     failure: handleFailure };
	
	// Render the Dialog
	modelLoaderDialog.render();
 
	YAHOO.util.Event.addListener("show", "click", modelLoaderDialog.show, modelLoaderDialog, true);
	YAHOO.util.Event.addListener("hide", "click", modelLoaderDialog.hide, modelLoaderDialog, true);
})

