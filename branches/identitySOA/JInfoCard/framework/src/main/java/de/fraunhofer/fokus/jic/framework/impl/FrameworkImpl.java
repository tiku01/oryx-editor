package de.fraunhofer.fokus.jic.framework.impl;

import java.io.InputStream;
import java.io.StringReader;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.saml2.core.EncryptedAssertion;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.encryption.Decrypter;
import org.opensaml.security.SAMLSignatureProfileValidator;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.encryption.DecryptionException;
import org.opensaml.xml.encryption.EncryptedData;
import org.opensaml.xml.encryption.EncryptionConstants;
import org.opensaml.xml.encryption.InlineEncryptedKeyResolver;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.BasicParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.schema.XSAny;
import org.opensaml.xml.schema.XSString;
import org.opensaml.xml.security.CriteriaSet;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.SecurityTestHelper;
import org.opensaml.xml.security.credential.BasicCredential;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xml.security.keyinfo.KeyInfoCriteria;
import org.opensaml.xml.security.keyinfo.StaticKeyInfoCredentialResolver;
import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureValidator;
import org.opensaml.xml.validation.ValidationException;
import org.opensaml.xml.validation.ValidatorSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.fraunhofer.fokus.jic.JICException;
import de.fraunhofer.fokus.jic.framework.Framework;
import de.fraunhofer.fokus.jic.framework.TokenValidator;
import de.fraunhofer.fokus.jic.identity.Claim;
import de.fraunhofer.fokus.jic.identity.ClaimIdentity;
import de.fraunhofer.fokus.jic.identity.impl.ClaimIdentityImpl;
import de.fraunhofer.fokus.jic.identity.impl.ClaimImpl;

public class FrameworkImpl implements Framework, TokenValidator {

	private BasicParserPool ppMgr = null;
	private final Logger log = LoggerFactory.getLogger(FrameworkImpl.class);
	// private KeyStore trustStore;
	private TokenValidator tokenValidator = null;
	private final int TIME_JITTER_SECONDS = 60;

	static {
		// get logger for this static block
		Logger _log = LoggerFactory.getLogger(FrameworkImpl.class);
		try {
			// initialize OpenSAML framework.
			_log.debug("initializing OpenSAML library");
			DefaultBootstrap.bootstrap();
		} catch (ConfigurationException e) {
			_log.error("error initialising OpenSAML library", e);
		}
		_log.debug("initialization done.");
	}

	public FrameworkImpl() throws JICException {
		ppMgr = new BasicParserPool();
		ppMgr.setNamespaceAware(true);
		tokenValidator = this;
	}

	public ClaimIdentity getID(String samlAssertion) throws JICException {
		log.debug("called getID(String)");
		return getID(samlAssertion, null);
	}

	public ClaimIdentity getID(InputStream is) throws JICException {
		log.debug("called getID(InputStream)");
		return getID(is, null);
	}

	public ClaimIdentity getID(Document doc) throws JICException {
		log.debug("called getID(Document)");
		return getID(doc, null);
	}

	public ClaimIdentity getID(String samlAssertion, PrivateKey pk)
			throws JICException {
		log.debug("called getID(String, PrivateKey)");
		log.debug(samlAssertion);
		try {
			log.debug("parsing string representation of SAML assertion.");
			Document doc = ppMgr.parse(new StringReader(samlAssertion));
			return getID(doc, pk);
		} catch (XMLParserException e) {
			throw new JICException(e);
		}
	}

