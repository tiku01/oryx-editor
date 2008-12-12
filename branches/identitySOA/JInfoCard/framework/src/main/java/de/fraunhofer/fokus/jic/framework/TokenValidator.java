package de.fraunhofer.fokus.jic.framework;

import org.joda.time.DateTime;
import org.opensaml.xml.signature.Signature;

import de.fraunhofer.fokus.jic.JICException;

public interface TokenValidator {

	/**
	 * validates the signature on the SAML token
	 * @param signature the signature to validate
	 * @throws JICException if validation failed
	 */
	public void validateSignature(Signature signature) throws JICException;
	
	/**
	 * validates the lifetime of a SAML token, possibly accounting for 
	 * clock differences between the STS / client computer.
	 *  
	 * @param notBefore the time before which a token should be considered invalid.
	 * @param notOnOrAfter the time after which a token should be considered invalid.
	 * @throws JICException if the token is expired or was sent from the future.
	 */
	public void validateTokenLifetime(DateTime notBefore, DateTime notOnOrAfter) throws JICException;
}