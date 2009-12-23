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
			'functionality': this.hallo.bind(this),
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
		var selection = this.facade.getSelection();
		var place = selection.first();
		var token = place.getChildNodes(false).first();

		var tokencenter = token.absoluteBounds().center();
		token.bounds.centerMoveTo(1,2);
		var tokencenter = token.absoluteBounds().center();
		
		this.facade.getCanvas().update();
		
	},
	
	resetTokenPosition: function()
	{
		// Get selected places		
		var allplaces = this.facade.getSelection().findAll(function(selectedItem) {
			return (selectedItem.getStencil().id() === "http://b3mn.org/stencilset/coloredpetrinet#Place");
		});
		
		if (allplaces.length > 0)
		{
			allplaces.each(function(place) {
				
				var placeBounds = place.absoluteBounds();
				var placeCenter = placeBounds.center();
				
				// Calculate radius in order to check if a token is in the place
				var radiusY = placeCenter.y - placeBounds.upperLeft().y;
				var radiusX = placeCenter.x - placeBounds.upperLeft().x;
				var radius = Math.min(radiusY,radiusX);
				var c = radius / 2;
				
				// Get all tokens inside the place 
				var alltokens = place.getChildNodes(false).findAll(function(child) {
					return (child.getStencil().id() === "http://b3mn.org/stencilset/coloredpetrinet#Token");
				});
				
				if (alltokens.length > 0)
				{
					var i = 0;
					var x = 0;
					var y = 0;
					
					alltokens.each(function(token) {
						var tokenBounds = token.absoluteBounds();
						var tokenCenter = tokenBounds.center();
						
						// Calculate the distance between token and center of the place
						var diffX = placeCenter.x - tokenCenter.x;
						var diffY = placeCenter.y - tokenCenter.y;
						var distanceToPlaceCenter= diffX*diffX + diffY*diffY; // take care it's squared
						
						// Check if the token is in the place
						if (radius*radius <= distanceToPlaceCenter)
						{	// if the token is out of the place, calculate the position for the token
							// the token are positioned in circle which is in the place
							y = Math.round(Math.sin((Math.PI / 6) * i) * c);
							x = Math.round(Math.cos((Math.PI / 6) * i) * c);
							// take care centerMoveTo is referred to the position in the selected place (not absolute) 
							token.bounds.centerMoveTo(place.bounds.width() / 2  + x, place.bounds.height() / 2 + y);
							token.update();
							i = i + 1;
						}
					});					
				}
			});

		}			
		this.facade.getCanvas().update();
	},
	
	showWindow: function()
	{
		var selection = this.facade.getStencilSets();
		
		
		
		var declarationsfromDiagram = this.facade.getCanvas();
		
		var test = declarationsfromDiagram[0][2];
		
		// Default definition types for variables and colors
		var defaultDefinitionTypes = ['Integer','Boolean','Char','String'];
		
		var currentDefinitionTypes = []; // for the current Set
		
		var declarationTypeData = 
			[
			 ['CS', 'ColorSet'],
			 ['VA', 'Variable']
			];

		// 2. Create the Store
		var store = new Ext.data.SimpleStore({ 
		fields : 
			[
			 	{ name: 'name', type: 'string' },
			 	{ name: 'type', type: 'string' },
		 		{ name: 'declarationtype', type: 'string' }
			],
			data: declarationsfromDiagram
		});
		
		
//		var defaultData = {
//			    fullname: 'Full Name',
//			    first: 'First Name'
//			};
//			var recId = 100; // provide unique id for the record
//			var r = new myStore.recordType(defaultData, ++recId);
		
		var declarationTypeCombo = new Ext.form.ComboBox({
		    typeAhead: true,
		    triggerAction: 'all',
		    editable: false,
		    lazyRender:true,
		    mode: 'local',
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
			            header: 'DeclarationType', // 5. Field can be edited
			            width: 100, 
			            sortable: false, 
			            dataIndex: 'declarationtype', 
			            editor: declarationTypeCombo
			        }],			        
			        clicksToEdit: 1,
			        stripeRows: true,
			        autoHeight:true,
			        width:500,
			        title:'Editor Grid'
			 });
		        
		var win = new Ext.Window({
			width:400,
	        id:'autoload-win',
	        height:300,
	        autoScroll:true,
	        title:"hallo",
	        tbar:[
	            {
	             	text:'-'
//	             	handler:function() {
//	                	win.load(win.autoLoad.url + '?' + (new Date).getTime());
//	            	}
	            },
	            {
	            	text: '+',
	            	handler: function(){	            		
	            		declarations.push(['Name','Integer','Variable']);
	            		store.loadData(declarations);
	            	}
	            }
	        ],
	        items: [simpleGrid]
	        
	    });
	    win.show();
    }	
});

