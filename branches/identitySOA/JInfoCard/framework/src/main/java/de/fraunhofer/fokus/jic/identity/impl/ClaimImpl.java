package de.fraunhofer.fokus.jic.identity.impl;

import java.io.Serializable;

import de.fraunhofer.fokus.jic.identity.Claim;
import de.fraunhofer.fokus.jic.identity.ClaimIdentity;

public class ClaimImpl implements Claim, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8578602476766749779L;
	private ClaimIdentity issuer, subject;
	// private final Logger log = LoggerFactory.getLogger(ClaimImpl.class);
	private String type, value;
	
	public ClaimImpl(
			ClaimIdentity issuer, ClaimIdentity subject,
			String type, String value) {
		/*
		if(log.isDebugEnabled()) {
			log.debug("creating new claim.");
			log.debug("issuer: " + issuer);
			log.debug("subject: " + subject);
			log.debug("type: " + type);
			log.debug("value: " + value);
		}
		*/
		this.issuer = issuer;
		this.subject = subject;
		this.type = type;
		this.value = value;
	}
	
	public ClaimIdentity getIsuuer() {
		return issuer;
	}

	public ClaimIdentity getSubject() {
		return subject;
	}

	public String getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	public String toString() {
		return value;
	}
}
