package de.hpi.pictureSupport.helper;

import java.util.ArrayList;
import java.util.HashMap;

import org.xmappr.Attribute;
import org.xmappr.DomElement;
import org.xmappr.Element;

/**
 * The Class XMLConvertible.
 */
public abstract class XMLConvertible {
	
	/** The unknown attributes. */
	@Attribute("*")
	protected HashMap<String,String> unknownAttributes = new HashMap<String,String>();

	/** The unknown children. */
	@Element("*")
	protected ArrayList<DomElement> unknownChildren;
	
	/**
	 * Gets the unknown attributes.
	 *
	 * @return the unknown attributes
	 */
	public HashMap<String,String> getUnknownAttributes() {
		return unknownAttributes;
	}
	
	/**
	 * Sets the unknown attributes.
	 *
	 * @param unknownAttributes the unknown attributes
	 */
	public void setUnknownAttributes(HashMap<String, String> unknownAttributes) {
		this.unknownAttributes = unknownAttributes;
	}

	/**
	 * Gets the unknown children.
	 *
	 * @return the unknown children
	 */
	public ArrayList<DomElement> getUnknownChildren() {
		return unknownChildren;
	}
	
	/**
	 * Sets the unknown children.
	 *
	 * @param unknownElements the new unknown children
	 */
	public void setUnknownChildren(ArrayList<DomElement> unknownElements) {
		unknownChildren = unknownElements;
	}
}
