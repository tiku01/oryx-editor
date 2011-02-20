package de.hpi.pictureSupport;

import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("organisationUnit")
public class PictureOrganisationUnit {

	@Attribute(name="id")
	protected int ID;
	
	@Element(name="name")
	protected String name;
	
	@Element(name="positions", targetType=PicturePositions.class)
	protected PicturePositions positions;

	public PicturePositions getPositions() {
		return positions;
	}

	public void setPositions(PicturePositions positions) {
		this.positions = positions;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
