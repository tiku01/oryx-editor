### Table of Content ###


# Introduction #

In this tutorial we want to give you a brief introduction into the stencil set specification. For more detailed information take a look to the [bachelor's paper of Nicolas Peters](http://oryx-editor.googlecode.com/files/OryxSSS.pdf)

A typical stencil set consists of a JSON file and several SVG files, as well as several
picture files. The JSON file contains the stencil set description, e.g. a definition
of every stencil.

For each stencil in a stencil set one SVG file that contains the graphical representation of the stencil and one picture file for the icon view is required.

These files are stored in a specific folder structure.
```
	letsdance
	   |- letsdance.json
	   |- icons 
	   |- view
```
# Create Graphical Representation #

First of all let us create the SVG files for the graphical representation of the "Let's dance" modeling language and store them in the view folder.

When using oryx elements and attributes in a SVG document, you have to set a prefix. In this example the prefix "oryx" is used.

```
<svg
   xmlns="http://www.w3.org/2000/svg"
   xmlns:oryx="http://www.b3mn.org/oryx"
   version="1.1">
	...
</svg>
```

## Specify Canvas ##

To start modeling, a kind of background/canvas is needed. On it you can place interaction and relationship objects.

Our background `node.diagram.svg` only consists of a square with a stroke and fill color. It is done by adding a "rect" element to the "g" element:

```
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<svg
   xmlns="http://www.w3.org/2000/svg"
   xmlns:oryx="http://www.b3mn.org/oryx"
   version="1.1">
   <g>
    <rect oryx:anchors="top bottom left right" x="0" y="0" width="80" 
     height="80" stroke="black" fill="#D3DEFF" stroke-width="2"/>
   </g>
</svg>
```

So that’s all, your first stencil is ready. Besides the SVG file you have to create a picture file for each stencil. It is recommended to use a size of 32 to 32 pixels and a common format for pictures on the web such as JPEG, PNG or BMP. To create picture files from SVG files use Inkscape for example and save them in the icon folder.

For Oryx we add the attribute “anchors” to the rect element. With these anchors the rectangle is fixed to all four sides so that it will always have the same distance to the border of the stencil. The border of a stencil is defined by the smallest rectangle aligned along the axes that includes all SVG shapes of the stencil.
Oryx anchors may also be used to pin an Element like the BPMN 2.0 Usertask symbol to the top left corner of a task. This can be done with the addition of the attribute oryx:anchors="top left" to the path representing the user task. This way it always stays there and doesn't get misplaced when the size of the task is changed.

Let’s take a look at the current result inside the oryx-editor. But therefore you have to define a kind of description for rules and additional attributes. To do this edit the JSON file. The basic structure of our Let’s Dance modeling language description is shown below:

```
{
	"title":"Let's Dance",
	"namespace":"http://b3mn.org/stencilset/letsdance#",
	"description":"Simple stencil set for Let's Dance diagrams.",
	 "stencils": [/*..*/],
	 "rules":{*/..*/}
}
```

The description of the head node is added just by insertion of a new node element into the stencils section.

```
{
	"title":"Let's Dance",
	"namespace":"http://b3mn.org/stencilset/letsdance#",
	"description":"Simple stencil set for Let's Dance diagrams.",
	 "stencils": [
		{
			"type":		"node",
			"id":		"Diagram",
			"title":	"Diagram",	
			"groups":	["Diagram"],
			"description":	"A Let’s Dance Diagram",
			"view":		"node.diagram.svg",
			"icon":		"diagram.png",
			"roles":		[]
		   }
	],
	 "rules":{*/..*/}
}
```

It is recommended to create a static XHTML site in `/editor/test/examples` that loads the Oryx Editor with your desired stencil set, to test it. In `/editor/test/examples` you can many other example files for various stencil sets.

In the case of Let's Dance it is the `editor_letsdance.xhtml`. All of the example files have the same structure. To customize one for your own stencil set, only two adjustments are necessary:

First add a reference to the folder, which contains your JSON File, icons and view elements . For example:

```
<meta name="oryx.type" content="http://b3mn.org/stencilset/letsdance#Diagram" />
```

You also have to add the id of the root diagram (canvas) at the end: _foldername#canavasID_.

Second insert a JavaScript tag to the body of the page, to specify which stencil set and stencil set extensions are supposed to load.

```
<body style="display: block; overflow: hidden;">
  <script type='text/javascript'>
    function onOryxResourcesLoaded(){
      new ORYX.Editor({
        id: 'oryx-canvas123',
        stencilset: {
          url: ORYX.CONFIG.ROOT_PATH + 'stencilsets/letsdance/letsdance.json'
        },
        ssextensions:[]
      });
    }
  </script>  
</body>
```

When deploying the Oryx editor during the development, the build target _build-with-xhtml-test-files-flag_ could be useful to have the test XHTML files available in the web archive.

So to deploy the Oryx editor you may be want run the targets _undeploy-editor, clean-editor, build-with-xhtml-test-files-flag, build-with-script-files-flag, build-editor, deploy-editor_

After a successful deployment, the example Let's Dance stencil set is available under `http://`_host_`:`_port_`/oryx/editor_letsdance.xhtml`

When opening it in Firefox, you should see a blank diagram background.

http://oryx-editor.googlecode.com/svn/wiki/pictures_letsdance/001_firstdiagram.PNG

## Draw Let's Dance Elements ##

Now it is the task to define the SVG files for the elements, which can put on this background diagram. Oryx decide between edges and Nodes.

A Node can be resized, text can be attached, colors and opacities can be changed and parts of the node can be hidden. Except resizing, these abilities are not only specified in the SVG document of a node, but you have to prepare the SVG representation for that.

Edges are lines of different forms with decorations attached to it (for example arrowheads). In Oryx, edges should also have the possibility to be divided into sections and to control the sections separately. This makes it possible to draw not only straight edges, but also edges with corners.

The very simple constructed language “Let’s Dance” basically offers two types of nodes and tree types of edges.

The simple interaction node basically consists in each case of a path on right respectively left side for receiver and sender. Also a rectangle is put below them.
Then add text to label sender, receiver and the message type. If you assign an unique id to each element in the same SVG file, you latter can change their attributes over the property panel inside the oryx-editor. The stencil is completed by adding magnets on each side of the stencil. With magnets you can define special points on a node where you can dock other nodes or edges to connect them.

```
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<svg
   xmlns:svg="http://www.w3.org/2000/svg"
   xmlns="http://www.w3.org/2000/svg"
   xmlns:oryx="http://www.b3mn.org/oryx"
   version="1.0">
  <oryx:magnets>
  	<oryx:magnet oryx:cx="75" oryx:cy="0" oryx:default="yes"/>
  	<oryx:magnet oryx:cx="75" oryx:cy="75"/>
  	<oryx:magnet oryx:cx="0" oryx:cy="36"/>
  	<oryx:magnet oryx:cx="150" oryx:cy="36"/>
  </oryx:magnets>
  <g>
    <path
       id="receiver"
       oryx:anchor="top bottom left right"
       oryx:resize="vertical horizontal"
       d="M60 0 L150 0 L150 50 L60 50 L75 25 z"
       stroke="black" fill="none" stroke-width="2" />
    <path
       id="sender"
       oryx:anchor="top bottom left right"
       oryx:resize="vertical horizontal"
       d="M0 0 L60 0 L75 25 L60 50 L0 50 z"
       stroke="black" fill="none" stroke-width="2" />
    <rect
       id="descriptionline"
       oryx:anchor="top bottom left right"
       oryx:resize="vertical horizontal"
       width="150" height="25"
       x="0" y="50"
       stroke="black" fill="none"/>
	<text id="messageType" x="75" y="60" oryx:align="middle center"></text>		
	<text id="senderText" x="4" y="28" oryx:algin="middle center"></text>
	<text id="receiverText" x="80" y="28" oryx:algin="middle center"></text>	
  </g>
</svg>
```

The specification of a complex interaction is even easier. It consist of big rectangle and a smaller one with a text inside for a possible guard condition.

```
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<svg
   xmlns:svg="http://www.w3.org/2000/svg"
   xmlns="http://www.w3.org/2000/svg"
   xmlns:oryx="http://www.b3mn.org/oryx"
   version="1.0">
  <oryx:magnets>
  	<oryx:magnet oryx:cx="150" oryx:cy="0" oryx:default="yes"/>
  	<oryx:magnet oryx:cx="150" oryx:cy="150"/>
  	<oryx:magnet oryx:cx="300" oryx:cy="75"/>
  	<oryx:magnet oryx:cx="0" oryx:cy="75"/>
  </oryx:magnets>
  <g>
    <rect
       id="border"
       oryx:anchor="top bottom left right"
       oryx:resize="vertical horizontal"
       width="300"
       height="150"
       x="0" y="0"
       stroke="black" fill="white" stroke-width="2" /> 
    <rect
       id="guardConditionBorder"
       oryx:anchor="top bottom left right"
       oryx:resize="vertical horizontal"
       width="300"
       height="25"
       x="0" y="-25"
       stroke="black" fill="white" stroke-width="2" />   
    <text id="guardConditionText" x="150" y="-12" oryx:align="middle center"></text> 
  </g>
</svg>
```

# Define Language description #

Furthermore you have to add the description in the JSON file for both types of interaction, like you did before for the top diagram.


```
{
			"type":		"node",                // type of the element node or edge
			"id":		"complexInteraction",  // unique id within the stencil set
			"title":	"ComplexInteraction",	
			"groups":	["Interaction"],       // group where to display stencil in oryx's shape repository
			"description":	"Representation of a complex Interaction in Let's Dance",
			"view":		"node.complexinteraction.svg",  // SVG-File of the stencil
			"icon":		"node.complexinteraction.png",  // icon for preview in shape repository
			"roles":	["interaction"]  // roles can later be used for defining rules
},
{
			"type":		"node",
			"id":		"simpleInteraction",
			"title":	"simpleInteraction",	
			"groups":	["Interaction"],
			"description":	"",
			"view":		"node.simpleinteraction.svg",
			"icon":		"simpleinteraction.png",
			"roles":	["interaction"]
}
```

But if you now try to drag & drop a new interaction on your diagram, oryx will permit this. The reason is a semantic control in the oryx-editor. At least you have to define which element can contain an other element and which ones can connect to each other.

![http://oryx-editor.googlecode.com/svn/wiki/pictures_letsdance/002_interaction_no_rules.png](http://oryx-editor.googlecode.com/svn/wiki/pictures_letsdance/002_interaction_no_rules.png)

So insert some in the rules section. The attributes of the rules section are arrays. They again hold the specified rules for connection or containment for example. In our Let’s Dance example add the following rules. Both the ID of stencil and its roles identify a stencil to create rules.

```
"rules":{
		"containmentRules":	[
			{
				"role":		"Diagram",
				"contains": [
					"interaction"
				]
			},
			{
				"role":		"complexInteraction",
				"contains": [
					"interaction"
				]
			}
		]
		
	}
```

Remember that we earlier added the role “interaction” to simple- and complexinteraction. It can now be used to specify the containment rule. Generally it is possible to address an element either over its id or roles.

![http://oryx-editor.googlecode.com/svn/wiki/pictures_letsdance/003_interaction_containmentrules.png](http://oryx-editor.googlecode.com/svn/wiki/pictures_letsdance/003_interaction_containmentrules.png)

## Change graphics over property panel ##

In the SVG graphics we added some empty text fields. Now we want to use them to label the Let’s Dance interaction elements over the properties panel. To start with the complex interaction is rather easy. There is only the guard condition: text can type in it and a Boolean signals, whether this part is visible or not. Properties have to be specified for each edge or node separately. It is just another attribute to the stencil, but each property has a unique id within a stencil.

```
…
{
			"type":		"node",
			"id":		"complexInteraction",
			"title":	"ComplexInteraction",	
			"groups":	["Interaction"],
			"description":	"Representation of a complex Interaction in Let's Dance",
			"view":		"node.complexinteraction.svg",
			"icon":		"node.complexinteraction.png",
			"roles":		["interaction"],
			"properties": [
				{
					"id":"guardCondition",
					"type":"String",   // for more information to different property types see [http://oryx-editor.googlecode.com/files/OryxSSS.pdf bachelor's paper of Nicolas Peters] 
					"title":"Guard Condition",
					"value":"",
					"description":"",
					"readonly":false,
					"optional":true,
					"length":"",
					"refToView":"guardConditionText",
					"wrapLines":false
				},
				{
					"id":"showGuardCondition",
					"type":"Boolean",
					"title":"Show Condition",
					"value": false,
					"description":"",
					"optional":false,
					"refToView": [
						"guardConditionText",
						"guardConditionBorder"
					]
				},
			]
		},
…
```

The 'refToView' attribute specifies an id as string or an array of ids of SVG elements
in the graphical representation of a stencil. If this attribute is set, the property
will manipulate the graphical representation at run-time, e. g. changing the color or
rendering text. Dependant on the property's type you can reference different types
of SVG elements.

![http://oryx-editor.googlecode.com/svn/wiki/pictures_letsdance/004_guardcondition_false.png](http://oryx-editor.googlecode.com/svn/wiki/pictures_letsdance/004_guardcondition_false.png)

![http://oryx-editor.googlecode.com/svn/wiki/pictures_letsdance/005_guardcondition_true.png](http://oryx-editor.googlecode.com/svn/wiki/pictures_letsdance/005_guardcondition_true.png)


And now insert the properties for the simple interaction.

```
{
			"type":			"node",
			"id":			"simpleInteraction",
			"title":		"simpleInteraction",	
			"groups":		["Interaction"],
			"description":	"",
			"view":			"node.simpleinteraction.svg",
			"icon":			"simpleinteraction.png",
			"roles":		["interaction"],
			"properties": [
				{
					"id":"messageType",
					"type":"String",       
					"title":"Message Type",
					"value":"",
					"description":"",
					"readonly":false,
					"optional":true,
					"length":"",
					"refToView":"messageType",
					"wrapLines":false
				},
				{
					"id":"senderText",
					"type":"String",
					"title":"Sender Text",
					"value":"",
					"description":"",
					"readonly":false,
					"optional":true,
					"length":"",
					"refToView":"senderText",
					"wrapLines":false
				},
				{
					"id":"receiverText",
					"type":"String",
					"title":"Receiver Text",
					"value":" ",
					"description":"",
					"readonly":false,
					"optional":true,
					"length":"",
					"refToView":"receiverText",
					"wrapLines":false
				},
				{
					"id":			"senderColor",
					"type":			"Color",
					"title":		"Sender Color",
					"value":		"#ffffff",
					"description":	"",
					"readonly":		false,
					"optional":		true,
					"refToView":	        "sender",
					"fill":			true,
					"stroke":		false
				},
				{
					"id":"receiverColor",
					"type":"Color",
					"title":"Receiver Color",
					"value":		"#ffffff",
					"description":"",
					"readonly":false,
					"optional":true,
					"refToView":"receiver",
					"fill":true,
					"stroke":false
				}
			]
}
```

In this case we also implement the capability to change the color of the sender and receiver part within the SVG representation. Therefore define the property type Color, access to the desired element over “refToView” and last define, if you want to change the color of the fill or stroke or maybe both.

![http://oryx-editor.googlecode.com/svn/wiki/pictures_letsdance/006_colorchange1.png](http://oryx-editor.googlecode.com/svn/wiki/pictures_letsdance/006_colorchange1.png)

![http://oryx-editor.googlecode.com/svn/wiki/pictures_letsdance/007_colorchange2.png](http://oryx-editor.googlecode.com/svn/wiki/pictures_letsdance/007_colorchange2.png)

## Realize control flow ##

In the last step we have to realize some control flow. It is done by drawing some arrows and declaring them as “edge” in the description file.

The “precedes” element consists of a black line and the arrow at the end is realized by markers. **Note** that it is extremely important that you do not reference markers that are not defined! Doing this will result in the unavailability of some features like PDF export.

```
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<svg
   xmlns="http://www.w3.org/2000/svg"
   xmlns:oryx="http://www.b3mn.org/oryx"
   version="1.1">
   <defs> 
		<marker id="arrowEnd" refX="10" refY="5" markerUnits="userSpaceOnUse" 
			markerWidth="10" markerHeight="10" orient="auto"> 
			<path d="M 0 0 L 10 5 L 0 10 z" fill="black" stroke="black"/> 
		</marker> 
	</defs> 
   <g>
   	<path d="M50 50 L100 50" stroke="black" fill="none" 
		stroke-width="2" marker-end="url(#arrowEnd)"/> 
		
   </g>
</svg>
In comparison to the “precedes” the “weakprecedes” element has just a dashed line shape. The stroke-dasharray="3, 4" attribute produces this effect.  
The inhibit edge uses a second marker for the vertical stroke at the starting.
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<svg
   xmlns="http://www.w3.org/2000/svg"
   xmlns:oryx="http://www.b3mn.org/oryx"
   version="1.1">
   <defs> 
		<marker id="arrowEnd" refX="10" refY="5" markerUnits="userSpaceOnUse" 
			markerWidth="10" markerHeight="10" orient="auto"> 
			<path d="M 0 0 L 10 5 L 0 10 z" fill="black" stroke="black"/> 
		</marker> 
		<marker id="inhibitStroke" refX="-10" refY="5" markerUnits="userSpaceOnUse" 
			markerWidth="2" markerHeight="10" orient="auto"> 
			<path d="M 0 0 L0 10 z" fill="none" stroke="black" stroke-width="4"/> 
		</marker> 
	</defs> 
   <g>
   	<path d="M50 50 L100 50" stroke="black" fill="none" 
		stroke-width="2" marker-end="url(#arrowEnd)" marker-start="url(#inhibitStroke)" /> 
		
   </g>
</svg>
```

Now add them to the description file of the Let’s Dance stencil set.

```
                {
			"type":			"edge",
			"id":			"Precedes",
			"title":		"Precedes",	
			"groups":		["Relationship"],
			"description":	        "A precedes edges",
			"view":			"edge.precedes.svg",
			"icon":			"precedes.png",
			"roles":		["controlflow"]
		},
		{
			"type":			"edge",
			"id":			"Inhibits",
			"title":		"Inhibits",	
			"groups":		["Relationship"],
			"description":	"An inhibits edges",
			"view":			"edge.inhibits.svg",
			"icon":			"edge.inhibits.png",
			"roles":		["controlflow"]
		},
		{
			"type":			"edge",
			"id":			"WeakPrecedes",
			"title":		"WeakPrecedes",	
			"groups":		["Relationship"],
			"description":	"A weak precedes edges",
			"view":			"edge.weakprecedes.svg",
			"icon":			"edge.weakprecedes.png",
			"roles":		["controlflow"]
		}

```

They are from type edge. To collect them in a own group in Oryx’s shape repository just set the value of the attribute groups to “Relationship” for instance.

Only the connection rules are left in the rules section. Add them through:

```
"rules":{
		"connectionRules": [
			{
				"role": 	"controlflow",
				"connects": [
					{
						"from": "interaction",
						"to":	"interaction"
					}
				],
			}
		],
		"containmentRules":	[
			{
				"role":		"Diagram",
				"contains": [
							"interaction"
				]
			},
			{
				"role":		"complexInteraction",
				"contains": [
							"interaction"
				]
			}
		]
		
	}
```

Each edge defines the role controlflow. It allows, that an edge connects two interactions of any type.

When looking at the border of an interaction while crossing it with the end of an edge – the magnets come out from behind and you dock to them.

![http://oryx-editor.googlecode.com/svn/wiki/pictures_letsdance/008_magnets.png](http://oryx-editor.googlecode.com/svn/wiki/pictures_letsdance/008_magnets.png)

![http://oryx-editor.googlecode.com/svn/wiki/pictures_letsdance/009_hover_buttons.png](http://oryx-editor.googlecode.com/svn/wiki/pictures_letsdance/009_hover_buttons.png)

The complete implementation of the Let's Dance modeling language is available over the oryx-editor svn. It contains all shapes and descriptions.

# Morphing #

In Oryx, it is possible to extend stencil sets with morphing capabilities. This means, that one can specify a collection of semantically related shapes of the same type, i.e., node or edge, which will represented by a single icon in the shape menu. After one element---the representative element of that collection---is drawn on the canvas a new menu appears under the highlighted element: the morph menu. This menu allows to select another element of that group and transform, or _morph_, the current element into another.

Morphing is realized through specifying the groups: Which elements belong together?

  1. Define a role for each group. We recommend to use a naming scheme like "GroupNameMorph", where GroupName is the name of your group. However, this name will not appear in the UI.
  1. Add a role to the "roles" section of each element, that contains its group. If you have elements that do not belong to a group, i.e., stand on their own, you have to create a separate group for it anyways, e.g. "ElementNameGroup". _Groups that contain only one element will not have any effect on the UI, the morph menu won't be shown._
  1. Add the "morphingRules" section to the "rules" section in your stencil set, follow the example below:

```
  // example shapes from petrinets.json
  {
    "type": "node",
    "id":"VerticalEmptyTransition",
    "title":"Empty Transition",
    "groups":[],
    "description":"An empty transition",
    "view":"node.transition.emptyV.svg",
    "icon":"new_transition_emptyV.png",
    "roles": [
      "transition",
      "fromtoall",
      "TransitionMorph"
    ],
  // ...

  {
    "type": "node",
    "id":"Transition",
    "title":"Transition",
    "groups":[],
    "description":"Atransition",
    "view":"node.transition.svg",
    "icon":"new_transition.png",
    "roles": [
      "transition",
      "fromtoall",
      "TransitionMorph"
    ],
  // ...

  // morphing rules from petrinets.json

  "morphingRules": [
    {
      "role": "TransitionMorph",
      "baseMorphs": ["Transition"]
    },
    {
      "role": "FlowMorph",
      "baseMorphs": ["Arc"]
    },
    {
      "role": "PlaceMorph",
      "baseMorphs": ["Place"]
    },
  ]

```

Please note: The baseMorphs array contains the ids of all shapes, that are supposed to show up in the shape menu. In the example above this is only a single one. However, it's up to you to select which elements should be directly at the hand of the modeler and which are second class, i.e. reachable through the morphing.

Morphing does not affect the shape repository, i.e., the container on the left of the UI providing all shapes.

# Stencil Set Callbacks #

There are three more optional attributes. They offer some capabilities to implement requirements, that are not practical with the default stencil set specification. Each of those attributes take an event type and additional user defined attributes respectively an event list. An editor plug-in can handle the appropriate event. See: [HowToDevelopAnEditorPlugin](HowToDevelopAnEditorPlugin.md) for implementation details. Do not forget to record the new plug-in into the _plugins.xml_.

Note that these events are handled immediately. They run on a higher priority than other events like _KeyDown_ or _MouseMoved_ .

## Layout ##

The events specified in the layout attribute are triggered whenever the position and size of a shape is recalculated. For example the pool-lane behavior of BPMN 1.1 or the row layout of XForms and BPEL are realized in that way.

![http://oryx-editor.googlecode.com/svn/wiki/pictures_sscallbacks/pool_lane.png](http://oryx-editor.googlecode.com/svn/wiki/pictures_sscallbacks/pool_lane.png)

![http://oryx-editor.googlecode.com/svn/wiki/pictures_sscallbacks/xforms.png](http://oryx-editor.googlecode.com/svn/wiki/pictures_sscallbacks/xforms.png)

To define a layout event, you have to set the layout attribute as part of the definition of a stencil as an array of events.

_from xforms.json:_

```
{
/*...*/
"layout": [
 { 
   "type"       : 'layout.rows',
   "marginTop"	: 	50,
   "marginLeft" :	50,
   "spacingX"	:	30,
   "spacingY"	:	30,
   "exclude"	:	["http://b3mn.org/stencilset/xforms#Label"]
 },
 {
   "type" 	: 'layout.xforms.label',
   "moveY" : -1,
   "moveX" : 0
 }
],
/*...*/
}
```

The event objects holds information affected shape and contains all further attributes specified in the stencils layout attribute.

_example from editor/scripts/plugins/xforms.js:_

```
ORYX.Plugins.XForms = 
{
  construct: function(facade) {
    this.facade = facade;
    
    /* Register on the appropriate event */
    this.facade.registerOnEvent('layout.xforms.label', this.handleLayoutLabel.bind(this));
  },
  
  /* Method to handle the layout event */
  handleLayoutLabel : function(event) {
    var shape = event.shape;
    var moveX = event.moveX;
    var moveY = event.moveY;
    
    var labels = shape.getChildNodes(false).findAll(function(node) {
	return (node.getStencil().id() === "http://b3mn.org/stencilset/xforms#Label");
    });
    
    if(labels.length > 0) {
	labels.each(function(label) {
	  var ul = label.bounds.upperLeft();
	  var lr = label.bounds.lowerRight();
	  ul.y = - label.bounds.height() + moveY;
	  lr.y = moveY;
	  ul.x = moveX;
	  lr.x = label.bounds.width() + moveX;
	  label.bounds.set(ul, lr);
	});
     }
  }
}
```

## Serialize ##

The event specified in the serialize attribute of a stencil is triggered whenever a shape is persisted into the DOM.

_example from workfolwnets.json:_

```
{
/*...*/
  "serialize":{"type":"Workflownets.Activity.serialize"},
/*...*/
}
```


The event holds information about the currently serialized shape and its persistence data. The plug-in should modify the data and put the result into the result attribute of the event.

This event is handled in _plugins/workflownets.js_:

```
ORYX.Plugins.Workflownets = {

  construct: function(facade){
    this.facade = facade;
		
    this.facade.registerOnEvent("Workflownets.Activity.serialize", this.handleSerialize.bind(this));
  },
	
  handleSerialize: function(event) {
    var shape = event.shape;
    var data = event.data;
    var numOfOutgoings = shape.getOutgoingShapes().length;
	data.push({
	  name:"numOfOutgoings",
	  prefix:"oryx",
	  value:numOfOutgoings,
	  type:"literal"
	});
		
    event.result = data;
  },
};
```


## Deserialize ##

The event specified in the deserialize attribute of stencil is triggered while loading an object of a stored model.

The handling is analogue to that of a serialize event.