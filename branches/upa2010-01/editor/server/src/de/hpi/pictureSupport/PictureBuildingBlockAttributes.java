package de.hpi.pictureSupport;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("buildingBlockAttributes")
public class PictureBuildingBlockAttributes {

	@Element(targetType=PictureStandardAttributes.class)
	private PictureStandardAttributes standardAttributes;
	
	@Element(targetType=PictureResourceAttributes.class)
	private PictureResourceAttributes resourceAttributes;

	public PictureStandardAttributes getStandardAttributes() {
		return standardAttributes;
	}

	public void setStandardAttributes(PictureStandardAttributes standardAttributes) {
		this.standardAttributes = standardAttributes;
	}

	public PictureResourceAttributes getResourceAttributes() {
		return resourceAttributes;
	}

	public void setResourceAttributes(PictureResourceAttributes resourceAttributes) {
		this.resourceAttributes = resourceAttributes;
	}
}
