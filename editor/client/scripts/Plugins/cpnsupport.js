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
		// shortcut
		var fm = Ext.form;
		
		// Default definition types for variables and colors
		var defaultDefinitionTypes = ['Integer','Boolean','Char','String'];
		
		var currentDefinitionTypes = []; // for the current Set
		
		var declarationTypeData = 
			[
			 ['CS', 'ColorSet'],
			 ['VA', 'Variable']
			];
		
		var sampleData = 
		[
			[ 'Bob', 'Interger', 'ColorSet'],
			[ 'Bill', '40', 'ColorSet'],
			[ 'Mike', '45', 'Variable']
		];

		// 2. Create the Store
		var store = new Ext.data.SimpleStore({ 
		fields : 
			[
			 	{ name: 'name', type: 'string' },
			 	{ name: 'type', type: 'string' },
		 		{ name: 'declarationtype', type: 'string' }
			],
			data: sampleData
		});
		
		var combo = new Ext.form.ComboBox({
		    typeAhead: true,
		    triggerAction: 'all',
		    lazyRender:true,
		    mode: 'local',
		    readOnly: true,
		    store: new Ext.data.SimpleStore({
		        id: 0,
		        fields: [
		            'declarationTypeId',
		            'displaydeclarationType'
		        ],
		        data: declarationTypeData
		    }),
		    valueField: 'declarationTypeId',
		    displayField: 'displaydeclarationType'
		});
		
		var simpleGrid = new Ext.grid.EditorGridPanel({
			  store: store,
			        columns: [{
			        	header: 'Name', // 4. Field cannot be edited.
			            width: 160, 
			            sortable: false, 
			            dataIndex: 'name',
			            editor: new Ext.form.TextField({
			            	allowBlank: false
			            })
			        },
			        {
			            header: 'Type', // 5. Field can be edited
			            width: 75, 
			            sortable: false, 
			            dataIndex: 'type', 
			            editor: new Ext.form.TextField({ 
			            	allowBlank: false
			            })
			        },
			        {
			            header: 'DaclarationType', // 5. Field can be edited
			            width: 75, 
			            sortable: false, 
			            dataIndex: 'declarationtype', 
			            editor: combo
			        }],			        
			        clicksToEdit: 1,
			        stripeRows: true,
			        autoHeight:true,
			        width:500,
			        title:'Editor Grid'
			 });
		
		var label = new Ext.form.Label({
			text: "huhu"			
		}); 
        
		var win = new Ext.Window({
			width:400,
	        id:'autoload-win',
	        height:300,
	        autoScroll:true,
	        title:"hallo",
	        tbar:[
	            {
	             	text:'+',
//	             	handler:function() {
//	                	win.load(win.autoLoad.url + '?' + (new Date).getTime());
//	            	}
	            },
	            {
	            	text: '-'
	            }
	        ],
	        items: [label, simpleGrid]
	    });
	    win.show();
    }	
});

