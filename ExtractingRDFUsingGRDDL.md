# Extracting RDF using GRDDL #

A short description of GRDDL taken from http://www.w3.org/2004/01/rdxh/spec:
> GRDDL is a mechanism for Gleaning Resource Descriptions from Dialects of Languages. This GRDDL specification introduces markup based on existing standards for declaring that an XML document includes data compatible with the Resource Description Framework (RDF) and for linking to algorithms (typically represented in XSLT), for extracting this data from the document.

Every process saved by the Oryx Editor contains RDF triples which are embedded into XHTML using eRDF. For more information on eRDF, see [Data Management](DataManagementImplementation.md). To describe the fact that eRDF data is embedded into a page, a profile must be specified in the head of such a document. The result should be of the following form:

```
<?xml version="1.0" encoding="utf-8"?>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:b3mn="http://b3mn.org/2007/b3mn"
      xmlns:ext="http://b3mn.org/2007/ext"
      xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
      xmlns:atom="http://b3mn.org/2007/atom+xhtml">

    <head profile="http://purl.org/NET/erdf/profile">
        [...]
    </head>

    [...]

</html>
```

The URL http://purl.org/NET/erdf/profile describes how a GRDDL compatible client would be able to understand how to transform a document containing eRDF into RDF as described in http://www.w3.org/TR/rdf-syntax-grammar/. It therefore references an XSL stylesheet located at http://purl.org/NET/erdf/extract-rdf.xsl.

If you don't have a GRDDL client or library at hand, you can download and use that stylesheet, since it is mature and will not likely change in the near future. When applied to an Oryx document, that stylesheet will produce valid RDF. I have used the saxon library  for Java to do this, and it works fine. General information on XSLT and how to apply it can be found in http://www.xml.com/pub/a/2003/11/26/learnXSLT.html. The saxon library is recommended there, too.

To further work with the data, there are several possibilities:
  * Use an RDF library to parse the data and make in accessible to the environment you're in. There should be plenty of those for common programming languages.
  * Use XPath/SAX/etc. to access specific data you're interested in without using decent knowledge of the RDF you're using. This should work for even more languages.
  * Transform the RDF into another format your software understands, e.g. using XSLT. This also can be used when writing transformation tools that convert Oryx Editor models into more general description languages.

## Understanding the extracted RDF ##

In the RDF that you have extracted from the Oryx Editor document, there will be a special resource that has the RDF type `http://oryx-editor.org/canvas`. It will have the following form:

```
<rdf:Description rdf:about="#oryxcanvas">
    <rdf:type rdf:resource="http://oryx-editor.org/canvas"/>
    <mode xmlns="http://oryx-editor.org/">writeable</mode>
    <mode xmlns="http://oryx-editor.org/">fullscreen</mode>
   
    [...]

</rdf:Description>
```

The information you will need from this resource is which stencilsets are used, and what resources described in this document actually belong to the model. Data you won't need is triples like the display or edit modes seen above.

Stencilset triples will be describes as following; with the stencil set being located at http://b3mn.hpi.uni-potsdam.de/data/stencilsets/bpmn/bpmn.json:
```
 <stencilset xmlns="http://oryx-editor.org/"
        rdf:resource="http://b3mn.hpi.uni-potsdam.de/data/stencilsets/bpmn/bpmn.json"/>
```

References to resources considered part of the model are described as follows; with #resource18 being an actual part of the model:
```
<render xmlns="http://oryx-editor.org/" rdf:resource="#resource18"/>
```

