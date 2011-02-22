package de.hpi.pictureSupport;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

/**
 * The Class PictureResources.
 */
@RootElement("resources")
public class PictureResources {

	/** The children. */
	@Element(name="resource", targetType=PictureResource.class)
	private ArrayList<PictureResource> children;

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public ArrayList<PictureResource> getChildren() {
		return children;
	}

	/**
	 * Sets the children.
	 *
	 * @param list the new children
	 */
	public void setChildren(ArrayList<PictureResource> list) {
		children = list;
	}
}
