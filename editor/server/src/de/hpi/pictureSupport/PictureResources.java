package de.hpi.pictureSupport;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("resources")
public class PictureResources {

	@Element(name="resource", targetType=PictureResource.class)
	private ArrayList<PictureResource> children;

	public ArrayList<PictureResource> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<PictureResource> list) {
		children = list;
	}
}
