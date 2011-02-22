package de.hpi.pictureSupport;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("variantAttributes")
public class PictureVariantAttributes {

	@Element(name="variantAttribute", targetType=PictureProcessAttribute.class)
	private ArrayList<PictureProcessAttribute> children;

	public ArrayList<PictureProcessAttribute> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<PictureProcessAttribute> children) {
		this.children = children;
	}
}
