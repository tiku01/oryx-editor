package de.hpi.pictureSupport;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("processModels")
public class PictureProcessModels {

	@Element(name="processModel", targetType=PictureProcessModel.class)
	private ArrayList<PictureProcessModel> children;

	public ArrayList<PictureProcessModel> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<PictureProcessModel> children) {
		this.children = children;
	}
}
