# Introduction #

Oryx can easily be extended by implementing a plugin. Most of the features are implemented as plugins. Plugins can be implemented on client side and on server side. Both types will be explained here.

# Create a Server Side Plugin #

Server side plugins are simply Java Servlets that extends the class _javax.servlet.http.HttpServlet. If you want to know more about Java Servlets go [here](http://java.sun.com/products/servlet/). You can add your servlet to the folder `trunk/editor/server/src/org/oryxeditor/server`. The_web.xml_file where you have to add information about your servlet can be found in `trunk/editor/etc`. You should also define the URL of your servlet in the editor's config.js file._


# Create a Client Side Plugin #

  * facade
  * events (com between plugins)
  * UI plugin vs. functionality plugin
  * aufbau

With client side plugins you can extend the functionality of the editor. For example, you can implement an import plugin for a specific data format. This feature will be offered in the toolbar to the user. Plugins can access the editor's core via the facade (see below) that defines an interface. The communication between different plugins is realized via an event mechanism.

The following steps must be performed when implementing a plugin:
  * Create a Javascript file with the basic structure of a plugin.
  * Define events in config.js.
  * Define all strings in the translation files of all available languages.
  * Add metadata about the plugin to the plugins configuration file.

## Create a Plugin File ##

Let us first create the plugin file to get to know the general structure of a plugin. I will use the undo/redo plugin as an example. All plugins can be found in `trunk/editor/client/scripts/Plugins`. You have to add your own plugin to this directory. The undo/redo plugin file is called `undo.js`. Let us have a look at the file:
```
if (!ORYX.Plugins) 
    ORYX.Plugins = new Object();

ORYX.Plugins.Undo = Clazz.extend({
	
  // Defines the facade
  facade		: undefined,
    
  // Defines the undo/redo Stack
  undoStack	: [],
  redoStack	: [],
	
  // Constructor 
  construct: function(facade){
    
    this.facade = facade;     
		
    // Offers the functionality of undo                
    this.facade.offer({
      name		: ORYX.I18N.Undo.undo,
      description	: ORYX.I18N.Undo.undoDesc,
      icon		: ORYX.PATH + "images/arrow_undo.png",
      functionality	: this.doUndo.bind(this),
      group		: ORYX.I18N.Undo.group,
      isEnabled		: function(){ return this.undoStack.length > 0 }.bind(this),
			   index			: 0
		          }); 

    // Offers the functionality of redo
    this.facade.offer({
      name		: ORYX.I18N.Undo.redo,
      description	: ORYX.I18N.Undo.redoDesc,
      icon		: ORYX.PATH + "images/arrow_redo.png",
      functionality	: this.doRedo.bind(this),
      group		: ORYX.I18N.Undo.group,
      isEnabled		: function(){ return this.redoStack.length > 0 }.bind(this),
			    index			: 1
		          }); 
		
    // Register on event for executing commands-->store all commands in a stack		 
    this.facade.registerOnEvent(ORYX.CONFIG.EVENT_EXECUTE_COMMANDS,
      this.handleExecuteCommands.bind(this) );
    	
  },
	
  handleExecuteCommands: function( evt ){
		
    //...
		
  },
	
  doUndo: function(){
	
    //...
	
  },
	
  doRedo: function(){
		
    //...
  }
	
});
```

The Undo class is part of the namespace _ORYX.Plugins_. It extends the _Clazz_ object that is used throughout the whole project as the base class. For example, it adds a constructor (function construct) that is automatically called when a new Object of that type is created. The constructor has a single parameter and that is a reference to the editor's facade.

Let us now have a look at the constructor. This plugin has two features: undo and redo. Both features must be registered with the facade's _offer_ method. Other plugins like the toolbar will get a notification when new features are registered and make it available to the user. If you want to create your own UI component, you have to implement an Ext component and add it to the UI with the facade's _addToRegion_ method. To learn more about Ext development, please go [here](http://extjs.com/deploy/dev/docs/). Note that the current Ext version we use is 2.0.2.

The undo/redo plugin also registers a callback on an event. Whenever the event _ORYX.CONFIG.EVENT\_EXECUTE\_COMMANDS_ is fired, the callback will be executed. The callback's parameters are event specific. If you cannot find information about the parameters in the config.js, search in other plugins for the event to find out the parameters on your own.

The rest of the methods in the plugin are the callbacks of the registered features and for the event. You can add any number of methods to the plugin object and perform any operation you like.

The server communication must implemented with Ajax requests. You can use the Ajax object of the Prototype library or the Ajax object of Ext.

## Define Events ##

If you want to define your own event, you can do so by adding a constant for the event to the config.js. Please provide some information about the event and its parameters.

## Define Strings in Translation Files ##

Please go [here](I18N.md) to learn more about internationalization in Oryx.

## Configure a Plugin ##

To make a plugin available in the editor you have to register it in the plugins configuration file (`trunk/editor/client/scripts/Plugins/plugins.xml`). For our example, you have to add the following line to the file:
```
<plugin source="undo.js" name="ORYX.Plugins.Undo" />
```
The attribute _source_ is the name of the Javascript file, the attribute _name_ specifies the name of the plugin's class.

Three configuration options are available. First you can define properties for a plugin that are passed to the plugins constructor. For example, the shapemenu plugin has the following properties:
```
<plugin source="shapemenu.js" name="ORYX.Plugins.ShapeMenuPlugin">			
  <property group="Z-Order" align="Oryx_Top" />
  <property group="Alignment" align="Oryx_Top" />
  <property group="Grouping" align="Oryx_Top" />
  <property group="Edit" align="Oryx_Left" />					
</plugin>
```
Second, you can reference a stencil set via its namespace and define it as required. For example, the epcSupport plugin implements features that only makes sense for EPC models. Therefore, it requires the EPC stencil set:
```
<plugin source="epcSupport.js" name="ORYX.Plugins.EPCSupport">
  <requires namespace="http://b3mn.org/stencilset/epc#"/>	
</plugin>
```
Third, you can explicitly exclude a stencil set:
```
<plugin source="file.js" name="ORYX.Plugins.Save">
  <notUsesIn namespace="http://b3mn.org/stencilset/xforms#"/>
</plugin>
```

## The Facade ##

The facade is the interface to access the editor. The interface object is one of the parameters of a plugin's constructor. The following methods are defined in the interface:

```
offer(pluginData: Object): void

getStencilSets(): Hash
getRules(): ORYX.Core.StencilSet.Rules
loadStencilSet(source:URL): void

createShape(option:Object): ORYX.Core.Shape
deleteShape(shape:ORYX.Core.Shape): void
getSelection(): ORYX.Core.Shape[]
setSelection(elements:ORYX.Core.Shape[], subSelectionElements:ORYX.Core.Shape[]): void
updateSelection(): void
getCanvas(): ORYX.Core.Canvas

importJSON(jsonObject: JSON, noSelectionAfterImport: boolean): ORYX.Core.Shape[]
importERDF(erdfDOM: XMLDocument): ORYX.Core.Shape[]
getERDF(): string

executeCommands(commands: ORYX.Core.Command[]): void
registerOnEvent(eventType: string, callback: function): void
unregisterOnEvent(eventType: string, callback: function): void
raiseEvent(event: Object, uiObj: ORYX.Core.!UIObject): void
enableEvent(eventType: string): void
disableEvent(eventType: string): void
eventCoordinates(event: Event): !SVGPoint

addToRegion(region: string, component: Ext.Component, title: string): Ext.Component
```

### offer ###

```
offer(pluginData): void
```

**Parameters:**

  * _pluginData_: Object with the following properties
    * _name_: string
      * Name of the feature.
    * _functionality_: function
      * Callback of the feature. Callback gets following parameters: Generated button (Ext.Button or Ext.menu.Item) and, if _toggle=false_, the event (Ext.EventObject), else the next state (boolean).
    * _keyCodes_: Array
      * List of Object that describes the key down event on which the functionality callback should be called
      * Each key code consists of a **key code** number (e.g. 67 represents 'c'), the **key action** (`ORYX.CONFIG.KEY_ACTION_DOWN, ORYX.CONFIG.KEY_ACTION_UP`) and a list of the three supported **meta keys** (`ORYX.CONFIG.META_KEY_META_CTRL, ORYX.CONFIG.META_KEY_ALT, ORYX.CONFIG.META_KEY_SHIFT`).
      * It is mentionable that the apple meta and control key are handled equally.
      * _Example:_
```
        keyCodes: [{
	    metaKeys: [ORYX.CONFIG.META_KEY_META_CTRL],
	    keyCode: 67,
            keyAction: ORYX.CONFIG.KEY_ACTION_DOWN
	}] 
```
    * _group_: string
      * Name of the group the feature belongs to (e. g. used by toolbar).
    * _dropDownGroupIcon_: string
      * (default: undefined) If provided, all plugins of the same _group_ and the same _dropDownGroupIcon_ are grouped together into one drop down button.
      * Pay attention on setting _index_ property correctly: Per group, the index of the buttons should be counted from left to right, including the items in the drop down list (e.g. if one group should have 2 simple buttons and 1 drop down button with 2 other buttons, the indices should be 1 + 2 for the simple buttons and 3 + 4 for the buttons in the drop down list (so do not restart counting from 1)).
    * _icon_: string
      * URL to an icon for the feature. URL must be absolute or relative to the root folder.
    * _description_: string
      * Description of the feature
    * _index_: int
      * Index for ordering of one group's features.
    * _minShape_: int
      * Minimum number of shapes that have to be selected so that the feature is available/activated.
    * _maxShape_: int
      * Maximum number of shapes that have to be selected so that the feature is available/activated.
    * _toggle_: boolean
      * (default: false) Creates a toggling button or, if _dropDownGroupIcon_ provided, a check box instead of simple one.
    * _isEnabled_: function
      * Optional callback that activates the feature, if it returns **true**, or deactivates it, if it returns **false**.

**Description:**

A plugin must register each of its features with this method. This method shall be called in the constructor of the plugin. Once registered, the features are available in the toolbar or any other plugin that offers a UI for accessing other plugins' features.


### getStencilSets ###

```
getStencilSets(): Hash
```

**Returns:**

A hash object with the stencil sets' namespaces of all available stencil sets as keys and the stencil set objects (_ORYX.Core.StencilSet.StencilSet_) as values.

**Description:**

This method is used for accessing the loaded stencil sets.


### getRules ###

```
getRules(): ORYX.Core.StencilSet.Rules
```

**Returns:**

An object of type _ORYX.Core.StencilSet.Rules_ that enables you to check if two shapes can be connected or contain each other.


### loadStencilSet ###

```
loadStencilSet(source:URL): void
```

**Parameters:**

  * _source_:URL
    * URL that points to a stencil set's definition file.

**Description:**

Calling this method loads a stencil set and fires a _stencilSetLoaded_ event.


### createShape ###

```
createShape(option:Object): ORYX.Core.Shape
```

**Parameters:**

  * _object_: Object
    * Object with parameters about the new shape

**Returns:**

The created shape object.

**Description:**

This method will soon be reimplemented, because it was initially designed for a specific case. If you need it, check the code where the method is used.


### deleteShape ###

```
deleteShape(shape:ORYX.Core.Shape): void
```

**Parameters:**

  * _shape_: ORYX.Core.Shape
    * Shape that you want to delete.


### getSelection ###

```
getSelection(): ORYX.Core.Shape[]
```

**Returns:**

An array of shape objects that are selected.


### setSelection ###

```
setSelection(elements:ORYX.Core.Shape[], subSelectionElements:ORYX.Core.Shape[]): void
```

**Parameters:**

  * _elements_: ORYX.Core.Shape[.md](.md)
    * Shape objects to select.
  * _subSelectionElements_: ORYX.Core.Shape[.md](.md)
    * A sub set of the selected objects for sub selection.

**Description:**

Set the selection of shape objects with this method. The method also fires a "selectionChanged" event.


### updateSelection ###

```
updateSelection(): void
```

**Description:**

Fires a "selectionChanged" event.


### getCanvas ###

```
getCanvas(): ORYX.Core.Canvas
```

**Returns:**

The editor's canvas object.


### importJSON ###

```
importJSON(jsonObject: JSON, noSelectionAfterImport: boolean): ORYX.Core.Shape[]
```

**Parameters:**

  * _jsonObject_: Object
    * JSON object that describes all shapes to import. It has the following structure:
      * ...
  * _noSelectionAfterImport_: boolean
    * If _true_, the imported objects will not be selected.

**Returns:**

An array of all created shapes.


### importERDF ###

```
importERDF(erdfDOM: XMLDocument): ORYX.Core.Shape[]
```

**Parameters:**

  * _erdfDOM_: XMLDocument
    * An XML document containing process data in eRDF format.

**Returns:**

An array of all created shapes.


### getERDF ###

```
getERDF(): string
```

**Returns:**

A string that represents the current model in eRDF format.


### executeCommands ###

```
executeCommands(commands: ORYX.Core.Command[]): void
```

**Parameters:**

  * _commands_: ORYX.Core.Command[.md](.md)
    * Array of Command objects. The Command class is abstract and must be extended by a sub class. The Command class defines the methods _execute_ and _rollback_.

**Description:**

If your feature needs undo/redo support, you have to implement it as a sub class of ORX.Core.Command and execute it with this method.


### registerOnEvent ###

```
registerOnEvent(eventType: string, callback: function): void
```

**Parameters:**

  * _eventType_: string
    * The type of event you want to register a callback. The types are defined in the file config.js.
  * _callback_: function
    * Function that is called when the event occurs.

**Description:**

Use this method to react on event occurrence.


### unregisterOnEvent ###

```
unregisterOnEvent(eventType: string, callback: function): void
```

**Parameters:**

  * _eventType_: string
    * The type of event you want to unregister a callback. The types are defined in the file config.js.
  * _callback_: function
    * Function that is called when the event occurs.

**Description:**

Use this method to stop reacting on an event occurrence.


### raiseEvent ###

```
raiseEvent(event: Object, uiObj: ORYX.Core.UIObject): void
```

**Parameters:**

  * _event_: Object
    * An event object with event specific attributes. The only attribute that is available in all events is _type_.
  * _uiObj_: function
    * You can optionally specify a ORYX.Core.UIObject object the event is referenced to. It is also possible to reference it in the event object.

**Description:**

Use this method to fire an event. For _event.type_ use one of the constants defined in config.js.


### enableEvent ###

```
enableEvent(eventType: string): void
```

**Parameters:**

  * _eventType_: string
    * The event type you want to enable.


### disableEvent ###

```
disableEvent(eventType: string): void
```

**Parameters:**

  * _eventType_: string
    * The event type you want to disable.


### eventCoordinates(event: Event): SVGPoint ###

```
eventCoordinates(event: Event): SVGPoint
```

**Parameters:**

  * _event_: Event
    * The event object of a mouse event.

**Returns:**

An SVGPoint that is the mouse position on the canvas.

**Description:**

Mouse events define the mouse position relative to the origin of the page. To find out the mouse position relative to the canvas, you can use this method.


### addToRegion ###

```
addToRegion(region: string, component: Ext.Component, title: string): Ext.Component
```

**Parameters:**

  * _region_: string
    * Decides where the component is added. Possible values: 'north', 'south', 'east', 'west'.
  * _component_: Ext.Component
    * The Ext.Component object to add.
  * _title_: string
    * The title of the added component.

**Returns:**

The added Ext.Component object.

**Description:**

Use this method to add a new UI component to the editor's UI.