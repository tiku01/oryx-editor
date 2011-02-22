package de.hpi.pictureSupport;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("processFlow")
public class PictureProcessFlow {

	@Element(name="subprocess", targetType=PictureSubprocess.class)
	private ArrayList<PictureSubprocess> children;

	public ArrayList<PictureSubprocess> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<PictureSubprocess> children) {
		this.children = children;
	}
}
