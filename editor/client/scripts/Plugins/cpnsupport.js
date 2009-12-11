if(!ORYX.Plugins)
	ORYX.Plugins = new Object();

ORYX.Plugins.CpnSupport = Clazz.extend({

	/**
	 *	Constructor
	 *	@param {Object} Facade: The Facade of the Editor
	 */
	construct: function(facade) {
		this.facade = facade;

		this.facade.offer
		({
			'name':ORYX.I18N.AddDocker.add,
			'functionality': this.showWindow.bind(this),
			'group': "CPN",
			'icon': ORYX.PATH + "images/cpn/cpn_button.png",
			'description': ORYX.I18N.AddDocker.addDesc,
			'index': 1,
            'toggle': true,
			'minShape': 0,
			'maxShape': 0});


		/*this.facade.offer({
			'name':ORYX.I18N.AddDocker.del,
			'functionality': this.enableDeleteDocker.bind(this),
			'group': ORYX.I18N.AddDocker.group,
			'icon': ORYX.PATH + "images/vector_delete.png",
			'description': ORYX.I18N.AddDocker.delDesc,
			'index': 2,
            'toggle': true,
			'minShape': 0,
			'maxShape': 0});*/
		
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_RESIZE_END, this.resetTokenPosition.bind(this));
	},
	
	hallo: function()
	{
		alert("a1");
	},
	
	resetTokenPosition: function()
	{
		var selection = this.facade.getSelection();
		
		
		var test1 = selection.first();
		var test2 = selection.first().properties;
		var test3 = selection.first().properties["oryx-title"];
		var canvas = this.facade.getCanvas();			
		
		test1.bounds.moveTo({
			x: 32,
			y: 40
		});
		
		var test0 = selection[0];
		
		var children = selection.child;
		
		console.log();
	},
	
	showWindow: function()
	{
        var window = new Ext.Window({
            title: "Colorset",
            height: 300
            width: 450,
            layout: fit,
            closable: true,
            tbar: [{
            	xtype: 
            	
            }]
        });
        
        window.show();
    },
    
    getEnv: function(){
        var env = "";
        
        env += "Browser: " + navigator.userAgent;
        
        env += "\n\nBrowser Plugins: ";
        if (navigator.plugins) {
            for (var i = 0; i < navigator.plugins.length; i++) {
                var plugin = navigator.plugins[i];
                env += plugin.name + ", ";
            }
        }
        
        if ((typeof(screen.width) != "undefined") && (screen.width && screen.height)) 
            env += "\n\nScreen Resolution: " + screen.width + 'x' + screen.height;
        
        return env;
    }
	
	
});

