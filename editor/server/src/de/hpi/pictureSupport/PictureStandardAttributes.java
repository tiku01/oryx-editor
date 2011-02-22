package de.hpi.pictureSupport;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

/**
 * The Class PictureStandardAttributes.
 */
@RootElement("standardAttributes")
public class PictureStandardAttributes {

	/** The children. */
	@Element(name="standardAttribute", targetType=PictureStandardAttribute.class)
	private ArrayList<PictureStandardAttribute> children;

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public ArrayList<PictureStandardAttribute> getChildren() {
		return children;
	}

	/**
	 * Sets the children.
	 *
	 * @param children the new children
	 */
	public void setChildren(ArrayList<PictureStandardAttribute> children) {
		this.children = children;
	}
}
