/**
 * 
 */
package de.fraunhofer.fokus.jic.identity.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.fokus.jic.identity.Claim;
import de.fraunhofer.fokus.jic.identity.ClaimIdentity;
import de.fraunhofer.fokus.jic.identity.ClaimUris;

/**
 * @author cht
 * 
 */
public class ClaimIdentityImpl extends HashMap<String, List<Claim>> implements ClaimIdentity, Serializable {

	private static final long serialVersionUID = 2284635844725662062L;
	private static transient Logger log = LoggerFactory.getLogger(ClaimIdentityImpl.class);
	
	public ClaimIdentityImpl() {
		log = LoggerFactory.getLogger(ClaimIdentityImpl.class);
	}

	@Override
	public List<Claim> get(Object key) {
		log.debug("called get(" + key + ").");

		if(!(key instanceof String))
			return super.get(key);
		/*
		 * "convenience" accessors for standard claims from ISIPv1.5
		 * necessary since JSP EL doesn't call getters because ClaimIdentity
		 * inherits from java.util.Map
		 */
		String str = (String) key;
		if("ppid".equals(str))
			return super.get(ClaimUris.PPID);
		else if("lastName".equals(str))
			return super.get(ClaimUris.SURNAME);
		else if("firstName".equals(str))
			return super.get(ClaimUris.GIVEN_NAME);
		else if("email".equals(str))
			return super.get(ClaimUris.EMAIL_ADDRESS);
		else if("address".equals(str))
			return super.get(ClaimUris.STREET_ADDRESS);
		else if("city".equals(str))
			return super.get(ClaimUris.LOCALITY);
		else if("province".equals(str))
			return super.get(ClaimUris.STATE_OR_PROVINCE);
		else if("postalCode".equals(str))
			return super.get(ClaimUris.POSTAL_CODE);
		else if("country".equals(str))
			return super.get(ClaimUris.COUNTRY);
		else if("homePhone".equals(str))
			return super.get(ClaimUris.HOME_PHONE);
		else if("otherPhone".equals(str))
			return super.get(ClaimUris.OTHER_PHONE);
		else if("mobilePhone".equals(str))
			return super.get(ClaimUris.MOBILE_PHONE);
		else if("birthDate".equals(str))
			return super.get(ClaimUris.DATE_OF_BIRTH);
		else if("gender".equals(str))
			return super.get(ClaimUris.GENDER);
		else if("webPage".equals(str))
			return super.get(ClaimUris.WEB_PAGE);
		else
			return super.get(key);
	}
}
