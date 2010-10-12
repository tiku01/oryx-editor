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
 * This is part of a connection. It comprises the nodes contained in one model 
 * of a connection and offers an appropriate toJson method   
 * @class ConnectionModel
 * @constructor
 * @param {String} model The title of the model
 * @param {String} url The URL of the ModelViewer
 * @param {Array of Nodes} nodes The nodes that should belong to the connection this ConnectionModel belongs to
 */
ConnectionModel = function(model, url, nodes){
	this.title = 	model,
	this.url   =	url,
	this.nodes = 	nodes
	
};

ConnectionModel.prototype = {
		
		/**
		 * Returns a JSON string of this object. Includes Title, URL and RessourceIDs of the nodes.
		 * @return {String}
		 */
		toJSON: function() {
			var resourceIds = Array();
			for (var i=0;i<this.nodes.length;i++)
				resourceIds.push({resourceId : this.nodes[i].resourceId});
			
			var obj = {
				title : this.title,
				url : this.url,
				nodes : resourceIds
			}			
			var str = Object.toJSON(obj);		
			return str;		
		}
		
};
		





	