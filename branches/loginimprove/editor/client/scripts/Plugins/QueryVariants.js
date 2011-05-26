/**
 * Copyright (c) 2010 Ahmed Awad and Emilian Pascalau
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
if (!ORYX.Plugins) 
    ORYX.Plugins = new Object();

ORYX.Plugins.QueryVariant = ORYX.Plugins.AbstractPlugin.extend({

    facade: undefined,
    
    construct: function(facade){
		
        this.facade = facade;
        
		this.active 		= false;
		this.raisedEventIds = [];
		
        this.facade.offer({
            'name': ORYX.I18N.QueryVariant.name,
            'functionality': this.showOverlay.bind(this),
            'group': ORYX.I18N.QueryVariant.group,
            'icon': ORYX.PATH + "stencilsets/bpmnqvar/bpmnqvar.png",
            'description': ORYX.I18N.QueryVariant.tooltip,
            'index': 0,
			'toggle': false,
            'minShape': 0,
            'maxShape': 0
        });
		
    },
    
	showOverlay: function(button, pressed){

		if (!pressed) {
			
			this.raisedEventIds = [];
			this.active 		= !this.active;
			
			return;
		} 
		
		var options = {
			command : 'undef'
		}
	
		this.issueQuery(options);
	},
	
	issueQuery : function(options){
		
		try {
			var serialized_rdf = this.getRDFFromDOM();
//			serialized_rdf = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + serialized_rdf;

			this.facade.raiseEvent({
	            type: ORYX.CONFIG.EVENT_LOADING_ENABLE,
				text: "Processing partial process model"  //ORYX.I18N.Save.saving
	        });
			// Send the request to the server.
			new Ajax.Request(ORYX.CONFIG.QUERYVARIANTEVAL_URL, {
				method: 'POST',
				asynchronous: true,
				parameters: {
					resource	: location.href,
					command		: options.command,
					modelID		: options.modelID,
					stopAtFirstMatch: options.stopAtFirstMatch,
				//data		: serialized_rdf
				data : location.href
				},
                onSuccess: function(response){
                    this.facade.raiseEvent({
						type:ORYX.CONFIG.EVENT_LOADING_DISABLE
					});
					
					var respXML = response.responseXML;

                    var root=respXML.firstChild;
                    var pg=root.getElementsByTagName("ProcessGraph");
					var source = location.href.substring(0,location.href.substring(8).indexOf("/")+8);
					//alert(source);
					var uri = source+"/backend/poem/new?stencilset=/stencilsets/bpmn1.1/bpmn1.1.json";
					editor = window.open( uri );
					editor.oryxCreator1283772618640 = self;
					editor.oryxCreator1283772618640.variant=respXML;
					
					
                }.bind(this),
				
				onFailure: function(response){
					this.facade.raiseEvent({
						type:ORYX.CONFIG.EVENT_LOADING_DISABLE
					});
					Ext.Msg.alert(ORYX.I18N.Oryx.title, "Server encountered an error (" + response.statusText + ").\n"
						+ response.responseText);
				}.bind(this)
			});
			
		} catch (error){
			this.facade.raiseEvent({type:ORYX.CONFIG.EVENT_LOADING_DISABLE});
			Ext.Msg.alert(ORYX.I18N.Oryx.title, error);
	 	}

	},
	
    processResultGraph: function(xmlNode){
        var graphElements = new Array();
		
		for (var k = 0; k < xmlNode.childNodes.length; k++) {
            var node = xmlNode.childNodes.item(k);
            if (!(node instanceof Text)) {
                if (node.hasAttribute("id")) { // it is a node
                	Ext.Msg.alert(node.getAttributeNode("id").nodeValue);
					graphElements.push({
						nodeType : node.getAttributeNode("type").nodeValue,
						nodeId : node.getAttributeNode("id").nodeValue,
						nodeLabel : node.getAttributeNode("label").nodeValue,
						nodeType2 : node.getAttributeNode("type2").nodeValue
					});
					
				} else if ((node.hasAttribute("from"))
						&& node.hasAttribute("to")) { // it is an edge
					graphElements.push({
						edgeType : node.getAttributeNode("type").nodeValue,
						from : node.getAttributeNode("from").nodeValue.substring(3),
						to : node.getAttributeNode("to").nodeValue.substring(3)
					});
				}
            }
        }
		return graphElements;
    },
	// Added by Ahmed Awad on 28.07.09 to extract the diagnosis and the match meta data
	processMatchDescription: function(xmlNode){
        var metadata = new Array();
		
		for (var k = 0; k < xmlNode.childNodes.length; k++) {
            var node = xmlNode.childNodes.item(k);
            if ((node.nodeName === "diagnosis")) {
                
					metadata.push({
						diagnosis : node.textContent
					});
					
				} else if ((node.nodeName === "match")) { // it is an edge
					metadata.push({
						match : node.textContent
					});
				}
            
        }
		return metadata;
    },
	/**
	 * 
	 * @param {Array} processList; 
	 * 		elements' fields: id location identifier for process
	 * 						  elements array of graph nodes/edges
	 */
	processProcessList: function(processList){
		if(processList.length == 0) {
			Ext.Msg.alert(ORYX.I18N.Oryx.title, "Found no matching processes!");
			return;
		}
		
		this.isRendering = true;
		
		// load process model meta data
		processList.each(this.getModelMetaData.bind(this));
		
		// transform array of objects into array of arrays
		var data = [];
		processList.each(function( pair ){
/*			var stencilset = pair.value.type;
			// Try to display stencilset title instead of uri
			this.facade.modelCache.getModelTypes().each(function(type){
				if (stencilset == type.namespace) {
					stencilset = type.title;
					return;
				}
			}.bind(this));
*/			
			data.push( [ pair.id, pair.metadata.thumbnailUri + "?" + Math.random(), unescape(pair.metadata.title), pair.metadata.type, pair.metadata.author, pair.elements, pair.description ] )  // Modified by Ahmed
		}.bind(this));

		
		// following is mostly UI logic
		var myProcsPopup = new Ext.Window({
			layout      : 'fit',
            width       : 500,
            height      : 300,
            closable	: true,
            plain       : true,
			modal		: true,
			autoScroll  : true, // Added by Ahmed Awad on 30.07.2009
			title       : 'Query Result',
			id			: 'procResPopup',
			
			buttons: [{
                text     : 'Close',
                handler  : function(){
                    myProcsPopup.close();
                }.bind(this)
            }]

		});
		
		var tableModel = new Ext.data.SimpleStore({
			fields: [
				{name: 'id'}, //, type: 'string', mapping: 'metadata.id'},
				{name: 'icon'}, //, mapping: 'metadata.icon'},
				{name: 'title'}, //, mapping: 'metadata.title'},
				{name: 'type'}, //, mapping: 'metadata.type'},
				{name: 'author'}, //, mapping: 'metadata.author'},
				{name: 'elements'}, //, type: 'array', mapping: 'elements'},
				{name: 'description'}, // Added by Ahmed Awad
			],
			data : data
		});
		
		var iconPanel = new Ext.Panel({
			border	: false,
			autoScroll : true, // Added by Ahmed Awad
	        items	: new this.dataGridPanel({
				store       : tableModel, 
				listeners   :{
					dblclick:this._onDblClick.bind(this)
				}
			})
	    });
		this.setPanelStyle();
		
		myProcsPopup.add(iconPanel);
		
		this.isRendering = false;
		
		myProcsPopup.show();
	},
	
	getModelMetaData : function(processEntry) {
		var metaUri = processEntry.id.replace(/\/rdf$/, '/meta');
		new Ajax.Request(metaUri, 
			 {
				method			: "get",
				asynchronous 	: false,
				onSuccess		: function(transport) {
					processEntry.metadata = transport.responseText.evalJSON();
				}.bind(this),
				onFailure		: function() {
					Ext.MessageBox.alert(ORYX.I18N.Oryx.title, "Error loading model meta data.");
				}.bind(this)
			});
		
	},
	
	_onDblClick: function(dataGrid, index, node, e){
		
		// Select the new range
		dataGrid.selectRange(index, index);

		// Get uri and matched element data from the clicked model
		var modelId 	= dataGrid.getRecord( node ).data.id;
		var matchedElements = dataGrid.getRecord( node ).data.elements;
		var description = dataGrid.getRecord( node ).data.description; // Added by Ahmed Awad on 30.07.09
		// convert object to JSOn representation
		var elementsAsJson = Ext.encode(matchedElements);
		var descriptionAsJson = Ext.encode(description); // Added by Ahmed Awad on 30.07.09
		// escape JSON string to become URI-compliant
		var encodedJson = encodeURIComponent(elementsAsJson);
		var encodedDescription = encodeURIComponent(descriptionAsJson);
		
		// remove the last URI segment, append editor's 'self' and json of model elements
		var slashPos = modelId.lastIndexOf("/");
		//var uri	= modelId.substr(0, slashPos) + "/self" + "?matches=" + encodedJson;
 	    var uri	= modelId.substr(0, slashPos) + "/self" + "?matches=" + encodedJson+"&description="+encodedDescription;
		// Open the model in Editor
		var editor = window.open( uri );
		window.setTimeout(
	        function() {
                if(!editor || !editor.opener || editor.closed) {
                        Ext.MessageBox.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Oryx.editorOpenTimeout).setIcon(Ext.MessageBox.QUESTION);
                }
	        }, 5000);			
		
	},
	
	dataGridPanel : Ext.extend(Ext.DataView, {
		multiSelect		: true,
		//simpleSelect	: true, 
	    cls				: 'iconview',
	    itemSelector	: 'dd',
	    overClass		: 'over',
		selectedClass	: 'selected',
	    tpl : new Ext.XTemplate(
        '<div>',
			'<dl class="repository_iconview">',
	            '<tpl for=".">',
					'<dd >',
					'<div class="image">',
					 '<img src="{icon}" title="{title}" /></div>',
		            '<div><span class="title" title="{[ values.title.length + (values.type.length*0.8) > 30 ? values.title : "" ]}" >{[ values.title.truncate(30 - (values.type.length*0.8)) ]}</span><span class="author" unselectable="on">({type})</span></div>',
		            '<div><span class="type">{author}</span></div>',
					'</dd>',
	            '</tpl>',
			'</dl>',
        '</div>'
	    )
	}), 
	
	setPanelStyle : function() {
		var styleRules = '\
.repository_iconview dd{\
	width		: 200px;\
	height		: 105px;\
	padding		: 10px;\
	border		: 1px solid #EEEEEE;\
	font-family	: tahoma,arial,sans-serif;\
	font-size	: 9px;\
	display		: block;\
	margin		: 5px;\
	text-align	: left;\
	float		: left;\
}\
.repository_iconview dl {\
	width		: 100%;\
	max-width	: 1000px;\
}\
.repository_iconview dd.over{\
	background-color	: #fff5e1;\
}\
.repository_iconview dd.selected{\
	border-color: #FC8B03;\
}\
.repository_iconview dd img{\
	max-width	: 190px;\
	max-height	: 70px;\
}\
.repository_iconview dd .image{\
	width	: 200px;\
	height	: 80px;\
	padding-bottom	: 10px;\
	text-align		: center;\
	vertical-align	: middle;\
	display	:table-cell;\
}\
.repository_iconview dd .title{\
	font-weight	: bold;\
	font-size	: 11px;\
	color		: #555555;\
}\
.repository_iconview dd .author{\
	margin-left	: 5px;\
}';
		Ext.util.CSS.createStyleSheet(styleRules, 'queryResultStyle');
	},
    
});
