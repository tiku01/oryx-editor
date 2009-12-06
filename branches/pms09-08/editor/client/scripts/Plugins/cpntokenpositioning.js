if (!ORYX.Plugins) 
    ORYX.Plugins = new Object();

ORYX.Plugins.cpntokenpositioning = Clazz.extend
(
	{	
		// Defines the facade
	    facade		: undefined,
	    		
		// Constructor 
	    construct: function(facade)
	    {
	    
	        this.facade = facade;     
			
			// Offers the functionality of positioning
	        this.facade.offer({
				name			: ORYX.I18N.Undo.undo,
				description		: ORYX.I18N.Undo.undoDesc,
				icon			: ORYX.PATH + "images/arrow_undo.png",
				keyCodes: [{
						metaKeys: [ORYX.CONFIG.META_KEY_META_CTRL],
						keyCode: 90,
						keyAction: ORYX.CONFIG.KEY_ACTION_DOWN
					}
			 	],
				functionality	: this.doUndo.bind(this),
				group			: ORYX.I18N.Undo.group,
				isEnabled		: function(){ return this.undoStack.length > 0 }.bind(this),
				index			: 0
			}); 
	
			// Offers the functionality of redo
	        this.facade.offer({
				name			: ORYX.I18N.Undo.redo,
				description		: ORYX.I18N.Undo.redoDesc,
				icon			: ORYX.PATH + "images/arrow_redo.png",
				keyCodes: [{
						metaKeys: [ORYX.CONFIG.META_KEY_META_CTRL],
						keyCode: 89,
						keyAction: ORYX.CONFIG.KEY_ACTION_DOWN
					}
			 	],
				functionality	: this.doRedo.bind(this),
				group			: ORYX.I18N.Undo.group,
				isEnabled		: function(){ return this.redoStack.length > 0 }.bind(this),
				index			: 1
			}); 
			
			// Register on event for executing commands --> store all commands in a stack		 
			this.facade.registerOnEvent(ORYX.CONFIG.EVENT_EXECUTE_COMMANDS, this.handleExecuteCommands.bind(this) );
    	
	