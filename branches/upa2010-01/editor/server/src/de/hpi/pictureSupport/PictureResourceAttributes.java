package de.hpi.pictureSupport;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("resourceAttributes")
public class PictureResourceAttributes {

	@Element(name="resourceAttribute", targetType=PictureResourceAttribute.class)
	private ArrayList<PictureResourceAttribute> children;

	public ArrayList<PictureResourceAttribute> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<PictureResourceAttribute> children) {
		this.children = children;
	}
}
