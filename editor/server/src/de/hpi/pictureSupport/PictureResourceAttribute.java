package de.hpi.pictureSupport;

import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("resourceAttribute")
public class PictureResourceAttribute {

	@Attribute
	private int ressourceId;
	
	@Attribute
	private int id;
	
	@Attribute
	private int attributeId;
	
	@Element
	private String value;

	public int getRessourceId() {
		return ressourceId;
	}

	public void setRessourceId(int ressourceId) {
		this.ressourceId = ressourceId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAttributeId() {
		return attributeId;
	}

	public void setAttributeId(int attributeId) {
		this.attributeId = attributeId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