	public ClaimIdentity getID(InputStream is, PrivateKey pk)
			throws JICException {
		log.debug("called getID(InputStream, PrivateKey)");
		try {
			log.debug("parsing string representation of SAML assertion.");
			Document doc = ppMgr.parse(is);
			return getID(doc, pk);
		} catch (XMLParserException e) {
			throw new JICException(e);
		}
	}
	/*
	// @Override
	public ClaimIdentity getID_(Document doc, PrivateKey pk) throws JICException {
		log.debug("called getID(Document, PrivateKey)");
		log.debug("converting DOM document to XMLObject.");
		if (pk != null) {
			log.info("got private key, trying to decrypt SAML token.");
			Element _encryptedDataElement = (Element) doc
					.getElementsByTagNameNS(
							EncryptionConstants.EncryptionSpecNS,
							EncryptionConstants._TAG_ENCRYPTEDDATA).item(0);
			try {
				log.debug("trying to decrypt.");
				XMLCipher _xmlCipher = XMLCipher.getInstance();
				_xmlCipher.init(XMLCipher.DECRYPT_MODE, null);
				_xmlCipher.setKEK(pk);
				_xmlCipher.doFinal(doc, _encryptedDataElement);
			} catch (XMLEncryptionException xee) {
				log.error("error setting up decryption.", xee);
				throw new JICException("error setting up decryption.", xee);
			} catch (Exception e) {
				log.info("decryption failed. Assuming the token was unencrypted, continuing.", e);
			}
		} else {
			log
					.info("no private key supplied, assuming unencrypted SAML token.");
		}

		Element _decryptedElement = doc.getDocumentElement();
		if (log.isDebugEnabled()) {
			log.debug("decrypted SAML token:\n{}", FrameworkUtil.xmlToString(doc));
		}
		UnmarshallerFactory umf = Configuration.getUnmarshallerFactory();
		Unmarshaller unmarshaller = umf.getUnmarshaller(_decryptedElement);
		try {
			log
					.debug("attempting to unmarshall DOM representation of SAML token.");
			XMLObject xmlobj = unmarshaller.unmarshall(_decryptedElement);
			return getID(xmlobj);
		} catch (UnmarshallingException e) {
			throw new JICException("error unmarshalling SAML assertion", e);
		}
	}
	*/
	
	public ClaimIdentity getID(Document doc, PrivateKey pk) throws JICException {
		if (log.isDebugEnabled()) {
			log.debug("incoming XML:\n{}", FrameworkUtil.xmlToString(doc));
		}
		return getID(doc.getDocumentElement(), pk);
	}

