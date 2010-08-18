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





ConnectionCollection = function(){	
	this.connections = [];
	this.autoConnector = null; 
	this.connector = null;
};




ConnectionCollection.prototype = {
		
		addConnection : function(connection){
			this.connections.push(connection);
			connection.display();
		},
		
		autoConnect : function(viewer1, viewer2) {
			this.autoConnector = new AutoConnector();	
			var mapping = this.autoConnector.autoConnect(viewer1, viewer2);
			return mapping;
		},
		
		repl : function (key, value) {
		    if (value instanceof Correspondence) {
		        return "sjdryknlksxkjkbn";//value.toJSON();
		    }
		    
		    if (key=="gadget") {
		    	return "sjsafdryknlksxkjkbn";
		    }
		    if (key=="models") {
		    	return "sjdasfryknlksxkjkbn";
		    }
		    return value;
		},
		
		/*
		 * Removes all connection (including the displayed), resulting in clear connection Collection
		 */
		clear : function() {		
			for (var i=0;i<this.connections;i++) {
				this.connections[i].remove();
			}
		},
		
		save : function() {
			

			
			var filename = "Correspondences";
			var l = this.connections[0].models.length;
			//create a useful name for the file
			for (var i=0; i<this.connections[0].models.length;i++){		
				if (this.connections[0].models[i]) {
					filename = filename + "_" + this.connections[0].models[i].title;
				}
								
			}

			var jsonString = this.connections.toJSON();
		
			
			this.openDownloadWindow( filename + ".cor",jsonString);
		},
		

		
		openModels : function(urls) {
			for (var l=0;l<urls.length;l++) {
				//opens a viewer
				gadgets.rpc.call(
						null, 
						'dispatcher.displayModel', 
						function(reply){return}, 
						urls[l] );
							
			}
		},
		

		
		load : function(jsonText, gadget, callback) {					
			var loader = new correspondenceLoader();
			loader.onLoadingComplete = callback;
			loader.load(jsonText, gadget, this);
			
		},
		
		openDownloadWindow: function(filename, content) {
			var win = window.open("");
			if (win != null) {
				win.document.open();
				win.document.write("<html><body>");
				var submitForm = win.document.createElement("form");
				win.document.body.appendChild(submitForm);
				
				var createHiddenElement = function(name, value) {
					var newElement = document.createElement("input");
					newElement.name=name;
					newElement.type="hidden";
					newElement.value = value;
					return newElement
				}
				
				submitForm.appendChild( createHiddenElement("download", content) );
				submitForm.appendChild( createHiddenElement("file", filename) );
				
				
				submitForm.method = "POST";
				win.document.write("</body></html>");
				win.document.close();
				submitForm.action= "/oryx/" + "/download";
				submitForm.submit();
			}		
		}
		}
		


