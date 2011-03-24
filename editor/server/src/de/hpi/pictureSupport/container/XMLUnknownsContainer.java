package de.hpi.pictureSupport.container;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmappr.DomElement;

/**
 * The Class XMLUnknownsContainer.
 */
public class XMLUnknownsContainer implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The unknown attributes. */
	protected HashMap<String,String> unknownAttributes;
	
	/** The unknown elements. */
	protected ArrayList<DomElement> unknownElements;
	
	/**
	 * Gets the unknown attributes.
	 *
	 * @return the unknown attributes
	 */
	public HashMap<String,String> getUnknownAttributes() {
		return unknownAttributes;
	}
	
	/**
	 * Gets the unknown elements.
	 *
	 * @return the unknown elements
	 */
	public ArrayList<DomElement> getUnknownElements() {
		return unknownElements;
	}
	
	/**
	 * Sets the unknown attributes.
	 *
	 * @param unknowns the unknowns
	 */
	public void setUnknownAttributes(HashMap<String,String> unknowns) {
		unknownAttributes = unknowns;
	}
	
	/**
	 * Sets the unknown elements.
	 *
	 * @param unknowns the new unknown elements
	 */
	public void setUnknownElements(ArrayList<DomElement> unknowns) {
		unknownElements = unknowns;
	}
}
