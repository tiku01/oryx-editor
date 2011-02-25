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
        
        // build a button in the tool bar
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
	
	calculateLabelHeight: function (labelElement, labelValue) {
		// if the label is empty, its height shall be 0
		if(labelValue === ""){return 0;}
		
		// the label is not empty, so at least we start with line count 1
		var fontSize = labelElement.getFontSize();
		var lineCount = new Integer(1);
		
		// for every line the count goes up
		labelValue.scan('\n', function() { lineCount += 1; });
		
		// the height accords to lines and the font size
		return lineCount * fontSize + 7;
	},
	
	dokuWikiStyle: function(string){
		/*
		 * need to find:
		 * 	1) *word* and *word word word* and so on to be printed BOLD
		 * 	2) _word_ and _word word word_ and so on to be printed italic
		 * 
		 * reference for finding RegExp: 
		 * http://de.selfhtml.org/javascript/objekte/regexp.htm
		 * 
		 * the string should not contain more than 3 words by now
		 * and should not contain anything else than ONE space between words
		 * TODO more generic RegExp
		 */
		var boldRegEx = /\*(\S+ ?(\S+)? ?(S+)?)\*/gi;
		var italicRegEx = /_(\S+ ?(\S+)? ?(S+)?)_/gi;
		var boldText = new Array();
		var italicText = new Array();
		var bolds = 0;
		var italics = 0;
		var bold;
		var italic;

		while(bold = boldRegEx.exec(string)){			
			boldText[bolds] = bold[1];
			bolds++;
		}
		
		while(italic = italicRegEx.exec(string)){
			italicText[italics] = italic[1];
			italics++;
		}
		
		//TODO manipulate HTML to show correct style
	},
	
	findLabelValue: function(shape,string){
		/* all properties of the given shape 
		 * that start with the given string get scanned
		 * and the value gets enriched by the content found
		 */
		var value = "";
		var properties = shape.properties.keys().findAll(function(element){return element.substr(5,string.length) === string;});
		properties.each(function(element){value += shape.properties[element];});
		return value;
	},
	
	handleProperties: function(event){		

		var shape = event.shape;
		var properties = shape._svgShapes.find(function(element) { return element.element.id === (shape.id + "properties_frame"); });
		var image_frames = shape._svgShapes.findAll(function(element) { return element.element.id.substr(element.element.id.length-5,5) === "image"; });
		var propHeight = 0;
		
		// before showing the properties the correct height of the node needs to be calculated
		if(shape.properties["oryx-basic-show_properties"]===true){
			// get all chapters and their content
			var description = shape.getLabels().find(function(label) { return label.id === (shape.id + "description"); });
			var descriptionValue = this.findLabelValue(shape,"description");
			var realisation = shape.getLabels().find(function(label) { return label.id === (shape.id + "realisation"); });
			var realisationValue = this.findLabelValue(shape,"realisation");
			var incoming = shape.getLabels().find(function(label) { return label.id === (shape.id + "incoming"); });
			var incomingValue = this.findLabelValue(shape,"incoming");
			var outgoing = shape.getLabels().find(function(label) { return label.id === (shape.id + "outgoing"); });
			var outgoingValue = this.findLabelValue(shape,"outgoing");
			var communication = shape.getLabels().find(function(label) { return label.id === (shape.id + "communication"); });
			var communicationValue = this.findLabelValue(shape,"communication");
			var payment = shape.getLabels().find(function(label) { return label.id === (shape.id + "payment"); });
			var paymentValue = this.findLabelValue(shape,"payment");
			var resource = shape.getLabels().find(function(label) { return label.id === (shape.id + "resource"); });
			var resourceValue = this.findLabelValue(shape,"resource");
			var comment = shape.getLabels().find(function(label) { return label.id === (shape.id + "comment"); });
			var commentValue = this.findLabelValue(shape,"comment");
			
			// style the text of the chapters
			var chapterValues = new Array(descriptionValue,realisationValue,incomingValue,outgoingValue,communicationValue,paymentValue,resourceValue,commentValue);
			for(var i = 0; i < chapterValues.length; i++){
				this.dokuWikiStyle(chapterValues[i]);
			}
			
			// calculate heights of all chapters
			var descriptionHeight = this.calculateLabelHeight(description,descriptionValue);
			var realisationHeight = this.calculateLabelHeight(realisation,realisationValue);
			var incomingHeight = this.calculateLabelHeight(incoming,incomingValue);
			var outgoingHeight = this.calculateLabelHeight(outgoing,outgoingValue);
			var communicationHeight = this.calculateLabelHeight(communication,communicationValue);
			var paymentHeight = this.calculateLabelHeight(payment,paymentValue);
			var resourceHeight = this.calculateLabelHeight(resource,resourceValue);
			var commentHeight = this.calculateLabelHeight(comment,commentValue);
			
			// set the order of the chapters
			var distanceTilRealisation = 60 + descriptionHeight;
			var distanceTilIncoming = distanceTilRealisation + realisationHeight;
			var distanceTilOutgoing = distanceTilIncoming + incomingHeight;
			var distanceTilCommunication = distanceTilOutgoing + outgoingHeight;
			var distanceTilPayment = distanceTilCommunication + communicationHeight;
			var distanceTilResource = distanceTilPayment + paymentHeight;
			var distanceTilComment = distanceTilResource + resourceHeight;
			var distanceTilBottom = distanceTilComment + commentHeight - 60;
			
			// set the chapters' and the properties' heights according to their content
			realisation.y = distanceTilRealisation;
			realisation.node.setAttribute("y", distanceTilRealisation);
			incoming.y = distanceTilIncoming;
			incoming.node.setAttribute("y", distanceTilIncoming);
			outgoing.y = distanceTilOutgoing;
			outgoing.node.setAttribute("y", distanceTilOutgoing);
			communication.y = distanceTilCommunication;
			communication.node.setAttribute("y", distanceTilCommunication);
			payment.y = distanceTilPayment;
			payment.node.setAttribute("y", distanceTilPayment);
			resource.y = distanceTilResource;
			resource.node.setAttribute("y", distanceTilResource);
			comment.y = distanceTilComment;
			comment.node.setAttribute("y", distanceTilComment);
			
			properties.height = distanceTilBottom;
			properties.element.setAttribute("height", distanceTilBottom);
			
			propHeight = properties.height;
		}
		
		/* bounds AND _oldBounds need to be set, 
		 * otherwise bounds are reset to _oldBounds 
		 * if the node is moved
		 */
		shape._oldBounds.set(
			shape.bounds.a.x, 
			shape.bounds.a.y, 
			shape.bounds.b.x, 
			shape.bounds.a.y + 60 + propHeight);
		shape.bounds.set(
			shape.bounds.a.x, 
			shape.bounds.a.y, 
			shape.bounds.b.x, 
			shape.bounds.a.y + 60 + propHeight);
		
		//resize the image frames on the left according to the shape's bounds
		image_frames.each(function(frame){
			frame.height = shape.bounds.height();
			frame.element.setAttribute("height",shape.bounds.height());
			
		});
	},
	
	importPicture: function(){
		this._doImport();
	},
 	
 	onSelectionChange: function(){},
 	
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
 	
 	//--------------------------------- AJAX Land ---------------------------------
	 
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
		var xmlDoc = parser.parseFromString(pictureXML,"application/xml");
		var allPages = xmlDoc.getElementsByTagName("process");
		
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
