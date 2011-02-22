package de.hpi.pictureSupport;

import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

/**
 * The Class PictureProcessAttribute.
 */
@RootElement("processAttribute")
public class PictureProcessAttribute {

	/** The attribute id. */
	@Attribute
	private int attributeId;
	
	/** The id. */
	@Attribute
	private int id;
	
	/** The value. */
	@Element
	private String value;

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
