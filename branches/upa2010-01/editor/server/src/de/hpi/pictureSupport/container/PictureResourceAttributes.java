package de.hpi.pictureSupport.container;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

import de.hpi.pictureSupport.PictureResourceAttribute;

/**
 * The Class PictureResourceAttributes.
 */
@RootElement("resourceAttributes")
public class PictureResourceAttributes {

	/** The children. */
	@Element(name="resourceAttribute", targetType=PictureResourceAttribute.class)
	private ArrayList<PictureResourceAttribute> children;

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public ArrayList<PictureResourceAttribute> getChildren() {
		return children;
	}

	/**
	 * Sets the children.
	 *
	 * @param children the new children
	 */
	public void setChildren(ArrayList<PictureResourceAttribute> children) {
		this.children = children;
	}
}
