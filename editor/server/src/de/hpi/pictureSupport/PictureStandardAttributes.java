package de.hpi.pictureSupport;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("standardAttributes")
public class PictureStandardAttributes {

	@Element(name="standardAttribute", targetType=PictureStandardAttribute.class)
	private ArrayList<PictureStandardAttribute> children;

	public ArrayList<PictureStandardAttribute> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<PictureStandardAttribute> children) {
		this.children = children;
	}
}
