package de.hpi.pictureSupport;

import org.xmappr.Attribute;
import org.xmappr.RootElement;

/**
 * The Class PictureBuildingBlockOccurrence.
 */
@RootElement("buildingBlockOccurrence")
public class PictureBuildingBlockOccurrence {

	/** The is twin. */
	@Attribute(name="twin")
	private Boolean isTwin;
	
	/** The building block. */
	@Attribute
	private int buildingBlock;

	/**
	 * Gets the checks if is twin.
	 *
	 * @return the checks if is twin
	 */
	public Boolean getIsTwin() {
		return isTwin;
	}

	/**
	 * Sets the checks if is twin.
	 *
	 * @param isTwin the new checks if is twin
	 */
	public void setIsTwin(Boolean isTwin) {
		this.isTwin = isTwin;
	}

	/**
	 * Gets the building block.
	 *
	 * @return the building block
	 */
	public int getBuildingBlock() {
		return buildingBlock;
	}

	/**
	 * Sets the building block.
	 *
	 * @param buildingBlock the new building block
	 */
	public void setBuildingBlock(int buildingBlock) {
		this.buildingBlock = buildingBlock;
	}
}
