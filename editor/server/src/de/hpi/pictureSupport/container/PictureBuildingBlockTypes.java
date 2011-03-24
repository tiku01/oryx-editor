package de.hpi.pictureSupport.container;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

import de.hpi.pictureSupport.PictureBuildingBlockType;

/**
 * The Class PictureBuildingBlockTypes.
 */
@RootElement("buildingBlockTypes")
public class PictureBuildingBlockTypes {

	/** The children. */
	@Element(name="buildingBlockType", targetType=PictureBuildingBlockType.class)
	private ArrayList<PictureBuildingBlockType> children;

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public ArrayList<PictureBuildingBlockType> getChildren() {
		return children;
	}

	/**
	 * Sets the children.
	 *
	 * @param list the new children
	 */
	public void setChildren(ArrayList<PictureBuildingBlockType> list) {
		children = list;
	}
}
