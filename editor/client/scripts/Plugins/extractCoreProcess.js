/**
 * Copyright (c) 2009
 * Willi Tscheschner
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


new function(){
	
	
	var CONST = {};
	CONST.EQUIVALENCE = "equivalence";
	CONST.ARBITRARY_EQUIVALENCE = "arbitrary";
	CONST.LOWEST_COMMON = "lowest";
	CONST.LARGEST_COMMON = "largest";
	CONST.COMBINED = "combined";

	CONST.BPMN_1_1 = "http://b3mn.org/stencilset/bpmn1.1#";

	CONST.TASK = "Task";
	
	/**
	 * Implementation of extraction a core process
	 * @class
	 *
	 */
	ORYX.Plugins.ExtractCoreProcess = ORYX.Plugins.AbstractPlugin.extend({
	
	    facade: undefined,
	    
	    // Offers the plugin functionality
	    construct: function(facade){
			
			arguments.callee.$.construct.apply(this, arguments);
			
			this.facade.offer({
	            'name': "Extract Core Process",
	            'functionality': this.showPanel.bind(this, this.startExtraction.bind(this)),
	            'icon': ORYX.PATH + "images/epc_export.png",
	            'description': "Extraction of a core process from several processes",
	            'index': 1,
	            'minShape': 0,
	            'maxShape': 0
	        });
	        
	    },
		
		/**
		 * Prepare all the data to start with extraction
		 * @param {Object} url1
		 * @param {Object} url2
		 * @param {Object} algorithm
		 */
		startExtraction: function(url1, url2, algorithm){
	   		
			var model1, model2;
			var finish = function(model){
				if (!model1) {
					model1 = model;
					return;
				}
				if (!model2) {
					model2 = model;
					this.extractCoreProcess(model1, model2, algorithm);
				}
			}.bind(this);
			
			this.retrieveModel(url1, finish);
			this.retrieveModel(url2, finish);
		
	   	},
		
		_model1Cached : $H({}),
		_model2Cached : $H({}),
		
		cacheGraph: function(model, val){
			var parseChilds = function(n){
				if (n.resourceId){
					val[n.resourceId] = n
				}
				if (n.childShapes){
					n.childShapes.each(function(nn){
						parseChilds(nn)
					})
				}
			}
			parseChilds(model);
		},
		
		extractCoreProcess: function(model1, model2, algorithm){
			
			//this.cacheGraph(model1, this._model1Cached);
			//this.cacheGraph(model2, this._model2Cached);
			
			//var graph = [];
				
			/*switch(algorithm){
				case CONST.EQUIVALENCE:
					graph = this.findEquivalenceActivities(model1, model2);
					break;
					
				case CONST.ARBITRARY_EQUIVALENCE:
					graph = this.findArbitraryEquivalenceActivities(model1, model2);
					break;
				case CONST.COMBINED:
					this.extractProcessConfiguration(model1, model2);
					break;
			}
			
			if (graph){
				graph.each(function(task){
					this.addShape(task);
				}.bind(this));	
			}*/
			
			this.extractProcessConfiguration(model1, model2, algorithm);
			
			this.facade.setSelection([]);
			this.facade.getCanvas().update();
		},
		
		/**
		 * ALGORITHMS
		 */
		
		/**
		 * Returns a set on activities which are occured at least once
		 */
		findEquivalenceActivities : function(model1, model2){

			var tasks1 = this.getTasks(model1);
			var tasks2 = this.getTasks(model2);
			
			return tasks1.findAll(function(t){ return tasks2.any(function(tt){ return tt.properties.name == t.properties.name })})

		},


		/**
		 * Returns a set on activities which are occured at least once
		 */
		findArbitraryEquivalenceActivities : function(model1, model2){

			var tasks1 = this.getTasks(model1);
			var tasks2 = this.getTasks(model2);
			
			return tasks1.findAll(function(t){ return tasks2.any(function(tt){ return tt.properties.name == t.properties.name })})
			
		},		



		/**
		 * Returns a set on activities which are occured at least once
		 */
		extractProcessConfiguration : function(model1, model2, alg){
			
			
			new Ajax.Request(ORYX.CONFIG.ROOT_PATH + "extract", {
	            method		: 'POST',
				asynchronous: false,
				parameters	: {modelA:Object.toJSON(model1), modelB:Object.toJSON(model2), algorithm:alg},
	            onSuccess	: function(request){
					var json = request.responseText;
					this.facade.importJSON(json);
					//json = json.startsWith('<?xml') ? json : '<?xml version="1.0" encoding="utf-8"?>'+json+'';	
					//var parser	= new DOMParser();	
					//console.log(parser.parseFromString(json ,"text/xml"));		
					//this.facade.importERDF(parser.parseFromString(json ,"text/xml"));
			
				}.bind(this),
				on404	: function(request){
					var json = request.responseText.evalJSON;
					Ext.Msg.alert("Extraction Failed", json.error);
				}
			});
			
											
		},			
		
		
		
		/**
		 * Returns a true if this activity is optional or not
		 * @param {Object} task
		 * @param {Object} model
		 */
		isOptional: function(task, model){
			
			var getPreviouses = function(t){
				return model.values().findAll(function(p){
					return (p.outgoing||[]).any(function(pr){
						return pr.resourceId === t;
					})
				})
			}
			//
			var checkPrevious = function(el){
				var previous = getPreviouses(el);
				
				do{
					if (previous && previous.stencil.id === CONST.XOR){
						return true;
					}
				}while(previous);
				return false;
			}
			
			return checkPreviouses(task)
			
		},
		
		/**
		 * 
		 */
		addShape: function(shape){
			
			var option = {
				type		: CONST.BPMN_1_1 + shape.stencil.id,
				namespace 	: CONST.BPMN_1_1,
				position	: shape.bounds.upperLeft
			}
			
			var node = this.facade.createShape(option);	
			
			var props = [] 
			$H(shape.properties).each(function(prop){
				props.push({
					prefix	: 'oryx',
               		name	: prop.key,
                  	value	: prop.value
				})
			})
			
			node.deserialize(props);
			
		},
		
		/**
		 * Returns a list of nodes
		 * @param {Object} node
		 */
		getTasks: function(node){
			
			if (!node){ return []; }
			
			var tasks = [];
			
			if (node.stencil.id == CONST.TASK){
				tasks.push(node);
			}
			
			(node.childShapes||[]).each(function(n){
				tasks = tasks.concat(this.getTasks(n));
			}.bind(this));
			
			return tasks;			
		},
		
		/**
		 * Retrieve the model with the given url
		 * @param {Object} url
		 */
		retrieveModel: function(url, callback){
			
			var attr = "model" + parseInt(Math.random()*100000);
			
			var s = document.createElement("script");
			s.src = url + "?jsonp=window."+attr+"=";
			$(s).observe("load", function(){
				callback(window[attr]);
			});
			
			document.getElementsByTagName("head")[0].appendChild(s);
						
	       /* new Ajax.Request( url, {
	            method		: 'GET',
				asynchronous: false,
	            onSuccess	: function(request){
					callback(request.responseText)
				}
			});*/
		},
	 
	 
		/** ********************************************************
		 * 
		 * UI-WINDOW
		 * 
		 * ********************************************************
		 * 
		 * Shows the Popup Window where u can specify the url
		 * and some advanced parameter
		 * 
		 * @param {Object} callback
		 */
		showPanel: function( callback ){
			
			var form = new Ext.form.FormPanel({
						    labelWidth: 	80,
						    defaultType: 	'textfield',
						    bodyStyle:		'padding:15px',
							defaults:		{width: 250, msgTarget:'side',labelSeparator:'', style:'overflow:hidden;'},
						    items: [{
									text:	'This tool extract the core process of different processes. You can load the core process by defining the urls to the related processes.',
									xtype: 	'label',
									style:	'margin-bottom:10px;display:block;'
						        },{
									boxLabel: 'Common Mandatory Activity',
									name	: 'algorithm',
									id		: CONST.ARBITRARY_EQUIVALENCE,
									xtype	: 'radio'
						        },{
									boxLabel: 'Common Activity',
									name	: 'algorithm',
									id		: CONST.EQUIVALENCE,
									xtype	: 'radio',
									checked	: true
						        },{
									boxLabel: 'Lowest Common Sub Traces',
									name	: 'algorithm',
									id		: CONST.LOWEST_COMMON,
									xtype	: 'radio'
						        },{
									boxLabel: 'Largest Common Sub Traces',
									name	: 'algorithm',
									id		: CONST.LARGEST_COMMON,
									xtype	: 'radio'
						        },{
									boxLabel: 'Process Configuration',
									name	: 'algorithm',
									id		: CONST.COMBINED,
									xtype	: 'radio'
						        },{
									fieldLabel: "Process 1",
									//value	: "http://localhost:8080/oryx/model1.xml"
									value	: "http://oryx-editor.org/backend/poem/model/4759/json"
								},{
									fieldLabel: "Process 2",
									//value	: "http://localhost:8080/oryx/model2.xml"
									value	: "http://oryx-editor.org/backend/poem/model/4419/json"
								}]
						});
	
	
	
			
			var window = new Ext.Window({
						    title:			"Extract Core Process",
						    width:			400,
						    items: 			[form],
							modal:			true,
							resizable:		false,
							autoHeight:		true,			    
							buttons:[{
									text:	'Extract',
									handler: function(){
										
										var alg = $A(form.getForm().el.dom.algorithm).find(function(r){ return r.checked});
										var url1 = form.items.get(6);
										var url2 = form.items.get(7);
										
										if (!alg || !url1.getValue() || !url2.getValue()){
											return;
										}
										
										callback(url1.getValue(), url2.getValue(), alg.id);
									
										window.close();
									}.bind(this)
								},{
									text:	'Cancel',
									handler: function(){
										window.close();
									}
								}]  
						})
							
			window.show()		
		}
		
	});
}();
