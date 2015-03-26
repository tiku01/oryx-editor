# Stencil Set Perspectives #

A stencil set like BPMN 2.0 contains about 50 stencils. This number of stencil sometimes lead to losing the overview. So the idea is to hide some stencils and therefore to show only the most important ones of a Stencil Set.

![http://oryx-editor.googlecode.com/svn/wiki/perspectives/choice_menu.png](http://oryx-editor.googlecode.com/svn/wiki/perspectives/choice_menu.png)

# Defining a Perspective #

A stencil set perspective is basically a short cut for loading or unloading a couple of StencilSetExtension . The definition of a perspective is done in `/editor/data/stencilsets/extensions/extensions.json`.

The definition is done in the JSON format. The perspectives array holds objects that always define one perspective.

```
"perspectives": [
    {
	"title":"Academic BPMN",
	"namespace":"http://oryx-editor.org/stencilsets/perspectives/academicbpmn#",
	"description":"Academic perspective on BPMN. Features the complete standard.",
	"stencilset":"http://b3mn.org/stencilset/bpmn1.1#",
	...
     },

... more perspectives ...

]
```

In general a perspective definition contains the title, namespace, description and the related stencil set of a perspective.

```
...
"removeExtensions" : [
        "http://oryx-editor.org/stencilsets/extensions/bpmn2.0basicsubset#",
	"http://oryx-editor.org/stencilsets/extensions/bpmn2.0basicsubset/choreography#"
],
...
```

The removeExtensions array contains namespace strings of extensions that definitely have to be unloaded when the perspective is selected. If the `removeAll` flag is set, all currently loaded extension are unloaded.

```
...
"addExtensions" : [
        "http://oryx-editor.org/stencilsets/extensions/example#",
	{
		"ifIsLoaded" : "http://oryx-editor.org/stencilsets/extensions/bpmn2.0choreography#",
		"add" : "http://oryx-editor.org/stencilsets/extensions/bpmn2.0basicsubset/choreography#",
		"default" : "http://oryx-editor.org/stencilsets/extensions/bpmn2.0basicsubset#"
	}
]
...
```

The addExtensions array contains all stencil set extensions that have to loaded for the certain perspective. It is either a namespace string of the extension or an object for a more sophisticated configuration.
Depending on the currently loaded stencil set extensions it is possible to load others for this perspective. 'ifIsLoaded' holds the required extension namespace. If this extension is loaded than the extension defined by 'add' is going to load as well. If not so the optional 'default' is loaded.