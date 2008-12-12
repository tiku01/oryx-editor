.. _intro:

************
Introduction
************

This document describes the JInfoCard, a framework for Java-based web
application servers that enables authentication using the Information
Card system developed by Microsoft.  This chapter provides a general
overview of the history of identity management applications, followed
by a description of the Information Card technology in the chapter
:ref:`infocards`.  The chapters :ref:`tech_specs` and
:ref:`architecture` offer a detailed description of the implementation
of JInfoCard, chapter :ref:`setup-and-installation` shows how to
integrate Information Card support into your own applications and how
to deploy the "MiniShop" sample application.


History
=======

Internet users have many digital identities, each with its own
context. One digital identity on eBay, maybe another one on Amazon, an
email provider, a work station, a photo community, home banking, car
rental programs and a few more services. Anonymous surfing and reading
pages on different boards would still be possible, but anyone who
wants to use these web services to contribute to a discussion, send a
mail, do bank transactions, rent a car, post a text or a picture would
have to identify themselves by registering with a web site.  The
personal data required for registration needs to be filled out on
every web site. Until now it has been possible to handle this by using
a registered account which contains personal data provided by the user
which is protected with a username and password.  The consequence of
this, besides many passwords, is that a lot of different information
about the user such as their name, post code, birth date, bank account
and much more are stored on each web service where the user is
registered. The multiplicity of accounts and passwords that users must
keep track of and the variety of methods used for authenticating to
sites results not only in user frustration but also insecure practices
such as reusing the same account names and passwords at many sites. In
an effort to address this deficiency, numerous digital identity
systems have been introduced, each with its own strengths and
weaknesses. But no one single system meets the needs of every digital
identity scenario. As a first step towards managing all these
different problems Microsoft created .NET Passport. It was supposed to
become the standard for registration systems.


Microsoft .Net Passport
-----------------------

Microsoft .NET Passport was a "unified-login" service which was
developed as a part of the company's .NET strategy in 1999. It was based on a
Single-Sign-On (SSO) solution that allowed users to log into many web
sites with only one account. The condition was that the web site had to
support .NET Passport.  In the past, it was mostly Microsoft's own
web sites such as MS hotmail or MSN that supported .NET Passport. It
was only necessary to register with .NET Passport once in order to use
all participant web pages and services. It was not necessary to
repeatedly give dates and information to different web services.  .NET
Passport used standard Web technologies and techniques, such as Secure
Sockets Layer (SSL), HTTP redirects, cookies, JavaScript and strong
symmetric key encryption to deliver the single sign-in service.

The whole .NET Passport system was administered centrally and all
users and participating web services had to register themselves with
Microsoft. This meant that all data was stored on Microsoft
servers. This was the biggest disadvantage and the main focus of
criticism for this SSO solution project. Because of the central
structure it was quite often a target for attacks and when participant
web services become aware of these gaps in security they stopped
supporting .NET Passport. As a result, the use of .NET Passport did
not become widespread.


Microsoft CardSpace / InformationCard
-------------------------------------

In May 2005 Microsoft published MS CardSpace (formerly InfoCard) as a
successor to and replacement of MS .NET Passport in order to guarantee
an easy and secure way to administer identities.  The core idea of MS
CardSpace is to be a meta-identity system that is independent of
protocols, which further supports HTTP(S), RTSP, RTCP and makes it
possible for many different identity systems to play easily together.

CardSpace overcomes barriers like the storage of user data on external
servers by storing private data on the user’s side and offering the
option that trusted third parties could be responsible for the actual
storage of authentication data. The card itself holds only Meta
information on how to actually access private user data from an
:term:`Identity Provider` (IdP).  Identity Providers may be trusted third
parties holding a :term:`Security Token Service` (STS) or even the user
himself. An improved level of protection is reached before various
forms of identity attacks such as phishing can occur.

In particular, since the users are in possession of their own data,
they can decide which information a website will receive. Different
web services can store different data, from different cards. This is
possible because InformationCard allows them to have as many digital
identities as anyone needs.  It is comparable with different credit
cards or membership cards in the real world.  Moreover with CardSpace
it is possible to use various online services with only one user
account/card.

Design goals include seamless integration into existing browsers and
websites independent of the underlying platforms, as well as improving
specific browser issues that affect the user experience directly, like
graceful fall-back in case of missing Windows Card Space (formerly
InfoCard) support or the possibility to handle WCS with security
settings set to a high level.  The underlying architecture itself with
all its subjects is called "The Identity Metasystem".  It was first
developed by Microsoft's Identity Architect, Kim Cameron, and it is
actually more of a shared vision for solving basic identity challenges
than a Microsoft specific initiative.


JInfoCard
----------------

In order to enable Information Card support for Java-based web
application servers, Fraunhofer FOKUS developed a platform independent
plugin called JInfoCard. The project's focus was on the development
of an application module that can be easily integrated for core
functionality as well as to provide the necessary documentation to
include this functionality in the existing authentication mechanisms
so that integration can be archived with minimum effort.

With this OpenSource project JInfoCard it will be possible to simply
enhance an already existing web application by adding InformationCard
support.  The purpose of the Fraunhofer Institute's project is to
demonstrate :term:`Information Card` interoperability on heterogeneous
platforms written using Java language to support Apache Tomcat and
other web application servers.

.. comment JBoss Enterprise Application Platform and IBM® WebSphere®
   Application Server platforms running on Linux or Windows.
