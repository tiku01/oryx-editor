/**
 * Copyright (c) 2009
 * Armin Zamani
 **/

if (!ORYX) ORYX = new Object();
if (!ORYX.Plugins) ORYX.Plugins = new Object();

ORYX.Plugins.BPMN2YAWLMapper = ORYX.Plugins.AbstractPlugin.extend({
	facade: undefined,
	construct: function(facade){
		this.facade = facade
		
        this.active = false;
        this.raisedEventIds = [];
		
		this.facade.offer({
			'name': 'BPMN to YAWL mapper',
			'functionality': this.perform.bind(this),
			'group': 'Mapping',
			'icon': ORYX.PATH + 'images/door.png',
			'description': 'Map this diagram to YAWL and execute it',
			'index': 1,
			'toggle': true,
			'minShape': 0,
			'maxShape': 0
		});
		
        this.facade.registerOnEvent(ORYX.Plugins.BPMN2YAWLMapper.RESET_ERRORS_EVENT, this.resetErrors.bind(this));
        this.facade.registerOnEvent(ORYX.Plugins.BPMN2YAWLMapper.SHOW_ERRORS_EVENT, this.doShowErrors.bind(this));
	},
	perform: function(button, pressed){
		if (!pressed) {
            this.resetErrors();
        } else {
        	this.checkSyntaxAndMapBPMNtoYAWL({
                onMappingSucceeded: function(){
                    button.toggle();
                    this.active = !this.active;
                    Ext.Msg.alert("The BPMN to YAWL mapper succeeded and has created an YAWL file in your Eclipse directory");
                },
                onErrors: function(){
                },
                onFailure: function(){
                    button.toggle();
                    this.active = !this.active;
                    Ext.Msg.alert("The connection to the server failed");
                }
            });
        }
	},
	
	checkSyntaxAndMapBPMNtoYAWL: function(options){
		if(!options)
            options = {};
		
		new Ajax.Request(ORYX.CONFIG.BPMN2YAWL_URL, {
			method: 'POST',
			asynchronous: false,
			parameters : {
				data: this.getRDFFromDOM()
			},
			onSuccess: function(request){
				var resp = request.responseText.evalJSON();
				
				if (resp instanceof Object) {
					resp = $H(resp)
					if (resp.size() > 0) {
						this.showErrors(resp);
                 
                    if(options.onErrors) options.onErrors();
				}
				else {
					if(options.onMappingSucceeded) options.onMappingSucceeded();
                }
            }
            else {
            	if(options.onFailure) options.onFailure();
            }
            Ext.Msg.hide();
			}.bind(this),
			onFailure: function(){
				Ext.Msg.hide();
				if(options.onFailure) options.onFailure();
			}
		})
	},
	
	getSubprocessRDF: function(){
		
	},
	
	/** Called on SHOW_ERRORS_EVENT.
     * 
     * @param {Object} event
     * @param {Object} args
     */
    doShowErrors: function(event, args){
        this.showErrors(event.errors);
    },
    
    /**
     * Shows overlays for each given error
     * @methodOf ORYX.Plugins.BPMN2YAWLMapper.prototype ?
     * @param {Hash|Object} errors
     * @example
     * showErrors({
     *     myShape1: "This has an error!",
     *     myShape2: "Another error!"
     * })
     */
    showErrors: function(errors){
        // If normal object is given, convert to hash
        if(!(errors instanceof Hash)){
            errors = new Hash(errors);
        }
        
        // Get all Valid ResourceIDs and collect all shapes
        errors.keys().each(function(value){
            var sh = this.facade.getCanvas().getChildShapeByResourceId(value);
            if (sh) {
                this.raiseOverlay(sh, errors[value]);
            }
        }.bind(this));
        this.active = !this.active;
    },
    
    /**
     * Resets all (displayed) errors
     * @methodOf ORYX.Plugins.BPMN2YAWLMapper.prototype
     */
    resetErrors: function(){
        this.raisedEventIds.each(function(id){
            this.facade.raiseEvent({
                type: ORYX.CONFIG.EVENT_OVERLAY_HIDE,
                id: id
            });
        }.bind(this))
        
        this.raisedEventIds = [];
        this.active = !this.active;
    },
    
    raiseOverlay: function(shape, errorMsg){
        var id = "syntaxchecker." + this.raisedEventIds.length;
        
        var cross = ORYX.Editor.graft("http://www.w3.org/2000/svg", null, ['path', {
            "title": errorMsg,
            "stroke-width": 5.0,
            "stroke": "red",
            "d": "M20,-5 L5,-20 M5,-5 L20,-20",
            "line-captions": "round"
        }]);
        
        this.facade.raiseEvent({
            type: ORYX.CONFIG.EVENT_OVERLAY_SHOW,
            id: id,
            shapes: [shape],
            node: cross,
            nodePosition: shape instanceof ORYX.Core.Edge ? "START" : "NW"
        });
        
        this.raisedEventIds.push(id);
        
        return cross;
    }
});

//Define the events
ORYX.Plugins.BPMN2YAWLMapper.RESET_ERRORS_EVENT = "resetErrors";
ORYX.Plugins.BPMN2YAWLMapper.SHOW_ERRORS_EVENT = "showErrors";