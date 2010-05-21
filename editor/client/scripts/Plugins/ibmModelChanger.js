if (!ORYX.Plugins) 
    ORYX.Plugins = new Object();

ORYX.Plugins.IBMModelChanger = Clazz.extend({
	
    facade: undefined,
	
    construct: function(facade){
		this.facade = facade;
		/*
		 * Offer a new Button for the Toolbar
		 */
		this.facade.offer({
			'name': "IBM Transformation",
			'functionality': this.doAction.bind(this),
			'group': "transformation",
			'icon': ORYX.PATH + "images/wrench.png",
			'description': "This does a transformation on your model",
			'index': 8,
			'minShape': 0,
			'maxShape': 0
		});},
		
	doAction: function(){
			var selectedShapes = this.facade.getSelection();
			var selectedIds = [];
			for(index =0;index<selectedShapes.size();index++){
				selectedIds.push(selectedShapes[index].getId());
			}
			var modelAsJson = this.facade.getSerializedJSON();
			Ext.Ajax.request({
				url: ORYX.CONFIG.ROOT_PATH +"bpmn2_0webcall",
				method: 'POST',
				success: function(request){
					/*
					 * remove all elements from canvas
					 * CAUTION cannot be undone
					 */
					var allShapes = this.facade.getCanvas().getChildShapes();
					for(i=0;i<allShapes.length;i++){
						this.facade.getCanvas().remove(allShapes[i]);
					}
					this.facade.importJSON(request.responseText, true); 
					}.bind(this),
				failure: function() {
					Ext.Msg.alert("Transformation failed");
					},
				params: {	
						data: 		modelAsJson,
						selection: 	selectedIds
						}
			});	
			
		}
})
			