/**
 * 
 */
package de.fraunhofer.fokus.jic.filter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.fokus.jic.JICException;
import de.fraunhofer.fokus.jic.framework.Framework;
import de.fraunhofer.fokus.jic.framework.impl.FrameworkImpl;
import de.fraunhofer.fokus.jic.identity.ClaimIdentity;

/**
 * the central part of the JInformationCard framework.
 * 
 * In order to work, it needs to be configured in the web applications
 * <code>web.xml</code> as described in the documentation of the 
 * {@link #init(FilterConfig)} method.
 * 
 * @author cht
 * @see #init(FilterConfig)
 * 
 */
public class JICFilter implements Filter {

	private final Logger log = LoggerFactory.getLogger(JICFilter.class);
	private String requestParameterName = null;
	private String claimIdRequestAttr = null;
	private Framework framework = null;
	private PrivateKey privateKey = null;
	
	/**
	 * Initializes JICFilter.
	 * 
	 * The following filter init-params should be set up in web.xml:
	 * 
	 * <table border="1">
	 * <tr>
	 * <th>Parameter name</th>
	 * <th>Description</th>
	 * <th>Default value</th>
	 * </tr>
	 * <tr>
	 * <td>request_param_name</td>
	 * <td>The request parameter name containing the SAML assertion sent by the Client</td>
	 * <td>xmltoken</td>
	 * </tr>
	 * <tr>
	 * <td>userid_request_attr</td>
	 * <td>the name of the (Servlet-)request attribute in which the returned ClaimIdentity object is placed</td>
	 * <td>userdata</td>
	 * </tr>
	 * </table>
	 * 
	 * In order for SSL support to be enabled, the following settings
	 * must be supplied as well:
	 * 
	 * <table border="1">
	 * <tr>
	 * <th>Parameter name</th>
	 * <th>Description</th>
	 * <th>Default value</th>
	 * </tr>
	 * <tr>
	 * <td>keystore_file</td>
	 * <td>path to the keystore file to be used.</td>
	 * <td>none</td>
	 * </tr>
	 * <tr>
	 * <td>keystore_alias</td>
	 * <td>Alias of the certificate within the keystore.</td>
	 * <td>none</td>
	 * </tr>
	 * <tr>
	 * <td>keystore_pswd</td>
	 * <td>password for the keystore.</td>
	 * <td>none</td>
	 * </tr>
	 * <tr>
	 * <td>private_key_pswd</td>
	 * <td>password for the private key.</td>
	 * <td>the value of keystore_pswd.</td>
	 * </tr>
	 * <tr>
	 * <td>keystore_type</td>
	 * <td>Type of the keystore used. Must be either "JKS" or "PKCS12"</td>
	 * <td>JKS</td>
	 * </tr>
	 * </table>
	 * 
	 * @param config the filter configuration
	 * @throws ServletException
	 */
	public void init(FilterConfig config) throws ServletException {
		log.debug("JICFilter.init start");
		
		requestParameterName = getInitParam(config,
				"request_param_name", "xmltoken");
		claimIdRequestAttr = getInitParam(config,
				"userid_request_attr", "userdata");
		
		privateKey = getPrivateKey(config);
			
		try {
			framework = new FrameworkImpl();
		} catch (JICException e) {
			log.error("error initializing JIC Framework", e);
			throw new ServletException("error initializing JIC Framework", e);
		}
		
		log.debug("JICFilter.init done.");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	/**
	 * processes the SAML token recieved from the client.
	 * 
	 * The token is extracted from the servlet request parameter specified in the
	 * webapp's configuration. If processing succeeds, an instance of
	 * {@link ClaimIdentity} is placed in the session attribute configured as described
	 * in {@link #init(FilterConfig)}.
	 * 
	 * @param req the servlet request
	 * @param resp the servlet response
	 * @throws IOException if IO problems occur
	 * @throws ServletException if an error occurs while processing the SAML token
	 */
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		
		log.debug("called JICFilter.doFilter");
		System.out.println("JICFilter in action!!!");
		// make sure this is a HttpServlet.
		HttpServletRequest request = null;
		HttpServletResponse response = null;
		try {
			request = (HttpServletRequest) req;
			response = (HttpServletResponse) resp;
		} catch (ClassCastException cce) {
			log.info("This is not an HttpServlet, exiting.", cce);
			chain.doFilter(req, resp);
			return;
		}
		
		String samlToken = request.getParameter(requestParameterName);
		System.out.println("JIC: " + samlToken);
//		if(samlToken == null || samlToken.length() == 0) {
//			// TODO: alternative: sendError(30x)?
//			log.info("no SAML token supplied, doing nothing.");
//			//response.sendRedirect(request.getHeader("referer"));
//			chain.doFilter(req, resp);
//		}

		ClaimIdentity _id = null;
		
		if(samlToken != null && samlToken.length() > 0) {
			try {
				_id = framework.getID(samlToken, privateKey);
			} catch (JICException jice) {
				// chain.doFilter(req, resp);
				log.info("error parsing SAML token", jice);
				//throw new ServletException("error parsing SAML token", jice);
				/* 
				 * FIXME: find best way to handle exception.
				 * alternatives:
				 * response.sendRedirect(request.getHeader("referer"));
				 * return?
				 */
			}
		}

		req.setAttribute(claimIdRequestAttr, _id);
		
		log.debug("JICFilter.doFilter done.");
		chain.doFilter(req, resp); // give other filters a chance to do their thing.
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		log.debug("called JICFilter.destroy.");
	}

