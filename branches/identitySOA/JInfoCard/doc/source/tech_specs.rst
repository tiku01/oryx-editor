.. highlight:: jsp

.. _tech_specs:

*************************
 Technical Specification
*************************

The main goal of the design of the JInfoCard framework has been the
integration of the framework into currently existent web sites -- it
should be done very easily and, without the need to make huge changes
to the site. This chapter shows how this goal was (hopefully)
achieved.


Technologies used by JInfoCard
==============================

The following sections give a quick overview of the technologies used
in JInfoCard.  The section :ref:`tech-servlet-filter` gives a short
introduction to servlet filters, :ref:`tech-jsp` and :ref:`tech-el` 
describe JSPs and EL, respectively.

.. _tech-jsp:

Java Server Pages
-----------------

Java Server Pages (JSP) and Servlets allow dynamic web pages to be
generated in order to answer to client`s web inquiry. Servlets are
compiled Java classes and they can be used as a substitute with CGI
programs. JSPs are text-based documents, consisting of static html and
Java source code. It is used to describe of dynamic behaviour and to
generate dynamic contents.

JSP is firstly accessible via java Servlet source code transfers and
is compiled afterwards.

The configuration file for JSP/Servlets is called Web.xml and is used
to tell the Web server where the various parts of the application can
be found, how to access them and, sometimes, who can access them.


.. _tech-el:

Expression Language
-------------------

The JSP Expression Language (EL) allows for increased separation of
concerns when designing Java-based web applications. A JSP using EL is
essentially a regular HTML file which only serves as a *view* of the
data provided by the back-end layers of the application.  It is
designed to be easy to manipulate by non-developers, i.e. web
designers.  This goal is achieved by removing all non presentation
related code from the JSP and instead including expressions of the
following form::
  
  ${firstName}

For details on the syntax of these expressions and how they are
resolved, please refer to [EL]_.

.. [EL] Sun Microsystems, Inc.: *"JavaServer Pages (TM) v2.0  Syntax
   Reference"*.
   Available from http://java.sun.com/products/jsp/syntax/2.0/syntaxref20.html

.. _tech-servlet-filter:

Servlet-Filter Framework
------------------------

A Filter is a new component type on Java Servlet specification version
2.3 [JSR053]_.  This filter dynamically intercepts requests and responses in
order to transform or use the information contained in the requests or
responses. Universal functions are provided which can be attached to
any kind of Servlets or JSP page. Filters do not create responses
themselves.

What is important here is that filters provide the ability to
encapsulate recurring tasks in reusable units. To transform a response
from a Servlet or a JSP page the filter can also be used.

Furthermore developers have the opportunity to write transformation
components that have been portable across containers since the
introduction of the filter.

Other types of functions are encryption, tokenizing, triggering
resource access events, logging and auditing, image conversion, data
compression, XSL/T transformations of XML content, mime-type chaining,
and caching.

The reusable JInfoCard framework component, referred to as "the
Framework" in the following sections, is implemented as a Servlet
Filter.  It is configured in a web application's ``web.xml``
configuration file and can be used to protect certain parts of a web
application from unauthorized access with very few changes to the
application's source code.

.. [JSR053] Sun Microsystem, Inc.  *"Java (TM) Servlet 2.3 and
   JavaServer Pages (TM) 1.2 Specifications"*.
   Available from http://jcp.org/aboutJava/communityprocess/final/jsr053/


JInfoCard Framework Implementation
==================================

The JInfoCard Framework is structured into three sub-packages of
``de.fraunhofer.fokus``. In general, they contain Java interfaces
which are implemented in the corresponding sub-sub-package ``impl``
(i.e. ``jic.filter.Framework`` is implemented in
``jic.filter.impl.FrameworkImpl``):

``jic``
  This package contains common classes used by the other
  packages. Currently this is only the class ``JICException``, the
  exception which is raised by the framework when an error occurs.

``jic.filter`` 
  This package contains the class ``JICFilter``, which implements the
  Servlet Filter interface. It retrieves it's configuration from the
  web application's ``web.xml``. When a user accesses a resource it is
  configured to protect, the filter intercepts the request and
  extracts the identity information (SAML assertion) from it.  JICFilter
  then uses the method ``Framework.getID()`` to process the token and
  places the returned ``ClaimIdentity`` object into the Session from
  where it can be retrieved by the web application as described in
  the section :ref:`displaying-claims`.

  If the identity information the user submitted is invalid or an
  error occurs when processing it, a ``ServletException`` is raised.
  The web application can catch the exception or redirect the user to
  an error page.

``jic.framework``
  The interface ``Framework`` in this package defines a method
  ``getID`` which is overloaded to accept a SAML token in a variety of
  forms - as a ``String``, an ``InputStream`` or a Document Object
  Model (DOM) ``Document`` (see [DOM3]_ and [DOM3Java]_ for details).
  It decrypts the assertion, if necessary, and validates the signature
  on it before processing it into a ``ClaimIdentity`` which can be
  used by the web application to determine the user's identity.

  The method optionally takes a ``PrivateKey`` as a parameter, which
  is used to decrypt the SAML assertion when a secure connection via
  SSL is used.

  The interface ``TokenValidator`` is intended as a future extension
  point which may allow client code to perform custom validation of
  the SAML assertion.

  .. note::

    The core Framework, composed of the interface
    ``jic.framework.Framework`` and it's implementation
    ``jic.framework.impl.FrameworkImpl`` have no connection to Java
    Servlet technology and thus can be reused even outside a web
    application context.

``jic.identity``
  This package contains the ``ClaimIdentity`` which is returned by
  ``Framework.getID()``.  It is essentially a subclass of
  ``java.util.Map<String, Claim>``, however the method ``get()`` is
  overridden to provide a range of "convenience accessors" for the
  standard claims defined in [ISIPv15]_.  Please see the method's
  documentation for the exact list.

  The Class ``Claim`` is a regular JavaBean which holds the claim type
  and -value, as well as a reference to the ClaimIdentity of which it
  is a part, for a single claim.



.. [DOM3] Arnaud Le Hors and Philippe Le Hégaret: *"W3C Document Object
   Model Level 3 Core"* (W3C Recommentation).
   Available from
   http://www.w3.org/TR/2004/REC-DOM-Level-3-Core-20040407/

.. [DOM3Java] Arnaud Le Hors and Philippe Le Hégaret: *"W3C Document Object
   Model Level 3 Core"* (W3C Recommentation), Appendix G: "Java Bindings".
   Available from http://www.w3.org/TR/2004/REC-DOM-Level-3-Core-20040407/java-binding.html
