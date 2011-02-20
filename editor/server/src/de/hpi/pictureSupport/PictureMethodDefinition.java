package de.hpi.pictureSupport;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("methodDefinition")
public class PictureMethodDefinition {

	@Element(name="buildingBlockTypes", targetType=PictureBuildingBlockTypes.class)
	protected PictureBuildingBlockTypes buildingBlockTypes;
	
	@Element(name="attributeTypes", targetType=PictureAttributeTypes.class)
	protected PictureAttributeTypes attributeTypes;
	
	public PictureBuildingBlockTypes getBuildingBlockTypes() {
		return buildingBlockTypes;
	}

	public void setBuildingBlockTypes(PictureBuildingBlockTypes buildingBlockTypes) {
		this.buildingBlockTypes = buildingBlockTypes;
	}

	public PictureAttributeTypes getAttributeTypes() {
		return attributeTypes;
	}

	public void setAttributeTypes(PictureAttributeTypes attributeTypes) {
		this.attributeTypes = attributeTypes;
	}

}
