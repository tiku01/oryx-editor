*******************
 Sequence Diagrams
*******************

Building on the user scenarios covering in the chapter 2.2 the
following set of scenarios in the form of sequence diagrams will be
introduced here:

* Attaching an :term:`information card <Information Card>` to the user
  account

* Detaching an attached information card from the user account

* Listing all attached information cards

* Login to the system by using an attached information card

* Register with the system with a new card


Attaching a Card
================

In order to use an information card to login to the system one has to
attach it to the account first. Alternatively a new account could be
created by using an information card plus some extra information, but
in the most cases it would be better to just stick with the card’s
existing account. There are two possibilities:

* Possibility one based on the HTTPS protocol (see Figure 6)
* Possibility two based on the HTTP protocol (see Figure 7)

**Figure 8 InfoCard attachment over https protocol**

**Figure 9 InfoCard attachment over http protocol**


Detaching a Card
================

In order to remove an existing link between an InformationCard and a
user account, an inverse operation has to be implemented – the
detachment of an InformationCard from an account. A particular account
is allowed to have more than one InformationCard, e.g. one on a
desktop and one a laptop or mobile device, therefore a detachment of a
card doesn’t automatically imply the deletion of the user account,
moreover this is a web site specific issue, which should be solved in
the page specific part of the framework implementation (see the two
customizable interfaces).

Similar to the previous operation the detachment could be done using
two different:

* Via HTTPS protocol (see Figure 8)
* Via HTTP protocol (see Figure 9)


**Figure 10 InformationCard detachment over HTTPS protocol**


**Figure 11 InformationCard detachment over HTTP protocol**


Performing a Detachment
=======================

There are two possible ways perform a detachment:

The first one is based on the same mechanism as the attachment of the
information card. The user is required to send an :term:`SAML` token
by choosing an information card, but instead of being registered
(attached) the card will be unregistered (detached). Of course this
would imply it is possibility to choose the wrong (unattached)
information card. On the other hand this is a very natural way to use
the information card. (you can try it out on the Mini shop site by
pressing the cross-stroke information card icon on the “my profile”
page)

The second way to do this is to list the attachments on the server
(:term:`relying party <Relying Party>`) and present them to the user. Unfortunately only one
set of items can be presented to the user (defined SAML token claims)
and only one of them (PPID = private personal identifier) 1is a unique
one, the rest of them such as a given name, surname, email address
etc. can/would/will be the same for most information cards. (You can
also find this functionality- only PPID is presented- on the Mini shop
site, under "my profile")


Login
=====

To login to a web site, the user connects to the relying party (RP)
with his browser. The relying party returns its policy to the local
computer. It states which :term:`claims <Claims>` need to be supplied.

On account of this, the Identity Selector will open and present a list
of cards outlining these claims. At this point the user simply selects
one of the cards.

The Identity Selector connects to the Identity Provider, the Security
Token Service and then to the client itself and receives a security
token which it sends back to the Relying Party.

The Relying Party validates the token and grants access to the web
service.


**Figure 12 Login with InformationCard over HTTPS**

**Figure 13 Login with InformationCard over HTTP**



Listing Attached Cards
======================

The system provides user`s discontinued attached InformationsCards for login.
Only the InformationCards, which meet all the web sites requirement are shown.
The user simply selects their InformationCard and then their chosen identity. 

It is possible that the user has severally identities on the web site
and consequently more than one attached InformationCard.


**Figure 14 List InformationCard attachments**


Functionality in MiniShop
=========================

**Figure 15 Implementation of framework functionality in MiniShop**


The implementation of the two main web page specific interfaces:

* IAttachmentHandler – responsible for the creating, removing and
  listing of the attachments
* IAssertionChecker – responsible for the validation of the incoming
  assertions against the stored attachment data

The framework has been plugged in to the Mini shop in two different ways:

Code snippet to integrate InformationCard Identity Selector on login site::

	<object type="application/x-informationCard" name="xmltoken">
		<param name="tokenType" value="urn:oasis:names:tc:SAML:1.0:assertion">
		<param name="requiredClaims"
		       value="http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier
		              http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname
			      http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname
 			      http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress" />
		<param name="optionalClaims"
	      	       value="http://schemas.xmlsoap.org/ws/2005/05/identity/claims/streetaddress
		              http://schemas.xmlsoap.org/ws/2005/05/identity/claims/locality
			      http://schemas.xmlsoap.org/ws/2005/05/identity/claims/stateorprovince
			      http://schemas.xmlsoap.org/ws/2005/05/identity/claims/postalcode
			      http://schemas.xmlsoap.org/ws/2005/05/identity/claims/country
			      http://schemas.xmlsoap.org/ws/2005/05/identity/claims/homephone
 			      http://schemas.xmlsoap.org/ws/2005/05/identity/claims/otherphone
 			      http://schemas.xmlsoap.org/ws/2005/05/identity/claims/mobilephone
 			      http://schemas.xmlsoap.org/ws/2005/05/identity/claims/dateofbirth
			      http://schemas.xmlsoap.org/ws/2005/05/identity/claims/gender" />
	</object>
	<input name="send"
	       type="image"
	       value="CARDSPACE"
	       src="images/informationcard.gif"
	       alt="Please choose an information card in order to login."
	       align="absmiddle" />

Inline code in the existing jsp, as an example excerpt from the
login.do.cs.jsp::

	{
		String xmltoken = request.getParameter("xmltoken");
		if (xmltoken == null || xmltoken.length() == 0) {
			// the posting has been cancelled
			response.sendRedirect("login_cs.jsp");
		}
		try {
			System.out.println("calling the RPMAnager ...");
			UserDataObject udo = null;
			IRPManager rpManager = new RPManager();
			udo = rpManager.login(xmltoken);
			String uid = udo.getProperty("uid");
			session.setAttribute("USER_ID", uid);
			session.setAttribute("USER_STATUS", "LOGGED_IN");
			response.sendRedirect("home.jsp");
		} catch (RPException ex) {
			ex.printStackTrace();
			session.setAttribute("ERR_CODE",									String.valueOf(ex.getErrno()));
			session.setAttribute("ERR_TEXT", ex.toString());
			session.setAttribute("ERR_LINK_TITLE", "home");
			session.setAttribute("ERR_LINK","home.jsp");
			response.sendRedirect("error.jsp");
	}

Outsourcing of the code into an HTTP filter –
MinishopDetachInformationCardFilter, which is handling all requests,
related to the operation “detach information card”



Web Application SAML Token
==========================

SAML defines a common XML framework for exchanging security :term:`assertions <Assertion>`
between entities.

In the InformationCard framework, there are exactly three different
claims requested:

* Givenname
* Surname
* Emailaddress

::

	<param name="requiredClaims"
	       value="http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier
	       http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname
	       http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname
	       http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress" />

These claims are necessary to register or attach an InformationCard
with the MiniShop.

But note, the SAML does not intend the admission of certain data,
these must be differently questioned.

