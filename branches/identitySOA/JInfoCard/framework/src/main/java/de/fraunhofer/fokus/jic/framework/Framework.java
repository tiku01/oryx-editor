/**
 * 
 */
package de.fraunhofer.fokus.jic.framework;

import java.io.InputStream;
import java.security.PrivateKey;

import de.fraunhofer.fokus.jic.JICException;
import de.fraunhofer.fokus.jic.identity.ClaimIdentity;

/**
 * @author cht
 * 
 */
public interface Framework {
	public ClaimIdentity getID(String samlAssertion) throws JICException;

	public ClaimIdentity getID(InputStream is) throws JICException;

	public ClaimIdentity getID(org.w3c.dom.Document samlAssertion)
			throws JICException;

	/*
	public ClaimIdentity getID(org.opensaml.xml.XMLObject samlAssertion)
			throws JICException;

	public ClaimIdentity getID(org.opensaml.saml1.core.Assertion saml1Assertion)
			throws JICException;

	public ClaimIdentity getID(org.opensaml.saml2.core.Assertion saml2Assertion)
			throws JICException;
	*/

	public ClaimIdentity getID(String samlAssertion, PrivateKey pk)
			throws JICException;

	public ClaimIdentity getID(InputStream is, PrivateKey pk)
			throws JICException;

	public ClaimIdentity getID(org.w3c.dom.Document samlAssertion, PrivateKey pk)
			throws JICException;

	/*
	public ClaimIdentity getID(org.opensaml.xml.XMLObject samlAssertion,
			PrivateKey pk) throws JICException;

	public ClaimIdentity getID(
			org.opensaml.saml1.core.Assertion saml1Assertion, PrivateKey pk)
			throws JICException;

	public ClaimIdentity getID(
			org.opensaml.saml2.core.Assertion saml2Assertion, PrivateKey pk)
			throws JICException;
	*/
}
