if(!ORYX) ORYX = new Object();
if(!ORYX.Plugins) ORYX.Plugins = new Object();


ORYX.Plugins.LAYOUTERTEST = ORYX.Plugins.AbstractPlugin.extend({
	facade: undefined,
	construct:function(facade){
		this.facade = facade;
		this.facade.offer({
		'name' : 'Do Layout',
		'functionality' : this.doLayout.bind(this),
		'group' : 'Layout',
		'icon' : ORYX.PATH + "images/checker_syntax.png",
		'description' : 'Description',
		'index' : 1,
		'minShape' : 0,
		'maxShape' : 0});
	},
	doLayout: function(){
		new Ajax.Request("/oryx/bpmnlayout",{
		method : 'POST',
		asynchronous : false,
		parameters : {
			rdf: this.getRDFFromDOM()
		},
		onSuccess:function(request){
			var resp = request.responseText.evalJSON();
			
			if (resp instanceof Array && resp.length > 0) {
				console.log(resp);
				resp.each(function(n){
					var shape = this.facade.getCanvas().getChildShapeByResourceId(n.id);
					var bound = n.bounds.split(",");
					shape.bounds.set(bound[0],bound[1],bound[2],bound[3]);
					shape.getDockers();
				}.bind(this));
			this.facade.getCanvas().update();
			}
		}.bind(this)
		})
		
	}
})