In the example process used for this short tutorial, the resource #resource18 is a pool. The extracted RDF states the following properties:
```
<rdf:Description rdf:about="#resource18">
    <type xmlns="http://oryx-editor.org/">http://b3mn.org/stencilset/bpmn#Pool</type>
    <poolId xmlns="http://oryx-editor.org/"/>
    <poolCategories xmlns="http://oryx-editor.org/"/>
    <poolDocumentation xmlns="http://oryx-editor.org/"/>
    <name xmlns="http://oryx-editor.org/"/>
    <participants xmlns="http://oryx-editor.org/"/>
    <lanes xmlns="http://oryx-editor.org/"/>
    <boundaryVisible xmlns="http://oryx-editor.org/">true</boundaryVisible>
    <processId xmlns="http://oryx-editor.org/"/>
    <processName xmlns="http://oryx-editor.org/"/>
    <processType xmlns="http://oryx-editor.org/">None</processType>
    <status xmlns="http://oryx-editor.org/">None</status>
    <adHoc xmlns="http://oryx-editor.org/"/>
    <adHocOrdering xmlns="http://oryx-editor.org/">Parallel</adHocOrdering>
    <adHocCompletionCondition xmlns="http://oryx-editor.org/"/>
    <suppressJoinFailure xmlns="http://oryx-editor.org/"/>
    <enableInstanceCompensation xmlns="http://oryx-editor.org/"/>
    <processCategories xmlns="http://oryx-editor.org/"/>
    <processDocumentation xmlns="http://oryx-editor.org/"/>
    <bgColor xmlns="http://oryx-editor.org/">#ffffff</bgColor>
    <bounds xmlns="http://oryx-editor.org/">43.99998474121094,273,1771.9942420319314,1464.474597192284</bounds>
</rdf:Description>
```

Most of the properties known for this pool are inherited from what the stencil set definition (s.a.) defined as properties. Beside those, there are certain properties that the editor adds to every element that has a visual representation in the model, including the bounds.

However, the knowledge gained from this block of data is not all that can be stated about this pool. To learn about additional statements, look up the resource #resource19:

```
<rdf:Description rdf:about="#resource19">
    <type xmlns="http://oryx-editor.org/">http://b3mn.org/stencilset/bpmn#Lane</type>
    <id xmlns="http://oryx-editor.org/"/>
    <categories xmlns="http://oryx-editor.org/"/>
    <documentation xmlns="http://oryx-editor.org/"/>
    <name xmlns="http://oryx-editor.org/">E-Mail Voting Process</name>
    <parentPool xmlns="http://oryx-editor.org/"/>
    <parentLane xmlns="http://oryx-editor.org/"/>
    <bgColor xmlns="http://oryx-editor.org/">#ffffff</bgColor>
    <bounds xmlns="http://oryx-editor.org/">30,0,1727.9942572907205,1191.474597192284</bounds>
    <parent xmlns="http://raziel.org/" rdf:resource="#resource18"/>
</rdf:Description>
```

This resource is a lane that is embedded into the pool, as the last statement says. The parent property, again, is a property added by the editor and is inherently troughout all stencil sets, independently of whether defined or not.

Another property which is esssential to learning about the process described in such an RDF document is outgoing, for it describes the logical connection between visual elements:

```
<rdf:Description rdf:about="#resource24">
    <type xmlns="http://oryx-editor.org/">http://b3mn.org/stencilset/bpmn#Subprocess</type>
    <id xmlns="http://oryx-editor.org/"/>
    <categories xmlns="http://oryx-editor.org/"/>
    <documentation xmlns="http://oryx-editor.org/"/>
    <pool xmlns="http://oryx-editor.org/"/>
    <lanes xmlns="http://oryx-editor.org/"/>
    [...]
    <bgColor xmlns="http://oryx-editor.org/">#ffffff</bgColor>
    <bounds xmlns="http://oryx-editor.org/">311.00001525878906,287,411.00001525878906,367</bounds>
    <outgoing xmlns="http://raziel.org/" rdf:resource="#resource41"/>
    <outgoing xmlns="http://raziel.org/" rdf:resource="#resource14"/>
    <parent xmlns="http://raziel.org/" rdf:resource="#resource19"/>
</rdf:Description>
```
Note, that an outgoing property does not define a sequence flow. It describes a connection between a node and an edge. An edge, then, is supposed to have an outgoing property itself connecting it to another node. Thus, the graph built from outgoing references is orthogonal to the parent property graph, but both are neccessary to understand the structure of the model.

When you need more information on the stencil set specific information, you may also want to look into the definition of it. See above for instructions on how to obtain the definition file from the canvas resource.