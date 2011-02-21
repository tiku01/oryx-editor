package de.hpi.pictureSupport;

import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("organisationUnit")
public class PictureOrganisationUnit {

	@Attribute
	private int id;
	
	@Element
	private String name;
	
	@Element(targetType=PicturePositions.class)
	private PicturePositions positions;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PicturePositions getPositions() {
		return positions;
	}

	public void setPositions(PicturePositions positions) {
		this.positions = positions;
	}
}
