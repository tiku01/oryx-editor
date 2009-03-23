if(!ORYX) ORYX = new Object();
if(!ORYX.Plugins) ORYX.Plugins = new Object();


ORYX.Plugins.LAYOUTERTEST = Clazz.extend({
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
		new Ajax.Request(ORYX.CONFIG.LAYOUTER-TEST,{
		method : 'POST',
		asynchronous : false,
		parameters : {
			data: this.facade.getERDF()
		},
		onSuccess:function(request){
			var resp = request.responseText.evalJSON();
			if (resp instanceof Array && resp.length > 0) {
				//				alert("Layouter test came back.");
				console.log(resp);
				
			}
		}.bind(this)
		})
		
	}
})







