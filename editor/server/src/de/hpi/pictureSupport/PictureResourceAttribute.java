package de.hpi.pictureSupport;

import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

/**
 * The Class PictureResourceAttribute.
 */
@RootElement("resourceAttribute")
public class PictureResourceAttribute {

	/** The ressource id. */
	@Attribute
	private int ressourceId;
	
	/** The id. */
	@Attribute
	private int id;
	
	/** The attribute id. */
	@Attribute
	private int attributeId;
	
	/** The value. */
	@Element
	private String value;

	/**
	 * Gets the ressource id.
	 *
	 * @return the ressource id
	 */
	public int getRessourceId() {
		return ressourceId;
	}

	/**
	 * Sets the ressource id.
	 *
	 * @param ressourceId the new ressource id
	 */
	public void setRessourceId(int ressourceId) {
		this.ressourceId = ressourceId;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Gets the attribute id.
	 *
	 * @return the attribute id
	 */
	public int getAttributeId() {
		return attributeId;
	}

	/**
	 * Sets the attribute id.
	 *
	 * @param attributeId the new attribute id
	 */
	public void setAttributeId(int attributeId) {
		this.attributeId = attributeId;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
