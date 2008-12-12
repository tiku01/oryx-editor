*****************************
 JInfoCard – Sample MiniShop
*****************************

To give a short introduction on how this may work with an existing
application, we've implemented a sample MiniShop. This MiniShop
demonstrates :term:`Information Card` :term:`interoperability
<Interoperability>` on heterogeneous platforms using JAVA.

Scenario 1 describes how to register and login to the MiniShop without
InformationCard (classical way) and scenario 2 shows the registration
with InformationCard.

Finally, scenario 3 demonstrates how to login with InformationCard

Scenario I – Registration and Login without InformationCard
===========================================================

**Figure 16 Homepage**

**Figure 17 Registration**

Figure 17 shows an initial registration in the “classical way” or
how to register an account on the MiniShop. The personal data required
for registration needs to be filled out by the web service. After
that, an email including activation code is sent to the entered email
address.

This code is necessary to finish the registration. 


**Figure 18 Enter activation key**


The received activation key must be inscribed into the marked field.
If the code is correct, the registration is successfully finished.
Note, that on registration with InformationCard no activation code was
sent. It is only sent on registration via “classical way” .

**Figure 19 Classical login**

The user now needs his or her user id and password to enter the
MiniShop.


Scenario II – Registration with InformationCard
===============================================

**Figure 20 Registration with InformationCard**

After the extension has been installed in the MiniShop, the user can
login in "classical way" via userid / password or use InformationCard
to register.


**Figure 21 Providing InformationCards**

Figure 21 shows the open identity selector on client site. The user
decides now which InformationCard he wants to send to MiniShop to
register.


**Figure 22 Logged in**

As one can see above, the registration is complete and the user
test100 is logged in with his previously sent InformationCard. Figure
23 also shows the list of the attached InformationCards.  In this case
the InformationCard “test” was sent and attached.  Note that it is
also possible to log in on “classical way” and attach an
InformationCard there.


Scenario III – Login with Information Card
==========================================

**Figure 23 Login using InformationCard**


Figure 24 shows the extension to login with InformationCard.  Using
this, the Identity Selector pops up and provides the users
InformationCards.


**Figure 24 Logged in with InformationCard**

As one can see, the user is logged in with his attached
InformationCard.


Common Part
===========

BioShopAssertionCheckerImpl and BioShopAttachmentHandlerImpl
implements the two web site specific interfaces IAssertionschecker and
IAttachmentHandler. The information from UserDataObeject and
AttachmentData are stored in a mySQL database.

**Figure 27 Common part**


JSP/Servlet Approach
====================

The servlet approach allows the full range of HTML for sophisticated
interfaces, tight integration with other server functions and access
to other programs. The servlet approach is used to support embedding
inside the server.

Filter Approach
===============

The HTTP filter to detach an InformationCard from an account is called
by class BioShopDetachInfoCardFilter. If a user chooses an
InformationCard which is indicated with PPID to detach this, the
filter would be addressed. The filter deletes with help by RPManager
the concerned attachment.

Code snippet::

    public void doFilter(ServletRequest request, ServletResponse response,
    	FilterChain filterChain) throws IOException, ServletException {
    	String _action = request.getParameter("_action_");
    	_action = _action.toLowerCase().trim();
    	HttpSession _session = ((HttpServletRequest) request).getSession(false);
    	HttpServletResponse _response = (HttpServletResponse) response;
    	boolean _redirected = false;
    	if (_session != null) {
    		String _uid = (String) _session.getAttribute("USER_ID");
    		assert (_uid != null && _uid.length() != 0);
    		if (_action.equals("do.delete.attachment.ppid")) {
    			log.debug("this is a <<do.delete.attachment.ppid>> operation");
    			IRPManager _manager = null;
    				
    				try {
    					UserDataObject _udo = new UserDataObject();
    				assert (_udo != null);
    				_udo.setProperty("uid", _uid);
    				_manager = new RPManager();
    				_manager.unregisterInfoCardPPID(request
    					.getParameter("ppid"), _udo);
    				_redirected = true;
    
    				_response.sendRedirect("profile_cs.jsp");
    			} catch (RPException ex) {
    	// ...
    	}
    		} else if (_action.equals("do.delete.attachment")) {
    	//...
    		} else {
    	/...
    		}
    	}
    	if (!_redirected) {
    		filterChain.doFilter(request, response);
    		}
    	}
