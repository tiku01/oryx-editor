# JavaScript #

From [Wikipedia](http://en.wikipedia.org/wiki/Javascript):
> JavaScript is an object-oriented scripting language used to enable programmatic
> access to objects within both the client application and other applications.

A good introduction into JavaScript is given by [w3schools](http://www.w3schools.com/JS/default.asp).

## Object Orientation ##

The JavaScript Magazine [&lt;jsmag&gt;](http://www.jsmag.com) offers many articles towards object orientation in JavaScript and how to use object literals properly, as well as design patterns for an elegant and effective use of the features of this language:

  * [JavaScript Patterns, Part 1: Literals and JSON](http://www.jsmag.com/main.issues.description/id=21/)
  * [JavaScript Patterns, Part 2: Functions](http://www.jsmag.com/main.issues.description/id=22/)
  * [JavaScript Patterns, Part 3: More Functions](http://www.jsmag.com/main.issues.description/id=24/)
  * [JavaScript Patterns, Part 4: Constructors](http://www.jsmag.com/main.issues.description/id=25/)

There are also many free resources discussing the correct object oriented use of JavaScript:
  * [Show Love to the Object Literal](http://www.wait-till-i.com/2006/02/16/show-love-to-the-object-literal/) by Chris Heilmann
  * [JSON for the Masses](http://www.dustindiaz.com/json-for-the-masses/) by Dustin Diaz
  * [JavaScript Private Public Privileged](http://www.dustindiaz.com/javascript-private-public-privileged/) discussing visibility of properties by Dustin Diaz

## Class-similar constructs ##

Classes provide a nice way to create blueprints for objects that all share the same interface. However, JavaScript does not support classes, but rather prototypes which can do much more and even more flexible. Some frameworks provide additional means that make these prototypes behave a little bit more like classes or, at least, support the creation of class-like blueprint prototypes in a way more common to, e.g., Java.

Among these frameworks is [prototype.js](http://prototypejs.org) that provides many extensions to native JavaScript (a lot of nice enumeration features, scope binding, Ajax encapsulation, etc.). **Note that Oryx uses prototype.js V1.5.1**

  * [O'Reilly's The Power of Prototype.js](http://www.oreillynet.com/xml/blog/2006/06/the_power_of_prototypejs_1.html)
  * [Developer Notes for prototype.js](http://www.sergiopereira.com/articles/prototype.js.html) (version 1.5.0)
  * An article about [class-inheritance](http://www.prototypejs.org/learn/class-inheritance) by the creators of prototype.js


## JSON ##

From [JSON.org](http://json.org):
> JSON (JavaScript Object Notation) is a lightweight data-interchange format. It is easy for humans to read and write. It is easy for machines to parse and generate. It is based on a subset of the JavaScript Programming Language ... JSON is a text format that is completely language independent but uses conventions that are familiar to programmers of the C-family of languages ... These properties make JSON an ideal data-interchange language.

A good [introduction](http://json.org/fatfree.html) is given by Douglas Crockford. http://json.org also provides a nice specification of this notation.

# Code Conventions #

We'd like to see nice, clean, and exciting code. Thus, we kindly ask to comply with the following coding conventions and guidelines.

## Code Correctness and Layout ##
[JS Lint](http://jslint.com) will hurt your feelings! An introduction into good coding style: http://www.jslint.com/lint.html

[Code Conventions](http://javascript.crockford.com/code.html), a guide to beautiful and manageable code by Douglas Crockford. We really encourage you to stick to these rules; proper use of whitespace, explicit declaration of variables, and such, really help to increase understandability and manageability of your code.

## Documentation ##
Again, JavaScript magazine provides a nice article on documentation: [Our Very Own JavaScript Documentation](http://www.jsmag.com/main.issues.description/id=24/)

Use inline comments to explain snippets of your code. Use block comments to document your code, e.g. attributes, methods, modules, etc. Think about your comments and documentation: Do they explain what a piece of code does (rather than how, e.g. "// add 2 to i" is not an appropriate comment) and can anybody, who did not write your code, understand it?

[jsdoc-toolkit](http://code.google.com/p/jsdoc-toolkit) provides a specification how to document parts of you code. Have a look at the [tag reference](http://code.google.com/p/jsdoc-toolkit/wiki/TagReference). We also encourage you to use this type of documentation, since it allows every contributor to create a documentation deck of the code.