# JavaScript #
For documenting JavaScript, [JsDoc Toolkit](http://code.google.com/p/jsdoc-toolkit/) is used in Oryx to generate documentation directly from code (like in JavaDoc). [Why aren't we using JSDoc?](http://code.google.com/p/jsdoc-toolkit/wiki/FAQ#How_this_project_is_related_to_JSDoc)

## Write Documentation ##
  * [Tag Reference](http://code.google.com/p/jsdoc-toolkit/wiki/TagReference)
  * [Inline Documentation (for nicer documentation, advanced)](http://code.google.com/p/jsdoc-toolkit/wiki/InlineDocs)

Small example:
```
/**
    @param {String} name
    @type Array
*/
function getPerson(name) {
}
```

JavaScript as a dynamic programming language based on prototypes makes documenting classes and methods more difficult than commenting Java code. Here are some best practices.

### Documenting namespaces ###
(This must be done if the jsdoc tool throws errors when it doesn't find a namespace)

```
/**
   @namespace Oryx name space for plugins
   @name ORYX.Plugins
*/
if(!Oryx.Plugins)
  Oryx.Plugins = {}
// Alternative
Ext.namespace("ORYX.Plugins");
```

### Documenting classes and functions constructed with Clazz.extend ###
If contructing a class, JSDoc cannot know if functions are instance or static methods. With the help of the tag `@methodOf ORYX.Plugins.MyPlugin.prototype` a function can directly reference if it belongs to the class or to an instance/ a prototype. So that you do not have to specify this for each method within one Clazz.extend call, use the [@lends](http://code.google.com/p/jsdoc-toolkit/wiki/TagLends) tag (must be defined _directly_ before the object) as in the following example:

```
/**
 * This is my wonderful class..
 * @class ORYX.Plugins.MyPlugin
 * @extends Clazz
 * @param {Object} myParam This is a param referring to the construct of the class.
*/
ORYX.Plugins.MyPlugin= Clazz.extend(
    /** @lends ORYX.Plugins.MyPlugin.prototype */
{
    construct: function(myParam){
      // Do sth.
    }

    /**
     * This is a function description
     */
    myFunction: function(){
        //do sth.
    }

   /**
    *  This is a private method
    *  @private
    */
   myPrivateFunction: function(){
       //do sth. private
   }
});
```

## Generate Documentation - Tool Support ##
  * [Tool Download](http://code.google.com/p/jsdoc-toolkit/downloads/list)
  * [Tool Command Line Options](http://code.google.com/p/jsdoc-toolkit/wiki/CommandlineOptions)

```
java -jar <JsDocDir>\jsrun.jar <JsDocDir>\app\run.js "<PathToSourceFile(s)>" -t=<JsDocDir>\templates\jsdoc -d="<OutputPath>"
```

_TODO: Writing ant tasks, generate jsdoc from whole javascript and publish it, intgration into cruise control, ..._