	public ClaimIdentity getID(Element elem, PrivateKey pk) throws JICException {
		UnmarshallerFactory umf = Configuration.getUnmarshallerFactory();
		Unmarshaller unmarshaller = umf.getUnmarshaller(elem);
		try {
			log.debug("attempting to unmarshall DOM representation of SAML token.");
			XMLObject xmlobj = unmarshaller.unmarshall(elem);
			return getID(xmlobj, pk);
		} catch (UnmarshallingException e) {
			throw new JICException("error unmarshalling SAML assertion", e);
		}
		
	}
	private ClaimIdentity getID(XMLObject xmlobj, PrivateKey pk) throws JICException {
		/*
		 * check whether we're dealing with a SAML1 or SAML2 assertion
		 */
		log.debug("checking type of XMLObject token.");
		
		if(pk == null) {
			log.info("no private key supplied, assuming plaintext SAML token.");
		} else {
			try {
				// check for encrypted content.
				if (xmlobj instanceof EncryptedAssertion) {
					
					log.debug("found encrypted SAML2 assertion, decrypting.");
					EncryptedAssertion encryptedAssertion = (EncryptedAssertion) xmlobj;

					/*
					 * WORKAROUND: Zermatt doesn't set the Type, causing decryption to barf :(.
					 */
					EncryptedData _encryptedData = encryptedAssertion.getEncryptedData();
					if(_encryptedData.getType() == null) {
						_encryptedData.setType(EncryptionConstants.TYPE_ELEMENT);
					}

					Decrypter _decrypter = getDecrypter(pk);
					org.opensaml.saml2.core.Assertion saml2Assertion;
					saml2Assertion = _decrypter.decrypt(encryptedAssertion);
					
					if(log.isDebugEnabled()) {
						log.debug("decrypted SAML2 Assertion:\n" + 
								FrameworkUtil.xmlToString(saml2Assertion.getDOM()));
					}
					
					return getID(saml2Assertion);
					
				} else if (xmlobj instanceof EncryptedData) {
					
					log.debug("found encrypted XML content, decrypting.");
					EncryptedData _encryptedData = (EncryptedData) xmlobj;

					/*
					 * WORKAROUND: Zermatt doesn't set the Type, causing decryption to barf :(.
					 */
					if(_encryptedData.getType() == null) {
						_encryptedData.setType(EncryptionConstants.TYPE_ELEMENT);
					}

					Decrypter _decrypter = getDecrypter(pk);
					
					// decrypted XMLObject will be checked further down...
					// FIXME: parameter true necessary?
					xmlobj = _decrypter.decryptData(_encryptedData, true);
					
					if(log.isDebugEnabled()) {
						log.debug("decrypted XMLObject:\n" +
								FrameworkUtil.xmlToString(xmlobj.getDOM()));
					}
				}
			} catch (DecryptionException de) {
				throw new JICException("error decrypting assertion", de);
			}
		} 

		if (xmlobj instanceof org.opensaml.saml1.core.Assertion) {
			log.debug("found SAML1 assertion.");
			org.opensaml.saml1.core.Assertion saml1Assertion = (org.opensaml.saml1.core.Assertion) xmlobj;
			return getID(saml1Assertion);
		} else if (xmlobj instanceof org.opensaml.saml2.core.Assertion) {
			log.debug("found SAML2 assertion.");
			org.opensaml.saml2.core.Assertion saml2Assertion = (org.opensaml.saml2.core.Assertion) xmlobj;
			return getID(saml2Assertion);
		} else {
			if (log.isErrorEnabled())
				log.error("unknown XML object supplied.\n" + xmlobj.toString());
			throw new JICException("unknown XML object supplied: "
					+ xmlobj.getElementQName().getNamespaceURI());
		}
	}
	
	private Decrypter getDecrypter(PrivateKey pk) {
		BasicCredential _cred = new BasicCredential();
		_cred.setPrivateKey(pk);
		// KeyInfoCredentialResolver keyResolver = new StaticKeyInfoCredentialResolver(_cred);
		// return new Decrypter(keyResolver, null, null);
		Decrypter _res = new Decrypter(
				null,
				new StaticKeyInfoCredentialResolver(_cred),
				new InlineEncryptedKeyResolver());
		/*
		 * this is necessary in order to workaround an issue where
		 * signature validation of a decrypted assertion fails
		 */
		_res.setRootInNewDocument(true);
		return _res;
	}
	
