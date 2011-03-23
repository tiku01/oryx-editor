package de.hpi.pictureSupport;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

/**
 * The Class PictureSubprocessAttributes.
 */
@RootElement("pictureVariants")
public class PictureVariants {

	/** The children. */
	@Element(name="variant", targetType=PictureVariant.class)
	private ArrayList<PictureVariant> children;

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public ArrayList<PictureVariant> getChildren() {
		return children;
	}

	/**
	 * Sets the children.
	 *
	 * @param children the new children
	 */
	public void setChildren(ArrayList<PictureVariant> children) {
		this.children = children;
	}
}
