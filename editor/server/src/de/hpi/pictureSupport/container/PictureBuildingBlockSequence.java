package de.hpi.pictureSupport.container;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

import de.hpi.pictureSupport.diagram.PictureBuildingBlockOccurrence;

/**
 * The Class PictureBuildingBlockSequence.
 */
@RootElement("buildingBlockSequence")
public class PictureBuildingBlockSequence {

	/** The children. */
	@Element(name="buildingBlockOccurrence", targetType=PictureBuildingBlockOccurrence.class)
	private ArrayList<PictureBuildingBlockOccurrence> children;

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public ArrayList<PictureBuildingBlockOccurrence> getChildren() {
		return children;
	}

	/**
	 * Sets the children.
	 *
	 * @param children the new children
	 */
	public void setChildren(ArrayList<PictureBuildingBlockOccurrence> children) {
		this.children = children;
	}
}
