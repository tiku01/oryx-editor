package de.hpi.pictureSupport;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

/**
 * The Class PictureProducts.
 */
@RootElement("products")
public class PictureProducts {

	/** The children. */
	@Element(name="product", targetType=PictureProduct.class)
	private ArrayList<PictureProduct> children;

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public ArrayList<PictureProduct> getChildren() {
		return children;
	}

	/**
	 * Sets the children.
	 *
	 * @param list the new children
	 */
	public void setChildren(ArrayList<PictureProduct> list) {
		children = list;
	}
}