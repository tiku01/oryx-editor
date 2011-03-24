package de.hpi.pictureSupport.container;

import org.xmappr.Element;
import org.xmappr.RootElement;

/**
 * The Class PictureBuildingBlockAttributes.
 */
@RootElement("buildingBlockAttributes")
public class PictureBuildingBlockAttributes {

	/** The standard attributes. */
	@Element(targetType=PictureStandardAttributes.class)
	private PictureStandardAttributes standardAttributes;
	
	/** The resource attributes. */
	@Element(targetType=PictureResourceAttributes.class)
	private PictureResourceAttributes resourceAttributes;

	/**
	 * Gets the standard attributes.
	 *
	 * @return the standard attributes
	 */
	public PictureStandardAttributes getStandardAttributes() {
		return standardAttributes;
	}

	/**
	 * Sets the standard attributes.
	 *
	 * @param standardAttributes the new standard attributes
	 */
	public void setStandardAttributes(PictureStandardAttributes standardAttributes) {
		this.standardAttributes = standardAttributes;
	}

	/**
	 * Gets the resource attributes.
	 *
	 * @return the resource attributes
	 */
	public PictureResourceAttributes getResourceAttributes() {
		return resourceAttributes;
	}

	/**
	 * Sets the resource attributes.
	 *
	 * @param resourceAttributes the new resource attributes
	 */
	public void setResourceAttributes(PictureResourceAttributes resourceAttributes) {
		this.resourceAttributes = resourceAttributes;
	}
}
