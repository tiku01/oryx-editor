package de.hpi.pictureSupport;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("buildingBlockRepository")
public class PictureBuildingBlockRepository {

	@Element(name="buildingBlock", targetType=PictureBuildingBlock.class)
	private ArrayList<PictureBuildingBlock> children;

	public ArrayList<PictureBuildingBlock> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<PictureBuildingBlock> children) {
		this.children = children;
	}
}
