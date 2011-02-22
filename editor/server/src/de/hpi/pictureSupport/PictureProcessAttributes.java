package de.hpi.pictureSupport;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("processAttributes")
public class PictureProcessAttributes {

	@Element(name="processAttribute", targetType=PictureProcessAttribute.class)
	private ArrayList<PictureProcessAttribute> children;

	public ArrayList<PictureProcessAttribute> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<PictureProcessAttribute> children) {
		this.children = children;
	}
}
