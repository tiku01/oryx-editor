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


function ModelChooserDialog(availableViewers, caller) {
	this.availableViewers = availableViewers;
	this.caller = caller;
}

ModelChooserDialog.prototype = {
		show : function() {
			dialog1.availableViewers = this.availableViewers;
			dialog1.setViewers(this.availableViewers);	
			dialog1.modelChooserDialog = this;
			dialog1.show();		
		},
		
		getViewer : function(url) {
			for (var i=0;i<this.availableViewers.length;i++) {
				if (this.availableViewers[i].viewer.getModelUri()==url) {
					return this.availableViewers[i];
				}
			}
		}

};





YAHOO.util.Event.onDOMReady(function () {
		
	
	// Define various event handlers for Dialog
	var handleSubmit = function() {
		var selectedViewers = [];
		var model1=document.getElementById("model1");
		selectedViewers.push(dialog1.modelChooserDialog.getViewer(model1.value));	
		var model2=document.getElementById("model2");
		selectedViewers.push(dialog1.modelChooserDialog.getViewer(model2.value));	
		dialog1.modelChooserDialog.caller.onModelChooserDialogClose(selectedViewers);
		this.cancel();
	};
	var handleCancel = function() {
		this.cancel();
	};

 
    // Remove progressively enhanced content class, just before creating the module
    YAHOO.util.Dom.removeClass("dialog1", "yui-pe-content");
 
	// Instantiate the Dialog
	dialog1 = new YAHOO.widget.Dialog("dialog1", 
							{ width : "30em",
							  fixedcenter : true,
							  visible : false, 
							  constraintoviewport : true,
							  buttons : [ { text:"Submit", handler:handleSubmit, isDefault:true },
								      { text:"Cancel", handler:handleCancel } ],
							  effect:[{effect:YAHOO.widget.ContainerEffect.FADE,duration:0.5}, 
								      {effect:YAHOO.widget.ContainerEffect.SLIDE,duration:0.5}] } ); 
 
	
	dialog1.setViewers = function(viewers) {		
		dialog1.populateSelector(viewers, "model1",0);
		dialog1.populateSelector(viewers, "model2",1);
	};	
	
	dialog1.populateSelector = function(viewers, id, index) {		
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
 

	
	// Render the Dialog
	dialog1.render();
 
	YAHOO.util.Event.addListener("show", "click", dialog1.show, dialog1, true);
	YAHOO.util.Event.addListener("hide", "click", dialog1.hide, dialog1, true);
});

