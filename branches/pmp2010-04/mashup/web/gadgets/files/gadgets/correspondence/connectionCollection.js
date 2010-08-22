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





/**
 * Encapsulate methods that operate on a number of connections to avoid bloating the correspondence class.  
 * @class ConnectionCollection
 * @constructor
 */

ConnectionCollection = function(){	
	this.connections = [];
	this.autoConnector = null; 
	this.connector = null;
};




ConnectionCollection.prototype = {
		
		/**
		 * Adds a connection to the ConnectionCollection and displays it.
		 * @param {Connection} connection The connection to add
		 */
		addConnection : function(connection){
			this.connections.push(connection);
			connection.display();
		},
		
		/**
		 * Populates this ConnectionCollection with automatically created connections
		 * @param {Array of {Integer, ModelViewer}} viewers
		 */
		autoConnect : function(viewers, gadget) {
			this.clear();
			this.autoConnector = new AutoConnector();	
			//functionality of autoConnector currently limited to 2 models
			var mapping = this.autoConnector.autoConnect(viewers[0].viewer, viewers[1].viewer);			
			var newConnection;
			for (var i=0;i<mapping.length;i++) {
				newConnection = new Connection(gadget, "generated");				
				for (var j=0;j<2;j++) {
					var nodes = [];
					nodes[0] = mapping[i].nodes[j];		
					var index = viewers[j].index;
					var url = viewers[j].viewer.getModelUri();
					var title = url.substring(url.lastIndexOf("/")+1,url.length);		
					newConnection.addModel(index, title , url, nodes);
				}
				this.addConnection(newConnection);
			}
			gadget.connectionCollection = this;			
		},
		
	
		/**
		 * Removes all connection (including the displayed), resulting in clear connection Collection
		 */
		clear : function() {		
			for (var i=0;i<this.connections;i++) {
				this.connections[i].remove();
			}
		},
		
		/**
		 * Serializes the connections, prompt for filename and open a new download window
		 */
		save : function() {			
			var filename = "Correspondences";
			var l = this.connections[0].models.length;
			//create a useful name for the file
			/*
			for (var i=0; i<this.connections[0].models.length;i++){		
				if (this.connections[0].models[i]) {
					filename = filename + "_" + this.connections[0].models[i].title;
				}
								
			}*/
			//prompt for filename
			filename = prompt("Filename: ");
			var jsonString = this.connections.toJSON();			
			this.openDownloadWindow( filename + ".cor",jsonString);
		},
		

		/**
		 *  Opens Viewers with the models given by the array of urls in th UI
		 *  @param {Array of String} urls The urls of the ModelViewer to display in Viewer
		 */
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
		

		/**
		 * Populate this ConnectionCollection with connections created from the given jsonText
		 * and execute callback
		 * @param {String}jsonText The json Representation of a ConnectionCollection
		 * @param {Correspondence} gadget The gadget the connections should belong to
		 * @param {Function} callback The function to execute after loading completed
		 */
		load : function(jsonText, gadget, callback) {					
			var loader = new correspondenceLoader();
			loader.onLoadingComplete = callback;
			loader.load(jsonText, gadget, this);
			
		},
		
		/**
		 * Opens a download window in the browser, so the user can download a file
		 * @param {String} filename The name of the downloadable file
		 * @param {String} content The text content of this file
		 */
		openDownloadWindow: function(filename, content) {
			var win = window.open("");
			if (win !== null) {
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
				};
				
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
		


