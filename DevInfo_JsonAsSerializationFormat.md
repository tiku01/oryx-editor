As internal serialization format for models in ORYX, (e)RDF is replaced by [JSON](http://en.wikipedia.org/wiki/JSON), a JavaScript-based, human-readable interchange format. For Oryx users, there is no need in performing any migration steps, all created models will be accessible like before. For Oryx developers, there are a number of new interfaces to make use of the new JSON representation of a model (see below). Although RDF is still supported (but deprecated), we strongly recommend to use JSON for any new developments to be future-proof.

The current status of the JSON support implementation is NOT ready for production use, some days of testing are still required. I would appreciate that you (as developer) update to the newest revision of Oryx so that potential bugs/ issues are discovered as fast as possible. In particular, it could happen that some special model attributes get lost/ are ignored while saving/ reloading a model (because of some wrong json/erdf conversions internally). Please do not hesitate to contact me/ the oryx mailing list, if any issues are found and I will try to fix them quickly.

# JSON Representation of a Model #
This is a small example of the JSON representaztion of a model. To see a more sophisticated one, just draw a new model and open the /json-URL instead of /self.

```
{
  resourceId: "mymodel1",
  childShapes: [ //list of child shapes, which can be nested by using the childShapes attribute in children
    {
    stencil:{ id:"Subprocess" },
      resourceId: "myshape1",
      outgoing:[ //list of resource ids defining the outgoings of a shape
        {resourceId: "AnotherShape"}
      ],
      target: {resourceId: "AnotherShape"},
      bounds:{ lowerRight:{ y:510, x:633 }, upperLeft:{ y:146, x:210 } },
      dockers:[{x:50,y:40},{x:437,y:31}], //set dockers for edges
      childShapes:[],
      properties:{}
    }
  ],
  properties:{ //model properties
    language: "English"
  },
  ssextensions: [ //optional list of stencil set extensions
    "/oryx/stencilsets/extensions/bpmncosts/bpmncosts.json"
  ],
  stencilset:{
    url:"http://localhost:8080/oryx/stencilsets/bpmn1.1/bpmn1.1.json"
  },
  stencil: {
    id:"BPMNDiagram"
  }
}
```

# Developer Notes #
  * Editor client-side plugins: The facade has now the methods `importJSON` and `getJSON` for dealing with JSON.
  * `Representation#getContent` (reads eRDF from database) is replaced by `Representation#getJson` and `Representation#getErdf` which performs the correct transformations to output format which is needed.
  * The JSON representation of a model can be accessed via HTTP by using URLs like `http://<host>:<port>/backend/poem/model/<modelId>/json`.
  * Editor instances are no longer described/ "configured" by rendering eRDF in the output HTML. The following snippets can be used to render an editor either for loading existing models or for creating a new one.
```
<!-- Creating a new model -->
<script type="text/javascript">
  function onOryxResourcesLoaded(){
    new ORYX.Editor({
      id: 'oryx-canvas123',
      fullscreen: true,
      stencilset: {
        url: "/oryx/stencilsets/bpmn1.1/bpmn1.1.json"
      }
    });
  }
</script>
```<!-- Open an existing model -->
<script type="text/javascript">
  function onOryxResourcesLoaded(){
    ORYX.Editor.createByUrl("/backend/poem/model/13/json", {
      id: 'oryx-canvas123',
      fullscreen: true
    });
  }
</script>```