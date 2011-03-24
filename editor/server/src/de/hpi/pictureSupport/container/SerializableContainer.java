package de.hpi.pictureSupport.container;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The Class SerializableContainer.
 *
 * @param <Container> the generic type
 */
public class  SerializableContainer<Container> implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The attributes. */
	protected HashMap<String,String> attributes;
	
	/** The elements. */
	protected ArrayList<Container> elements;

	
	/**
	 * Gets the attributes.
	 *
	 * @return the attributes
	 */
	public HashMap<String,String> getAttributes() {
		if (attributes == null){
			attributes = new HashMap<String,String>();
		}
		return attributes;
	}
	
	/**
	 * Gets the elements.
	 *
	 * @return the elements
	 */
	public ArrayList<Container> getElements() {
		if (elements == null){
			elements = new ArrayList<Container>();
		}
		return elements;
	}
	
	/**
	 * Sets the attributes.
	 *
	 * @param unknowns the unknowns
	 */
	public void setAttributes(HashMap<String,String> unknowns) {
		attributes = unknowns;
	}
	
	/**
	 * Sets the elements.
	 *
	 * @param unknowns the new elements
	 */
	public void setElements(ArrayList<Container> unknowns) {
		elements = unknowns;
	}
	
	
	
}
