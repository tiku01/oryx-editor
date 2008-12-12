***********************
 Technical Description
***********************

It is important to know what JInfoCard does and how it does it.

However, this is especially important for web administrators who want
to use this Java extension and it is also important to know how it is
implemented and how the framework is structured.  This leads us to a
description of the JInfoCard framework architecture, the
structure of relaying party core main classes and to the customized
data structure.


Architecture
============

The framework was designed so that it should be possible for any web
service to adopt the InformationCard functionalites. The condition
is that the web service uses a Java based application server as
runtime environment. Because of this the focus was on this product
during the architecture’s development. One can infer from
illustration 1, that the JInfoCard framework is easy to
integrate into fully comprised environments without much effort.

There are two different ways to integrate the framework: Direct
integration into comprised JSPs and/or Servlets Indirect integration,
by using HTTP-Filter Technology to handle the peg queries and send
them to the suitable target (redirection)

**Figure 3 Top level architecture**

The :term:`Relying Party` Core exists in the essentials of the following
interfaces:

IRPManager builds the business-interface of the frameworks; all
communication with the framework happens over this interface; at the
moment only direct Java-inproc-communication is supported.

The business interface is based basically on two other interfaces:
ISAMLTokenHandler and ISAMLTokenDecrypter, used to interpret a
:term:`SAML`-:term:`Token` and verify/treat the information within and
decrypt the SAML token.

Furthermore there are two interfaces which should implemented in a
web-site-specific manner in order to realize their proprietary
functionality:

IAttachmentHandler and IAssertionsChecker

**Figure 4 Relying Party Core - main classes**


Information Model
-----------------

Two main data structures are of most interest to the user:

* UserDataObject
* AttachmentData

**Figure 5 Customizable data structures**

Both of them are customizable containers allowing web site/solution
specific data to pass through the framework or to be taken from the
framework.

The UserDataObject contains a property object which allows a set of
name-value-pairs to be stored according to that particular web page's
requirements.

The AttachmentData contains data characterizing a particular
attachment of an :term:`information card <Information Card>` and user
account, e.g. ppid, e-mail, first names, last names etc.
