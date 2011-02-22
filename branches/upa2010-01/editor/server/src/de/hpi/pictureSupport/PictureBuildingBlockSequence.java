package de.hpi.pictureSupport;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("buildingBlockSequence")
public class PictureBuildingBlockSequence {

	@Element(name="buildingBlockOccurrence", targetType=PictureBuildingBlockOccurrence.class)
	private ArrayList<PictureBuildingBlockOccurrence> children;

	public ArrayList<PictureBuildingBlockOccurrence> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<PictureBuildingBlockOccurrence> children) {
		this.children = children;
	}
}
