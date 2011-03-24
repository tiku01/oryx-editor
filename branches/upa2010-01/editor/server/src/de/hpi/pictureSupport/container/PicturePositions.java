package de.hpi.pictureSupport.container;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

import de.hpi.pictureSupport.PicturePosition;

/**
 * The Class PicturePositions.
 */
@RootElement("positions")
public class PicturePositions {

	/** The children. */
	@Element(name="position", targetType=PicturePosition.class)
	private ArrayList<PicturePosition> children;

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public ArrayList<PicturePosition> getChildren() {
		return children;
	}

	/**
	 * Sets the children.
	 *
	 * @param children the new children
	 */
	public void setChildren(ArrayList<PicturePosition> children) {
		this.children = children;
	}
}
