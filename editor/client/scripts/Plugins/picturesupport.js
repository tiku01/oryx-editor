/**
 * Copyright (c) 2010
 * Tobias Metzke
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/
 
if(!ORYX.Plugins) {
	ORYX.Plugins = {};
}

// just for logging
ORYX_LOGLEVEL = 3;
 
ORYX.Plugins.PictureSupport = ORYX.Plugins.AbstractPlugin.extend({
 
	 construct: function(){
        // Call super class constructor
        arguments.callee.$.construct.apply(this, arguments);
        
        // build an import button in the tool bar
        this.facade.offer({
			'name':				"Import PICTURE XML",
			'functionality': 	this.importPicture.bind(this),
			'group': 			"Export",
			'dropDownGroupIcon':ORYX.PATH + "images/import.png",
			'icon': 			ORYX.PATH + "images/page_white_javascript.png",
			'description': 		ORYX.I18N.PictureSupport.importDescription,
			'index': 			1,
			'minShape': 		0,
			'maxShape': 		0
		});
        
        /* ATM unused
        // change the shape menu's alignment
        ORYX.CONFIG.SHAPEMENU_RIGHT = ORYX.CONFIG.SHAPEMENU_BOTTOM;
        ORYX.CONFIG.SHAPEMENU_BUTTONS_PER_LEVEL_BOTTOM = 6;
        */
        
        // catch occurring events
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_LOADED, this.pictureInstantiation.bind(this));
		this.facade.registerOnEvent('layout.picture.node', this.handleProperties.bind(this));
		
	},
	
	/**
	 * A method for calculating the height of a given
	 * label element according to its content and
	 * the font size this content has.
	 * @param labelElement the label element (e.g. an HTML text tag) 
	 * 		to calculate the height for
	 * @param labelValue the content of the label element
	 * @return integer that returns the height of the label element
	 */
	calculateLabelHeight: function (labelElement, labelValue) {
		// if the label is empty, its height shall be 0
		if(labelValue === ""){return 0;}
		
		// the label is not empty, so at least we start with line count 1
		var fontSize = labelElement.getFontSize();
		var lineCount = 1;
		
		// for every line the count goes up
		labelValue.scan('\n', function() { lineCount += 1; });
		
		// the height accords to lines and the font size
		return lineCount * fontSize + 7;
	},
	
	/**
	 * A method for finding the whole text of a shape's properties
	 * that start with a certain substring.
	 * The property's IDs need to start with "oryx-" + the given string.
	 * @param shape the shape to be scanned
	 * @param string the string all to-be-scanned properties begin with
	 * @return string concatenated content of all matching property values
	 */
	findLabelValue: function(shape,string){
		var value = "";
		var properties = shape.properties.keys().findAll(function(element){return element.substr(5,element.length) === string;});
		properties.each(function(element){value += shape.properties[element];});
		ORYX.Log.info(value);
		return value;
	},
	
	/**
	 *
	 */
	findLabels: function(shape, string){
		return shape.getLabels().findAll(function(label) {return label.id.substr(0,(shape.id + string).length) === (shape.id + string);})
	},
	
	/**
	 * A method that handles the arrangement of a node's properties and their contents.
	 * @param event the event that triggered the method and holds the node that needs to be rearranged
	 */
	handleProperties: function(event){		
		var shape = event.shape;
		var properties = shape._svgShapes.find(function(element) { return element.element.id === (shape.id + "properties_frame"); });
		var image_frames = shape._svgShapes.findAll(function(element) { return element.element.id.substr(element.element.id.length-5,5) === "image"; });
		var propHeight = 0;
		var titleHeight = shape._svgShapes.find(function(element) { return element.element.id === (shape.id + "text_frame_title"); }).height;
		
		/* before showing the properties the correct height of the node needs to be calculated
		 * and the chapters need to be arranged according to their content
		 */
		if(shape.properties["oryx-basic-show-properties"]===true){
			
			// minimum distance of a property entry from top of the node
			var distanceFromTitle = titleHeight + 5;
			
			var chapters = new Array("description","realisation","incoming","outgoing","communication","payment","resource","comment");		
			// all chapters need several styling steps now
			chapters.each(function(chapter){
				this.findLabels(shape,chapter).each(function(label){
					// get the value of the label
					var text = this.findLabelValue(shape,label.id.slice(shape.id.length,label.id.length));
					// calculate height of the label
					var height = this.calculateLabelHeight(label,text);
					// calculate the label's distance from top
					var distanceFromTop = distanceFromTitle;
					distanceFromTitle += height;
					// set the label's position
					label.y = distanceFromTop;
					label.node.setAttribute("y", distanceFromTop);
				}.bind(this))
			}.bind(this));
			
			// set the properties' height
			propHeight = distanceFromTitle - titleHeight;
			properties.element.setAttribute("height", propHeight);
		}
		
		/* bounds AND _oldBounds need to be set, 
		 * otherwise bounds are reset to _oldBounds 
		 * if the node is moved
		 */
		shape._oldBounds.set(
			shape.bounds.a.x, 
			shape.bounds.a.y, 
			shape.bounds.b.x, 
			shape.bounds.a.y + titleHeight + propHeight);
		shape.bounds.set(
			shape.bounds.a.x, 
			shape.bounds.a.y, 
			shape.bounds.b.x, 
			shape.bounds.a.y + titleHeight + propHeight);
		
		//resize the image frames on the left according to the shape's bounds
		image_frames.each(function(frame){
			frame.height = shape.bounds.height();
			frame.element.setAttribute("height",shape.bounds.height());
			
		});
	},
	
	/**
	 * The import method that holds the major importing functionality
	 */
	importPicture: function(){
		this._doImport().bind(this);
	},
 	
 	onSelectionChange: function(){},
 	
 	/**
 	 * The instantiation method that does all necessary jobs before the user can start modeling
 	 */
 	pictureInstantiation: function(event){
 		//create a process lane if the canvas is empty
		if(this.facade.getCanvas().children.length === 0){				
			this.facade.createShape({
				type: "http://b3mn.org/stencilset/picture#process",
				namespace: "http://b3mn.org/stencilset/picture#",
				position: {x: 0, y: 0}
			}).refresh();
		}
	},
 	
 	//--------------------------------- (to-be-refactored AJAX Land) ---------------------------------
	 
	_doImport: function( successCallback )
	{
		// Define the form panel
	    var form = new Ext.form.FormPanel({
			baseCls: 		'x-plain',
	        labelWidth: 	50,
	        defaultType: 	'textfield',
	        items: 
	        [
	         {
	            text : 		ORYX.I18N.PictureSupport.importTask, 
				style : 	'font-size:12px;margin-bottom:10px;display:block;',
	            anchor:		'100%',
				xtype : 	'label' 
	         },
	         {
	            fieldLabel: ORYX.I18N.PictureSupport.file,
	            name: 		'subject',
				inputType : 'file',
				style : 	'margin-bottom:10px;display:block;',
				itemCls :	'ext_specific_window_overflow'
	         }, 
	         {
	            xtype: 'textarea',
	            hideLabel: true,
	            name: 'msg',
	            anchor: '100% -63'  
	         }
	        ]
	    });

		// Create the panel
		var dialog = new Ext.Window({ 
			autoCreate: true, 
			layout: 	'fit',
			plain:		true,
			bodyStyle: 	'padding:5px;',
			title: 		ORYX.I18N.PictureSupport.picture, 
			height: 	350, 
			width:		500,
			modal:		true,
			fixedcenter:true, 
			shadow:		true, 
			proxyDrag: 	true,
			resizable:	true,
			items: 		[form],
			buttons:[
				{
					text: ORYX.I18N.PictureSupport.importLable,
					handler:function(){
						
						var loadMask = new Ext.LoadMask(Ext.getBody(), {msg:ORYX.I18N.jPDLSupport.impProgress});
						loadMask.show();
						
						window.setTimeout(function()
						{					
							// Get the text which is in the text field
							var pictureToImport =  form.items.items[2].getValue();							
							this._getAllPages(pictureToImport, loadMask);

						}.bind(this), 100);

						dialog.hide();
						
					}.bind(this)
					
				},
				{
					text: ORYX.I18N.PictureSupport.close,
					handler:function()
					{						
						dialog.hide();					
					}.bind(this)
				}
			]
		});
		
		// Destroy the panel when hiding
		dialog.on('hide', function()
		{
			dialog.destroy(true);
			delete dialog;
		});

		// Show the panel
		dialog.show();
		
				
		// Adds the change event handler to 
		form.items.items[1].getEl().dom.addEventListener('change',function(evt){ var text = evt.target.files[0].getAsText('UTF-8'); form.items.items[2].setValue(text); }, true);
	},
	
	_getAllPages: function(pictureXML, loadMask)
	{		
		var parser = new DOMParser();
		ORYX.Log.info("pictureXML: ",pcitureXML);
		var xmlDoc = parser.parseFromString(pictureXML,"application/xml");
		var allPages = xmlDoc.getElementsByTagName("process");
		ORYX.Log.info("allPages: ",allPages);
		
		// If there are no pages then it is probably that pictureXML is not a picture-File
		if (allPages.length === 0)
		{
			loadMask.hide();
			this._showErrorMessageBox(ORYX.I18N.PictureSupport.title, ORYX.I18N.PictureSupport.wrongPictureFile);
			return;
		}
		
		if (allPages.length === 1)
		{
			pageAttr = allPages[0].children[0];
			pageName = pageAttr.attributes[0].nodeValue;
			
			this._sendRequest(
					ORYX.CONFIG.PICTUREIMPORTER,
					'POST',
					{ 
						'pagesToImport': pageName,
						'data' : pictureXML 
					},
					function( arg )
					{
						if (arg.startsWith("error:"))
						{
							this._showErrorMessageBox(ORYX.I18N.Oryx.title, arg);
							loadMask.hide();
						}
						else
						{
							this.facade.importJSON(arg); 
							loadMask.hide();							
						}
					}.bind(this),
					function()
					{
						loadMask.hide();
						this._showErrorMessageBox(ORYX.I18N.Oryx.title, ORYX.I18N.PictureSupport.serverConnectionFailed);
					}.bind(this)
				);
			
			return;
		}
		
		var i, pageName, data = [];
		for (i = 0; i < allPages.length; i++)
		{
			pageAttr = allPages[i].children[0];
			pageName = pageAttr.attributes[0].nodeValue;
			data.push([pageName]);
		}
		
		loadMask.hide();
		this.showPageDialog(data, pictureXML);		
	},
	
	_showErrorMessageBox: function(title, msg)
	{
        Ext.MessageBox.show({
           title: title,
           msg: msg,
           buttons: Ext.MessageBox.OK,
           icon: Ext.MessageBox.ERROR
       });
	},
	
	
	showPageDialog: function(data, pictureXML)
	{
		var reader = new Ext.data.ArrayReader(
				{}, 
				[ {name: 'name'} ]);
		
		var sm = new Ext.grid.CheckboxSelectionModel(
			{
				singleSelect: true
			});
		
	    var grid2 = new Ext.grid.GridPanel({
    		store: new Ext.data.Store({
	            reader: reader,
	            data: data
	        }),
	        cm: new Ext.grid.ColumnModel([
	            {
	            	id:'name',
	            	width:200,
	            	sortable: true, 
	            	dataIndex: 'name'
	            },
				sm
			]),
			sm: sm,
			frame:true,
			hideHeaders:true,
			iconCls:'icon-grid',
			listeners : {
				render: function() {
					var recs=[];
					this.grid.getStore().each(function(rec)
					{
						if(rec.data.engaged){
							recs.push(rec);
						}
					}.bind(this));
					this.suspendEvents();
					this.selectRecords(recs);
					this.resumeEvents();
				}.bind(sm)
			}
	    });
	    
	    // Create a new Panel
        var panel = new Ext.Panel({
            items: [{
                xtype: 'label',
                text: 'Picture Page',
                style: 'margin:10px;display:block'
            }, grid2],
            frame: true
        });
        
        // Create a new Window
        var window = new Ext.Window({
            id: 'oryx_new_page_selection',
            autoWidth: true,
            title: ORYX.I18N.PictureSupport.title,
            floating: true,
            shim: true,
            modal: true,
            resizable: true,
            autoHeight: true,
            items: [panel],
            buttons: [{
                text: ORYX.I18N.PictureSupport.importLable,
                handler: function()
                {
            		var chosenRecs = "";

            		// Actually it doesn't matter because it's one
            		sm.getSelections().each(function(rec)
            		{
						chosenRecs = rec.data.name;						
					}.bind(this));
            		
            		if (chosenRecs.length === 0)
            		{
            			alert(ORYX.I18N.PictureSupport.noPageSelection);
            			return;
            		}
            		
            		var loadMask = new Ext.LoadMask(Ext.getBody(), {msg:ORYX.I18N.PictureSupport.importProgress});
					loadMask.show();
					
            		window.hide();
            		
        			pageName = chosenRecs;
        			this._sendRequest(
        					ORYX.CONFIG.PICTUREIMPORTER,
        					'POST',
        					{ 
        						'pagesToImport': pageName,
        						'data' : pictureXML 
        					},
        					function( arg )
        					{
								if (arg.startsWith("error:"))
								{
									this._showErrorMessageBox(ORYX.I18N.Oryx.title, arg);
									loadMask.hide();
								}
								else
								{
									this.facade.importJSON(arg); 
									loadMask.hide();							
								}
							}.bind(this),
        					function()
        					{
								loadMask.hide();
								this._showErrorMessageBox(ORYX.I18N.Oryx.title, ORYX.I18N.PictureSupport.serverConnectionFailed);
							}.bind(this)
        				);
                }.bind(this)
            }, 
            {
                text: ORYX.I18N.PictureSupport.close,
                handler: function(){
                    window.hide();
                }.bind(this)
            }]
        });
        
        // Show the window
        window.show();
	}		
 	
 });
