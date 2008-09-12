Ext.namespace('Repository');


Repository2 = {
		construct : function(modelUris, currentUser) {
			this._currentUser = currentUser;
			this._publicUser = 'public';
			this._modelCache = new DataCache(modelUris);
			// Event handler
			this._viewChangedHandler = new EventHandler();
			this._selectionChangedHandler = new EventHandler();
			this._filterChangedHandler = new EventHandler();
			// Plugin facade
			this._facade = null;
			// Model arrays
			this._filteredModels = this._modelCache.getIds();
			this._selectedModels = new Array();
			
			this._filters = new Hash();
			this._sort = new Array();
			
			// UI
			this._controls = new Object();
			
			this._plugins = [];
			this._views = new Array();
			this._currentView = -1;
			
			this.bootstrapUI();
			this.loadPlugins();
		},

		getFacade : function() {
			if (!this._facade) {
				this._facade = {
						// Event handler
						registerOnViewChanged : this._viewChangedHandler.registerCallback.bind(this._viewChangedHandler),
						registerOnSelectionChanged : this._selectionChangedHandler.registerCallback.bind(this._selectionChangedHandler),
						registerOnFilterChanged : this._filterChangedHandler.registerCallback.bind(this._filterChangedHandler),
						
						modelCache : this._modelCache,
						
						applyFilter : this.applyFilter.bind(this),
						removeFilter : this.removeFilter.bind(this),
						getFilteredModels : this.getFilteredModels.bind(this),
						
						getViews : this.getViews.bind(this),
						switchView : this.switchView.bind(this),
						
						changeSelection : this.changeSelection.bind(this),
						getSelectedModels : this.getSelectedModels.bind(this),
						
						createNewModel : this.createNewModel.bind(this),
						openModelInEditor : this.openModelInEditor.bind(this),
						
						registerPluginOnPanel : this.registerPluginOnPanel.bind(this),
						registerPluginOnToolbar : this.registerPluginOnToolbar.bind(this),
						registerPluginOnView : this.registerPluginOnView.bind(this)
				};
			}
			return this._facade;
		},
		
		applyFilter : function(name, parameters) {
			Ext.Ajax.request({
				url : 'filter', 
				method : 'post',
				params : {
					filterName : name,
					modelIds : this._modelCache.getIds().toJSON(),
					"params" : parameters.toJSON()
				},
				success : function(response) {
					// Store the result in the _filter hash
					this._filters.set(name, Ext.util.JSON.decode(response.responseText));
					this.updateFilteredIds();
				}.bind(this)
			});
		},
		
		removeFilter : function(name) {
			if (this._filters.get(name) != undefined) {
				this._filters.unset(name);
				this.updateFilteredIds();
			}
		},
		
		updateFilteredIds : function() {
			if (this._filters.keys().length > 0) {
				var filterResult = this._filters.get(this._filters.keys()[0]);
				// filterresult is somehow an intersection between the results of all filters
				filterResult.each(function(pair) {
					var modelId = pair.value;
					this._filters.each(function(filterPair){
						var result = filterPair.value;
						if (result.indexOf(modelId) == -1) {
							filterResult = filterResult.without(modelId);
							return;
						}
					}.bind(this));
				}.bind(this));
				this._filterChangedHandler.invoke(filterResult);
				this._filteredModels = filterResult;
			} else {
				this._filterChangedHandler.invoke(this._modelCache.getIds());
				this._filteredModels = this._modelCache.getIds();
			}
		},
		
		getFilteredModels : function(){
			return this._filteredModels;
		},		
		
		getViews : function(){
			return this._views;
		},
		
		switchView : function(name) {
			this._currentView = name;
			this._viewChangedHandler.invoke(name);
		},
		
		changeSelection : function(selectedIds) {
			selArray =  $A(selectedIds); // Make sure that it's an array
			this._selectedModels = selArray;
			this._selectionChangedHandler.invoke(selArray);
		},
		
		getSelectedModels : function() {
			return this._selectedModels;
		},
		
		createNewModel : function(stencilset) {
			
		},
		
		openModelInEditor : function (model_id) {
		
		},
		
		registerPluginOnPanel : function(pluginName, panelName) {
			panel = this._controls[panelName + 'Panel'];
			if (!panel) return null; // Panel doesn't exist
			var pluginPanel = new Ext.Panel({
				pluginName : pluginName,
                title: pluginName,
                collapsible: true,
                collapsed: false,
                split : true
			});
			pluginPanel = panel.add(pluginPanel);
			panel.doLayout(); // Force rendering to display image and generate body
			return pluginPanel;
		},
		
		registerPluginOnToolbar : function(plugin) {
			if (plugin) {
				if ((plugin.text != "undefined") && (typeof(plugin.handler) == "function")) {
					var menu = null;
					// if the button should be added to a sub menu try to find it and create it if it isn't there
					if (plugin.menu != undefined) {
						this._controls.toolbar.items.each(function(item) {
							if ((item.text == plugin.text) && (item.menu != "undefined")) {
								menu = item.menu;
							}
						});
						// If no menu exists
						if (menu == null) {
							menu = new Ext.menu.Menu({items : []});
							this._controls.toolbar.addButton(new Ext.Toolbar.Button({
									id : 'bla',
									text : plugin.menu, 
									"menu" : menu
								}));
						}
						menu.addMenuItem({
							text : plugin.text,
							handler : plugin.handler,
							icon : plugin.icon,
							qtip : plugin.text
						});
						menu.addMenuItem({
							text : 'TEST',
							handler : plugin.handler,
							icon : plugin.icon,
							qtip : plugin.text
						});
					} else {
						this._controls.toolbar.addButton(new Ext.Toolbar.Button({
							text : plugin.text,
							handler : plugin.handler,
							iconCls: 'some_class_that_does_not_exist_but_fixes-rendering', // do not remove!
							icon : plugin.icon
						}));
					}
				}
			}
		},
		
		registerPluginOnView : function(config) {
			// TODO: check if all values are passed and check whether the plugin already exists
			this._views.push(config.name); 
			if (this._currentView == -1)
				this._currentView = 0;
			this.registerPluginOnToolbar({
				text : config.name, 
				icon : config.icon, 
				menu : 'Views', 
				handler : function() {this.switchView(config.name)}.bind(this)
			});
			return this._controls.viewPanel;
		},
		
		loadPlugins : function() {
			this._plugins.push(new Repository.Plugins.ModelTypeFilter(this.getFacade()));
			this._plugins.push(new Repository.Plugins.NewModelControls(this.getFacade()));
			this._plugins.push(new Repository.Plugins.DebugView(this.getFacade()));
			this._plugins.push(new Repository.Plugins.ModelTagInfo(this.getFacade()));
			this.switchView('Debug View');
		},
		
		
		/* This functions defines and initialize the basic UI components
		 * 
		 */
		bootstrapUI : function() {
			test_tpl = new Ext.XTemplate('<div id="oryx_repository_header"> <tpl if="isPublicUser"> PUBLIC </tpl><tpl if="!isPublicUser">  NOT PUBLIC </tpl></div>');
			
			// View panel
			this._controls.viewPanel = new Ext.Panel({ 
                region: 'north',
                height : 400
            });
			// Left panel
			this._controls.leftPanel = new Ext.Panel({ 
                region: 'west',
                title: 'Organize Models',
                collapsible: true,
                collapsed: false,
                split : true,			            		
            });
			// Right panel
			this._controls.rightPanel = new Ext.Panel({ 
                region: 'east',
                title: 'Model Info',
                collapsible: true,
                collapsed: false,
                split : true,		            		
            });			
			// Bottom panel
			this._controls.bottomPanel = new Ext.Panel({ 
                region: 'south',
                title: 'Comments',
                collapsible: true,
                collapsed: false,
                split : true,
                hidden : true,		            		
            });
			// Toolbar
			this._controls.toolbar = new Ext.Toolbar({
    		   region : 'south', 
    		   items : [] // Button
     	   	});
			// center panel, contains view panel and bottom panel
			this._controls.centerPanel = new Ext.Panel({
				region : 'center',
				items : [this._controls.viewPanel, this._controls.bottomPanel]
			});
			
			this._viewport = new Ext.Viewport({
					layout: 'border',
					margins : '0 0 0 0',
					defaults: {}, // default config for all child widgets
					items: [ 
					        new Ext.Panel({ // Header panel for login and toolbar
								region : 'north',
								height : 60,
								margins : '0 0 0 0',
								border : true,
								items :[{ // Header with logo and login
							                region: 'north',
							                html: Repository.Templates.login().apply({currentUser : this._currentUser, isPublicUser : this._currentUser=='public'}),
							                height: 30
							           },
							           this._controls.toolbar
							           	] // Toolbar
							}), // Panel 
							this._controls.centerPanel,
				            this._controls.leftPanel,	
						    this._controls.rightPanel
					       ]
			});
		}
}