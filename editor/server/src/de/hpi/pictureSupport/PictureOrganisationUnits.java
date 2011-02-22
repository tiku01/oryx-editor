package de.hpi.pictureSupport;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

/**
 * The Class PictureOrganisationUnits.
 */
@RootElement("organisationUnits")
public class PictureOrganisationUnits {

	/** The children. */
	@Element(name="organisationUnit", targetType=PictureOrganisationUnit.class)
	private ArrayList<PictureOrganisationUnit> children;

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public ArrayList<PictureOrganisationUnit> getChildren() {
		return children;
	}

	/**
	 * Sets the children.
	 *
	 * @param list the new children
	 */
	public void setChildren(ArrayList<PictureOrganisationUnit> list) {
		children = list;
	}
}