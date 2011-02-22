package de.hpi.pictureSupport;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

/**
 * The Class PictureBuildingBlockRepository.
 */
@RootElement("buildingBlockRepository")
public class PictureBuildingBlockRepository {

	/** The children. */
	@Element(name="buildingBlock", targetType=PictureBuildingBlock.class)
	private ArrayList<PictureBuildingBlock> children;

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public ArrayList<PictureBuildingBlock> getChildren() {
		return children;
	}

	/**
	 * Sets the children.
	 *
	 * @param children the new children
	 */
	public void setChildren(ArrayList<PictureBuildingBlock> children) {
		this.children = children;
	}
}
