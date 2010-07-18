package de.hpi.visio.data;

import org.xmappr.Attribute;
import org.xmappr.RootElement;

/**
 * Class for xmappr - xml to java mapping. Shapes are linked to master shapes by
 * the id. So this class is imported to resolve nameU values.
 * 
 * @author Thamsen
 */
@RootElement("Master")
public class Master {

	@Attribute("ID")
	public String masterId;

	@Attribute("NameU")
	public String name;

	/**
	 * @return The nameU value of the master shape. If necessary the nameU value
	 *         will be normalized (removal of the numbering).
	 */
	public String getName() {
		if (name != null && name.contains("."))
			name = normalizeName(name);
		return name;
	}

	public String getMasterId() {
		return masterId;
	}

	/*
	 * Normalizing is necessary because if several masters for the same type (=
	 * same nameU) are in a given visio document, the masters have nameU-values,
	 * which are numbered: e.g. "Pool Header", "Pool Header.2", "Pool Header.3"
	 */
	private String normalizeName(String incorrectName) {
		int end = incorrectName.indexOf(".");
		String correctedName = incorrectName.substring(0, end);
		return correctedName;
	}

}
