package de.fraunhofer.fokus.jic.identity;

/**
 * @author cht
 * 
 * Convenience class for accessing the Claim URIs as defined in ISIP v1.5,
 * section 8.5
 * 
 */
public class ClaimUris {
	// Claim URIs as defined in ISIP v1.5, section 8.5
	public static final String PPID = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier";
	public static final String SURNAME = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname";
	public static final String GIVEN_NAME = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname";
	public static final String EMAIL_ADDRESS = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress";
	public static final String STREET_ADDRESS = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/streetaddress";
	public static final String LOCALITY = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/locality";
	public static final String STATE_OR_PROVINCE = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/stateorprovince";
	public static final String POSTAL_CODE = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/postalcode";
	public static final String COUNTRY = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/country";
	public static final String HOME_PHONE = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/homephone";
	public static final String OTHER_PHONE = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/otherphone";
	public static final String MOBILE_PHONE = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/mobilephone";
	public static final String DATE_OF_BIRTH = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/dateofbirth";
	public static final String GENDER = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/gender";
	public static final String WEB_PAGE = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/webpage";

	/*
	 * getters for the static fields above. easier to access from JSPs.
	 */
	public String getPpid() {
		return PPID;
	}

	public String getSurname() {
		return SURNAME;
	}

	public String getGivenName() {
		return GIVEN_NAME;
	}

	public String getEmailAddress() {
		return EMAIL_ADDRESS;
	}

	public String getStreetAddress() {
		return STREET_ADDRESS;
	}

	public String getLocality() {
		return LOCALITY;
	}

	public String getStateOrProvince() {
		return STATE_OR_PROVINCE;
	}

	public String getPostalCode() {
		return POSTAL_CODE;
	}

	public String getCountry() {
		return COUNTRY;
	}

	public String getHomePhone() {
		return HOME_PHONE;
	}

	public String getWorkPhone() {
		return OTHER_PHONE;
	}

	public String getMobilePhone() {
		return MOBILE_PHONE;
	}

	public String getDateOfBirth() {
		return DATE_OF_BIRTH;
	}

	public String getGender() {
		return GENDER;
	}

	public String getWebPage() {
		return WEB_PAGE;
	}

	/*
	 * aliases for common alternate names. Should these be deleted?
	 */
	public String getLastName() {
		return getSurname();
	}

	public String getFirstName() {
		return getGivenName();
	}

	public String getEmail() {
		return getEmailAddress();
	}

	public String getAddress() {
		return getStreetAddress();
	}

	public String getCity() {
		return getLocality();
	}

	public String getProvince() {
		return getStateOrProvince();
	}

	public String getBirthDate() {
		return getDateOfBirth();
	}
}
