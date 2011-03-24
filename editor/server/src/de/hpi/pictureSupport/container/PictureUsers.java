package de.hpi.pictureSupport.container;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

import de.hpi.pictureSupport.PictureUser;

/**
 * The Class PictureUsers.
 */
@RootElement("users")
public class PictureUsers {

	/** The children. */
	@Element(name="user", targetType=PictureUser.class)
	private ArrayList<PictureUser> children;

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public ArrayList<PictureUser> getChildren() {
		return children;
	}

	/**
	 * Sets the children.
	 *
	 * @param list the new children
	 */
	public void setChildren(ArrayList<PictureUser> list) {
		children = list;
	}
}