package de.hpi.pictureSupport;

import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

/**
 * The Class PictureBuildingBlock.
 */
@RootElement("buildingBlock")
public class PictureBuildingBlock {

	/** The type. */
	@Attribute
	private int type;
	
	/** The id. */
	@Attribute
	private int id;
	
	/** The name. */
	@Element
	private String name;
	
	/** The description. */
	@Element
	private String description;
	
	/** The propability. */
	@Element
	private int propability;
	
	/** The processing time. */
	@Element
	private float processingTime;
	
	/** The position type. */
	@Element
	private String positionType;
	
	/** The building block attributes. */
	@Element(targetType=PictureBuildingBlockAttributes.class)
	private PictureBuildingBlockAttributes buildingBlockAttributes;

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(int type) {
		this.type = type;
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
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the propability.
	 *
	 * @return the propability
	 */
	public int getPropability() {
		return propability;
	}

	/**
	 * Sets the propability.
	 *
	 * @param propability the new propability
	 */
	public void setPropability(int propability) {
		this.propability = propability;
	}

	/**
	 * Gets the processing time.
	 *
	 * @return the processing time
	 */
	public float getProcessingTime() {
		return processingTime;
	}

	/**
	 * Sets the processing time.
	 *
	 * @param processingTime the new processing time
	 */
	public void setProcessingTime(float processingTime) {
		this.processingTime = processingTime;
	}

	/**
	 * Gets the position type.
	 *
	 * @return the position type
	 */
	public String getPositionType() {
		return positionType;
	}

	/**
	 * Sets the position type.
	 *
	 * @param positionType the new position type
	 */
	public void setPositionType(String positionType) {
		this.positionType = positionType;
	}

	/**
	 * Gets the building block attributes.
	 *
	 * @return the building block attributes
	 */
	public PictureBuildingBlockAttributes getBuildingBlockAttributes() {
		return buildingBlockAttributes;
	}

	/**
	 * Sets the building block attributes.
	 *
	 * @param buildingBlockAttributes the new building block attributes
	 */
	public void setBuildingBlockAttributes(
			PictureBuildingBlockAttributes buildingBlockAttributes) {
		this.buildingBlockAttributes = buildingBlockAttributes;
	}
}
