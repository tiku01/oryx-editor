package de.hpi.pictureSupport;

import org.xmappr.Attribute;
import org.xmappr.RootElement;

@RootElement("buildingBlockOccurrence")
public class PictureBuildingBlockOccurrence {

	@Attribute(name="twin")
	private Boolean isTwin;
	
	@Attribute
	private int buildingBlock;

	public Boolean getIsTwin() {
		return isTwin;
	}

	public void setIsTwin(Boolean isTwin) {
		this.isTwin = isTwin;
	}

	public int getBuildingBlock() {
		return buildingBlock;
	}

	public void setBuildingBlock(int buildingBlock) {
		this.buildingBlock = buildingBlock;
	}
}
