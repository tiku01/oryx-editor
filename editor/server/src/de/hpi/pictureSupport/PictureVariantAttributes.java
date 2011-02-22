package de.hpi.pictureSupport;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

/**
 * The Class PictureVariantAttributes.
 */
@RootElement("variantAttributes")
public class PictureVariantAttributes {

	/** The children. */
	@Element(name="variantAttribute", targetType=PictureProcessAttribute.class)
	private ArrayList<PictureProcessAttribute> children;

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public ArrayList<PictureProcessAttribute> getChildren() {
		return children;
	}

	/**
	 * Sets the children.
	 *
	 * @param children the new children
	 */
	public void setChildren(ArrayList<PictureProcessAttribute> children) {
		this.children = children;
	}
}
