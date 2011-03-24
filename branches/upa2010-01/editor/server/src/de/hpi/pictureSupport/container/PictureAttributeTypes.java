package de.hpi.pictureSupport.container;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

import de.hpi.pictureSupport.PictureAttributeType;

/**
 * The Class PictureAttributeTypes.
 */
@RootElement("attributeTypes")
public class PictureAttributeTypes {

	/** The children. */
	@Element(name="attributeType", targetType=PictureAttributeType.class)
	private ArrayList<PictureAttributeType> children;

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public ArrayList<PictureAttributeType> getChildren() {
		return children;
	}

	/**
	 * Sets the children.
	 *
	 * @param list the new children
	 */
	public void setChildren(ArrayList<PictureAttributeType> list) {
		children = list;
	}
}
