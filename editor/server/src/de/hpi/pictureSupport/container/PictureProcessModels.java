package de.hpi.pictureSupport.container;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

import de.hpi.pictureSupport.diagram.PictureProcessModel;

/**
 * The Class PictureProcessModels.
 */
@RootElement("processModels")
public class PictureProcessModels {

	/** The children. */
	@Element(name="processModel", targetType=PictureProcessModel.class)
	private ArrayList<PictureProcessModel> children;

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public ArrayList<PictureProcessModel> getChildren() {
		return children;
	}

	/**
	 * Sets the children.
	 *
	 * @param children the new children
	 */
	public void setChildren(ArrayList<PictureProcessModel> children) {
		this.children = children;
	}
}
