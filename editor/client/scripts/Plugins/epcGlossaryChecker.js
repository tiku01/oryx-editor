/**
 * Copyright (c) 2009
 * Nicolas Peters
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

if(!ORYX.Plugins)
	ORYX.Plugins = new Object();

/**
 * Supports EPCs by offering a syntax check and export and import ability..
 * 
 * 
 */
ORYX.Plugins.EPCGlossaryChecker = Clazz.extend({

	facade: undefined,

	/**
	 * Offers the plugin functionality:
	 * 
	 */
	construct: function(facade) {
		this.facade = facade;
		
		this.raisedEventIds = [];
		
		this.facade.offer({
			'name':"Checks EPC against glossary",
			'functionality': this.checkEPC.bind(this),
			'group': "Glossary",
			'icon': ORYX.PATH + "images/book_open.png",
			'description': '',
			'index': 1,
			'toggle': true,
			'minShape': 0,
			'maxShape': 0});
	
	},		

	
	/**
	 * Exports the diagram into an AML or EPML file
	 */
	checkEPC: function(button, pressed){

		if(!pressed) {
			
			this.raisedEventIds.each(function(id){
				this.facade.raiseEvent({
						type: 	ORYX.CONFIG.EVENT_OVERLAY_HIDE,
						id: 	id
					});
			}.bind(this))

			this.raisedEventIds = [];
			
		} else {
			this.facade.raiseEvent({type:ORYX.CONFIG.EVENT_LOADING_ENABLE, text:'Checking EPC Model against Glossary...'});
			var xmlSerializer = new XMLSerializer();

			
			// TODO: a Syntax Syntax-Check should be triggered, here.
			 
			// TODO: get process' name
			var resource = "Oryx-EPC";
			
			// Force to set all resource IDs
			var serializedDOM = DataManager.serializeDOM( this.facade );

			//add namespaces
			serializedDOM = '<?xml version="1.0" encoding="utf-8"?>' +
			'<html xmlns="http://www.w3.org/1999/xhtml" ' +
			'xmlns:b3mn="http://b3mn.org/2007/b3mn" ' +
			'xmlns:ext="http://b3mn.org/2007/ext" ' +
			'xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" ' +
			'xmlns:atom="http://b3mn.org/2007/atom+xhtml">' +
			'<head profile="http://purl.org/NET/erdf/profile">' +
			'<link rel="schema.dc" href="http://purl.org/dc/elements/1.1/" />' +
			'<link rel="schema.dcTerms" href="http://purl.org/dc/terms/ " />' +
			'<link rel="schema.b3mn" href="http://b3mn.org" />' +
			'<link rel="schema.oryx" href="http://oryx-editor.org/" />' +
			'<link rel="schema.raziel" href="http://raziel.org/" />' +
			'<base href="' +
			location.href.split("?")[0] +
			'" />' +
			'</head><body>' +
			serializedDOM +
			'<div id="generatedProcessInfos"><span class="oryx-id">' + resource + '</span>' + 
			'<span class="oryx-name">' + resource + '</span></div>' +
			'</body></html>';
			
			/*
			 * Transform eRDF -> RDF
			 */
			var erdf2rdfXslt = ORYX.PATH + "/lib/extract-rdf.xsl";

			var rdfResultString;
			rdfResult = this.transformString(serializedDOM, erdf2rdfXslt, true);
			if (rdfResult instanceof String) {
				rdfResultString = rdfResult;
				rdfResult = null;
			} else {
				rdfResultString = xmlSerializer.serializeToString(rdfResult);
				if (!rdfResultString.startsWith("<?xml")) {
					rdfResultString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + rdfResultString;
				}
			}
			
//			/*
//			 * Transform RDF -> EPML
//			 */
//			var rdf2epmlXslt = ORYX.PATH + "/xslt/RDF2EPML.xslt";
//			
//			var epmlResult = this.transformDOM(rdfResult, rdf2epmlXslt, true);
//			var epmlResultString;
//			if (epmlResult instanceof String) {
//				epmlResultString = epmlResult;
//				epmlResult = null;
//			} else {
//				epmlResultString = xmlSerializer.serializeToString(epmlResult);
//				if (!epmlResultString.startsWith("<?xml")) {
//					epmlResultString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + epmlResultString;
//				}
//			}
//			
//			this.facade.raiseEvent({type:ORYX.CONFIG.EVENT_LOADING_DISABLE});
//			
//			// At the moment, only EPML is going to be returned.
//			this.openDownloadWindow(resource + ".epml", epmlResultString);
			
			new Ajax.Request("/ipml_glossary/epcChecker", {
	            method: 'POST',
	            asynchronous: false,
	            parameters: {"process":rdfResultString},
	            onSuccess: this.handleResult.bind(this),
	            
	            onFailure: function(transport){
	            
	            	this.facade.raiseEvent({type:ORYX.CONFIG.EVENT_LOADING_DISABLE});
	                Ext.Msg.alert("Oryx", "Failed");
	                
	            }.bind(this)
	        });
		}
    },
    
    handleResult: function(transport) {
    	this.facade.raiseEvent({type:ORYX.CONFIG.EVENT_LOADING_DISABLE});
    	
    	console.log(transport);
    	var result = transport.responseText.evalJSON();
    	
    	var notInGlossary = result.notInGlossary;
    	var invalidTypes = result.invalidTypes;
    	var invalidBPs = result.invalidBPs;
    	
    	if(notInGlossary.length === 0 &&
    		invalidTypes === 0 &&
    		invalidBPs === 0) {
    		this.facade.raiseEvent({type:ORYX.CONFIG.EVENT_LOADING_STATUS, text:'Model is conform to glossary.', timout:10000});
    		return true;
    	}
    	
    	if(!result.profileGenerable) {
    		Ext.Msg.alert("Oryx", "For this EPC model it is not possible to generate a behavioural profile.");
    	}
    	
    	this.handleInvalidTypes(invalidTypes);
    	this.handleNotInGlossary(notInGlossary);
    	this.handleInvalidBPs(invalidBPs);
    	
    },
    
    handleInvalidTypes: function(invalidTypes) {
    	var canvas = this.facade.getCanvas();
    	invalidTypes.each(function(invalidType) {
    		var node = canvas.getChildShapeByResourceId(invalidType.id);
    		invalidType.validTypes = invalidType.validTypes.collect(function(type) {
    			return type.capitalize();
    		});
    		if(node) {
    			this.raiseOverlay(node, "This label is only defined for elements of type " + invalidType.validTypes.join(", "));
    		}
    	}.bind(this));
    },
    
    handleNotInGlossary: function(notInGlossary) {
    	var canvas = this.facade.getCanvas();
    	notInGlossary.each(function(id) {
    		var node = canvas.getChildShapeByResourceId(id);
    		if(node) {
    			this.raiseOverlay(node, "This label is not defined in the glossary.");
    		}
    	}.bind(this));
    },
    
    handleInvalidBPs: function(invalidBPs) {
    	var canvas = this.facade.getCanvas();
    	invalidBPs.each(function(invalidBP) {
    		var node = canvas.getChildShapeByResourceId(invalidBP.id1);
    		if(node) {
    			var msg = "A " + invalidBP.definedRelation + ' relation is defined between this label and the label "' +
    			invalidBP.label2 + '", but a ' + invalidBP.foundRelation + " relation is found in your model.";
    			this.raiseOverlay(node, msg);
    		}
    	}.bind(this));
    },
	
	/**
	 * Transforms the given string via xslt.
	 * 
	 * @param {String} string
	 * @param {String} xsltPath
	 * @param {Boolean} getDOM
	 */
	transformString: function(string_, xsltPath, getDOM){
		var parser = new DOMParser();
		var parsedDOM = parser.parseFromString(string_,"text/xml");	
		
		return this.transformDOM(parsedDOM, xsltPath, getDOM);
	},
	
	/**
	 * Transforms the given dom via xslt.
	 * 
	 * @param {Object} domContent
	 * @param {String} xsltPath
	 * @param {Boolean} getDOM
	 */
	transformDOM: function(domContent, xsltPath, getDOM){	
		if (domContent == null) {
			return new String("Parse Error: \nThe given dom content is null.");
		}
		var result;
		var resultString;
		var xsltProcessor = new XSLTProcessor();
		var xslRef = document.implementation.createDocument("", "", null);
		xslRef.async = false;
		xslRef.load(xsltPath);
		
		xsltProcessor.importStylesheet(xslRef);
		try {
			result = xsltProcessor.transformToDocument(domContent);
		} catch (error){
			return new String("Parse Error: "+error.name + "\n" + error.message);
		}
		if (getDOM){
			return result;
		}
		resultString = (new XMLSerializer()).serializeToString(result);
		return resultString;
	},
	
	raiseOverlay: function(shape, errorMsg) {
		
		var id = "epcglossarychecker." + this.raisedEventIds.length;
		
		var cross = ORYX.Editor.graft("http://www.w3.org/2000/svg", null ,
			['path', {
				"title":errorMsg, "stroke-width": 5.0, "stroke":"#ad0f5b", "d":  "M20,-5 L5,-20 M5,-5 L20,-20", "line-captions": "round"
				}]);

		this.facade.raiseEvent({
			type: 			ORYX.CONFIG.EVENT_OVERLAY_SHOW,
			id: 			id,
			shapes: 		[shape],
			node:			cross,
			nodePosition:	shape instanceof ORYX.Core.Edge ? "START" : "NE"
		});		
		
		this.raisedEventIds.push(id);
		
		return cross;		
	}
});