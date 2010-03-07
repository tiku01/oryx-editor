/**
 * Copyright (c) 2009
 * Armin Zamani
 **/

if (!ORYX.Plugins) 
    ORYX.Plugins = new Object();

ORYX.Plugins.BPMN2YAWLMapper = ORYX.Plugins.AbstractPlugin.extend({
	/**@private*/
	construct: function(){
		arguments.callee.$.construct.apply(this, arguments);
		
        this.active = false;
        this.raisedEventIds = [];
		
		this.facade.offer({
			'name': 'BPMN to YAWL mapper',
			'functionality': this.perform.bind(this),
			'group': 'Mapping',
			'icon': ORYX.PATH + 'images/door.png',
			'description': 'Map this diagram to YAWL and execute it',
			'index': 1,
			'minShape': 0,
			'maxShape': 0
		});
		
        this.facade.registerOnEvent(ORYX.Plugins.BPMN2YAWLMapper.RESET_ERRORS_EVENT, this.resetErrors.bind(this));
        this.facade.registerOnEvent(ORYX.Plugins.BPMN2YAWLMapper.SHOW_ERRORS_EVENT, this.doShowErrors.bind(this));
	},
	perform: function(button, pressed){
		this.resetErrors();
		this.checkSyntaxAndMapBPMNtoYAWL({
			onMappingSucceeded: function(){
			this.setActivated(false);

			Ext.Msg.alert("The BPMN to YAWL mapper succeeded and has created an YAWL file in your Eclipse directory");
		},
		onErrors: function(){
		},
		onFailure: function(){
			this.setActivated(false);
			Ext.Msg.alert("The connection to the server failed");
		}
		});
	},
	
	/**
     * Sets the activated state of the plugin
     * @param {Object} activated
     */
    setActivated: function(activated){
        if(activated === undefined){
            this.active = !this.active;
        } else {
            this.active = activated;
        }
    },
	
	checkSyntaxAndMapBPMNtoYAWL: function(options){
		Ext.applyIf(options || {}, {
	          showErrors: true,
	          ononMappingSucceeded: Ext.emptyFn,
	          onErrors: Ext.emptyFn,
	          onFailure: Ext.emptyFn
	        });
		
		var data = this.getRDFFromDOM();
		
		new Ajax.Request(ORYX.CONFIG.BPMN2YAWL_URL, {
			method: 'POST',
			asynchronous: false,
			parameters : {
				data: data
			},
			onSuccess: function(request){
				var resp = request.responseText.evalJSON();
				
				Ext.Msg.hide();
				
				if (resp instanceof Object) {
					resp = $H(resp)
					if (resp.size() > 0) {
						if(options.showErrors) this.showErrors(resp);
						options.onErrors();
					}
					else {
						Ext.Msg.alert("Mapping succeeded");
						options.onMappingSucceeded();
					}
				}
				else {
					options.onFailure();
				}
			}.bind(this),
			onFailure: function(){
				Ext.Msg.hide();
				options.onFailure();
			}
		});
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
     * @methodOf ORYX.Plugins.BPMN2YAWLMapper.prototype
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
        //this.active = !this.active;
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
        this.active = false;
    },
    
    raiseOverlay: function(shape, errorMsg){
        var id = "syntaxchecker." + this.raisedEventIds.length;
        var crossId = ORYX.Editor.provideId();
        var cross = ORYX.Editor.graft("http://www.w3.org/2000/svg", null, ['path', {
        	"id":crossId,
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