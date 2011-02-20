package de.hpi.pictureSupport;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("positions")
public class PicturePositions {

	@Element(name="position", targetType=PicturePosition.class)
	protected ArrayList<PicturePosition> children;

	public ArrayList<PicturePosition> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<PicturePosition> children) {
		this.children = children;
	}
}
