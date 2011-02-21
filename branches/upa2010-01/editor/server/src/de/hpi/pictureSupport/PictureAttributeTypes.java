package de.hpi.pictureSupport;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("attributeTypes")
public class PictureAttributeTypes {

	@Element(name="attributeType", targetType=PictureAttributeType.class)
	private ArrayList<PictureAttributeType> children;

	public ArrayList<PictureAttributeType> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<PictureAttributeType> list) {
		children = list;
	}
}
