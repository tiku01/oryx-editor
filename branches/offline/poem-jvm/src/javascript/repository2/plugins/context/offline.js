// define namespace
if(!Repository) var Repository = {};
if(!Repository.Plugins) Repository.Plugins = {};

/**
 * Supplies filtering by model type (stencil set)
 * Note: Only stencil sets defined in the stencilsets.json can be selected as filter
 */

Repository.Plugins.Offline = {
	
	// This plugin is only enabled 
	// when user is logged on
	enabled:true,
	STORE_NAME:"oryx",
	MANIFEST_FILENAME:"../manifest.json",
	localServer:false,
	store:false,
	
	offlineActive:true,
	
	construct: function( facade ) {
		// Set the name
		this.name = Repository.I18N.OfflinePlugin.name;
		this._currentGUIGen=false;
		this._myProgressBar=false;

		// call Plugin super class
		arguments.callee.$.construct.apply(this, arguments);
		this._offlineUser=false;
		this._labelmodels=false;
		this._selectedData=false;
		//this._loadDefaultData();
		this._offlineModels=[];
	},
	render: function(modelData){
		if( !this.enabled ){ return }
		if(!this._currentGUIGen){
			this._chooseGUI();
		}
		this._selectedData=modelData;
		// Set absolute Height
		this.panel.getEl().setHeight( this.panel.getEl().getHeight() )
		this._offlineModels=this.facade.modelCache.getOfflineModels();
		if(this._currentGUIGen){
			this._currentGUIGen();}
		this.panel.doLayout();
		
	},
	checkForUpdates: function(){
		if(!navigator.onLine){return} //Offline no updated possible
		
		var data=[];
		this.facade.modelCache.getOfflineChangedModels().each(function(model){
			data.push(model);
			})
		if(data.length==0){return};
		var reader = new Ext.data.ArrayReader({}, [
       {name: 'uri'},
	    {name: 'name'},
	    {name: 'types'}
	    ]);
		
		var sm = new Ext.grid.CheckboxSelectionModel();
	    var grid2 = new Ext.grid.GridPanel({
	    		store: new Ext.data.Store({
		            reader: reader,
		            data: data
		        	}),
		        cm: new Ext.grid.ColumnModel([
		            sm,
		            {id:'uri',header: Repository.I18N.OfflinePlugin.updateHeaderUri, width: 80, sortable: true, dataIndex: 'uri'},
					{header: Repository.I18N.OfflinePlugin.updateHeaderName, width: 100, sortable: true, dataIndex: 'name'},
					{header: Repository.I18N.OfflinePlugin.updateHeaderTypes, width: 60, sortable: true, dataIndex: 'types'}
					]),
			sm: sm,
	        width:300,
	        height:150,
	        frame:true,
	        title:Repository.I18N.OfflinePlugin.updateTitle,
	        iconCls:'icon-grid'
	    });
		var newURLWin = new Ext.Window({
					title:		Repository.I18N.OfflinePlugin.updateTableTitle, 
					//bodyStyle:	"background:white;padding:0px", 
					width:		'auto', 
					height:		'auto',
					//html:"<div style='font-weight:bold;margin-bottom:10px'></div><span></span>",
					buttons:[
					{text:Repository.I18N.OfflinePlugin.updateButton,handler:function(){
									var toUpdate=[];
									sm.getSelections().each(function(record){
										toUpdate.push(record.data.uri.substring(7));
									});
									toUpdate.each(function(tmp){
										this.facade.modelCache.updateChange(tmp, function(){
											Ext.Msg.alert(Repository.I18N.OfflinePlugin.updateStatus,Repository.I18N.OfflinePlugin.successfulUploadOf+tmp);
											this.facade.updateView();
											}.bind(this));
									}.bind(this));
									
									
									this.render(this._selectedData)
									newURLWin.destroy()
											
								}.bind(this)},
					{text:Repository.I18N.OfflinePlugin.discardButton,handler:function(){
									var toUpdate=[];
									sm.getSelections().each(function(record){
										toUpdate.push(record.data.uri.substring(7));
									});
									toUpdate.each(function(tmp){
										this.facade.modelCache.discardChange(tmp, function(){
											this.facade.updateView();
										}.bind(this));
										
									}.bind(this));
									this.render(this._selectedData)
									newURLWin.destroy()
									
								}.bind(this)},
					{text:Repository.I18N.OfflinePlugin.closeButton,handler:function(){
								newURLWin.destroy()
								}.bind(this)}
							]
				});
		newURLWin.add(grid2);
		newURLWin.show();
			
	},

	_askForIdentity: function(){
		var panelData=[];
		panelData.push({text:Repository.I18N.OfflinePlugin.chooseAnOfflinIdentity, xtype:'label', style:"display:block;font-weight:bold;margin-bottom:10px;"})
		var newUrlWin = new Ext.Window({
			title:		Repository.I18N.OfflinePlugin.offlineLogin, 
			width:		'auto', 
			height:		'auto',
		});
		var label=false;
		var button=false;
		var users=this.facade.modelCache.getAllOfflineUser()
		if (users.length == 1) {
			this._enableGearsFor(users[0])
		}
		else if (users.length == 0) {
					label = {
						text: "public",
						xtype: 'label',
						style: "display:block;font-weight:bold"
					};
					button = new Ext.LinkButton({
						image: '../images/silk/user_go.png',
						imageStyle: 'width:16px;',
						text: Repository.I18N.OfflinePlugin.use+"public",
						click: function(tmp, win){
							if (this._enableGearsFor(tmp)) {
								this.facade.updateView();
								this.render([]);
								win.destroy();
							}
							
						}.bind(this, "public", newUrlWin)						,
						border: '4px'
					});
					panelData.push(new Ext.Panel({
						style: 'padding:10px;',
						border: false,
						items: [label, button]
					}))
				newUrlWin.add(new Ext.Panel({
					style: 'padding:10px;',
					border: false,
					items: panelData
				}))
				newUrlWin.show();
			}
		else {
				users.each(function(user){
					label = {
						text: user,
						xtype: 'label',
						style: "display:block;font-weight:bold"
					};
					button = new Ext.LinkButton({
						image: '../images/silk/user_go.png',
						imageStyle: 'width:16px;',
						text: Repository.I18N.OfflinePlugin.use+user,
						click: function(tmp, win){
							this._enableGearsFor(tmp);
							this.facade.updateView();
							this.render([]);
							win.destroy();
						}.bind(this, user, newUrlWin)						,
						border: '4px'
					});
					panelData.push(new Ext.Panel({
						style: 'padding:10px;',
						border: false,
						items: [label, button]
					}))
				}.bind(this));
				newUrlWin.add(new Ext.Panel({
					style: 'padding:10px;',
					border: false,
					items: panelData
				}))
				newUrlWin.show();
			}
	},
	_chooseGUI:function(){
		//only activated on startup--> first render
		
		var decideForGears=false;
		if (this.myPanel) {
					this.myPanel.destroy()
				};
		if(this.facade.getCurrentUser()=="public" && navigator.onLine){
			this._currentGUIGen=this._generateNoPublic;
			return
		}
		// What we get?
		if (!window.google || !google.gears) {
			// No gears at all
			this._currentGUIGen=this._generateInstall;
			
		}
		else {
			//gears without permission
			if (!google.gears.factory.hasPermission) {
				//ask for permission
				if (!google.gears.factory.getPermission(Repository.I18N.OfflinePlugin.gearsTitle, "../images/style/oryx.gif", Repository.I18N.OfflinePlugin.pleasePermit)) {
					this._currentGUIGen = this._generateDecline;
				}
				else{
					var decideForGears=true;
				}
			}
			//Gears with permission
			if(google.gears.factory.hasPermission) {
				// Gears is running, horay
				//this.facade.modelCache.setGears(true);
				this.facade.modelCache.enableGears();

				if (!navigator.onLine){
					if(!this._enableGearsFor(this.facade.getCurrentUser())){this._askForIdentity()}
					
					this._currentGUIGen=this._generateNormal;
				}
				else{
					var userFound=false;
					if(decideForGears){
						userFound=this._createOfflineUserForCurrent()	}
					else{
						userFound=this._enableGearsFor(this.facade.getCurrentUser())}
					if (userFound) {
						this._currentGUIGen = this._generateNormal;
						this.facade.modelCache.addListenerGearsManagedStore(this._updateProgressBar.bind(this));
					}
				}				
			};
		};
	},
	_generateNoPublic: function(){
				var label			= {text: "Please, log in to enable offline capability", xtype:'label', style:"display:block;font-weight:bold;margin-bottom:10px;"};
				
				
				this.myPanel = new Ext.Panel({
					style	: 'padding:10px;', 
					border	: false,
					items	: [label]
				});
			this._deleteItems(this.panel);
			this.panel.add(this.myPanel);
			this.panel.doLayout();
	},
	_generateDecline: function(){
				var declineLabel			= {text: Repository.I18N.OfflinePlugin.declineNotice, xtype:'label', style:"display:block;font-weight:bold;margin-bottom:10px;"};
				
				
				this.myPanel = new Ext.Panel({
					style	: 'padding:10px;', 
					border	: false,
					items	: [declineLabel]
				});
			this._deleteItems(this.panel);
			this.panel.add(this.myPanel);
			this.panel.doLayout();
	},
	_generateInstall: function(){
				var label			= {text: Repository.I18N.OfflinePlugin.installNotice, xtype:'label', style:"display:block;font-weight:bold;margin-bottom:10px;"};

				var button1			= new Ext.LinkButton({
											image		:'../images/silk/arrow_down.png',
											text 		: "Install",
											click 		: function(){
												location.href = "http://gears.google.com/?action=install&message=" +Repository.I18N.OfflinePlugin.gearsInstall+
												"&return=" +
												location.href +
												"&name="+Repository.I18N.OfflinePlugin.gearsTitle
											}.bind(this)
														
											});
			this.myPanel = new Ext.Panel({
					style	: 'padding:10px;', 
					border	: false,
					items	: [label, button1]
				});
			this._deleteItems(this.panel);
			this.panel.add(this.myPanel);
			this.panel.doLayout();
			this.panel.collapse();
			
	},
	_generateNormal: function(){
		var normalItems=[];
		
		//Add update notice
		var changedMod=this.facade.modelCache.getOfflineChangedModels()
		if(changedMod.length!=0 && navigator.onLine){
			var label = {
				text: Repository.I18N.OfflinePlugin.updateNote,
				xtype: 'label',
				style: "display:block;font-weight:bold;margin-bottom:10px;"
			};
			var button=new Ext.LinkButton({
											image		:'../images/silk/exclamation.png',
											imageStyle	:'width:16px;',
											text 		: Repository.I18N.OfflinePlugin.updateButton,
											click 		: this.checkForUpdates.bind(this),
											border		: '4px',
														
											});
			normalItems.push(new Ext.Panel({
					style	: 'padding:10px;', 
					border	: false,
					items	: [label, button]
				}))
					
		}
		//Add selection specific on/offline button
		if(this._selectedData && this.facade.modelCache.getCurrentOfflineUser() && navigator.onLine){
			if($H(this._selectedData).keys().length !== 0){
				var yetOffline=0;
					this._offlineModels.each(function(offID){
					if(this._selectedData.keys().member(offID)){yetOffline++;}
				}.bind(this))
				if (yetOffline>0) {
					var label={text: Repository.I18N.OfflinePlugin.removeSelection, xtype:'label', style:"display:block;font-weight:bold;margin-bottom:10px;"};
					var button= new Ext.LinkButton({
											image		:'../images/online_klein.png',
											imageStyle	:'width:16px;',
											text 		: Repository.I18N.OfflinePlugin.removeSelection,
											click 		: this._deleteOfflineModels.bind(this,false),
											border		: '4px'
											});
					normalItems.push(new Ext.Panel({
						style	: 'padding:10px;', 
						border	: false,
						items	: [label, button]
					}))
				}
				if(yetOffline<this._selectedData.keys().length && navigator.onLine){
					var label={text: Repository.I18N.OfflinePlugin.addSelection, xtype:'label', style:"display:block;font-weight:bold;margin-bottom:10px;"};
					var button= new Ext.LinkButton({
											image		:'../images/offline_klein.png',
											imageStyle	:'width:16px;',
											text 		: Repository.I18N.OfflinePlugin.addSelection,
											click 		: this._setModelsOffline.bind(this),
											border		: '4px',
														
											});
					normalItems.push(new Ext.Panel({
						style	: 'padding:10px;', 
						border	: false,
						items	: [label, button]
					}))
					
				}
				}
				
				
		}
		
		//Add progress bar
		if(!this._myProgressBar){
		this._myProgressBar= new Ext.ProgressBar();
		this._myProgressBar.setVisible(false);
		};
		normalItems.push(this._myProgressBar);
		
		// Add blank button
		/*var blankLabel={text: Repository.I18N.OfflinePlugin.blankData, xtype:'label', style:"display:block;font-weight:bold;margin-bottom:10px;"};
		var blankButton= new Ext.LinkButton({
											image		:'../images/silk/cancel.png',
											imageStyle	:'width:16px;',
											text 		: Repository.I18N.OfflinePlugin.blankData,
											click 		: this._blankOfflineData.bind(this),
											border		: '4px',
														
											});
					normalItems.push(new Ext.Panel({
						style	: 'padding:10px;', 
						border	: false,
						items	: [blankLabel, blankButton]
					}))*/
		normalItems.push(this._generateLoginPanel())
		// Add the panel
		this.myPanel = new Ext.Panel({ 
					style	: 'padding:10px;', 
					border	: false,
					items	: normalItems
				});
		this._deleteItems(this.panel);
		this.panel.add( this.myPanel );
		
		
		this.panel.doLayout();
						
	},
	_generateDebugPanel: function(){
		
	
			var timerId = window.setInterval(function() {
			var errorLabel			= {text: this.facade.modelCache.lastGearsError, xtype:'label', style:"display:block;font-weight:bold;margin-bottom:10px;"};
				
				if(!this.myPanel2){
				this.myPanel2 = new Ext.Panel({
					style	: 'padding:10px;', 
					border	: false,
					items	: [errorLabel]
				});}
				
			this.panel.add(this.myPanel2);
			this.panel.doLayout();
		  	}.bind(this), 1000);
	},
	_generateLoginPanel: function(){
		var loginStuff=new Array();
		var user=this.facade.modelCache.getCurrentOfflineUser();
		var allUsers=this.facade.modelCache.getAllOfflineUser()
		if(user &&!navigator.onLine){
					loginStuff.push({text: Repository.I18N.OfflinePlugin.yourCurrentIdentityIs+ " \n "+user, xtype:'label', style:"display:block;margin-bottom:10px;"});
					if (allUsers.length > 1) {
						loginStuff.push({text:Repository.I18N.OfflinePlugin.youCanSwitchYourIdentity, xtype:'label', style:"display:block;margin-bottom:10px;"});
						loginStuff.push({
							text: Repository.I18N.OfflinePlugin.switchIdentity,
							xtype: 'label',
							style: "display:block;font-weight:bold;margin-bottom:10px;"
						});
						
						loginStuff.push(new Ext.LinkButton({
							image: '../images/silk/user_edit.png',
							imageStyle: 'width:16px;',
							text: Repository.I18N.OfflinePlugin.switchIdentity,
							click: function(){
								this._askForIdentity()
							}.bind(this)							,
							border: '4px',
						
						}));
					}
		}
		else if(!user &&!navigator.onLine){
			
			loginStuff.push({text: Repository.I18N.OfflinePlugin.youNotLoggedChooseIdentity, xtype:'label', style:"display:block;font-weight:bold;margin-bottom:10px;"});
			if (allUsers.length >= 1) {
				loginStuff.push({
					text: "Choose Identity",
					xtype: 'label',
					style: "display:block;font-weight:bold;margin-bottom:10px;"
				});
				loginStuff.push(new Ext.LinkButton({
					image: '../images/silk/user_delete.png',
					imageStyle: 'width:16px;',
					text: Repository.I18N.OfflinePlugin.chooseIdentity,
					click: function(){
						this._askForIdentity()
					}.bind(this)					,
					border: '4px',
				
				}));
			}
		}
		else if(user){
			if (user != "public") {
					loginStuff.push({text: Repository.I18N.OfflinePlugin.workingOfflineCapable+" \n "+Repository.I18N.OfflinePlugin.disableOfflineCapability, xtype:'label', style:"display:block;margin-bottom:10px;"});
					
					loginStuff.push({
						text: Repository.I18N.OfflinePlugin.disableCurrentAccount,
						xtype: 'label',
						style: "display:block;font-weight:bold;margin-bottom:10px;"
					});
					loginStuff.push(new Ext.LinkButton({
						image: '../images/silk/user_delete.png',
						imageStyle: 'width:16px;',
						text: Repository.I18N.OfflinePlugin.disableCurrentAccount,
						click: function(){
							this.facade.modelCache.deleteOfflineUser(this.facade.getCurrentUser())
							this.render(this._selectedData);
							this.facade.applyFilter();
						}.bind(this),
						border: '4px',
					
					}))
					if (!this._labelmodels) {
						this._labelmodels=true
						this.facade.updateView();
						
					}
					}
					else{
						loginStuff.push({text: "public"+" \n ", xtype:'label', style:"display:block;margin-bottom:10px;"});

					}
			
		}
		else{
			loginStuff.push({text: Repository.I18N.OfflinePlugin.gearsNotEnabledforAccount+"\n "+Repository.I18N.OfflinePlugin.getAnofflineLogin, xtype:'label', style:"display:block;margin-bottom:10px;"});
			loginStuff.push({text: Repository.I18N.OfflinePlugin.getOfflineLogin, xtype:'label', style:"display:block;font-weight:bold;margin-bottom:10px;"});
			loginStuff.push(new Ext.LinkButton({
									image		:'../images/silk/user_add.png',
									imageStyle	:'width:16px;',
									text 		: Repository.I18N.OfflinePlugin.getOfflineLogin,
									click 		: this._createOfflineUserForCurrent.bind(this),
									border		: '4px',
												
									}));
		}
				//User count
					loginStuff.push(new Ext.Panel({
						style	: 'padding:10px;', 
						border	: false,
						items	: [{text: Repository.I18N.OfflinePlugin.totalNumberOfOfflineUsers+allUsers.length, xtype:'label', style:"display:block;margin-bottom:10px;"}]
					}))
							
		
		return new Ext.Panel({ 
					style	: 'padding:10px;', 
					border	: false,
					items	: loginStuff
		});
	},
	_addItems: function( panel, items ){
		panel.add(new Ext.Panel({items:items, border: false})) 		
		panel.getEl().setHeight()
		panel.doLayout();
	},
	_deleteItems: function( panel ){
		if( panel && panel.items ){
			panel.getEl().setHeight( panel.getEl().getHeight() )
			panel.items.each(function(item){ panel.remove( item ) }.bind(this));
		}
			
	},
	_deleteModels: function() {
		
		this.facade.modelCache.deleteData( this.facade.getSelectedModels(), "/self", null, function(){ this.facade.applyFilter() }.bind(this) );
		 
	},
	_storeChanges: function(name, summary){

		this.facade.modelCache.setData( this.facade.getSelectedModels(), "/meta", { title:(name), summary:(summary) },  function(){  this.facade.updateView() }.bind(this) )
		
	},

	_blankOfflineData: function() {
		 /*this.facade.modelCache.removeStores();
		 this.facade.modelCache.removeDatabase();*/
		this.facade.modelCache.clearCache();
		this.facade.modelCache.restoreDefault();
		this.facade.modelCache.clearDatabase();
		this.facade.updateView();
		},
	_setModelsOffline: function(){
		var models=this.facade.getSelectedModels();
		this.facade.modelCache.saveModelsOffline(models);
		this._offlineModels=this.facade.modelCache.getOfflineModels();
		this.facade.updateView();
		this.render();
	},
	_deleteOfflineModels:function(all){
		if(all){
			this.facade.modelCache.deleteOfflineModels(this.facade.modelCache.getOfflineModels());
		}
		else{
			var models=this.facade.getSelectedModels();
			this.facade.modelCache.deleteOfflineModels(models);
		}
		this._offlineModels=this.facade.modelCache.getOfflineModels();
		this.facade.updateView();
		this.render();
	},
	_updateProgressBar:function(details){
		
		if (this._myProgressBar) {
			this._myProgressBar.setVisible(true);
			if (details.filesComplete == details.filesTotal) {
				this._myProgressBar.updateProgress((details.filesComplete / details.filesTotal),Repository.I18N.OfflinePlugin.offlineCapable)
			}
			else {
				this._myProgressBar.updateProgress((details.filesComplete / details.filesTotal), Repository.I18N.OfflinePlugin.loadingFiles + details.filesComplete);
			}
		}
	},
	loadDefaultData:function(){
		this._enableGearsFor('Public')
	},
	_enableGearsFor: function(name){
		try{
				if (google.gears.factory.hasPermission) {
					if(this.facade.modelCache.setOfflineUser(name)){
						this._offlineUser=this.facade.modelCache.getCurrentOfflineUser();
						
						if (!navigator.onLine) {
							//write in the cookie
							var cookie_date = new Date ( );  // current date & time
							cookie_date.setTime ( cookie_date.getTime() - 1 );
							document.cookie = "JSESSIONID" + "=0; expires=" + cookie_date.toGMTString();
							document.cookie="identifier="+name;
							if (this.facade.getCurrentUser() != name) {
							  	window.location.reload()
								return
							  }	
							//Load all Data from Database
							this.facade.modelCache.getOfflineTypes();
							this.facade.modelCache.getOfflineData();
							

								 
						}
						else {
							//Ensure offline Data
							this.facade.modelCache.loadOfflineUserData();
							this.facade.modelCache.getDefaultDataOffline();
							this.facade.modelCache.saveModelsOffline(this.facade.modelCache.getOfflineModels(this.facade.getCurrentUser()))
					};
					this.facade.applyFilter()
					return true;
					}
					else{
						Ext.Msg.alert(Repository.I18N.OfflinePlugin.noOfflineUser,Repository.I18N.OfflinePlugin.betterUse+ this.facade.modelCache.getAllOfflineUser())
						return false}
					
									}
			}catch(e){
				Ext.Msg.alert(Repository.I18N.OfflinePlugin.offlineError,Repository.I18N.OfflinePlugin.loadingCapabilityFailed+e)
				return false;}
	},
	_createOfflineUserForCurrent: function(){
		var user=this.facade.getCurrentUser()
		this.facade.modelCache.createOfflineUser(user)
		var result=this._enableGearsFor(user)
		return result		
	}
	
	
};

Repository.Plugins.Offline = Repository.Core.ContextPlugin.extend(Repository.Plugins.Offline);
