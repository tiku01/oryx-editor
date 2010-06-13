/** Import of Visio BPMN diagrams, that have been build with own stencil sets.
 *	Created in the PMP seminar 2010. 
 *  Author: Lauritz Thamsen
 **/
if (!ORYX.Plugins)
	ORYX.Plugins = new Object();

/**
 * Enables importing of Visio diagrams in the current model (editor).
 */
ORYX.Plugins.VisioBPMNImport = ORYX.Plugins.AbstractPlugin.extend( {

	construct : function() {
		// Call super class constructor
	arguments.callee.$.construct.apply(this, arguments);

	this.facade.offer( {
		'name' : ORYX.I18N.VisioBPMNImport.name,
		'functionality' : this.showImportDialog.bind(this),
		'group' : 'Import',
		dropDownGroupIcon : ORYX.PATH + "images/import.png",
		'icon' : ORYX.PATH + "images/visio_icon.png",
		'description' : ORYX.I18N.VisioBPMNImport.desc,
		'index' : 1,
		'minShape' : 0,
		'maxShape' : 0
	});
},

/**
 * Opens a upload dialog.
 *
 */
showImportDialog : function(successCallback) {

	var form = new Ext.form.FormPanel( {
		baseCls : 'x-plain',
		labelWidth : 50,
		defaultType : 'textfield',
		items : [ {
			text : ORYX.I18N.VisioBPMNImport.selectFile,
			style : 'font-size:12px;margin-bottom:10px;display:block;',
			anchor : '100%',
			xtype : 'label'
		}, {
			fieldLabel : ORYX.I18N.VisioBPMNImport.file,
			name : 'subject',
			inputType : 'file',
			style : 'margin-bottom:10px;display:block;',
			itemCls : 'ext_specific_window_overflow'
		}, {
			xtype : 'textarea',
			hideLabel : true,
			name : 'msg',
			anchor : '100% -63'
		} ]
	});

	// Create the panel
	var dialog = new Ext.Window( {
		autoCreate : true,
		layout : 'fit',
		plain : true,
		bodyStyle : 'padding:5px;',
		title : ORYX.I18N.VisioBPMNImport.name,
		height : 350,
		width : 500,
		modal : true,
		fixedcenter : true,
		shadow : true,
		proxyDrag : true,
		resizable : true,
		items : [ form ],
		buttons : [ {
			text : ORYX.I18N.VisioBPMNImport.btnImp,
			handler : function() {

				var loadMask = new Ext.LoadMask(Ext.getBody(), {
					msg : ORYX.I18N.VisioBPMNImport.progress
				});
				loadMask.show();

				window.setTimeout(function() {
					var vdx = form.items.items[2].getValue();
					this._getAllPages(vdx, load);
				}.bind(this), 100);
				dialog.hide();
			}.bind(this)
		}, {
			text : ORYX.I18N.VisioBPMNImport.btnClose,
			handler : function() {
				dialog.hide();
			}.bind(this)
		} ]
	});

	// Destroy the panel when hiding
	dialog.on('hide', function() {
		dialog.destroy(true);
		delete dialog;
	});

	// Show the panel
	dialog.show();

	// Adds the change event handler to 
	form.items.items[1].getEl().dom.addEventListener('change', function(evt) {
		var text = evt.target.files[0].getAsText('UTF-8');
		form.items.items[2].setValue(text);
	}, true)

},

_getAllPages : function(vdx, loadMask) {

	var parser = new DOMParser();
	var xmlDoc = parser.parseFromString(vdx, "text/xml");
	var allPages = xmlDoc.getElementsByTagName("VisioDocument");

	// If there are no pages in the vdx file, it's propably not a correct file
	if (allPages.length == 0) {
		loadMask.hide();
		this._showErrorMessageBox(ORYX.I18N.VisioBPMNImport.name,
				ORYX.I18N.VisioBPMNImport.notVdxError);

		return;
	}

	if (allPages.length == 1) {

		this._sendRequest(ORYX.CONFIG.VISIO_BPMN_IMPORT, 'POST', {
			'data' : vdx
		}, function(arg) {
			if (arg.startsWith("error:")) {
				this._showErrorMessageBox(ORYX.I18N.Oryx.title, arg);
				loadMask.hide();
			} else {
				this.facade.importJSON(arg);
				loadMask.hide();
			}
		}.bind(this), function() {
			loadMask.hide();
			this._showErrorMessageBox(ORYX.I18N.Oryx.title,
					ORYX.I18N.VisioBPMNImport.connectionError);
		}.bind(this));

		return;
	}

	var i, pageName, data = [];
	for (i = 0; i < allPages.length; i++) {
		pageAttr = allPages[i].children[0];
		pageName = pageAttr.attributes[0].nodeValue;
		data.push( [ pageName ]);
	}

	loadMask.hide();
	this.showPageDialog(data, vdx);
},

_showErrorMessageBox: function(title, msg){
    Ext.MessageBox.show({
       title: title,
       msg: msg,
       buttons: Ext.MessageBox.OK,
       icon: Ext.MessageBox.ERROR
   });
}
});
