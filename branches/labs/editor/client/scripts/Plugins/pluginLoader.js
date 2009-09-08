if (!ORYX.Plugins) 
    ORYX.Plugins = new Object();

ORYX.Plugins.PluginLoader = Clazz.extend({
	
    facade: undefined,
	
	processURI: undefined,
	
    construct: function(facade){
		this.facade = facade;
		
		this.facade.offer({
			'name': ORYX.I18N.PluginLoad.AddPluginButtonName,
			'functionality': this.showManageDialog.bind(this),
			'group': "",
			'icon': ORYX.PATH + "images/labs/script_add.png",
			'description': ORYX.I18N.PluginLoad.AddPluginButtonDesc,
			'index': 8,
			'minShape': 0,
			'maxShape': 0
		});},
	showManageDialog: function(){
	/*var myMask = new Ext.LoadMask(Ext.getBody(), {msg:ORYX.I18N.Oryx.pleaseWait});
	myMask.show();*/
	var data=[];
	var plugins=this.facade.getAvailablePlugins();
		plugins.each(function(plugin){
			data.push([plugin.name, plugin.active===true]);
			})
		if(data.length==0){return};
		var reader = new Ext.data.ArrayReader({}, [
        {name: 'name'},
		{name: 'active'} ]);
		
		var sm = new Ext.grid.CheckboxSelectionModel({
			listeners:{
			beforerowselect: function(sm,nbr,exist,rec){
				this.facade.activatePluginByName(rec.data.name, 
						function(sucess,err){
							if(!!sucess){
								sm.suspendEvents();
								sm.selectRow(nbr, true);
								sm.resumeEvents();
							}else{
								Ext.Msg.show({
		   							   title: ORYX.I18N.PluginLoad.loadErrorTitle,
									   msg: ORYX.I18N.PluginLoad.loadErrorDesc + ORYX.I18N.PluginLoad[err],
									   buttons: Ext.MessageBox.OK
									});
							}});
				return false;
				}.bind(this),
			rowdeselect: function(sm,nbr,rec){
						sm.suspendEvents();
						sm.selectRow(nbr, true);
						sm.resumeEvents();
					}
			}});
	    var grid2 = new Ext.grid.GridPanel({
	    		store: new Ext.data.Store({
		            reader: reader,
		            data: data
		        	}),
		        cm: new Ext.grid.ColumnModel([
		            
		            {id:'name',width:250, sortable: true, dataIndex: 'name'},
					sm]),
			sm: sm,
	        width:310,
	        height:150,
	        frame:true,
			hideHeaders:true,
	        iconCls:'icon-grid',
			listeners : {
				render: function() {
					var recs=[];
					this.grid.getStore().each(function(rec){

						if(rec.data.active){
							recs.push(rec);
						}
					}.bind(this));
					this.suspendEvents();
					this.selectRecords(recs);
					this.resumeEvents();
				}.bind(sm)
			}
	    });

		var newURLWin = new Ext.Window({
					title:		ORYX.I18N.PluginLoad.WindowTitle, 
					//bodyStyle:	"background:white;padding:0px", 
					width:		'auto', 
					height:		'auto',
					//html:"<div style='font-weight:bold;margin-bottom:10px'></div><span></span>",
				});
		newURLWin.add(grid2);
		//myMask.hide();
		newURLWin.show();
		}
		})
			