/**
 * Copyright (c) 2009
 * Armin Zamani
 * 
 **/

if (!ORYX) ORYX = new Object();
if (!ORYX.Plugins) ORYX.Plugins = new Object();

ORYX.Plugins.BPMN2YAWLMapper = ORYX.Plugins.AbstractPlugin.extend({
	facade: undefined,
	construct: function(facade){
		this.facade = facade
		this.facade.offer({
			'name': 'BPMN to YAWL mapper',
			'functionality': this.BPMNtoYAWLsyntaxCheck.bind(this),
			'group': 'Mapping',
			'icon': ORYX.PATH + 'images/control_end.png',
			'description': 'Map this diagram to YAWL and execute it',
			'index': 1,
			'minShape': 0,
			'maxShape': 0
		})
	},
	BPMNtoYAWLsyntaxCheck: function(){
		alert("Hello, you clicked on the BPMN2YAWL execution button. You are still on client side.")
		new Ajax.Request(ORYX.CONFIG.BPMN2YAWL_URL, {
			method: 'POST',
			asynchronous: false,
			parameters : {
				data: this.getRDFFromDOM()
			},
			onSuccess: function(request){
				alert("BPMN to YAWL Mapper succeeded");
			}
		})
	}
})