	/*
	private ClaimIdentity getID(XMLObject xmlobj) throws JICException {
		 // check whether we're dealing with a SAML1 or SAML2 assertion
		log.debug("checking type of XMLObject token.");
		if (xmlobj instanceof org.opensaml.saml1.core.Assertion) {
			log.debug("found SAML1 assertion.");
			org.opensaml.saml1.core.Assertion saml1Assertion = (org.opensaml.saml1.core.Assertion) xmlobj;
			return getID(saml1Assertion);
		} else if (xmlobj instanceof org.opensaml.saml2.core.Assertion) {
			log.debug("found SAML2 assertion.");
			org.opensaml.saml2.core.Assertion saml2Assertion = (org.opensaml.saml2.core.Assertion) xmlobj;
			return getID(saml2Assertion);
		} else {
			if (log.isErrorEnabled())
				log.error("unknown XML object supplied.\n" + xmlobj.toString());
			throw new JICException("unknown XML object supplied: "
					+ xmlobj.getElementQName().getNamespaceURI());
		}
	}
	*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fraunhofer.fokus.jic.framework.impl.SignatureValidator#validateSignature(org.opensaml.xml.signature.Signature)
	 */
	public void validateSignature(Signature sig) throws JICException {

		// prevent some DOS attacks. see
		// https://spaces.internet2.edu/display/OpenSAML/OSTwoUserManJavaDSIG#OSTwoUserManJavaDSIG-VerifyingaSignaturewithaCredential
		SAMLSignatureProfileValidator _profileValidator = new SAMLSignatureProfileValidator();
		try {
			_profileValidator.validate(sig);
		} catch (ValidationException ve) {
			throw new JICException(
					"assertion doesn't comply with the SAML signature profile.",
					ve);
		}

		KeyInfoCredentialResolver _resolver = SecurityTestHelper
				.buildBasicInlineKeyInfoResolver();

		KeyInfo _ki = sig.getKeyInfo();
		CriteriaSet criteriaSet = new CriteriaSet(new KeyInfoCriteria(_ki));
		try {
			for (Credential _cred : _resolver.resolve(criteriaSet)) {
				log.debug("found credential of type: "
						+ _cred.getClass().getName());
				/*
				 * if (_cred instanceof X509Credential) { X509Credential
				 * _x509cred = (X509Credential) _cred;
				 * java.security.cert.X509Certificate _cert =
				 * _x509cred.getEntityCertificate(); }
				 */
				log.debug("checking signature");
				SignatureValidator _sigValidator = new SignatureValidator(_cred);
				try {
					_sigValidator.validate(sig);
				} catch (ValidationException ve) {
					log.debug("validation failed.", ve);
					throw new JICException("validating the signature failed.",
							ve);
				}
				log.debug("success!");
			}
		} catch (SecurityException se) {
			throw new JICException("error resolving the criteriaSet.", se);
		}
		
		/*
		 CredentialResolver _resolver = new KeyStoreCredentialResolver(
				 trustStore, new HashMap<String,String>(), // NOTE: no passwords for trustStore
				 UsageType.SIGNING // only consider signing certs 
		 			);
		 CriteriaSet _criteriaSet = new CriteriaSet();
		 try {
			 for (Credential _cred : _resolver.resolve(_criteriaSet)) {
				 try { 
					 SignatureValidator _validator = new SignatureValidator(_cred);
					 _validator.validate(sig);
					 return true; 
				 }catch (ValidationException ve) { // try next entry in trust store
					 continue;
				 }
			 }
		 } catch (SecurityException se) { 
			 throw new JICException("error retrieving credentials from trust store.", se); } // no cert in trust store was applicable
		 }
		return false;
		*/

		// this is where it gets ugly...
		// TODO: put KeyStore in JICFilter and config!
		/*
		 * KeyInfoCredentialResolver keyInfoCredResolver =
		 * Configuration.getGlobalSecurityConfiguration().getDefaultKeyInfoCredentialResolver();
		 * KeyStore _keystore; Collection<X509Certificate> anchors = new
		 * ArrayList<X509Certificate>(); Collection<X509CRL> crls = new
		 * ArrayList<X509CRL>(); Set<String> names = new HashSet<String>();
		 * int depth = 10; for(Enumeration<String> e = _keystore.aliases();
		 * e.hasMoreElements();) { String _alias = e.nextElement();
		 * if(_keystore.isCertificateEntry(_alias)) { log.debug(_alias);
		 * Certificate _cert = _keystore.getCertificate(_alias); if (_cert
		 * instanceof X509Certificate) { anchors.add((X509Certificate) _cert); } } }
		 * PKIXValidationInformation _validationInfo = new
		 * BasicPKIXValidationInformation(anchors, crls, depth); List<PKIXValidationInformation>
		 * _validationInfoList = new ArrayList<PKIXValidationInformation>();
		 * _validationInfoList.add(_validationInfo);
		 * PKIXValidationInformationResolver _resolver = new
		 * StaticPKIXValidationInformationResolver(_validationInfoList, names);
		 * PKIXSignatureTrustEngine _trustEngine;
		 */
		/*
		 * sig.getSigningCredential() CriteriaSet _criteriaSet = new
		 * CriteriaSet(); // _criteriaSet.add( new
		 * EntityIDCriteria(response.getIssuer().getValue()) ); //
		 * _criteriaSet.add( new
		 * MetadataCriteria(IDPSSODescriptor.DEFAULT_ELEMENT_NAME,
		 * SAMLConstants.SAML20P_NS) ); _criteriaSet.add( new
		 * UsageCriteria(UsageType.SIGNING) ); if(!_trustEngine.validate(sig,
		 * _criteriaSet)) throw new JICException("signature invalid or token was
		 * signed by an untrusted source.");
		 */
	}

