package de.hpi.pictureSupport;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("organisationUnits")
public class PictureOrganisationUnits {

	@Element(name="organisationUnit", targetType=PictureOrganisationUnit.class)
	private ArrayList<PictureOrganisationUnit> children;

	public ArrayList<PictureOrganisationUnit> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<PictureOrganisationUnit> list) {
		children = list;
	}
}