package de.hpi.pictureSupport;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

/**
 * The Class PictureProcessFlow.
 */
@RootElement("processFlow")
public class PictureProcessFlow {

	/** The children. */
	@Element(name="subprocess", targetType=PictureSubprocess.class)
	private ArrayList<PictureSubprocess> children;

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public ArrayList<PictureSubprocess> getChildren() {
		return children;
	}

	/**
	 * Sets the children.
	 *
	 * @param children the new children
	 */
	public void setChildren(ArrayList<PictureSubprocess> children) {
		this.children = children;
	}
}
