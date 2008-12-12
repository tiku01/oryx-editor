package de.fraunhofer.fokus.jic.identity.impl;

import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.util.encoders.Base64;

public class IdentityUtil {
	/**
	 * convert the ppid of the information card to a user friendly name
	 * 
	 * @param ppid
	 *            of the information card
	 * @return the user friendly name of the ppid
	 */
	public static String friendlyIdentifier(String ppid) {
		// code map
		char[] ss = { 'Q', 'L', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
				'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P',
				'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

		// base 64 decoding
		byte[] b = Base64.decode(ppid.getBytes());

		// sha1 decoding
		SHA1Digest digEng = new SHA1Digest();
		digEng.update(b, 0, b.length);
		byte[] b1 = new byte[digEng.getDigestSize()];
		digEng.doFinal(b1, 0);

		// convert the bytes to ints
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 10; i++) {
			int ii = byte2int(b1[i]) % 32;
			if (i == 3 || i == 7) {
				sb.append("-");
			}
			// mapping of the int to mapping code
			sb.append(ss[ii]);

		}

		return sb.toString();
	}

	static public int byte2int(byte b) {
		return ((b < 0) ? (0x100 + b) : b);
	}

}
