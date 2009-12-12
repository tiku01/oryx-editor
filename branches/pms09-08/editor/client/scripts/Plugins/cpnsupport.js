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
		// Default definition types for variables and colors
		var defaultDefinitionTypes = ['Integer','Boolean','Char','String'];
		
		var currentDefinitionTypes = []; // for the current Set
		
		// Test es klappt
		var data = [
		            ['Name', 'String'],
		            ['Alter', 'Integer']
		           ];
		// Test Data
		
		
		
		var colorDeclarationStore = new Ext.data.SimpleStore({
			fields: ['ColorName', 'Definition']
		});
		
		var colorDeclarationCM = new Ext.grid.ColumnModel
		([
			{
				header: "ColorName",
				sortable: true,
				dataIndex: 'ColorName'
			},
			{
				header: "Definition",
				sortable: true,
				dataIndex: 'Definition'
		    }
		]);
        
		var colorGrid = new Ext.grid.GridPanel({
			id: 'colors-declaration',
			deferRowRender: false,
			height: 300,
			width: 300,
			frame: true,
		    store: colorDeclarationStore,
		    cm: colorDeclarationCM,
            listeners:
            {
                "render": function()
                {
                    this.getStore().loadData(data);
                    // store abfragen
                }
            }
		});
		
		// Create a new Panel for the Color definition        
        var colortab = new Ext.Panel({
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
       
//        var variableGrid = colorGrid.cloneConfig({
//        	id: "variable-declaration",
//        	store: new Ext.data.SimpleStore({
//    			fields: ['VariableName', 'Definition']
//    		}),
//        	cm: new Ext.grid.ColumnModel
//    		([
//  			{
//  				header: "Variable",
//  				sortable: true,
//  				dataIndex: 'VariableName'
//  			},
//  			{
//  				header: "Definition",
//  				sortable: true,
//  				dataIndex: 'Definition'
//  		    }
//  			]) 
//        });
        
        
        
     // Test es klappt
		var vardata = [
		            ['asd', 'String'],
		            ['Aasder', 'Integer']
		           ];
		// Test Data
		
		
		
		var variableDeclarationStore = new Ext.data.SimpleStore({
			fields: ['VariableName', 'Definition']
		});
		
		var variableDeclarationCM = new Ext.grid.ColumnModel
		([
			{
				header: "VariableName",
				sortable: true,
				dataIndex: 'VariableName'
			},
			{
				header: "Definition",
				sortable: true,
				dataIndex: 'Definition'
		    }
		]);
        
		var variableGrid = new Ext.grid.GridPanel({
			id: 'varibale-declaration',
			deferRowRender: false,
			height: 300,
			width: 300,
			frame: true,
		    store: variableDeclarationStore,
		    cm: variableDeclarationCM,
            listeners:
            {
                "render": function()
                {
                    this.getStore().loadData(vardata);
                    // store abfragen
                }
            }
		});
		
		
        
        var variabletab = new Ext.Panel({
            items: 
                [{
                    xtype: 'label',
                    text: "BlaBlaBla",
                    style: 'margin:10px;display:block'
                },
                	variableGrid
                ],
                title: 'Variables',
                buttonAlign: "left",
                buttons: 
                [{
    	            id: "Add_Button_Var",
//    	            icon: "images/cpn/add.png",
//    	            iconAlign: "left",
    	            text: "+",
    	            handler: function(){alert("hallo");}.bind(this)
                },
                {
    	            id: "Delete_Button_Var",
//    	            icon: "images/cpn/delete.png",
//    	            iconAlign: "left",
    	            text: "-",
    	            handler: function(){alert("hallo");}.bind(this)
                }]
            });
 
        
        // create the Tabspanel
        var tabs = new Ext.TabPanel({
        	activeTab: 0,
        	height: 300,
        	width: 400,
            items: 
            [
             	colortab,
             	variabletab,             	
            ]
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
            items: [tabs]           
        });		
		
        window.show();
    }	
});

