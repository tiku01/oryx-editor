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
		// Default definition types for variables and colors
		var defaultDefinitionTypes = ['Integer','Boolean','Char','String'];
		
		var currentDefinitionTypes = []; // for the current Set
		
		// Test es klappt
		var data = [
		            ['Name', 'String', true],
		            ['Alter', 'Integer', false]
		           ];
		// Test Data
		
		
		
		var DeclarationStore = new Ext.data.SimpleStore({
			fields: ['Name', 'Declaration', 'VariableOrNot']
		});
		
		var DeclarationCM = new Ext.grid.ColumnModel
		([
			{
				header: "Name",
				sortable: true,
				dataIndex: 'Name'
			},
			{
				header: "Declaration",
				sortable: true,
				dataIndex: 'Declaration'
		    }
		]);
		
		
		        
		var colorGrid = new Ext.grid.GridPanel({
			id: 'declaration',
			deferRowRender: false,
			height: 300,
			width: 300,
			frame: true,
		    store: DeclarationStore,
		    cm: DeclarationCM,
            listeners:
            {
                "render": function()
                {
                    this.getStore().loadData(data);
                    // store abfragen für currentdeclarations
                }
            }
		});
		
		// Create a new Panel for the Color definition        
        var panel = new Ext.Panel({
        	title: 'Colors',
            items: [{
                xtype: 'label',
                text: ORYX.I18N.SSExtensionLoader.panelText,
                style: 'margin:10px;display:block'
            }, colorGrid,
            ],
            frame: true,
            buttonAlign: "left",
            buttons: 
            [{
	            id: "Add_Button",
//	            icon: "images/cpn/add.png",
//	            iconAlign: "left",
	            text: "+",
	            handler: function(){alert("hallo");}.bind(this)
            },
            {
	            id: "Delete_Button",
//	            icon: "images/cpn/delete.png",
//	            iconAlign: "left",
	            text: "-",
	            handler: function(){alert("hallo");}.bind(this)
            }]
        });        
      
        
        // Create a Window for the ColorSet Declaration
        var window = new Ext.Window({
            id: 'oryx_new_stencilset_extention_window',
            width: 400,
            title: "Declaration",
            floating: true,
            shim: true,
            modal: true,
            resizable: false,
            items: [panel]           
        });		
		
        window.show();
    }	
});