	public void validateTokenLifetime(DateTime notBefore, DateTime notOnOrAfter) throws JICException {
		DateTime now = new DateTime();
		if (now.minusSeconds(TIME_JITTER_SECONDS).isAfter(notOnOrAfter))
			throw new JICException("received expired token.");
		if (now.plusSeconds(TIME_JITTER_SECONDS).isBefore(notBefore))
			throw new JICException("token is expired or was sent from the future.");
	}
	
	private ClaimIdentity getID(org.opensaml.saml1.core.Assertion saml1Assertion)
			throws JICException {
		try {
			log.debug("validating the assertion.");
			ValidatorSuite _validator = Configuration
					.getValidatorSuite("saml1-schema-validator");
			_validator.validate(saml1Assertion);
		} catch (ValidationException e) {
			log.info("invalid SAML1 assertion supplied", e);
			throw new JICException("invalid SAML1 assertion", e);
		}

		if (saml1Assertion.isSigned()) {
			log.debug("assertion is signed, validating the signature...");
			tokenValidator.validateSignature(saml1Assertion.getSignature());
			log.debug("basic cryptographic validation succeeded.");

		} else {
			throw new JICException("Assertion has no signature.");
		}

		// validate token lifetime
		DateTime notBefore = saml1Assertion.getConditions().getNotBefore();
		DateTime notOnOrAfter = saml1Assertion.getConditions().getNotOnOrAfter();
		tokenValidator.validateTokenLifetime(notBefore, notOnOrAfter);

		// get issuer as ClaimIdentity
		String _issuerType = "urn:oasis:names:tc:SAML:2.0:assertion:NameIDType";
		ClaimIdentity _issuer = new ClaimIdentityImpl();
		String _is = saml1Assertion.getIssuer();
		Claim _issuerClaim = new ClaimImpl(null, null, _issuerType, _is);
		ArrayList<Claim> _issuerList = new ArrayList<Claim>();
		_issuerList.add(_issuerClaim);
		_issuer.put(_issuerType, _issuerList);
		
		log.debug("building ClaimIdentity.");
		ClaimIdentity _id = new ClaimIdentityImpl();
		for (org.opensaml.saml1.core.AttributeStatement _statement : saml1Assertion
				.getAttributeStatements()) {
			for (org.opensaml.saml1.core.Attribute _attr : _statement
					.getAttributes()) {
				String _name = _attr.getAttributeName();
				String _ns = _attr.getAttributeNamespace();
				String _type = _ns + "/" + _name;

				_id.put(_type, makeClaimList(_id, _issuer,
						_type, _attr.getAttributeValues()));
			}
		}
		return _id;
	}

