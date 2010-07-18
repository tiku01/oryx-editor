
ConnectionModel = function(model, url, nodes){
	this.title = 	model,
	this.url   =	url,
	this.nodes = 	nodes
	
};

ConnectionModel.prototype = {
		
		toJSON: function() {
			var resourceIds = Array();
			for (var i=0;i<this.nodes.length;i++)
				resourceIds.push({resourceId : this.nodes[i].resourceId,
								  //properties : this.nodes[i].properties,
								  //stencil : this.nodes[i].stencil,
								  //outgoing : this.nodes[i].outgoing
								});
			
			var obj = {
				title : this.title,
				url : this.url,
				nodes : resourceIds
			}			
			var str = Object.toJSON(obj);		
			return str;		
		}
		
};
		





	