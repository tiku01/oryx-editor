package de.hpi.pictureSupport;

import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("buildingBlock")
public class PictureBuildingBlock {

	@Attribute
	private int type;
	
	@Attribute
	private int id;
	
	@Element
	private String name;
	
	@Element
	private String description;
	
	@Element
	private int propability;
	
	@Element
	private float processingTime;
	
	@Element
	private String positionType;
	
	@Element(targetType=PictureBuildingBlockAttributes.class)
	private PictureBuildingBlockAttributes buildingBlockAttributes;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPropability() {
		return propability;
	}

	public void setPropability(int propability) {
		this.propability = propability;
	}

	public float getProcessingTime() {
		return processingTime;
	}

	public void setProcessingTime(float processingTime) {
		this.processingTime = processingTime;
	}

	public String getPositionType() {
		return positionType;
	}

	public void setPositionType(String positionType) {
		this.positionType = positionType;
	}

	public PictureBuildingBlockAttributes getBuildingBlockAttributes() {
		return buildingBlockAttributes;
	}

	public void setBuildingBlockAttributes(
			PictureBuildingBlockAttributes buildingBlockAttributes) {
		this.buildingBlockAttributes = buildingBlockAttributes;
	}
}