	private ClaimIdentity getID(org.opensaml.saml2.core.Assertion saml2Assertion)
			throws JICException {
		try {
			// validate the assertion against schema
			log.debug("validating the assertion.");
			ValidatorSuite _validator = Configuration
					.getValidatorSuite("saml2-core-schema-validator");
			_validator.validate(saml2Assertion);
		} catch (ValidationException e) {
			log.warn("invalid SAML2 assertion supplied", e);
			/* 
			 * WORKAROUND: higgins STS creates invalid SAML2 assertions,
			 * so we ignore the validation error.
			 * (treats issuer like in SAML1, but this has changed :(.)
			 */
			// throw new JICException("invalid SAML2 assertion", e);
		}
		
		if (saml2Assertion.isSigned()) {
			// validate the signature
			log.debug("assertion is signed, validating the signature...");
			tokenValidator.validateSignature(saml2Assertion.getSignature());
			log.debug("basic cryptographic validation succeeded.");

		} else {
			throw new JICException("Assertion has no signature.");
		}
		
		// validate token lifetime
		DateTime notBefore = saml2Assertion.getConditions().getNotBefore();
		DateTime notOnOrAfter = saml2Assertion.getConditions().getNotOnOrAfter();
		tokenValidator.validateTokenLifetime(notBefore, notOnOrAfter);
		
		// get issuer as ClaimIdentity
		String _issuerType = "urn:oasis:names:tc:SAML:2.0:assertion:NameIDType";
		ClaimIdentity _issuer = new ClaimIdentityImpl();
		Issuer _is = saml2Assertion.getIssuer();
		Claim _issuerClaim = new ClaimImpl(null, null, _issuerType, _is.getValue());
		ArrayList<Claim> _issuerList = new ArrayList<Claim>();
		_issuerList.add(_issuerClaim);
		_issuer.put(_issuerType, _issuerList);

		// build the identity.
		log.debug("building ClaimIdentity.");
		ClaimIdentity _id = new ClaimIdentityImpl();
		for (org.opensaml.saml2.core.AttributeStatement _statement : saml2Assertion
				.getAttributeStatements()) {
			for (org.opensaml.saml2.core.EncryptedAttribute _encAttr : _statement
					.getEncryptedAttributes()) {
				// FIXME: encrypted attrs are not handled.
				log.error("JInfoCard doesn't support encrypted attributes.");
				if (log.isDebugEnabled()) {
					log.debug(_encAttr.toString());
				}
			}
			for (org.opensaml.saml2.core.Attribute _attr : _statement
					.getAttributes()) {
				String _name = _attr.getName();
				String _nameFormat = _attr.getNameFormat();

				/*
				 * WORKAROUND: Higgins STS puts the namespace inside the
				 * NameFormat attribute and the name inside the Name attribute
				 * (like SAML1 tokens). However, the OASIS draft
				 * SAML2-Infocard-profile available from
				 * http://www.oasis-open.org/committees/download.php/28626/draft-sstc-saml2-infocard-01.pdf
				 * states in section 2.4.4 that the NameFormat must be
				 * urn:oasis:names:tc:SAML:2.0:attrname-format:uri and the Name
				 * attribute should contain the complete Claim URI.
				 * 
				 * WORKARROUND 2: Zermatt doesn't set NameFormat at all, but
				 * otherwise conforms to the SAML2-Infocard-profile.
				 */
				String _type;
				if (_nameFormat == null || 
					"urn:oasis:names:tc:SAML:2.0:attrname-format:uri".equals(_nameFormat)) {
					_type = _name;
				} else {
					_type = _nameFormat + "/" + _name;
				}

				_id.put(_type, makeClaimList(_id, _issuer,
						// not yet implemented
						_type, _attr.getAttributeValues()));
			}
		}
		return _id;
	}

	private List<Claim> makeClaimList(ClaimIdentity subject,
			ClaimIdentity issuer, String type, List<XMLObject> vals) {

		List<Claim> _ret = new ArrayList<Claim>();

		for (XMLObject _xval : vals) {
			_ret.add(makeClaim(subject, issuer, type, _xval));
		}

		return _ret;
	}

	private Claim makeClaim(ClaimIdentity subject, ClaimIdentity issuer,
			String type, XMLObject xval) {
		String _val;
		if (xval instanceof XSString) {
			_val = ((XSString) xval).getValue();
		} else if (xval instanceof XSAny) {
			_val = ((XSAny) xval).getTextContent();
		} else {
			// fallback, shouldn't happen.
			_val = xval.getDOM().getTextContent();
		}
		log.debug("attribute name: " + type + ", value: " + _val);
		return new ClaimImpl(issuer, subject, type, _val);
	}
}