	private String getInitParam(FilterConfig config, String paramName, String defaultValue) {
		String res = config.getInitParameter(paramName);
		if(res == null || res.length() == 0) {
			log.warn(String.format(
					"%s not set in web.xml, using \"%s\".",
					paramName, defaultValue));
			res = defaultValue;
		}
		log.debug("returning value [" + res + "] for parameter [" + paramName + "]");
		return res;
	}
	
	private PrivateKey getPrivateKey(FilterConfig config) {

		PrivateKey res = null;
		
		// SSL-related setup
		String _keystoreName = getInitParam(config, "keystore_file", null);
		String _keystoreAlias = getInitParam(config, "keystore_alias", null);
		String _keystorePswd = getInitParam(config, "keystore_pswd", null);
		String _privateKeyPswd = getInitParam(config, "private_key_pswd", _keystorePswd);
		String _keystoreType = getInitParam(config, "keystore_type", "JKS");
		if( _keystoreName != null &&
			_keystorePswd != null &&
			_privateKeyPswd != null &&
			_keystoreAlias != null) {
			try {
				log.debug("loading private key...");
				KeyStore _keystore = KeyStore.getInstance(_keystoreType);
				// InputStream _is = new FileInputStream(_keystoreName);
				InputStream _is = getClass().getResourceAsStream(_keystoreName);
				// InputStream _is = config.getServletContext().getResourceAsStream(_keystoreName);
				if(_is == null) {
					log.error("private key not found: " + config.getServletContext().getResource(_keystoreName));
					return null;
				}
				_keystore.load(_is, _keystorePswd.toCharArray());
				_is.close();
				if(log.isDebugEnabled()) {
					log.debug("listing keystore's aliases:");
					for(Enumeration<String> e = _keystore.aliases(); e.hasMoreElements();) {
						log.debug(e.nextElement());
					}
				}
				res = (PrivateKey) _keystore.getKey(_keystoreAlias, _privateKeyPswd.toCharArray());
				log.debug("private key loaded.");
			} catch (KeyStoreException kse) {
				log.error("error loading keystore: " + _keystoreName, kse);
			} catch (FileNotFoundException fnfe) {
				log.error("File not found: " + _keystoreName, fnfe);
			} catch (NoSuchAlgorithmException nsae) {
				log.error("bad algorithm loading key. Check if BouncyCastle jar is in endorsed dir.", nsae);
			} catch (CertificateException ce) {
				log.error("the algorithm used to check the integrity of the keystore cannot be found", ce);
			} catch (IOException ioe) {
				log.error("keystore IO problem.", ioe);
			} catch (UnrecoverableKeyException uke) {
				log.error("cannot recover key", uke);
			}
		} else {
			log.info("keystore_file, keystore_alias or keystore_pswd not supplied, SSL will NOT be supported!");
		}
		return res;
	}

}
