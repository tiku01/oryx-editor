.. _architecture:

************************
 JInfoCard Architecture
************************



Overview
========

The JInfoCard Framework is composed of two distinct components,
the Framework which can be integrated into third-party web
applications and a sample web application, called MiniShop, which
shows how such an integration is possible.  The components are
packaged as seperate projects and can be used individually as
described in the installation and setup instructions below.


The JInfoCard Framework
=======================

.. highlight:: xml

The Framework is JInfoCard's central component.  It transforms the
SAML token sent by the user into a ClaimIdentity object containing the
user's identity attributes.

This functionality is realized in the class ``FrameworkImpl`` which
implements the interface ``Framework``, found in the Java package
``de.fraunhofer.fokus.jic.framework.impl``.  The method ``getID`` is
overloaded to accept the SAML input in a variety of formats, ranging
from a plain text serialisation to an already parsed DOM document.  If
a private key is supplied, the JInfoCard Framework will attempt to
decrypt the token.  This is necessary if the relying party can be
accessed over an SSL connection, because in this case the client will
encrypt the token before it is sent.

In order to insure that the token cannot be modified or otherwise
misused by a third party, it contains a digital signature and a
validity period, both of which are verified by the JInfoCard
Framework.

Third party web applications can easily integrate the framework by
configuring the Servlet filter
``de.fraunhofer.fokus.jic.filter.JICFilter`` in their ``web.xml``
configuration file as follows::

  <filter>
    <filter-name>JICFilter</filter-name>
    <filter-class>
      de.fraunhofer.fokus.jic.filter.JICFilter
    </filter-class>
    <init-param>
      <!--
      the name of the HTTP request parameter containing the SAML token
      supplied by the user.
      -->
      <param-name>request_param_name</param-name>
      <param-value>xmltoken</param-value>
    </init-param>
    <init-param>
      <!--
      The name of the Session attribute into which the resulting
      ClaimIdentity object should be placed.
      -->
      <param-name>userid_request_attr</param-name>
      <param-value>userdata</param-value>
    </init-param>
  
    <!--
    SSL setup.
    When deploying an HTTP-only (noSSL) relying party,
    you can omit these settings. 
    -->
    <init-param>
      <!--
      the path should be relative to the WEB-INF/classes
      directory.
      -->
      <param-name>keystore_file</param-name>
      <param-value>/path/to/keystore.p12</param-value>
    </init-param>
    <init-param>
      <param-name>keystore_alias</param-name>
      <param-value>tomcat</param-value>
    </init-param>
    <init-param>
      <param-name>keystore_pswd</param-name>
      <param-value>s3cr3t</param-value>
    </init-param>
    <init-param>
      <param-name>private_key_pswd</param-name>
      <param-value>3v3n_m0r3_s3cr3t</param-value>
    </init-param>
    <init-param>
      <!-- can be PKCS12 or JKS -->
      <param-name>keystore_type</param-name>
      <param-value>PKCS12</param-value>
    </init-param>
  </filter>
  <filter-mapping>
    <!--
    specify the URLs or servlets to which the filter should be
    applied.
    -->  
    <filter-name>JICFilter</filter-name>
    <url-pattern>/shop.jsp</url-pattern>
  </filter-mapping>
  <error-page>
    <!--
    the Servlet or JSP to use if an invalid token is detected
    by the JInfoCard Framework.
    -->
    <exception-type>javax.servlet.ServletException</exception-type>
    <location>/error.jsp</location>
  </error-page>


The MiniShop Sample Web Application
===================================

.. highlight:: xml

The sample webapp demonstrates an easy way to integrate Information
Cards into a web application.  It is implemented with Java Server
Pages (JSPs) and makes use of the ``JICFilter`` Framework component
described above.  Please refer to the chapter
:ref:`setup-and-installation` for detailed instructions on how to
setup and deploy the sample web application.

Overview
--------

The file ``index.jsp`` contains the ``<object>`` and ``<form>`` tags
used to invoke the Identity Selector on the user's computer.  When the
user submits his or her digital identity to the MiniShop
(``shop.jsp``), the JICFilter is invoked, thus causing the SAML
assertion to be processed.  The resulting ``ClaimIdentity`` is placed
into the Session attribute "userdata".

The MiniShop makes use of a custom claim (which is supplied by a trusted
Identity Provider) to determine wether a user is
of full age.  This claim is processed in a second Servlet Filter,
``AuthorisationFilter`` (in the Java Package
``de.fraunhofer.fokus.minishop``), which extracts the value of said
claim and adds additional attributes to the Session which the JSP
``shop.jsp`` then uses to determine which articles should be displayed
to the user.  The AuthorisationFilter is configured as follows::

  <filter>
    <filter-name>AuthorisationFilter</filter-name>
    <filter-class>
      de.fraunhofer.fokus.jic.minishop.AuthorisationFilter
    </filter-class>
    <init-param>
      <param-name>error_page</param-name>
      <param-value>error.jsp</param-value>
    </init-param>
    <init-param>
      <param-name>userid_request_attr</param-name>
      <param-value>userdata</param-value>
    </init-param>
  </filter>
  <filter-mapping>
    <filter-name>AuthorisationFilter</filter-name>
    <url-pattern>/shop.jsp</url-pattern>
  </filter-mapping>


.. _displaying-claims:

Displaying Claims in Java Server Pages (JSPs)
---------------------------------------------

.. highlight: jsp

After the processing has taken place, the Java Server Page
``shop.jsp`` can now use standard Expression Language (EL) markup to
display the claims that the user sent::

  <li>First Name: ${userdata.firstName[0]}</li>
  <li>Last Name: ${userdata.lastName[0]}</li>
  <li>Email: ${userdata.email[0]}</li>

.. note::

  Since each claim may occur more than once, array notation is used to
  access the first claim in the userdata session variable.

The class ``ClaimUris`` defines standard JavaBean accessor methods for
the claim types defined in [ISIPv15]_, so that the following syntax
may be used instead after the class is imported into the JSP::
  
  <jsp:useBean
    id="uris"
    class="de.fraunhofer.fokus.jic.identity.ClaimUris"
    scope="page" />
    
  [...]
  
  <li>Email: ${userdata[uris.emailAddress][0]}</li>

Custom, site-specific claims can be accessed by using the following
alternative notation::

  <li>
  PPID:
  ${userdata["http://example.com/cusomclaims/mycustomclaimtype"][0]}
  </li>


.. [ISIPv15] A. Nanda and M. Jones: *"Identity Selector
   Interoperability Profile V1.5 and companion guides"*. Available from
   http://www.microsoft.com/downloads/details.aspx?FamilyID=b94817fc-3991-4dd0-8e85-b73e626f6764
