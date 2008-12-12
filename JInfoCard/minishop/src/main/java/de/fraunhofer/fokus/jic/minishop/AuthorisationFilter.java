/**
 * 
 */
package de.fraunhofer.fokus.jic.minishop;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.fokus.jic.identity.ClaimIdentity;
import de.fraunhofer.fokus.jic.identity.ClaimUris;
import de.fraunhofer.fokus.jic.identity.impl.IdentityUtil;

/**
 * @author cht
 *
 */
public class AuthorisationFilter implements Filter {
	
	private static final Logger log = LoggerFactory.getLogger(AuthorisationFilter.class);
	private String errorPage = null;
	private String claimIdRequestAttr = null;

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig config) throws ServletException {
		log.debug("called AuthorisationFilter.init.");
		
		errorPage = config.getInitParameter("error_page");
		if(errorPage == null || errorPage.length() == 0) {
			log.warn("error_page not configured in web.xml, using \"error.jsp\".");
			errorPage = "error.jsp";
		}
		
		claimIdRequestAttr = config.getInitParameter("userdata_request_attr");
		if(claimIdRequestAttr == null || claimIdRequestAttr.length() == 0) {
			log.warn("userdata_request_attr not configured in web.xml, using \"userdata\".");
			claimIdRequestAttr = "userdata";
		}
		log.debug("AuthorisationFilter.init done.");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		
		log.debug("called AuthorisationFilter.doFilter");
		
		// make sure this is a HttpServlet.
		HttpServletRequest request = null;
		// HttpServletResponse response = null;
		try {
			request = (HttpServletRequest) req;
			// response = (HttpServletResponse) resp;
		} catch (ClassCastException cce) {
			log.info("This is not an HTTPServlet, exiting.", cce);
			chain.doFilter(req, resp);
			return;
		}
		
		 
		ClaimIdentity _id = (ClaimIdentity) req.getAttribute(claimIdRequestAttr);
		// check if ID supplied
		if(_id == null) {
			log.info("no ClaimIdentity found, rejecting request.");
			sendError(req, resp, "no ClaimIdentity found, rejecting request.");
			return;
		}
		
		HttpSession _session = request.getSession();
		
		// check if ID contains required claims
		String[] requiredClaimURIs = {
				CustomClaimUris.GIVEN_NAME,
				CustomClaimUris.SURNAME,
				CustomClaimUris.EMAIL_ADDRESS,
				CustomClaimUris.PPID
		};
		if(!allClaimsSupplied(_id, requiredClaimURIs)) {
			sendError(req, resp, "a required claim was not supplied");
			return;
		}
		log.debug("session var for userdata: [" + claimIdRequestAttr + "].");
		_session.setAttribute(claimIdRequestAttr, _id);
		
		_session.setAttribute(
				"friendlyid",
				IdentityUtil.friendlyIdentifier(_id.get(ClaimUris.PPID).get(0).toString()));
		
		// check for "of-full-age" claim.
		String[] optionalClaimURIs = {
				CustomClaimUris.OF_FULL_AGE
		};		
		if(allClaimsSupplied(_id, optionalClaimURIs)) {
			log.info("found \"of-full-age\" claim!");
			_session.setAttribute("offullage", true);
		} else {
			log.info("\"of-full-age\" claim not supplied.");
			_session.setAttribute("offullage", false);
		}
		
		log.debug("AuthorisationFilter.doFilter done.");
		chain.doFilter(req, resp);
	}

	private void sendError(ServletRequest req, ServletResponse resp, String message) 
	throws ServletException, IOException {
		req.setAttribute("error", message);
		req.getRequestDispatcher(errorPage).forward(req, resp);
	}
	
	private boolean allClaimsSupplied(ClaimIdentity id, String[] claims) {
		return id.keySet().containsAll(new HashSet<String>(Arrays.asList(claims)));
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		log.debug("called AuthorisationFilter.destroy.");
	}
	
	/**
	 * sample (inner) class showing how to extend the range of expected Claims.
	 */
	class CustomClaimUris extends ClaimUris {
		public static final String OF_FULL_AGE = "http://elan.fokus.fraunhofer.de/claim/offullage";
		public String getOfFullAge() { return OF_FULL_AGE; }
	}
}
