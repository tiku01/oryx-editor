# Introduction #

We use a relational database (Postgres 8.3) for storing data. The schema is very generic and enables tree structured data and axes as known in xpath.


# Details #

## The database schema ##

![http://oryx-editor.googlecode.com/svn/wiki/ServerDoc/DB-Schema.png](http://oryx-editor.googlecode.com/svn/wiki/ServerDoc/DB-Schema.png)

### Identity ###
Every non-static item in the database has a unique identifier, the usecase is a web application so it is an uri. For easy use of references in the database, there is an ID for every uri. In case of users the uri is the users OpenID. Models get an uri created by the system.

### Representation ###
For every model a representation is saved. This representation contains title(text), type(text), summary(text), content(text), svg(text), updated(date) and created(date).
The content is saved as eRDF same as used in client. The SVG is saved for a graphical representation. It is a representation of the Oryx-Canvas and enables server-sided export of image formats(pdf,png) without knowledge about stencil sets and shapes.

### Strucuture ###
Structure is used for describing the tree structures in the relational database. Every identity has exactly one position in the tree, definied by a string. The position encoding used for calculating this string guarantees string sortability. So functionality of xpath-axes is enabled with simple string operations.

### Interaction ###
Interaction entities are used for representing access rights. Subject is a user, Object is a model. The Term is a value of {read,write,owner} and defines the set of operations the subject is allowed to do in context of the object. read=(read), write=(read,update), owner=(read,update,insert,delete). An insert in context of an model means to create a new interaction entity with this model as the object.
![http://oryx-editor.googlecode.com/svn/wiki/ServerDoc/Acces-Rights.png](http://oryx-editor.googlecode.com/svn/wiki/ServerDoc/Acces-Rights.png)

subject\_descend: True if the interaction should be inherited by the subtree of the subject.

object\_descend: True if the interaction should be inherited by the subtree of the object.

object\_self: True if there should be an access right for the object. Object\_self=false only makes sense with object\_descend=true.

object\_restrict\_to\_parent: Object\_restrict\_to\_parent only make sense, if object\_descend=true. If object\_restrict\_to\_parent is true, then the access right effects all descending objects (users) in the tree structure. But one speciﬁc subject can only access objects which are direct child elements in the tree.

Plugin