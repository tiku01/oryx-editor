package de.hpi.pictureSupport;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("processes")
public class PictureProcesses {

	@Element(name="process", targetType=PictureProcess.class)
	private ArrayList<PictureProcess> children;

	public ArrayList<PictureProcess> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<PictureProcess> list) {
		children = list;
	}
}
