package de.hpi.pictureSupport;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("users")
public class PictureUsers {

	@Element(name="user", targetType=PictureUser.class)
	protected ArrayList<PictureUser> children;

	public ArrayList<PictureUser> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<PictureUser> list) {
		children = list;
	}
}