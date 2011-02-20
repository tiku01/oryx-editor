package de.hpi.pictureSupport;

import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("position")
public class PicturePosition {

	@Attribute(name="id")
	protected int ID;
	
	@Attribute(name="type")
	protected String type;
	
	@Element(name="description")
	protected String description;
	
	@Element(name="name")
	protected String name;

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
