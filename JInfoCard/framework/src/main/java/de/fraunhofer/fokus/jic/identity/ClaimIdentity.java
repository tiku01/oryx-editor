/**
 * 
 */
package de.fraunhofer.fokus.jic.identity;

import java.util.List;
import java.util.Map;

/**
 * @author cht
 * 
 * represents a user identity based on claims.
 * 
 * For the commonly used claims defined in {@link ClaimUris} and ISIP v1.5, the
 * map key is the URI. Since SAML conceptually allows the same claim to occur
 * several times with different values within an assertion, the map value is a
 * list of {@link Claim} objects.
 * 
 * For example, to access the first PPID claim from a JSP page, you can use one
 * of the following methods:
 * <ul>
 * <li><code>${userdata.ppid[0]}</code></li>
 * <li><code>${userdata[uris.ppid][0]}</code></li>
 * <li><code>${userdata["http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier"][0]}</code></li>
 * </ul> 
 * 
 * the second method assumes that {@link ClaimUris} has been imported as
 * <code>uris</code> as follows:
 *  
 * <code>
 * &lt;jsp:useBean id="uris" class="de.fraunhofer.fokus.jic.identity.ClaimUris" scope="page" /&gt;
 * </code>
 * 
 * <p>From Java code, the following should be used:</p>
 * 
 * <code>claimid.get(ClaimUris.PPID).get(0)</code>
 * 
 */
public interface ClaimIdentity extends Map<String, List<Claim>> {

}
