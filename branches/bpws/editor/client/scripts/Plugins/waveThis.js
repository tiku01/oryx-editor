if(!ORYX.Plugins)
	ORYX.Plugins = new Object();
if (!ORYX.Config) {
	ORYX.Config = new Object();
}
/*
 * http://oryx.processwave.org/gadget/oryx_stable.xml
 */
ORYX.Config.WaveThisGadgetUri = "http://ddj0ahgq8zch6.cloudfront.net/gadget/oryx_stable.xml";
ORYX.Plugins.WaveThis = Clazz.extend({
	
	/**
	 *	Constructor
	 *	@param {Object} Facade: The Facade of the Editor
	 */
	construct: function(facade) {
		this.facade = facade;
		this.facade.offer({
			'name':				ORYX.I18N.WaveThis.name,
			'functionality': 	this.waveThis.bind(this),
			'group': 			ORYX.I18N.WaveThis.group,
			'icon': 			ORYX.PATH + "images/waveThis.png",
			'description': 		ORYX.I18N.WaveThis.desc,
            'dropDownGroupIcon':ORYX.PATH + "images/export2.png",

		});
		
		this.changeDifference = 0;
		
		// Register on events for executing commands and save, to monitor the changed status of the model 
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_UNDO_EXECUTE, function(){ this.changeDifference++ }.bind(this) );
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_EXECUTE_COMMANDS, function(){ this.changeDifference++ }.bind(this) );
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_UNDO_ROLLBACK, function(){ this.changeDifference-- }.bind(this) );
		
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MODEL_SAVED, function(){ this.changeDifference =0}.bind(this) );

	},
	waveThis: function(){
		var modelUri;
		if(!location.hash.slice(1)){
			Ext.Msg.alert(ORYX.I18N.WaveThis.name, ORYX.I18N.WaveThis.failUnsaved);
			return;
		}
		else{
			modelUri = ORYX.CONFIG.WEB_URL+'/backend/poem/'+(location.hash.slice(1).replace(/^\/?/,"").replace(/\/?$/,""))+"/json";
		}
		if(this.changeDifference!=0){
	        Ext.Msg.confirm(ORYX.I18N.WaveThis.name, "You have unsaved changes in your model. Proceed?", function(id){
	        	if(id=="yes"){
	        		this._openWave(modelUri);
	        	}
	        },this);
		}else{
			this._openWave(modelUri);
		}
		
	},
	_openWave: function(modelUri){
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
			
			submitForm.appendChild( createHiddenElement("u", modelUri) );
			submitForm.appendChild( createHiddenElement("g", ORYX.Config.WaveThisGadgetUri) );
			
			
			submitForm.method = "POST";
			win.document.write("</body></html>");
			win.document.close();
			submitForm.action= "https://wave.google.com/wave/wavethis?t=Oryx%20Model%20Export";
			submitForm.submit();
		}
	}
})