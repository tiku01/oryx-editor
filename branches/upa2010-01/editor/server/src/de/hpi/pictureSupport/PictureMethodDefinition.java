package de.hpi.pictureSupport;

import org.xmappr.Element;
import org.xmappr.RootElement;

/**
 * The Class PictureMethodDefinition.
 */
@RootElement("methodDefinition")
public class PictureMethodDefinition {

	/** The building block types. */
	@Element(targetType=PictureBuildingBlockTypes.class)
	private PictureBuildingBlockTypes buildingBlockTypes;
	
	/** The attribute types. */
	@Element(targetType=PictureAttributeTypes.class)
	private PictureAttributeTypes attributeTypes;
	
	/**
	 * Gets the building block types.
	 *
	 * @return the building block types
	 */
	public PictureBuildingBlockTypes getBuildingBlockTypes() {
		return buildingBlockTypes;
	}

	/**
	 * Sets the building block types.
	 *
	 * @param buildingBlockTypes the new building block types
	 */
	public void setBuildingBlockTypes(PictureBuildingBlockTypes buildingBlockTypes) {
		this.buildingBlockTypes = buildingBlockTypes;
	}

	/**
	 * Gets the attribute types.
	 *
	 * @return the attribute types
	 */
	public PictureAttributeTypes getAttributeTypes() {
		return attributeTypes;
	}

	/**
	 * Sets the attribute types.
	 *
	 * @param attributeTypes the new attribute types
	 */
	public void setAttributeTypes(PictureAttributeTypes attributeTypes) {
		this.attributeTypes = attributeTypes;
	}

}
