package de.hpi.pictureSupport;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("buildingBlockTypes")
public class PictureBuildingBlockTypes {

	@Element(name="buildingBlockType", targetType=PictureBuildingBlockType.class)
	private ArrayList<PictureBuildingBlockType> children;

	public ArrayList<PictureBuildingBlockType> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<PictureBuildingBlockType> list) {
		children = list;
	}
}
