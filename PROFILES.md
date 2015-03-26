# Definition #
A profile is a predefined set of plugins, which is used to deliver different topic specific plugins configurations to
the client. Profiles are compiled to build-time and can only include a subset of all plugins defined in the plugins.xml.
Afterwards each profile is accesible via an unique editor url "oryx/editor;PROFILENAME".

Only constributers of the Oryx -Project can define profiles, they areu not customization concept for the common user.
The common use case for defining a new profile is, that developer of plugins or stencilsets can define their own oryx configuration
to offer only relevant plugins to the client, which are accesible offer the unique URL and also may referenced by this.
Especially on working with proof-of-concept plugins, which are not stabil, but should be accessible, a developer can define a profile in
combination with the instabil plugin and show it to the cummunity without irritating users of other profiles.

# HowTo Create a Profile? #
All profiles have to be defined in the profiles.xml.So to creating a new profile, one has to add the profile configuration
to the profiles.xml (located in the plugins folder)

```
<profiles>
	//Define name, stencilset and dependencies
	<profile name="newProfile" stencilset="stencilsets/bpmn1.1/bpmn1.1.json" depends="default">
		//Define included plugins by name
		<plugin name="ORYX.Plugins.PluginLoader"/>

		<plugin name="ORYX.Plugins.View">
			// add or overwrite plugin properties
			<property zoomLevel="0" maxFitToScreenLevel="0,1" />
		</plugin>
		<plugin name="ORYX.Plugins.DragDropResize"/>
	</profile>
	...
</profiles>
```

The profile tag has the following attributes:
| name | the unique name of a profile (resulting url: "oryx/editor;PROFILENAME") |
|:-----|:------------------------------------------------------------------------|
| stencilset | path of the standard stencilset json file, which should be loaded for the profile |
| depends | comma separated list of other profiles, all configurations of here defined profiles are copied into the profile itself. |
So the "newProfile" includes the explicit defined plugins as well as all plugins defined in the default profile.
Each profile node can contain none or severall plugin child nodes.

Each plugin node has one attribute:
| name | the name of the plugin which has to be the same as in the plugins.xml defined |
|:-----|:------------------------------------------------------------------------------|

Each plugin node can contains property-nodes, this mechanism can also be used directly in the plugins.xml.
Each property node consist of severall attributes, defining properties of a plugin. Each attribute represents a
key/value- pair which will be offered to the plugin at runtime.
| [key](key.md) | defines a named property |
|:--------------|:-------------------------|

Properties of the profiles.xml will overwrite existing properties with the same name, or will be added to the plugin node.
The structure of property-nodes will not be considered.
Additional ALL plugins defined as CORE (core-Attribute is set to true) will be added to each profile.

# CAUTION PROFILE DEFINITION IS STILL IN PROGRESS #