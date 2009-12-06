if (!ORYX.Plugins) 
    ORYX.Plugins = new Object();

ORYX.Plugins.cpntokenpositioning = Clazz.extend
({
	// Defines the facade
    facade		: undefined,
    
	// Constructor 
    construct: function(facade)
    {
    
        this.facade = facade;     
		
		// Offers the functionality of tokenpositioning                
        this.facade.offer
        ({
			name			: "Token Positioning",
			description		: "Token Positioning",
			icon			: ORYX.PATH + "images/arrow_undo.png",
			functionality	: this.tokenpositioning.bind(this),
			group			: ORYX.I18N.Undo.group,
			isEnabled		: true,
			index			: 0
		}); 
		
        // Register in order to get events when a place is resize
		//this.facade.registerOnEvent(ORYX.CONFIG.EVENT_RESIZE_END, this.tokenpositioning.bind(this));
    },
    
    tokenpositioning: function(param)
    {
    	alert("hjallsod");    	
    }
})
    	
	