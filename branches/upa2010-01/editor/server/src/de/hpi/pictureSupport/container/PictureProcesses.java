package de.hpi.pictureSupport.container;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

import de.hpi.pictureSupport.PictureProcess;

/**
 * The Class PictureProcesses.
 */
@RootElement("processes")
public class PictureProcesses {

	/** The children. */
	@Element(name="process", targetType=PictureProcess.class)
	private ArrayList<PictureProcess> children;

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public ArrayList<PictureProcess> getChildren() {
		return children;
	}

	/**
	 * Sets the children.
	 *
	 * @param list the new children
	 */
	public void setChildren(ArrayList<PictureProcess> list) {
		children = list;
	}
}
